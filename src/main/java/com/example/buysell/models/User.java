package com.example.buysell.models;

import com.example.buysell.models.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;
@Data
@AllArgsConstructor
public class User implements UserDetails {
    private Long id;
    private String email;
    private String phone_number;
    private String name;
    private Image avatar;
    private boolean active;
    private String activation_code;
    private String password;
    private Set<Role> roles = new HashSet<>();
    private List<Product> products = new ArrayList<>();

    // Конструкторы
    public User() {}

    public User(Long id, String email, String phoneNumber, String name, boolean active, String activationCode, String password) {
        this.id = id;
        this.email = email;
        this.phone_number = phoneNumber;
        this.name = name;
        this.active = active;
        this.activation_code = activationCode;
        this.password = password;
    }

    // Методы для работы с продуктами
    public void addProductToUser(Product product) {
        product.setUser(this);
        products.add(product);
    }

    public boolean isAdmin() {
        return roles.contains(Role.ROLE_ADMIN);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }
}