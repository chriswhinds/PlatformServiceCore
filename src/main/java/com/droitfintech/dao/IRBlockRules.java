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
@Table(name="IRBlockRules")
@JsonIgnoreProperties({"sefBlockSizePhase"})
public class IRBlockRules implements Serializable
{

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator="TDSS_GEN")
    @Column(name="idIRBlockRules")
    private int idIRBlockRules;

    @Column(name="assetClass")
    private String assetClass;

    @Column(name="tenorGreaterThan")
    private String tenorGreaterThan;

    @Column(name="tenorLessThanOrEqualTo")
    private String tenorLessThanOrEqualTo;

    @Column(name="superMajor")
    private BigDecimal superMajor;

    @Column(name="major")
    private BigDecimal major;

    @Column(name="nonMajor")
    private BigDecimal nonMajor;

    @Column(name="interimReportingCap")
    private BigDecimal interimReportingCap;

    @Column(name="superMajorReportingCap")
    private BigDecimal superMajorReportingCap;

    @Column(name="majorReportingCap")
    private BigDecimal majorReportingCap;

    @Column(name="nonMajorReportingCap")
    private BigDecimal nonMajorReportingCap;

    @ManyToOne
    @JoinColumn(name = "idSEFBlockSizePhase")
    private SEFBlockSizePhase sefBlockSizePhase;


    public int getIdIRBlockRules()
    {
        return idIRBlockRules;
    }


    public void setIdIRBlockRules(int idIRBlockRules)
    {
        this.idIRBlockRules = idIRBlockRules;
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


    public BigDecimal getSuperMajor()
    {
        return superMajor;
    }


    public void setSuperMajor(BigDecimal superMajor)
    {
        this.superMajor = superMajor;
    }


    public BigDecimal getMajor()
    {
        return major;
    }


    public void setMajor(BigDecimal major)
    {
        this.major = major;
    }


    public BigDecimal getNonMajor()
    {
        return nonMajor;
    }


    public void setNonMajor(BigDecimal nonMajor)
    {
        this.nonMajor = nonMajor;
    }


    public BigDecimal getInterimReportingCap()
    {
        return interimReportingCap;
    }


    public void setInterimReportingCap(BigDecimal interimReportingCap)
    {
        this.interimReportingCap = interimReportingCap;
    }


    public BigDecimal getSuperMajorReportingCap()
    {
        return superMajorReportingCap;
    }


    public void setSuperMajorReportingCap(BigDecimal superMajorReportingCap)
    {
        this.superMajorReportingCap = superMajorReportingCap;
    }


    public BigDecimal getMajorReportingCap()
    {
        return majorReportingCap;
    }


    public void setMajorReportingCap(BigDecimal majorReportingCap)
    {
        this.majorReportingCap = majorReportingCap;
    }


    public BigDecimal getNonMajorReportingCap()
    {
        return nonMajorReportingCap;
    }


    public void setNonMajorReportingCap(BigDecimal nonMajorReportingCap)
    {
        this.nonMajorReportingCap = nonMajorReportingCap;
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
