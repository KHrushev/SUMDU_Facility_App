package com.khrushchov.dbproject.model;

import javax.persistence.*;

@Entity
@Table(name = "FACILITIES")
public class Facility {
    @Id
    @Column(name = "facility_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "facility_name")
    private String facilityName;

    public Facility() {}

    public Facility(int id, String facilityName) {
        this.id = id;
        this.facilityName = facilityName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }
}
