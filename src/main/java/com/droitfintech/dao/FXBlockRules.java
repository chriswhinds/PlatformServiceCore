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
@Table(name="FXBlockRules")
@JsonIgnoreProperties({"sefBlockSizePhase"})
public class FXBlockRules implements Serializable
{

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator="TDSS_GEN")
    @Column(name="idFXBlockRules")
    private int idFXBlockRules;

    @Column(name="assetClass")
    private String assetClass;

    @Column(name="currency1")
    private String currency1;

    @Column(name="currency2")
    private String currency2;

    @Column(name="blockSize")
    private BigDecimal blockSize;

    @Column(name="reportingCap")
    private BigDecimal reportingCap;

    @ManyToOne
    @JoinColumn(name = "idSEFBlockSizePhase")
    private SEFBlockSizePhase sefBlockSizePhase;


    public String getAssetClass()
    {
        return assetClass;
    }


    public void setAssetClass(String assetClass)
    {
        this.assetClass = assetClass;
    }


    public BigDecimal getBlockSize()
    {
        return blockSize;
    }


    public void setBlockSize(BigDecimal blockSize)
    {
        this.blockSize = blockSize;
    }


    public BigDecimal getReportingCap()
    {
        return reportingCap;
    }


    public void setReportingCap(BigDecimal reportingCap)
    {
        this.reportingCap = reportingCap;
    }


    public SEFBlockSizePhase getSefBlockSizePhase()
    {
        return sefBlockSizePhase;
    }

    public void setSefBlockSizePhase(SEFBlockSizePhase sefBlockSizePhase)
    {
        this.sefBlockSizePhase = sefBlockSizePhase;
    }


    public int getIdFXBlockRules()
    {
        return idFXBlockRules;
    }


    public void setIdFXBlockRules(int idFXBlockRules)
    {
        this.idFXBlockRules = idFXBlockRules;
    }


    public String getCurrency1()
    {
        return currency1;
    }


    public void setCurrency1(String currency1)
    {
        this.currency1 = currency1;
    }


    public String getCurrency2()
    {
        return currency2;
    }


    public void setCurrency2(String currency2)
    {
        this.currency2 = currency2;
    }
}
