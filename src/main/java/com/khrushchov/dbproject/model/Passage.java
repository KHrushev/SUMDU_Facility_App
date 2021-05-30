package com.khrushchov.dbproject.model;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "PASSAGES")
public class Passage {
    @Id
    @Column(name = "passage_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "enter_time")
    private Timestamp enterTime;
    @Column(name = "exit_time")
    private Timestamp exitTime;
    @Column(name = "employee_id")
    private int employeeID;
    @Column(name = "facility_id")
    private int facilityID;
    private int worktime;

    public Passage() {}

    public Passage(int id, Timestamp enterTime, Timestamp exitTime, int employeeID, int facilityID, int worktime) {
        this.id = id;
        this.enterTime = enterTime;
        this.exitTime = exitTime;
        this.employeeID = employeeID;
        this.facilityID = facilityID;
        this.worktime = worktime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Timestamp getEnterTime() {
        return enterTime;
    }

    public void setEnterTime(Timestamp enterTime) {
        this.enterTime = enterTime;
    }

    public Timestamp getExitTime() {
        return exitTime;
    }

    public void setExitTime(Timestamp exitTime) {
        this.exitTime = exitTime;
    }

    public int getEmployeeID() {
        return employeeID;
    }

    public void setEmployeeID(int employeeID) {
        this.employeeID = employeeID;
    }

    public int getFacilityID() {
        return facilityID;
    }

    public void setFacilityID(int facilityID) {
        this.facilityID = facilityID;
    }

    public int getWorktime() {
        return worktime;
    }

    public void setWorktime(int worktime) {
        this.worktime = worktime;
    }
}
