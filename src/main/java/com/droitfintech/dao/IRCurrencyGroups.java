package com.droitfintech.dao;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


@Entity
@Table(name="IRCurrencyGroups")
public class IRCurrencyGroups implements Serializable
{

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator="TDSS_GEN")
    @Column(name="idIRCurrencyGroups")
    private int idIRCurrencyGroups;

    @Column(name="currencyGroupName")
    private String currencyGroupName;

    @Column(name="currencyGroupValues")
    private String currencyGroupValues;

    @ManyToOne
    @JoinColumn(name = "idSEFBlockSizePhase")
    private SEFBlockSizePhase sefBlockSizePhase;


    public int getIdIRCurrencyGroups()
    {
        return idIRCurrencyGroups;
    }


    public void setIdIRCurrencyGroups(int idIRCurrencyGroups)
    {
        this.idIRCurrencyGroups = idIRCurrencyGroups;
    }


    public String getCurrencyGroupName()
    {
        return currencyGroupName;
    }


    public void setCurrencyGroupName(String currencyGroupName)
    {
        this.currencyGroupName = currencyGroupName;
    }


    public String getCurrencyGroupValues()
    {
        return currencyGroupValues;
    }


    public void setCurrencyGroupValues(String currencyGroupValues)
    {
        this.currencyGroupValues = currencyGroupValues;
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

