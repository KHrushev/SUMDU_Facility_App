package com.khrushchov.dbproject.DAO;

import com.khrushchov.dbproject.model.Employee;
import com.khrushchov.dbproject.model.Facility;
import com.khrushchov.dbproject.model.Passage;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class OracleDAOConnection implements DAOConnection {
    private static OracleDAOConnection oracleDAOConnection;

    private Connection connection;
    private Statement statement;
    private ResultSet resultSet;

    private OracleDAOConnection() {
        super();
    }

    public static OracleDAOConnection getInstance() {
        if (oracleDAOConnection != null) {
            return oracleDAOConnection;
        } else {
            oracleDAOConnection = new OracleDAOConnection();
            return oracleDAOConnection;
        }
    }

    @Override
    public List<Employee> findPassagesByFacilityID(int facilityID) {
        List<Employee> employees = new ArrayList<>();

        try {
            connection = DBConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM EMPLOYEE e WHERE e.employee_id in (SELECT DISTINCT PASSAGES.employee_id FROM PASSAGES WHERE PASSAGES.FACILITY_ID = " + facilityID +" AND ENTER_TIME > (CURRENT_DATE - 7))");

            while (resultSet.next()) {
                employees.add(parseEmployee(resultSet));
            }

            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return employees;
    }

    @Override
    public List<Employee> findEmployeesThatVisitedFacility(int facilityID) {
        List<Employee> employees = new ArrayList<>();

        try {
            connection = DBConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM EMPLOYEE where EMPLOYEE_ID in (SELECT DISTINCT PASSAGES.EMPLOYEE_ID from PASSAGES where FACILITY_ID = " + facilityID + ")");

            while (resultSet.next()) {
                employees.add(parseEmployee(resultSet));
            }

            return employees;
        } catch (SQLException e) {
            e.printStackTrace();
            return employees;
        }
    }

    @Override
    public List<Employee> findEmployeesByHoursWorkedAtFacility(int hours, int facilityID) {
        List<Employee> employees = new ArrayList<>();

        try {
            connection = DBConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery("select * from EMPLOYEE where employee_id in (select employee_id from PASSAGES having sum(worktime) < " + hours + " and passages.facility_id = " + facilityID + " group by employee_id, PASSAGES.facility_id)");

            while (resultSet.next()) {
                employees.add(parseEmployee(resultSet));
            }

            return employees;
        } catch (SQLException e) {
            e.printStackTrace();
            return employees;
        }
    }

    @Override
    public List<Employee> findOverlappedEmployeesAtFacility(int employeeID, int facilityID) {
        List<Employee> employees = new ArrayList<>();

        try {
            connection = DBConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery("select * from EMPLOYEE where employee_id in (\n" +
                    "    select distinct t1.employee_id from PASSAGES t1\n" +
                    "    join PASSAGES t2\n" +
                    "    on (t1.enter_time>= t2.enter_time and t1.enter_time <= t2.exit_time  and t1.facility_id = t2.facility_id and t1.passage_id != t2.passage_id)\n" +
                    "    or (t1.exit_time >= t2.enter_time and t1.exit_time  <= t2.exit_time  and t1.facility_id = t2.facility_id and t1.passage_id != t2.passage_id)\n" +
                    "    or (t1.exit_time >= t2.exit_time  and t1.enter_time <= t2.enter_time and t1.facility_id = t2.facility_id and t1.passage_id != t2.passage_id)\n" +
                    "    where t1.facility_id = " + facilityID + " and t2.facility_id = " + facilityID + "\n" +
                    ") and employee_id != " + employeeID);

            while (resultSet.next()) {
//                System.out.println(resultSet.getInt(1));
                employees.add(parseEmployee(resultSet));
            }

            return employees;
        } catch (SQLException e) {
            e.printStackTrace();
            return employees;
        }
    }

    @Override
    public Map<Employee, Integer> findTopEmployeesByTotalTimeLate() {
        Map<Employee, Integer> employeeByLateTime = new HashMap<>();

        try {
            connection = DBConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery("select e.employee_id, e.full_name, e.role, sum(extract(hour from enter_time) - 9) as total_time_late from PASSAGES p join EMPLOYEE e on p.employee_id = e.employee_id where extract(hour from enter_time) > 9 and ROWNUM <= 5 group by p.employee_id, e.employee_id, e.role, e.full_name order by total_time_late desc");

            while (resultSet.next()) {
                int id = resultSet.getInt("EMPLOYEE_ID");
                String role = resultSet.getString("ROLE");
                String fullName = resultSet.getString("FULL_NAME");
                int timeLate = resultSet.getInt("TOTAL_TIME_LATE");

                employeeByLateTime.put(new Employee(id, role, fullName), timeLate);
            }

            employeeByLateTime = employeeByLateTime.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
            return employeeByLateTime;
        } catch (SQLException e) {
            e.printStackTrace();
            return employeeByLateTime;
        }
    }

    @Override
    public Map<Integer, Integer> findFacilitiesByIllegalTimeSpent() {
        Map<Integer, Integer> facilityWorkTimeMap = new HashMap<>();

        try {
            connection = DBConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery("select p.facility_id, sum(p.WORKTIME) as \"ILLEGAL WORK TIME\" from PASSAGES p join EMPLOYEE e on p.employee_id = e.employee_id where e.facility_id != p.facility_id group by p.facility_id");

            while (resultSet.next()) {
                int id = resultSet.getInt("FACILITY_ID");
                int illegalWorkTime = resultSet.getInt("ILLEGAL WORK TIME");

                facilityWorkTimeMap.put(id, illegalWorkTime);
            }

            facilityWorkTimeMap = facilityWorkTimeMap.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (oldValue, newValue) -> oldValue, LinkedHashMap::new));
            return facilityWorkTimeMap;
        } catch (SQLException e) {
            e.printStackTrace();
            return facilityWorkTimeMap;
        }
    }

    @Override
    public List<Passage> findPassagesByEmployeeID(int employeeID) {
        List<Passage> passages = new ArrayList<>();

        try {
            connection = DBConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery("select * from PASSAGES where EMPLOYEE_ID =" + employeeID);

            while (resultSet.next()) {
                passages.add(parsePassage(resultSet));
            }

            return passages;
        } catch (SQLException e) {
            e.printStackTrace();
            return passages;
        }
    }

    @Override
    public Passage findPassageByEmployeeIDAndEnterTime(int employeeID, Timestamp enterDate) {
        Passage passage = new Passage();

        try {
            connection = DBConnectionPool.getConnection();
            statement = connection.createStatement();

            String sDateTime = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(enterDate);

            resultSet = statement.executeQuery("select * from PASSAGES where EMPLOYEE_ID = " + employeeID + " and ENTER_TIME = '" + sDateTime + "'");

            while (resultSet.next()) {
                passage = parsePassage(resultSet);
            }

            return passage;
        } catch (SQLException e) {
            e.printStackTrace();
            return passage;
        }
    }

    @Override
    public List<Facility> findFacilityByEmployeeID(int employeeID) {
        List<Facility> facilities = new ArrayList<>();

        try {
            connection = DBConnectionPool.getConnection();
            statement = connection.createStatement();

            resultSet = statement.executeQuery("select * from FACILITIES f where f.facility_id = (select e.facility_id from EMPLOYEE e where e.employee_id = " + employeeID +")");

            while (resultSet.next()) {
                facilities.add(parseFacility(resultSet));
            }

            return facilities;
        } catch (SQLException e) {
            e.printStackTrace();
            return facilities;
        }
    }

    @Override
    public Map<Integer, Integer> findSystemMalfunctions() {
        Map<Integer, Integer> passageMap = new HashMap<>();

        try {
            connection = DBConnectionPool.getConnection();
            statement = connection.createStatement();

            resultSet = statement.executeQuery("select distinct p1.passage_id as \"FIRST_PASSAGE\", p2.passage_id as \"SECOND_PASSAGE\" from PASSAGES p1 join PASSAGES p2 on p1.enter_time >= p2.enter_time and p1.exit_time <= p2.exit_time and p1.employee_id = p2.employee_id and p1.passage_id != p2.passage_id order by p1.passage_id");

            while (resultSet.next()) {
                passageMap.put(resultSet.getInt("FIRST_PASSAGE"), resultSet.getInt("SECOND_PASSAGE"));
            }

            System.out.println(passageMap.size());
            passageMap = passageMap.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (oldValue, newValue) -> oldValue, LinkedHashMap::new));

            return passageMap;
        } catch (SQLException e) {
            e.printStackTrace();
            return passageMap;
        }
    }

    private Facility parseFacility(ResultSet resultSet) {
        try {
            int id = resultSet.getInt("FACILITY_ID");
            String facilityName = resultSet.getString("FACILITY_NAME");

            return new Facility(id, facilityName);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Passage parsePassage(ResultSet resultSet) {
        try {
            int id = resultSet.getInt("PASSAGE_ID");
            Timestamp enterTime = resultSet.getTimestamp("ENTER_TIME");
            Timestamp exitTime = resultSet.getTimestamp("EXIT_TIME");
            int employeeID = resultSet.getInt("EMPLOYEE_ID");
            int facilityID = resultSet.getInt("FACILITY_ID");
            int worktime = resultSet.getInt("WORKTIME");

            return new Passage(id, enterTime, exitTime, employeeID, facilityID, worktime);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Employee parseEmployee(ResultSet resultSet) {
        try {
            int id = resultSet.getInt("EMPLOYEE_ID");
            String username = resultSet.getString("USERNAME");
            int facilityID = resultSet.getInt("FACILITY_ID");
            String role = resultSet.getString("ROLE");
            String fullName = resultSet.getString("FULL_NAME");

            return new Employee(id, username, facilityID, role, fullName);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
