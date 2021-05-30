package com.khrushchov.dbproject.controllers;

import com.khrushchov.dbproject.model.Employee;
import com.khrushchov.dbproject.security.SecurityUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

@Controller
public class HomeController {
    @Autowired private SecurityUserDetailsService userDetailsService;
    @Autowired private PasswordEncoder passwordEncoder;

    @GetMapping("/home")
    public String index() {
        return "index";
    }

    @GetMapping("/login")
    public String login(HttpServletRequest request, HttpSession session) {
        session.setAttribute(
                "error", getErrorMessage(request, "SPRING_SECURITY_LAST_EXCEPTION")
        );
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @PostMapping(
            value = "/register",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = {
            MediaType.APPLICATION_ATOM_XML_VALUE, MediaType.APPLICATION_JSON_VALUE }
    )
    public void addEmployee(@RequestParam Map<String, String> body) {
        Employee employee = new Employee();

        employee.setUsername(body.get("username"));
        employee.setPassword(passwordEncoder.encode(body.get("password")));
        employee.setFullName(body.get("fullName"));
        employee.setFacilityID(Integer.parseInt(body.get("facilityID")));
        employee.setRole(body.get("role"));

        userDetailsService.createEmployee(employee);
    }

    private String getErrorMessage(HttpServletRequest request, String key) {
        Exception exception = (Exception) request.getSession().getAttribute(key);
        String error = "";
        if (exception instanceof BadCredentialsException) {
            error = "Invalid username and password!";
        } else {
            error = "Invalid username and password!";
        }
        return error;
    }
}
