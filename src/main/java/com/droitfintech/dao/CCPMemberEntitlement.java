package com.droitfintech.dao;
import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import com.droitfintech.model.DeepCopiable;
import com.droitfintech.model.DeepCopyIgnore;
import com.droitfintech.model.DeepCopyIgnore.Type;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table (name="CCPMemberEntitlement")
public class CCPMemberEntitlement implements Serializable, DeepCopiable<CCPMemberEntitlement> {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.TABLE, generator="TDSS_GEN")
    @Column (name="idCCPMemberEntitlement")
    @DeepCopyIgnore(type=Type.NULL)
    private int idCCPMemberEntitlement;

    @ManyToOne
    @JoinColumn(name="idCCPMembershipVersion")
    @DeepCopyIgnore(type=Type.NULL)
    private CCPMembershipVersion ccpMembershipVersion;

    @ManyToOne
    @JoinColumn(name="idMemberMaster")
    @DeepCopyIgnore(type=Type.REFERENCE)
    private MemberMaster memberMaster;

    @Column (name="assetClass")
    private String assetClass;

    @Column (name="clearingMethod")
    private String clearingMethod;

    public CCPMemberEntitlement() {
        ;
    }

    public CCPMemberEntitlement(CCPMembershipVersion ccpMembershipVersion) {
        this.ccpMembershipVersion = ccpMembershipVersion;
        ccpMembershipVersion.addCcpMemberEntitlement(this);
    }

    public int getIdCCPMemberEntitlement() {
        return idCCPMemberEntitlement;
    }

    public void setIdCCPMemberEntitlement(int idCCPMemberEntitlement) {
        this.idCCPMemberEntitlement = idCCPMemberEntitlement;
    }

    @JsonIgnore
    public CCPMembershipVersion getCcpMembershipVersion() {
        return ccpMembershipVersion;
    }

    public void setCcpMembershipVersion(CCPMembershipVersion ccpMembershipVersion) {
        this.ccpMembershipVersion = ccpMembershipVersion;
    }

    public MemberMaster getMemberMaster() {
        return memberMaster;
    }

    public void setMemberMaster(MemberMaster memberMaster) {
        this.memberMaster = memberMaster;
    }

    public String getAssetClass() {
        return assetClass;
    }

    public void setAssetClass(String assetClass) {
        this.assetClass = assetClass;
    }

    public String getClearingMethod() {
        return clearingMethod;
    }

    public void setClearingMethod(String clearingMethod) {
        this.clearingMethod = clearingMethod;
    }
}
