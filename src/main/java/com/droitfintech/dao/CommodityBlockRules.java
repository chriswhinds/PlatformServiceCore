package com.droitfintech.dao;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

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
@Table(name = "CommodityBlockRules")
public class CommodityBlockRules implements Serializable
{

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "TDSS_GEN")
    @Column (name = "idCommodityBlockRule")
    private int idCommodityBlockRule;
    @Column (name = "assetClass")
    private String assetClass;
    @Column (name = "relatedFuturesContract")
    private String relatedFuturesContract;
    @Column (name = "exchanges")
    private String exchanges;
    @Column (name = "units")
    private String units;
    @Column (name = "commodityIndex")
    private boolean commodityIndex;
    @Column (name = "blockSize")
    private BigDecimal blockSize;
    @Column (name = "reportingCap")
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


    public int getIdCommodityBlockRule()
    {
        return idCommodityBlockRule;
    }


    public void setIdCommodityBlockRule(int idCommodityBlockRule)
    {
        this.idCommodityBlockRule = idCommodityBlockRule;
    }


    public String getRelatedFuturesContract()
    {
        return relatedFuturesContract;
    }


    public void setRelatedFuturesContract(String relatedFuturesContract)
    {
        this.relatedFuturesContract = relatedFuturesContract;
    }


    public String getExchanges()
    {
        return exchanges;
    }


    public void setExchanges(String exchanges)
    {
        this.exchanges = exchanges;
    }


    public String getUnits()
    {
        return units;
    }


    public void setUnits(String units)
    {
        this.units = units;
    }


    public boolean isCommodityIndex()
    {
        return commodityIndex;
    }


    public void setCommodityIndex(boolean commodityIndex)
    {
        this.commodityIndex = commodityIndex;
    }
}
