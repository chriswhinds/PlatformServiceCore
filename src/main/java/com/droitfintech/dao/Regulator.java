package com.droitfintech.dao;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import com.droitfintech.model.FinMktInfra;
import com.droitfintech.model.FinMktInfraVersion;
import com.fasterxml.jackson.annotation.JsonIgnore;

import com.droitfintech.model.FinMktInfraVersion.FinMktInfraVersionType;


@Entity
@Table (name = "Regulator")
@DiscriminatorValue("Regulator")
@PrimaryKeyJoinColumn(name = "idRegulator")
public class Regulator extends FinMktInfra implements Serializable
{

    private static final long serialVersionUID = 1L;

    @Column(name ="ruleMaking")
    private String ruleMaking;

    @Column (name = "jurisdiction")
    private String jurisdiction;

    @Column (name = "legislation")
    private String legislation;

    @Override
    public String toString() {
        return "Regulator " + this.getShortName();
    }

    public Regulator() {
        this.setFinMktInfraType("Regulator");
    }

    public String getRuleMaking()
    {
        return ruleMaking;
    }


    public void setRuleMaking(String ruleMaking)
    {
        this.ruleMaking = ruleMaking;
    }


    public String getJurisdiction()
    {
        return jurisdiction;
    }


    public void setJurisdiction(String jurisdiction)
    {
        this.jurisdiction = jurisdiction;
    }


    public String getLegislation()
    {
        return legislation;
    }


    public void setLegislation(String legislation)
    {
        this.legislation = legislation;
    }

    @JsonIgnore
    public int getIdRegulator() {
        return this.getIdFinMktInfra();
    }

    public void setIdRegulator(int id) {
        this.setIdFinMktInfra(id);
    }

    @JsonIgnore
    public String getRegulatorName() {
        return this.getShortName();
    }

    public void setRegulatorName(String name) {
        this.setShortName(name);
    }

    @JsonIgnore
    public Set<FinMktInfraVersion> getMatVersions() {
        Set<FinMktInfraVersion> res = new LinkedHashSet<FinMktInfraVersion>();
        for (FinMktInfraVersion v: this.getFinMktInfraVersions()) {
            if (FinMktInfraVersionType.MAT.name().equals(v.getVersionType())) {
                res.add(v);
            }
        }
        return res;
    }

}
