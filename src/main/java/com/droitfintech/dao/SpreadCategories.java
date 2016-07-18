package com.droitfintech.dao;

import javax.persistence.*;
import java.io.Serializable;

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
@Table(name = "SpreadCategories")
public class SpreadCategories implements Serializable
{

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator="TDSS_GEN")
    @Column (name = "idSpreadCategories")
    private int idSpreadCategories;

    @Column (name = "spreadCategoryName")
    private String spreadCategoryName;

    @Column (name = "spreadGreaterThan")
    private Integer spreadGreaterThan;

    @Column (name = "spreadLessThanOrEqualTo")
    private Integer spreadLessThanOrEqualTo;

    @ManyToOne
    @JoinColumn(name = "idSEFBlockSizePhase")
    private SEFBlockSizePhase sefBlockSizePhase;


    public int getIdSpreadCategories()
    {
        return idSpreadCategories;
    }


    public void setIdSpreadCategories(int idSpreadCategories)
    {
        this.idSpreadCategories = idSpreadCategories;
    }


    public String getSpreadCategoryName()
    {
        return spreadCategoryName;
    }


    public void setSpreadCategoryName(String spreadCategoryName)
    {
        this.spreadCategoryName = spreadCategoryName;
    }


    public Integer getSpreadGreaterThan()
    {
        return spreadGreaterThan;
    }


    public void setSpreadGreaterThan(Integer spreadGreaterThan)
    {
        this.spreadGreaterThan = spreadGreaterThan;
    }


    public Integer getSpreadLessThanOrEqualTo()
    {
        return spreadLessThanOrEqualTo;
    }


    public void setSpreadLessThanOrEqualTo(Integer spreadLessThanOrEqualTo)
    {
        this.spreadLessThanOrEqualTo = spreadLessThanOrEqualTo;
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
