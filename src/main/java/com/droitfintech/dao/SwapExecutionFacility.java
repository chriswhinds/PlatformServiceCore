package com.droitfintech.dao;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.*;

import com.droitfintech.model.FinMktInfra;
import com.droitfintech.model.FinMktInfraVersion;
import com.fasterxml.jackson.annotation.JsonIgnore;

import com.droitfintech.model.FinMktInfraVersion.FinMktInfraVersionType;


@Entity
@Table (name="SwapExecutionFacility")
@DiscriminatorValue("SEF")
@PrimaryKeyJoinColumn(name = "idSwapExecutionFacility")

public class SwapExecutionFacility extends FinMktInfra implements Serializable{

    private static final long serialVersionUID = 1L;

    public SwapExecutionFacility() {
        this.setFinMktInfraType("SEF");
    }

    public SwapExecutionFacility(String shortName, String longName, String country, String region) {
        super("SEF", region, country, longName, shortName);
    }

    @Override
    public String toString() {
        return "SEF " + this.getShortName();
    }

    @JsonIgnore
    public Set<FinMktInfraVersion> getListedProductsVersions() {
        Set<FinMktInfraVersion> res = new LinkedHashSet<FinMktInfraVersion>();
        for (FinMktInfraVersion v: this.getFinMktInfraVersions()) {
            if (FinMktInfraVersionType.ListedProduct.name().equals(v.getVersionType())) {
                res.add(v);
            }
        }
        return res;
    }

}
