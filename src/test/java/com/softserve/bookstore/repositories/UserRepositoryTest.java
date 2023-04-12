package com.softserve.bookstore.repositories;

import com.softserve.bookstore.connection.ConnectionManager;
import com.softserve.bookstore.exceptions.UserNotFoundException;
import com.softserve.bookstore.models.Role;
import com.softserve.bookstore.models.User;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class})
public class UserRepositoryTest {

    private static final User expectedUser1 = new User(1, "user1@gmail.com", "user1", Collections.emptyList(), Arrays.asList(Role.USER, Role.ADMIN));
    private static final User expectedUser2 = new User(2, "user2@gmail.com", "user2", Collections.emptyList(), List.of(Role.USER));
    private static List<User> users = List.of(expectedUser1, expectedUser2);

    private final String expectedExceptionMessage = "Expected exception message";

    @InjectMocks
    private UserRepository userRepository;
    @Mock
    private ConnectionManager mockConnectionManager;
    @Mock
    private Connection mockConnection;
    @Mock
    private PreparedStatement mockPreparedStatement;
    @Mock
    private ResultSet mockResultSet;

    @Test
    @SneakyThrows
    public void findAll_ReturnsTwoUser_Success() {
        when(mockConnection.prepareStatement(UserRepository.SELECT_USERS)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        mockResultSet = createMockForResultSet();
        List<User> userList = userRepository.findAll();

        verify(mockResultSet, times(3)).next();
        assertEquals(userList.size(), 2);

        User foundUser1 = userList.get(0);
        assertEquals(foundUser1.getUserId(), expectedUser1.getUserId());
        assertEquals(foundUser1.getEmail(), expectedUser1.getEmail());
        assertEquals(foundUser1.getPassword(), expectedUser1.getPassword());

        User foundUser2 = userList.get(1);
        assertEquals(foundUser2.getUserId(), expectedUser2.getUserId());
        assertEquals(foundUser2.getEmail(), expectedUser2.getEmail());
        assertEquals(foundUser2.getPassword(), expectedUser2.getPassword());
    }

    @Test
    public void findLastUsers_Returns_Success() throws SQLException {
        List<User> users = List.of(expectedUser1, expectedUser2);
        when(mockConnection.prepareStatement(UserRepository.SELECT_LAST_USERS)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        mockResultSet = createMockForResultSet();

        List<User>  lastUsersAdded = userRepository.findLastUsersAdded(users.size());
        verify(mockResultSet, times(3)).next();
        assertEquals(users.size(), lastUsersAdded.size());
    }

      @Test
      @Transactional
      @Rollback
      public void deleteUser_Returns_OneAffectedRow() throws SQLException, UserNotFoundException {
          when(mockConnection.prepareStatement(UserRepository.SELECT_USERS))
                  .thenReturn(mockPreparedStatement);
          when(mockPreparedStatement.executeQuery())
                  .thenReturn(mockResultSet);

          mockResultSet = createMockForResultSet();

          when(mockConnection.prepareStatement(UserRepository.DELETE_ROLE))
                  .thenReturn(mockPreparedStatement);
          when(mockPreparedStatement.executeUpdate())
                  .thenReturn(1);

          when(mockConnection.prepareStatement(UserRepository.DELETE_ORDER))
                  .thenReturn(mockPreparedStatement);
          when(mockPreparedStatement.executeUpdate())
                  .thenReturn(1);

          when(mockConnection.prepareStatement(UserRepository.DELETE_USER))
                  .thenReturn(mockPreparedStatement);
          when(mockPreparedStatement.executeUpdate())
                  .thenReturn(1);

          int rowsAffected = userRepository.deleteUserById(expectedUser1.getUserId());

          verify(mockResultSet, times(3)).next();
          assertEquals(2, users.size());
          verify(mockPreparedStatement, times(3)).setInt(1, expectedUser1.getUserId());
          assertEquals(1, rowsAffected);
      }

    @Test
    @Transactional
    @Rollback
    public void add_AddsTwoUsers_Success() throws SQLException, UserNotFoundException {
        when(mockConnection.prepareStatement(UserRepository.INSERT_USER)).thenReturn(mockPreparedStatement);
        int[] expectedBatchResult = {1, 1};
        when(mockPreparedStatement.executeBatch()).thenReturn(expectedBatchResult);

        when(mockConnection.prepareStatement(UserRepository.SELECT_LAST_USERS)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        mockResultSet = createMockForResultSet();

        List<User>  lastUsersAdded = userRepository.findLastUsersAdded(users.size());
        userRepository.addUsers(users);

        assertEquals(users.size(), lastUsersAdded.size());

        verify(mockPreparedStatement, times(2)).addBatch();
        verify(mockPreparedStatement).executeBatch();
        verify(mockPreparedStatement, times(2)).setInt(1, users.size());
        verify(mockPreparedStatement, times(2)).executeQuery();
        verify(mockResultSet, times(2)).getInt(User.FIELD_USER_ID);
        verify(mockResultSet, times(2)).getString(User.FIELD_EMAIL);
        verify(mockResultSet, times(2)).getString(User.FIELD_PASSWORD);
    }

    @Test
    public void findAll_ThrowsSqlException_Failure() throws SQLException {
        doThrow (new SQLException(expectedExceptionMessage)).when(mockConnection).prepareStatement(UserRepository.SELECT_USERS);
        Exception actualException = assertThrows(SQLException.class, () -> {
            userRepository.findAll();
        });
        assertEquals(actualException.getMessage(), expectedExceptionMessage);
    }

    @Test
    public void deleteUser_ThrowsSqlException_Failure() throws SQLException {
        doThrow (new SQLException(expectedExceptionMessage)).when(mockConnection).prepareStatement(UserRepository.DELETE_USER);
        Exception actualException = assertThrows(SQLException.class, () -> {
            userRepository.deleteUserById(expectedUser1.getUserId());
        });
        assertEquals(actualException.getMessage(), expectedExceptionMessage);
    }

    @Test
    public void deleteUser_ThrowsUserNotFoundException_Failure() throws SQLException {
        doThrow (new UserNotFoundException(expectedExceptionMessage)).when(mockConnection).prepareStatement(UserRepository.DELETE_USER);
        Exception actualException = assertThrows(UserNotFoundException.class, () -> {
            userRepository.deleteUserById(expectedUser1.getUserId());
        });
        assertEquals(actualException.getMessage(), expectedExceptionMessage);
    }





    public ResultSet  createMockForResultSet() throws SQLException {
        when(mockResultSet.next())
                .thenReturn(true)
                .thenReturn(true)
                .thenReturn(false);

        when(mockResultSet.getInt(User.FIELD_USER_ID))
                .thenReturn(expectedUser1.getUserId())
                .thenReturn(expectedUser2.getUserId());
        when(mockResultSet.getString(User.FIELD_EMAIL))
                .thenReturn(expectedUser1.getEmail())
                .thenReturn(expectedUser2.getEmail());
        when(mockResultSet.getString(User.FIELD_PASSWORD))
                .thenReturn(expectedUser1.getPassword())
                .thenReturn(expectedUser2.getPassword());
        return mockResultSet;
    }

}