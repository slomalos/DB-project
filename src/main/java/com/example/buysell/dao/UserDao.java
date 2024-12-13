package com.example.buysell.dao;

import com.example.buysell.models.User;
import com.example.buysell.models.enums.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class UserDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public User findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{email}, new UserRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public User findById(Long id) {
        String sql = "SELECT id, email, phone_number, name, active, activation_code, password FROM users WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{id}, new UserRowMapper());
    }

    public List<User> findAll() {
        String sql = "SELECT id, email, phone_number, name, active, activation_code, password FROM users";
        return jdbcTemplate.query(sql, new UserRowMapper());
    }

    public void save(User user) {
        String sql = "INSERT INTO users (email, phone_number, name, active, activation_code, password) VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, user.getEmail(), user.getPhone_number(), user.getName(), user.isActive(), user.getActivation_code(), user.getPassword());
    }

    public void update(User user) {
        String sql = "UPDATE users SET email = ?, phone_number = ?, name = ?, active = ?, activation_code = ?, password = ? WHERE id = ?";
        jdbcTemplate.update(sql, user.getEmail(), user.getPhone_number(), user.getName(), user.isActive(), user.getActivation_code(), user.getPassword(), user.getId());
    }

    public void delete(Long id) {
        String sql = "DELETE FROM users WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    public void addRoleToUser(Long userId, Role role) {
        String sql = "INSERT INTO user_role (user_id, roles) VALUES (?, ?)";
        jdbcTemplate.update(sql, userId, role.name());
    }

    public void removeRoleFromUser(Long userId, Role role) {
        String sql = "DELETE FROM user_role WHERE user_id = ? AND roles = ?";
        jdbcTemplate.update(sql, userId, role.name());
    }

    private class UserRowMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setId(rs.getLong("id"));
            user.setEmail(rs.getString("email"));
            user.setPhone_number(rs.getString("phone_number")); // Убедитесь, что колонка существует
            user.setName(rs.getString("name"));
            user.setActive(rs.getBoolean("active"));
            user.setActivation_code(rs.getString("activation_code"));
            user.setPassword(rs.getString("password"));

            String roleSql = "SELECT roles FROM user_role WHERE user_id = ?";
            Set<Role> roles = new HashSet<>(jdbcTemplate.query(roleSql, new Object[]{user.getId()}, (rs1, rowNum1) -> Role.valueOf(rs1.getString("roles"))));
            user.setRoles(roles);

            return user;
        }
    }
}