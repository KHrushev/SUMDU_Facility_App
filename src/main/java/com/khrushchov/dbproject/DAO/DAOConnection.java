package com.khrushchov.dbproject.DAO;

import com.khrushchov.dbproject.model.Employee;
import com.khrushchov.dbproject.model.Facility;
import com.khrushchov.dbproject.model.Passage;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

public interface DAOConnection {
    List<Employee> findPassagesByFacilityID(int facilityID);
    List<Employee> findEmployeesThatVisitedFacility(int facilityID);
    List<Employee> findEmployeesByHoursWorkedAtFacility(int hours, int facilityID);
    List<Employee> findOverlappedEmployeesAtFacility(int employeeID, int facilityID);
    Map<Employee, Integer> findTopEmployeesByTotalTimeLate();
    Map<Integer, Integer> findSystemMalfunctions();
    Map<Integer, Integer> findFacilitiesByIllegalTimeSpent();
    List<Facility> findFacilityByEmployeeID(int employeeID);
    List<Passage> findPassagesByEmployeeID(int employeeID);
    Passage findPassageByEmployeeIDAndEnterTime(int employeeID, Timestamp enterDate);
}
