package com.droitfintech.dao;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table (name = "CreditBlockRules")
@JsonIgnoreProperties({"sefBlockSizePhase"})
public class CreditBlockRules implements Serializable
{

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator="TDSS_GEN")
    @Column (name = "idCreditBlockRules")
    private int idCreditBlockRules;
    @Column (name = "assetClass")
    private String assetClass;
    @Column (name = "tenorGreaterThan")
    private String tenorGreaterThan;
    @Column (name = "tenorLessThanOrEqualTo")
    private String tenorLessThanOrEqualTo;
    @Column (name = "low")
    private BigDecimal low;
    @Column (name = "medium")
    private BigDecimal medium;
    @Column (name = "high")
    private BigDecimal high;
    @Column (name = "interimReportingCap")
    private BigDecimal interimReportingCap;
    @Column (name = "lowReportingCap")
    private BigDecimal lowReportingCap;
    @Column (name = "mediumReportingCap")
    private BigDecimal mediumReportingCap;
    @Column (name = "highReportingCap")
    private BigDecimal highReportingCap;

    @ManyToOne
    @JoinColumn(name = "idSEFBlockSizePhase")
    private SEFBlockSizePhase sefBlockSizePhase;


    public int getIdCreditBlockRules()
    {
        return idCreditBlockRules;
    }


    public void setIdCreditBlockRules(int idCreditBlockRules)
    {
        this.idCreditBlockRules = idCreditBlockRules;
    }


    public String getAssetClass()
    {
        return assetClass;
    }


    public void setAssetClass(String assetClass)
    {
        this.assetClass = assetClass;
    }


    public String getTenorGreaterThan()
    {
        return tenorGreaterThan;
    }


    public void setTenorGreaterThan(String tenorGreaterThan)
    {
        this.tenorGreaterThan = tenorGreaterThan;
    }


    public String getTenorLessThanOrEqualTo()
    {
        return tenorLessThanOrEqualTo;
    }


    public void setTenorLessThanOrEqualTo(String tenorLessThanOrEqualTo)
    {
        this.tenorLessThanOrEqualTo = tenorLessThanOrEqualTo;
    }


    public BigDecimal getLow()
    {
        return low;
    }


    public void setLow(BigDecimal low)
    {
        this.low = low;
    }


    public BigDecimal getMedium()
    {
        return medium;
    }


    public void setMedium(BigDecimal medium)
    {
        this.medium = medium;
    }


    public BigDecimal getHigh()
    {
        return high;
    }


    public void setHigh(BigDecimal high)
    {
        this.high = high;
    }


    public BigDecimal getInterimReportingCap()
    {
        return interimReportingCap;
    }


    public void setInterimReportingCap(BigDecimal interimReportingCap)
    {
        this.interimReportingCap = interimReportingCap;
    }


    public BigDecimal getLowReportingCap()
    {
        return lowReportingCap;
    }


    public void setLowReportingCap(BigDecimal lowReportingCap)
    {
        this.lowReportingCap = lowReportingCap;
    }


    public BigDecimal getMediumReportingCap()
    {
        return mediumReportingCap;
    }


    public void setMediumReportingCap(BigDecimal mediumReportingCap)
    {
        this.mediumReportingCap = mediumReportingCap;
    }


    public BigDecimal getHighReportingCap()
    {
        return highReportingCap;
    }


    public void setHighReportingCap(BigDecimal highReportingCap)
    {
        this.highReportingCap = highReportingCap;
    }


    public SEFBlockSizePhase getSefBlockSizePhase()
    {
        return sefBlockSizePhase;
    }

    public void setSefBlockSizePhase(SEFBlockSizePhase sefBlockSizePhase)
    {
        this.sefBlockSizePhase = sefBlockSizePhase;
    }
}
