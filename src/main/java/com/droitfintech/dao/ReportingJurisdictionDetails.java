package com.droitfintech.dao;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


/*
 * Author: Baljeet Singh *
 */
@Entity
@Table (name = "ReportingJurisdictionDetails")
public class ReportingJurisdictionDetails implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ixReportingJurisdiction")
    private int ixReportingJurisdiction; // Auto generated id

    @Column(name = "ixProductMaster")
    private int ixProductMaster; // Foreign Key of Product Master table.

    @Column(name = "idRegulator")
    private int idRegulator; // Foreign Key of Regulator Master table.

    @Column(name = "reportingSDR")
    private String reportingSDR; // SDR for reporting

    @Column(name = "reportingDate")
    private long reportingDate; // Reporting Date for trade


    public int getIxReportingJurisdiction() // getter method for
    // ixRegulatoryJurisdiction
    {
        return ixReportingJurisdiction;
    }


    public void setIxReportingJurisdiction(int ixRegulatoryJurisdiction) // setter
    // method
    // for
    // ixRegulatoryJurisdiction
    {
        this.ixReportingJurisdiction = ixRegulatoryJurisdiction;
    }


    public int getPrimaryKey() // getter method for primary key
    {
        return ixReportingJurisdiction;
    }


    public void setPrimaryKey(int ixReportingJurisdiction) // setter method for
    // primary key
    {
        this.ixReportingJurisdiction = ixReportingJurisdiction;
    }


    public int getIxProductMaster() // getter method for ixProductMaster
    {
        return ixProductMaster;
    }


    public void setIxProductMaster(int ixProductMaster) // setter method for
    // ixProductMaster
    {
        this.ixProductMaster = ixProductMaster;
    }


    public int getIdRegulator() // getter method for ixRegulator
    {
        return idRegulator;
    }


    public void setIdRegulator(int idRegulator) // setter method for
    // ixRegulator
    {
        this.idRegulator = idRegulator;
    }


    public long getReportingDate() // getter method for reportingDate
    {
        return reportingDate;
    }


    public void setReportingDate(long reportingDate) // setter method for reportingDate
    {
        this.reportingDate = reportingDate;
    }


    public String getReportingSDR() // getter method for reportingSDR
    {
        return reportingSDR;
    }


    public void setReportingSDR(String reportingSDR) // setter method for reportingSDR
    {
        this.reportingSDR = reportingSDR;
    }


    @Override
    public String toString() {
        return "[ixRegulatoryJurisdiction=" + this.ixReportingJurisdiction + ", ixProductMaster="
                + this.ixProductMaster + ", idRegulator=" + this.idRegulator + ", reportingSDR="
                + this.reportingSDR + ", reportingDate=" + this.reportingDate + "]";
    }


    @Override
    public boolean equals(Object arg0) {
        if (this == arg0) {
            return true;
        } else if (arg0 instanceof ReportingJurisdictionDetails) {
            return toString().equals(((ReportingJurisdictionDetails) arg0).toString());
        }
        return false;
    }


    @Override
    public int hashCode() {
        String valueString = this.ixReportingJurisdiction + " | " + this.ixProductMaster + " | "
                + this.idRegulator + " | " + this.reportingSDR + " | " + this.reportingDate;
        return valueString.hashCode();
    }


    @Override
    public Object clone() {
        ReportingJurisdictionDetails _regulatoryJurisdictionDetails = new ReportingJurisdictionDetails();
        _regulatoryJurisdictionDetails.setIxReportingJurisdiction(this.getIxReportingJurisdiction());
        _regulatoryJurisdictionDetails.setIdRegulator(this.getIdRegulator());
        _regulatoryJurisdictionDetails.setIxProductMaster(this.getIxProductMaster());
        _regulatoryJurisdictionDetails.setReportingSDR(this.getReportingSDR());
        _regulatoryJurisdictionDetails.setReportingDate(this.getReportingDate());
        return _regulatoryJurisdictionDetails;
    }
}