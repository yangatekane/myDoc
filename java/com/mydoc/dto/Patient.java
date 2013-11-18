package com.mydoc.dto;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yanga on 2013/11/02.
 */
public class Patient implements Serializable{
    public String name;
    public String surname;
    public String pcase;
    public String paymentType;
    public String dateAdmitted;
    public boolean signiture;
    public Map<String,List<Content>> patientContent;

    public Patient(String name, String surname,
                   String pcase, String paymentType,
                   String dateAdmitted, boolean signiture) {
        this.name = name;
        this.surname = surname;
        this.pcase = pcase;
        this.paymentType = paymentType;
        this.dateAdmitted = dateAdmitted;
        this.signiture = signiture;
        this.patientContent = new LinkedHashMap<String, List<Content>>();
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getPcase() {
        return pcase;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public String getDateAdmitted() {
        return dateAdmitted;
    }

    public boolean isSigniture() {
        return signiture;
    }

    public Map<String, List<Content>> getPatientContent() {
        return patientContent;
    }
}
