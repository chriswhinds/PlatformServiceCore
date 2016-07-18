package com.droitfintech.dao;

import javax.persistence.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="MATRatesReport")
public class MATRatesReport {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy= GenerationType.TABLE, generator="TDSS_GEN")
    @Column (name="idMATRatesReport")
    private int idMATRatesReport;

    @Column
    private String currency;

    @Column
    private String floatIndex;

    @Column
    private String effectiveDate;

    @Column
    private String variant;

    @Column
    private String standardMaturities;

    @Column
    private String matDate;

    @Column
    private String indexTenor;

    @Column
    private String floatPayFrequency;

    @Column
    private String fixedPayFrequency;

    @Column
    private String fixedDCF;

    @Column
    private String bdc;

    @Column
    private String holidayCalendars;

    @Column
    private String rollConvention;

    public int getIdMATRatesReport() {
        return idMATRatesReport;
    }

    public void setIdMATRatesReport(int idMATRatesReport) {
        this.idMATRatesReport = idMATRatesReport;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getFloatIndex() {
        return floatIndex;
    }

    public void setFloatIndex(String floatIndex) {
        this.floatIndex = floatIndex;
    }

    public String getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(String effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public String getVariant() {
        return variant;
    }

    public void setVariant(String variant) {
        this.variant = variant;
    }

    public String getStandardMaturities() {
        return standardMaturities;
    }

    public void setStandardMaturities(String standardMaturities) {
        this.standardMaturities = standardMaturities;
    }

    public String getMatDate() {
        return matDate;
    }

    public void setMatDate(String matDate) {
        this.matDate = matDate;
    }

    public String getIndexTenor() {
        return indexTenor;
    }

    public void setIndexTenor(String indexTenor) {
        this.indexTenor = indexTenor;
    }

    public String getFloatPayFrequency() {
        return floatPayFrequency;
    }

    public void setFloatPayFrequency(String floatPayFrequency) {
        this.floatPayFrequency = floatPayFrequency;
    }

    public String getFixedPayFrequency() {
        return fixedPayFrequency;
    }

    public void setFixedPayFrequency(String fixedPayFrequency) {
        this.fixedPayFrequency = fixedPayFrequency;
    }

    public String getFixedDCF() {
        return fixedDCF;
    }

    public void setFixedDCF(String fixedDCF) {
        this.fixedDCF = fixedDCF;
    }

    public String getBdc() {
        return bdc;
    }

    public void setBdc(String bdc) {
        this.bdc = bdc;
    }

    public String getHolidayCalendars() {
        return holidayCalendars;
    }

    public void setHolidayCalendars(String holidayCalendars) {
        this.holidayCalendars = holidayCalendars;
    }

    public String getRollConvention() {
        return rollConvention;
    }

    public void setRollConvention(String rollConvention) {
        this.rollConvention = rollConvention;
    }

}
