package com.softserve.bookstore.repositories;

import com.softserve.bookstore.connection.ConnectionManager;
import com.softserve.bookstore.exceptions.UserNotFoundException;
import com.softserve.bookstore.generated.Role;
import com.softserve.bookstore.generated.User;
import com.softserve.bookstore.generated.UserDto;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.sql.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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

        mockResultSet = createUsersListMockForResultSet();
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
    public void getUserById_ReturnsUser_Success() throws SQLException {
        when(mockConnection.prepareStatement(UserRepository.SELECT_USERS)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        mockResultSet = createUsersListMockForResultSet();
        userRepository.findAll();

        when(mockConnection.prepareStatement(UserRepository.SELECT_USERS_ROLES)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        mockResultSet = createUsersListMockForResultSet();
        User user = userRepository.getUserById(expectedUser1.getUserId()).get();
        assertEquals(user.getUserId(), expectedUser1.getUserId());
    }

    @Test
    public void getUserByEmail_ReturnsUser_Success() {
        User user = userRepository.getUserByEmail(users, expectedUser1.getEmail()).get();
        assertEquals(user.getEmail(), expectedUser1.getEmail());
        assertEquals(user.getUserId(), expectedUser1.getUserId());
    }

    @Test
    public void findLastUsers_Returns_Success() throws SQLException {
        List<User> users = List.of(expectedUser1, expectedUser2);
        when(mockConnection.prepareStatement(UserRepository.SELECT_LAST_USERS)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        mockResultSet = createUsersListMockForResultSet();

        List<User> lastUsersAdded = userRepository.findLastUsersAdded(users.size());
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

        mockResultSet = createUsersListMockForResultSet();

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
        mockResultSet = createUsersListMockForResultSet();

        List<User> lastUsersAdded = userRepository.findLastUsersAdded(users.size());
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
    public void addUser_Adds_Success() throws SQLException {
        when(mockConnection.prepareStatement(UserRepository.INSERT_USER, Statement.RETURN_GENERATED_KEYS)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        when(mockPreparedStatement.getGeneratedKeys()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(1);

        UserDto userDto = userRepository.addUser(expectedUser1);

        verify(mockPreparedStatement, times(1)).setString(1, expectedUser1.getEmail());
        verify(mockPreparedStatement, times(1)).setString(1, expectedUser1.getPassword());
        assertEquals(expectedUser1.getEmail(), userDto.getEmail());

    }

    @Test
    public void findAll_ThrowsSqlException_Failure() throws SQLException {
        doThrow(new SQLException(expectedExceptionMessage)).when(mockConnection).prepareStatement(UserRepository.SELECT_USERS);
        Exception actualException = assertThrows(SQLException.class, () -> {
            userRepository.findAll();
        });
        assertEquals(actualException.getMessage(), expectedExceptionMessage);
    }

    @Test
    public void deleteUser_ThrowsUserNotFound_Failure() throws SQLException {
        doThrow(new UserNotFoundException(expectedExceptionMessage)).when(mockConnection).prepareStatement(UserRepository.SELECT_USERS);
        Exception actualException = assertThrows(UserNotFoundException.class, () -> {
            userRepository.deleteUserById(expectedUser1.getUserId());
        });
        assertEquals(actualException.getMessage(), expectedExceptionMessage);
    }

    @Test
    public void deleteUser_ThrowsSqlException_Failure() throws SQLException {
        when(mockConnection.prepareStatement(UserRepository.SELECT_USERS)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        mockResultSet = createUsersListMockForResultSet();

        doThrow(new SQLException(expectedExceptionMessage)).when(mockConnection).prepareStatement(UserRepository.DELETE_ROLE);
        Exception actualException = assertThrows(SQLException.class, () -> {
            userRepository.deleteUserById(expectedUser1.getUserId());
        });
        assertEquals(actualException.getMessage(), expectedExceptionMessage);
    }

    @Test
    public void subscribeToNewsletter_Success() throws SQLException {
        when(mockConnection.prepareStatement(UserRepository.SUBSCRIBE_USER)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        userRepository.subscribeToNewsletter(expectedUser1.getUserId());
        verify(mockPreparedStatement, times(1)).setInt(1, expectedUser1.getUserId());
    }

    @Test
    public void unsubscribeToNewsletter_Success() throws SQLException {
        when(mockConnection.prepareStatement(UserRepository.UNSUBSCRIBE_USER)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        userRepository.unsubscribeFromNewsletter(expectedUser1.getUserId());
        verify(mockPreparedStatement, times(1)).setInt(1, expectedUser1.getUserId());
    }


    public ResultSet createUsersListMockForResultSet() throws SQLException {
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

    public ResultSet createUserMockResultSet() throws SQLException {
        when(mockResultSet.next())
                .thenReturn(true)
                .thenReturn(false);

        when(mockResultSet.getInt(User.FIELD_USER_ID)).thenReturn(expectedUser1.getUserId());
        when(mockResultSet.getString(User.FIELD_EMAIL)).thenReturn(expectedUser1.getEmail());
        when(mockResultSet.getString(User.FIELD_PASSWORD)).thenReturn(expectedUser1.getPassword());
        return mockResultSet;
    }

    public ResultSet createRolesListMockForResultSet() throws SQLException {
        when(mockResultSet.next())
                .thenReturn(true)
                .thenReturn(false);

        when(mockResultSet.getInt("id"))
                .thenReturn(anyInt());
        when(mockResultSet.getInt("id_user"))
                .thenReturn(expectedUser1.getUserId());
        when(mockResultSet.getInt("id_role"))
                .thenReturn(1);
        return mockResultSet;
    }

}