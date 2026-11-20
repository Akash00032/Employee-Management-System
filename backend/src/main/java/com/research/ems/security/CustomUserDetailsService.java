package com.research.ems.security;

import com.research.ems.repository.UserAccountRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserAccountRepository userAccountRepository;

    public CustomUserDetailsService(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        return userAccountRepository.findByUsernameIgnoreCase(username)
                .map(CustomUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User account not found"));
    }
}
