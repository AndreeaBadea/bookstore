package com.softserve.bookstore.repositories;

import com.softserve.bookstore.connection.ConnectionManager;
import com.softserve.bookstore.models.Role;
import com.softserve.bookstore.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.tinylog.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

@Repository
public class RoleRepository {

    public static final String INSERT_USERS_ROLES = "INSERT INTO users_roles(id_user, id_role) VALUES(?,?)";

    @Autowired
    ConnectionManager connectionManager;

    public void addRole(String roleName, User user) throws SQLException {
        Connection connection = connectionManager.getConnection();

        Map<String, Integer> roleIds = new HashMap<>();
        roleIds.put(Role.USER.toString(), 1);
        roleIds.put(Role.ADMIN.toString(), 2);

        int roleId = Optional.ofNullable(roleIds.get(roleName))
                .orElseThrow(() -> new IllegalArgumentException("Invalid role name."));

        List<Role> roles = new ArrayList<>();
        roles.add(Role.valueOf(roleName));
        user.setRoles(roles);

        PreparedStatement roleStatement = connection.prepareStatement(INSERT_USERS_ROLES);
        roleStatement.setInt(1, user.getUserId());
        roleStatement.setInt(2, roleId);
        roleStatement.executeUpdate();
        Logger.info("User " + user.getEmail() + " has now role " + user.getRoles());
    }
}
