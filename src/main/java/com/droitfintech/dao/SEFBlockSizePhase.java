package com.droitfintech.dao;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


@Entity
@Table (name = "SEFBlockSizePhase")
public class SEFBlockSizePhase implements Serializable
{

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator="TDSS_GEN")
    @Column (name = "idSEFBlockSizePhase")
    private int idSEFBlockSizePhase;

    @Temporal(TemporalType.DATE)
    @Column (name = "effectiveDate")
    private Date effectiveDate;

    @ManyToOne
    @JoinColumn(name = "idSEFBlockSizeVersion")
    private SEFBlockSizeVersion sefBlockSizeVersion;

    @OneToMany(mappedBy = "sefBlockSizePhase")
    private Set<EquityBlockRules> equityBlockRules;
    @OneToMany(mappedBy = "sefBlockSizePhase")
    private Set<CommodityBlockRules> commodityBlockRules;
    @OneToMany(mappedBy = "sefBlockSizePhase")
    private Set<FXBlockRules> fxBlockRules;
    @OneToMany(mappedBy = "sefBlockSizePhase")
    private Set<SpreadCategories> spreadCategories;
    @OneToMany(mappedBy = "sefBlockSizePhase")
    private Set<CreditBlockRules> creditBlockRules;
    @OneToMany(mappedBy = "sefBlockSizePhase")
    private Set<IRCurrencyGroups> irCurrencyGroups;
    @OneToMany(mappedBy = "sefBlockSizePhase")
    private Set<IRBlockRules> irBlockRules;


    public void setIdSEFBlockSizePhase(int idSEFBlockSizePhase)
    {
        this.idSEFBlockSizePhase = idSEFBlockSizePhase;
    }


    public int getIdSEFBlockSizePhase()
    {
        return idSEFBlockSizePhase;
    }


    public Date getEffectiveDate()
    {
        return effectiveDate;
    }


    public void setEffectiveDate(Date effectiveDate)
    {
        this.effectiveDate = effectiveDate;
    }


    public void setSefBlockSizeVersion(SEFBlockSizeVersion sefBlockSizeVersion)
    {
        this.sefBlockSizeVersion = sefBlockSizeVersion;
    }


    public SEFBlockSizeVersion getSefBlockSizeVersion()
    {
        return sefBlockSizeVersion;
    }


    public void setEquityBlockRules(Set<EquityBlockRules> equityBlockRules)
    {
        this.equityBlockRules = equityBlockRules;
    }


    public Set<EquityBlockRules> getEquityBlockRules()
    {
        return equityBlockRules;
    }


    public void setCommodityBlockRules(Set<CommodityBlockRules> commodityBlockRules)
    {
        this.commodityBlockRules = commodityBlockRules;
    }


    public Set<CommodityBlockRules> getCommodityBlockRules()
    {
        return commodityBlockRules;
    }


    public void setFxBlockRules(Set<FXBlockRules> fxBlockRules)
    {
        this.fxBlockRules = fxBlockRules;
    }


    public Set<FXBlockRules> getFxBlockRules()
    {
        return fxBlockRules;
    }


    public void setSpreadCategories(Set<SpreadCategories> spreadCategories)
    {
        this.spreadCategories = spreadCategories;
    }


    public Set<SpreadCategories> getSpreadCategories()
    {
        return spreadCategories;
    }


    public void setCreditBlockRules(Set<CreditBlockRules> creditBlockRules)
    {
        this.creditBlockRules = creditBlockRules;
    }


    public Set<CreditBlockRules> getCreditBlockRules()
    {
        return creditBlockRules;
    }


    public void setIrCurrencyGroups(Set<IRCurrencyGroups> irCurrencyGroups)
    {
        this.irCurrencyGroups = irCurrencyGroups;
    }


    public Set<IRCurrencyGroups> getIrCurrencyGroups()
    {
        return irCurrencyGroups;
    }


    public void setIrBlockRules(Set<IRBlockRules> irBlockRules)
    {
        this.irBlockRules = irBlockRules;
    }


    public Set<IRBlockRules> getIrBlockRules()
    {
        return irBlockRules;
    }
}
