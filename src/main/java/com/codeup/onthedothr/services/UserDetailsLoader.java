package com.codeup.onthedothr.services;

import com.codeup.onthedothr.models.Employee;
import com.codeup.onthedothr.models.UserWithRoles;
import com.codeup.onthedothr.repositories.EmployeeRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserDetailsLoader implements UserDetailsService {
    private final EmployeeRepository employees;

    public UserDetailsLoader(EmployeeRepository employees) {
        this.employees = employees;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Employee employee = employees.findByUsername(username);
        if (employee == null) {
            throw new UsernameNotFoundException("No user found for " + username);
        }

        return new UserWithRoles(employee);
    }
}
