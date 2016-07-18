package com.droitfintech.dao;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

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

import com.droitfintech.model.JsonDateSerializer;
import com.droitfintech.model.RelationshipRemover;
import com.droitfintech.model.RelationshipSetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Entity
@Table (name="CCPMembershipVersion")
public class CCPMembershipVersion implements Serializable{

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.TABLE, generator="TDSS_GEN")
    @Column (name="idCCPMembershipVersion")
    private int idCCPMembershipVersion;

    @Column (name="versionDate")
    @Temporal(TemporalType.DATE)
    private Date versionDate;

    @Column (name="versionNumber")
    private BigDecimal versionNumber;

    @Column (name="uuid")
    private String uuid;

    @ManyToOne
    @JoinColumn(name="idCentralCounterparty")
    private CentralCounterparty centralCounterparty;

    @OneToMany(mappedBy = "ccpMembershipVersion", cascade = CascadeType.ALL)
    @OrderBy("idCCPMemberEntitlement")
    private Set<CCPMemberEntitlement> ccpMemberEntitlements;

    public CCPMembershipVersion() {
    }

    public CCPMembershipVersion(CentralCounterparty ccp, Date versionDate, BigDecimal versionNumber) {
        this.centralCounterparty = ccp;
        this.versionDate = versionDate;
        this.versionNumber = (versionNumber != null) ? versionNumber : BigDecimal.valueOf(1L);
    }

    public int getIdCCPMembershipVersion() {
        return idCCPMembershipVersion;
    }

    public void setIdCCPMembershipVersion(int idCCPMembershipVersion) {
        this.idCCPMembershipVersion = idCCPMembershipVersion;
    }

    @JsonSerialize(using=JsonDateSerializer.class)
    public Date getVersionDate() {
        return versionDate;
    }

    public void setVersionDate(Date versionDate) {
        this.versionDate = versionDate;
    }

    public BigDecimal getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(BigDecimal versionNumber) {
        this.versionNumber = versionNumber;
    }

    @JsonIgnore
    public CentralCounterparty getCentralCounterparty() {
        return centralCounterparty;
    }

    public void setCentralCounterparty(CentralCounterparty centralCounterparty) {
        this.centralCounterparty = centralCounterparty;
    }

    /**
     * @return the ccpMemberEntitlements
     */
    @JsonIgnore
    public Set<CCPMemberEntitlement> getCcpMemberEntitlements()
    {
        if (this.ccpMemberEntitlements == null) {
            // We use a linkedhashset to provide some stable ordering when creating new versions
            this.ccpMemberEntitlements = new LinkedHashSet<CCPMemberEntitlement>();
        }
        return ccpMemberEntitlements;
    }


    /**
     * @param ccpMemberEntitlements the ccpMemberEntitlements to set
     */
    public void setCcpMemberEntitlements(Set<CCPMemberEntitlement> ccpMemberEntitlements)
    {
        this.ccpMemberEntitlements = ccpMemberEntitlements;
    }

    @RelationshipSetter(paramClass=CCPMemberEntitlement.class)
    public CCPMemberEntitlement addCcpMemberEntitlement(CCPMemberEntitlement ent) {
        getCcpMemberEntitlements().add(ent);
        ent.setCcpMembershipVersion(this);
        return ent;
    }

    @RelationshipRemover(paramClass=CCPMemberEntitlement.class)
    public CCPMemberEntitlement removeCcpMemberEntitlement(CCPMemberEntitlement ent) {
        getCcpMemberEntitlements().remove(ent);
        ent.setCcpMembershipVersion(null);
        return ent;
    }


    /**
     * @return the uuid
     */
    public String getUuid()
    {
        return uuid;
    }


    /**
     * @param uuid the uuid to set
     */
    public void setUuid(String uuid)
    {
        this.uuid = uuid;
    }

}
