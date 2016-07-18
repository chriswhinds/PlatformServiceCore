package com.droitfintech.model;

/**
 * Created by christopherwhinds on 7/8/16.
 */

import java.io.Serializable;

import javax.persistence.*;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.droitfintech.model.RelationshipRemover;
import com.droitfintech.model.RelationshipSetter;

import java.util.Set;


/**
 * The persistent class for the CentralCounterparty database table.
 *
 */

@Entity
@Table (name="FinMktInfra")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "finMktInfraType", discriminatorType = DiscriminatorType.STRING)
public abstract class FinMktInfra implements Serializable
{
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.TABLE, generator="TDSS_GEN")
    @Column (name="idFinMktInfra")
    private int idFinMktInfra;

    @Column (name="finMktInfraType")
    private String finMktInfraType;

    @Column (name="country")
    private String country;

    @Column (name="longName")
    private String longName;

    @Column (name="region")
    private String region;

    @Column (name="shortName")
    private String shortName;

    //bi-directional many-to-one association to FinMktInfraVersion
    @OneToMany(mappedBy="finMktInfra", fetch=FetchType.EAGER)
    @OrderBy("idFinMktInfraVersion")
    private Set<FinMktInfraVersion> finMktInfraVersions;

    public FinMktInfra()
    {
    }


    public FinMktInfra(String type, String region, String country, String longName, String shortName) {
        this.finMktInfraType = type;
        this.region = region;
        this.country = country;
        this.shortName = shortName;
        this.longName = longName;
    }

    public int getIdFinMktInfra() {
        return this.idFinMktInfra;
    }

    public void setIdFinMktInfra(int idFinMktInfra)
    {
        this.idFinMktInfra = idFinMktInfra;
    }

    @JsonIgnore
    public String getFinMktInfraType() {
        return finMktInfraType;
    }

    public void setFinMktInfraType(String finMktInfraType){
        this.finMktInfraType = finMktInfraType;
    }

    public String getCountry() {
        return this.country;
    }

    public void setCountry(String country) {
        this.country = country;
    }


    public String getLongName()
    {
        return this.longName;
    }


    public void setLongName(String longName)
    {
        this.longName = longName;
    }


    public String getRegion()
    {
        return this.region;
    }


    public void setRegion(String region)
    {
        this.region = region;
    }


    public String getShortName()
    {
        return this.shortName;
    }


    public void setShortName(String shortName)
    {
        this.shortName = shortName;
    }

    @JsonIgnore
    public Set<FinMktInfraVersion> getFinMktInfraVersions() {
        return this.finMktInfraVersions;
    }

    @RelationshipSetter(paramClass=FinMktInfraVersion.class)
    public FinMktInfraVersion addFinMktInfraVersion(FinMktInfraVersion finMktInfraVersion) {
        getFinMktInfraVersions().add(finMktInfraVersion);
        finMktInfraVersion.setFinMktInfra(this);

        return finMktInfraVersion;
    }

    @RelationshipRemover(paramClass=FinMktInfraVersion.class)
    public FinMktInfraVersion removeFinMktInfraVersion(FinMktInfraVersion finMktInfraVersion) {
        getFinMktInfraVersions().remove(finMktInfraVersion);
        finMktInfraVersion.setFinMktInfra(null);

        return finMktInfraVersion;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) { return false; }
        if (obj == this) { return true; }
        if (obj.getClass() != getClass()) {
            return false;
        }
        FinMktInfra rhs = (FinMktInfra) obj;
        return new EqualsBuilder().
                append(this.shortName, rhs.getShortName()).
                isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).
                append(this.shortName).
                toHashCode();
    }

    @Override
    public String toString() {
        return "FinanceMarketInfrastructure " + this.shortName;
    }
}