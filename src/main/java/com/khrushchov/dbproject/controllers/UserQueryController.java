package com.khrushchov.dbproject.controllers;

import com.khrushchov.dbproject.DAO.DAOConnection;
import com.khrushchov.dbproject.DAO.OracleDAOConnection;
import com.khrushchov.dbproject.model.Employee;
import com.khrushchov.dbproject.model.Facility;
import com.khrushchov.dbproject.model.Passage;
import com.khrushchov.dbproject.repositories.PassageRepository;
import com.khrushchov.dbproject.security.SecurityUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.Timestamp;
import java.util.List;

@Controller
public class UserQueryController {
    @Autowired private SecurityUserDetailsService userDetailsService;
    @Autowired private PassageRepository passageRepository;

    @GetMapping("/userEntrances")
    public String userEntrances(Authentication authentication, Model model) {
        DAOConnection daoConnection = OracleDAOConnection.getInstance();

        Employee employee = (Employee) userDetailsService.loadUserByUsername(authentication.getName());

        List<Passage> passages = daoConnection.findPassagesByEmployeeID(employee.getId());

        if (passages.size()>0) {
            model.addAttribute("passages", passages);
            model.addAttribute("userEntrances", "true");
        } else {
            model.addAttribute("userEntrances", "false");
        }

        return "index";
    }

    @PostMapping("/registerPassage")
    public String registerPassage(Authentication authentication, Model model, @RequestParam(name = "enterTime") String sEnterTime, @RequestParam(name = "exitTime") String sExitTime, @RequestParam(name = "facilityID") String sFacilityID) {
        DAOConnection daoConnection = OracleDAOConnection.getInstance();
        Employee employee = (Employee) userDetailsService.loadUserByUsername(authentication.getName());

        sEnterTime = sEnterTime.replace('T', ' ').concat(":00.0");
        sExitTime = sExitTime.replace('T', ' ').concat(":00.0");
        Timestamp enterDate = Timestamp.valueOf(sEnterTime);
        Timestamp exitDate = Timestamp.valueOf(sExitTime);

        if (enterDate.after(exitDate) || enterDate.equals(exitDate)) {
            model.addAttribute("inputError", "true");
            model.addAttribute("errorMsg", "Entered enter or exit times are incorrect.");
            return "index";
        } else if (Integer.parseInt(sFacilityID) != employee.getFacilityID()) {
            model.addAttribute("inputError", "true");
            model.addAttribute("errorMsg", "Entered passage ID does not correspond to current user's facility ID");
            return "index";
        } else {
            Passage newPassage = new Passage();
            newPassage.setEnterTime(enterDate);
            newPassage.setExitTime(exitDate);
            newPassage.setEmployeeID(employee.getId());
            newPassage.setFacilityID(Integer.parseInt(sFacilityID));

            passageRepository.save(newPassage);

            Passage passageCheck = daoConnection.findPassageByEmployeeIDAndEnterTime(employee.getId(), enterDate);

            if (passageCheck != null) {
                model.addAttribute("passageRegistered", "true");
            } else {
                model.addAttribute("passageRegistered", "false");
            }

            return "index";
        }
    }


    @GetMapping("/designatedFacility")
    public String designatedFacility(Model model, Authentication auth) {
        DAOConnection daoConnection = OracleDAOConnection.getInstance();

        Employee employee = (Employee) userDetailsService.loadUserByUsername(auth.getName());

        List<Facility> facilities = daoConnection.findFacilityByEmployeeID(employee.getId());

        if (facilities != null) {
            model.addAttribute("facilities", facilities);
            model.addAttribute("designatedFacility", "true");
        } else {
            model.addAttribute("designatedFacility", "false");
        }

        return "index";
    }
}
