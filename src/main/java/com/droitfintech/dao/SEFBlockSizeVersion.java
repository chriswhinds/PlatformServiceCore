package com.droitfintech.dao;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.droitfintech.model.JsonDateSerializer;
import com.droitfintech.model.RelationshipRemover;
import com.droitfintech.model.RelationshipSetter;


@Entity
@Table (name = "SEFBlockSizeVersion")
public class SEFBlockSizeVersion implements Serializable
{

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator="TDSS_GEN")
    @Column (name = "idSEFBlockSizeVersion")
    private int idSEFBlockSizeVersion;

    @Temporal(TemporalType.TIMESTAMP)
    @Column (name = "versionDate")
    private Date versionDate;

    @Column (name = "versionNumber")
    private BigDecimal versionNumber;

    @Column (name = "uuid")
    private String uuid;

    @Column (name = "legislation")
    private String legislation;

    @ManyToOne
    @JoinColumn(name = "idRegulator")
    private Regulator regulator;

    @OneToMany(mappedBy = "sefBlockSizeVersion")
    private Set<SEFBlockSizePhase> sefBlockSizePhases;


    public void setIdSEFBlockSizeVersion(int idSEFBlockSizeVersion)
    {
        this.idSEFBlockSizeVersion = idSEFBlockSizeVersion;
    }


    public int getIdSEFBlockSizeVersion()
    {
        return idSEFBlockSizeVersion;
    }

    @JsonSerialize(using=JsonDateSerializer.class)
    public Date getVersionDate()
    {
        return versionDate;
    }


    public void setVersionDate(Date versionDate)
    {
        this.versionDate = versionDate;
    }


    public BigDecimal getVersionNumber()
    {
        return versionNumber;
    }


    public void setVersionNumber(BigDecimal versionNumber)
    {
        this.versionNumber = versionNumber;
    }


    public String getUuid()
    {
        return uuid;
    }


    public void setUuid(String uuid)
    {
        this.uuid = uuid;
    }


    public String getLegislation()
    {
        return legislation;
    }


    public void setLegislation(String legislation)
    {
        this.legislation = legislation;
    }


    public void setRegulator(Regulator regulator)
    {
        this.regulator = regulator;
    }

    @JsonIgnore
    public Regulator getRegulator()
    {
        return regulator;
    }


    public void setSefBlockSizePhases(Set<SEFBlockSizePhase> sefBlockSizePhases)
    {
        this.sefBlockSizePhases = sefBlockSizePhases;
    }

    @RelationshipSetter(paramClass=SEFBlockSizePhase.class)
    public SEFBlockSizePhase addSefBlockSizePhase(SEFBlockSizePhase phase) {
        getSefBlockSizePhases().add(phase);
        phase.setSefBlockSizeVersion(this);

        return phase;
    }

    @RelationshipRemover(paramClass=SEFBlockSizePhase.class)
    public SEFBlockSizePhase removeSefBlockSizePhase(SEFBlockSizePhase phase) {
        getSefBlockSizePhases().remove(phase);
        phase.setSefBlockSizeVersion(null);

        return phase;
    }

    @JsonIgnore
    public Set<SEFBlockSizePhase> getSefBlockSizePhases()
    {
        if (this.sefBlockSizePhases == null) {
            this.sefBlockSizePhases = new HashSet<SEFBlockSizePhase>();
        }
        return sefBlockSizePhases;
    }
}
