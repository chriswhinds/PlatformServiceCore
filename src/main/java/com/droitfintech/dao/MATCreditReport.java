package com.droitfintech.dao;

import javax.persistence.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="MATCreditReport")
public class MATCreditReport {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy= GenerationType.TABLE, generator="TDSS_GEN")
    @Column (name="idMATCreditReport")
    private int idMATCreditReport;

    @Column
    private String creditIndex;

    @Column
    private String currency;

    @Column
    private String series;

    @Column
    private String redid;

    @Column
    private String term;

    @Column
    private String indexEffectiveDate;

    @Column
    private String maturityDate;

    @Column
    private String matEffectiveDate;

    public int getIdMATCreditReport() {
        return idMATCreditReport;
    }

    public void setIdMATCreditReport(int idMATCreditReport) {
        this.idMATCreditReport = idMATCreditReport;
    }

    public String getCreditIndex() {
        return creditIndex;
    }

    public void setCreditIndex(String creditIndex) {
        this.creditIndex = creditIndex;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = series;
    }

    public String getRedid() {
        return redid;
    }

    public void setRedid(String redid) {
        this.redid = redid;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getIndexEffectiveDate() {
        return indexEffectiveDate;
    }

    public void setIndexEffectiveDate(String indexEffectiveDate) {
        this.indexEffectiveDate = indexEffectiveDate;
    }

    public String getMaturityDate() {
        return maturityDate;
    }

    public void setMaturityDate(String maturityDate) {
        this.maturityDate = maturityDate;
    }

    public String getMatEffectiveDate() {
        return matEffectiveDate;
    }

    public void setMatEffectiveDate(String matEffectiveDate) {
        this.matEffectiveDate = matEffectiveDate;
    }

}
