package com.opspilot.platform.security;

import com.opspilot.platform.user.Employee;
import com.opspilot.platform.user.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

/**
 * Custom UserDetailsService implementation for loading employee details.
 * Used by Spring Security for authentication.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CustomUserDetailsService implements UserDetailsService {

    private final EmployeeRepository employeeRepository;

    /**
     * Load user by username (email).
     *
     * @param email the employee email
     * @return UserDetails object
     * @throws UsernameNotFoundException if employee not found
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("Loading user details for email: {}", email);

        Employee employee = employeeRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Employee not found with email: {}", email);
                    return new UsernameNotFoundException("Employee not found with email: " + email);
                });

        if (!employee.getActive()) {
            log.warn("Inactive employee attempted login: {}", email);
            throw new UsernameNotFoundException("Employee account is inactive");
        }

        List<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + employee.getRole().name())
        );

        return User.builder()
                .username(employee.getEmail())
                .password(employee.getPassword())
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(!employee.getActive())
                .build();
    }
}

