package com.khrushchov.dbproject.controllers;

import com.khrushchov.dbproject.DAO.DAOConnection;
import com.khrushchov.dbproject.DAO.OracleDAOConnection;
import com.khrushchov.dbproject.model.Employee;
import com.khrushchov.dbproject.model.Facility;
import com.khrushchov.dbproject.repositories.FacilityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Controller
public class AdminQueryController {
    @Autowired private FacilityRepository facilityRepository;

    @PostMapping("/addFacility")
    public String addFacility(Model model, @RequestParam(name = "facilityName") String facilityName) {
        Facility newFacility = new Facility();
        newFacility.setFacilityName(facilityName);

        facilityRepository.save(newFacility);

        model.addAttribute("facilityAdded", true);

        return "index";
    }

    @GetMapping("/entrancesByFacilityID")
    public String entrancesByFacilityID(Model model, @RequestParam(name = "facilityID") String sFacilityID) {
        DAOConnection daoConnection = OracleDAOConnection.getInstance();
        int facilityID = Integer.parseInt(sFacilityID);
        List<Employee> employees = daoConnection.findPassagesByFacilityID(facilityID);

        if (employees.size()>0) {
            model.addAttribute("employees", employees);
            model.addAttribute("entrancesByFacilityID", "true");
        } else {
            model.addAttribute("entrancesByFacilityID", "false");
        }

        return "index";
    }

    @GetMapping("/facilityVisitors")
    public String facilityVisitors(Model model, @RequestParam(name = "facilityID") String sFacilityID) {
        DAOConnection daoConnection = OracleDAOConnection.getInstance();
        int facilityID = Integer.parseInt(sFacilityID);
        List<Employee> employees = daoConnection.findEmployeesThatVisitedFacility(facilityID);

        if (employees.size()>0) {
            model.addAttribute("employees", employees);
            model.addAttribute("facilityVisitors", "true");
        } else {
            model.addAttribute("facilityVisitors", "false");
        }

        return "index";
    }

    @GetMapping("/hoursSpent")
    public String hoursSpent(Model model, @RequestParam(name = "facilityID") String sFacilityID, @RequestParam(name = "hoursSpent") String sHours) {
        DAOConnection daoConnection = OracleDAOConnection.getInstance();
        int facilityID = Integer.parseInt(sFacilityID);
        int hours = Integer.parseInt(sHours);
        List<Employee> employees = daoConnection.findEmployeesByHoursWorkedAtFacility(hours, facilityID);

        if (employees.size()>0) {
            model.addAttribute("employees", employees);
            model.addAttribute("hoursSpent", "true");
        } else {
            model.addAttribute("hoursSpent", "false");
        }

        return "index";
    }

    @GetMapping("/overlappingEmployees")
    public String overlappingEmployees(Model model, @RequestParam(name = "facilityID") String sFacilityID, @RequestParam(name = "employeeID") String sEmployeeID) {
        DAOConnection daoConnection = OracleDAOConnection.getInstance();
        int facilityID = Integer.parseInt(sFacilityID);
        int employeeID = Integer.parseInt(sEmployeeID);
        List<Employee> employees = daoConnection.findOverlappedEmployeesAtFacility(employeeID, facilityID);

        if (employees.size()>0) {
            model.addAttribute("employees", employees);
            model.addAttribute("overlappingEmployees", "true");
        } else {
            model.addAttribute("overlappingEmployees", "false");
        }

        return "index";
    }

    @GetMapping("/totalTimeLateRating")
    public String totalTimeLateRating(Model model) {
        DAOConnection daoConnection = OracleDAOConnection.getInstance();

        Map<Employee, Integer> rating = daoConnection.findTopEmployeesByTotalTimeLate();

        if (rating.size()>0) {
            model.addAttribute("rating", rating);
            model.addAttribute("totalTimeLateRating", "true");
        } else {
            model.addAttribute("totalTimeLateRating", "false");
        }

        return "index";
    }

    @GetMapping("/illegalWorkTime")
    public String illegalWorkTime(Model model) {
        DAOConnection daoConnection = OracleDAOConnection.getInstance();

        Map<Integer, Integer> rating = daoConnection.findFacilitiesByIllegalTimeSpent();

        if (rating.size()>0) {
            model.addAttribute("rating", rating);
            model.addAttribute("illegalWorkTime", "true");
        } else {
            model.addAttribute("illegalWorkTime", "false");
        }

        return "index";
    }

    @GetMapping("/systemMalfunctions")
    public String systemMalfunctions(Model model) {
        DAOConnection daoConnection = OracleDAOConnection.getInstance();

        Map<Integer, Integer> malfunctions = daoConnection.findSystemMalfunctions();

        if (malfunctions.size()>0) {
            model.addAttribute("malfunctions", malfunctions);
            model.addAttribute("systemMalfunctions", "true");
        } else {
            model.addAttribute("systemMalfunctions", "false");
        }

        return "index";
    }
}
