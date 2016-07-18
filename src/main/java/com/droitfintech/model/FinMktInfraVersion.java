package com.droitfintech.model;


/**
 * Created by christopherwhinds on 7/8/16.
 */

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import com.droitfintech.model.JsonDateSerializer;
import com.droitfintech.model.RelationshipRemover;
import com.droitfintech.model.RelationshipSetter;


/**
 * The persistent class for the FinMktInfraVersion database table.
 *
 */

@Entity
@Table (name="FinMktInfraVersion")
public class FinMktInfraVersion implements Serializable {
    private static final long serialVersionUID = 1L;

    // Discriminator values for versionType
    // FKA FinMktInfraVersionType

    // TODO REVISIT

    public enum FinMktInfraVersionType {
        MAT,
        ListedProduct,
        CCPEligibility,
        MandClearing,
        SEFBlockRptCap,
        CCPMembership;
    }

    @Id
    @GeneratedValue(strategy=GenerationType.TABLE, generator="TDSS_GEN")
    @Column (name="idFinMktInfraVersion")
    private int idFinMktInfraVersion;

    @Column (name="uuid")
    private String uuid;

    @Column (name="versionDate")
    @Temporal(TemporalType.DATE)
    private Date versionDate;

    @Column (name="versionNumber")
    private BigDecimal versionNumber;

    @Column (name="versionType")
    private String versionType;

    @ManyToOne
    @JoinColumn(name="idFinMktInfra")
    private FinMktInfra finMktInfra;

    //bi-directional many-to-one association to SubProductRule
    @OneToMany(mappedBy="finMktInfraVersion", orphanRemoval=true, cascade = CascadeType.ALL)
    @OrderBy("idSubProductRules")
    private Set<SubProductRules> subProductRules;

    //bi-directional many-to-one association to CCPEligibilityMasterBase
    @OneToMany(mappedBy="finMktInfraVersion", orphanRemoval=true, cascade = CascadeType.ALL)
    @OrderBy("idMasterBase")
    private Set<MasterBase> masterBases;

    @OneToMany(mappedBy = "clearingMandateVersion")
    @OrderBy("idMandatoryClearingEnumerationMatrix")
    private Set<MandatoryClearingEnumerationMatrix> mandatoryClearingEnumerationMatrixSet;

    public FinMktInfraVersion() {
    }

    public FinMktInfraVersion(FinMktInfra finMktInfra, Date versionDate, BigDecimal versionNumber) {
        this.finMktInfra = finMktInfra;
        this.versionDate = versionDate;
        this.versionNumber = (versionNumber != null) ? versionNumber : BigDecimal.valueOf(1L);
    }

    public FinMktInfraVersion(FinMktInfraVersion template, Date versionDate) {
        this.finMktInfra = template.getFinMktInfra();
        this.versionDate = versionDate;
        this.versionNumber = (template.getVersionNumber() != null) ? template.getVersionNumber().add(BigDecimal.valueOf(1L)) : BigDecimal.valueOf(1L);
    }

    @JsonIgnore
    public Set<SubProductRules> getSubProductRules() {
        if (this.subProductRules == null) {
            this.subProductRules = new LinkedHashSet<SubProductRules>();
        }
        return this.subProductRules;
    }

    public void setSubProductRules(Set<SubProductRules> subProductRules) {
        this.subProductRules = subProductRules;
    }

    @RelationshipSetter(paramClass=SubProductRules.class)
    public SubProductRules addSubProductRule(SubProductRules subProductRules) {
        getSubProductRules().add(subProductRules);
        subProductRules.setFinMktInfraVersion(this);

        return subProductRules;
    }

    @RelationshipRemover(paramClass=SubProductRules.class)
    public SubProductRules removeSubProductRules(SubProductRules subProductRules) {
        getSubProductRules().remove(subProductRules);
        subProductRules.setFinMktInfraVersion(null);

        return subProductRules;
    }

    @JsonIgnore
    public Set<MasterBase> getMasterBases() {
        if (this.masterBases == null) {
            this.masterBases = new LinkedHashSet<MasterBase>();
        }
        return this.masterBases;
    }

    public void setMasterBase(Set<MasterBase> masterBases) {
        this.masterBases = masterBases;
    }

    @RelationshipSetter(paramClass=MasterBase.class)
    public MasterBase addMasterBase(MasterBase masterBase) {
        getMasterBases().add(masterBase);
        masterBase.setFinMktInfraVersion(this);

        return masterBase;
    }

    @RelationshipRemover(paramClass=MasterBase.class)
    public MasterBase removeMasterBase(MasterBase masterBase) {
        getMasterBases().remove(masterBase);
        masterBase.setFinMktInfraVersion(null);

        return masterBase;
    }

    @JsonIgnore
    public FinMktInfra getFinMktInfra() {
        return finMktInfra;
    }

    public void setFinMktInfra(FinMktInfra infra) {
        this.finMktInfra = infra;
    }

    public int getIdFinMktInfraVersion() {
        return this.idFinMktInfraVersion;
    }

    public void setIdFinMktInfraVersion(int idFinMktInfraVersion) {
        this.idFinMktInfraVersion = idFinMktInfraVersion;
    }

    public String getUuid() {
        return this.uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @JsonSerialize(using=JsonDateSerializer.class)
    public Date getVersionDate() {
        return this.versionDate;
    }

    public void setVersionDate(Date versionDate) {
        this.versionDate = versionDate;
    }

    public BigDecimal getVersionNumber() {
        return this.versionNumber;
    }

    public void setVersionNumber(BigDecimal versionNumber) {
        this.versionNumber = versionNumber;
    }

    public String getVersionType() {
        return versionType;
    }

    public void setVersionType(String versionType) {
        this.versionType = versionType;
    }

    /**
     * @return the mandatoryClearingEnumerationMatrixSet
     */
    @JsonIgnore
    public Set<MandatoryClearingEnumerationMatrix> getMandatoryClearingEnumerationMatrixSet()
    {
        return mandatoryClearingEnumerationMatrixSet;
    }


    /**
     * @param mandatoryClearingEnumerationMatrixSet
     *            the mandatoryClearingEnumerationMatrixSet to set
     */
    public void setMandatoryClearingEnumerationMatrixSet(
            Set<MandatoryClearingEnumerationMatrix> mandatoryClearingEnumerationMatrixSet)
    {
        this.mandatoryClearingEnumerationMatrixSet = mandatoryClearingEnumerationMatrixSet;
    }


    public MandatoryClearingEnumerationMatrix addMandatoryClearingEnumerationMatrix(
            MandatoryClearingEnumerationMatrix mandatoryClearingEnumerationMatrix)
    {
        getMandatoryClearingEnumerationMatrixSet().add(mandatoryClearingEnumerationMatrix);
        mandatoryClearingEnumerationMatrix.setClearingMandateVersion(this);
        return mandatoryClearingEnumerationMatrix;
    }


    public MandatoryClearingEnumerationMatrix removeMandatoryClearingEnumerationMatrix(
            MandatoryClearingEnumerationMatrix mandatoryClearingEnumerationMatrix)
    {
        getMandatoryClearingEnumerationMatrixSet().remove(mandatoryClearingEnumerationMatrix);
        mandatoryClearingEnumerationMatrix.setClearingMandateVersion(null);
        return mandatoryClearingEnumerationMatrix;
    }

    @Override
    public String toString() {
        return StringUtils.join(new Object[] {
                this.getFinMktInfra(),
                this.getVersionType(),
                this.getVersionDate()
        }, ' ');
    }

}

