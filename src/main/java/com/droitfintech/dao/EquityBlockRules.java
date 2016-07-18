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


@Entity
@Table (name = "EquityBlockRules")
public class EquityBlockRules implements Serializable
{

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator="TDSS_GEN")
    @Column (name = "idEquityBlockRule")
    private int idEquityBlockRule;
    @Column (name = "assetClass")
    private String assetClass;
    @Column (name = "blockSize")
    private BigDecimal blockSize;
    @Column (name = "reportingCap")
    private BigDecimal reportingCap;
    @ManyToOne
    @JoinColumn(name = "idSEFBlockSizePhase")
    private SEFBlockSizePhase sefBlockSizePhase;


    public int getIdEquityBlockRule()
    {
        return idEquityBlockRule;
    }


    public void setIdEquityBlockRule(int idEquityBlockRule)
    {
        this.idEquityBlockRule = idEquityBlockRule;
    }


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
}
