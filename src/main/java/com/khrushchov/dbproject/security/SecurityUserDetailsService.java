package com.khrushchov.dbproject.security;

import com.khrushchov.dbproject.model.Employee;
import com.khrushchov.dbproject.repositories.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class SecurityUserDetailsService implements UserDetailsService {
    @Autowired
    private EmployeeRepository employeeRepository;


    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return employeeRepository.findEmployeeByUsername(s).orElseThrow(() -> new UsernameNotFoundException("User not present"));
    }

    public void createEmployee(UserDetails employee) {
        employeeRepository.save((Employee) employee);
    }
}
