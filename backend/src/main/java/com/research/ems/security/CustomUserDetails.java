package com.research.ems.security;

import com.research.ems.entity.UserAccount;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Locale;

public class CustomUserDetails implements UserDetails {

    private final UserAccount userAccount;

    public CustomUserDetails(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String role = userAccount.getRole().name().toUpperCase(Locale.ROOT);
        role = "ROLE_" + role;
        return java.util.List.of(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getPassword() {
        return userAccount.getPassword();
    }

    @Override
    public String getUsername() {
        return userAccount.getUsername();
    }

    @Override
    public boolean isEnabled() {
        return Boolean.TRUE.equals(userAccount.getEnabled());
    }
}
