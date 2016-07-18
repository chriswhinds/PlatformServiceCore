package com.droitfintech.dao;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

import com.droitfintech.model.FinMktInfra;
import com.droitfintech.model.RelationshipRemover;
import com.droitfintech.model.RelationshipSetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * The persistent class for the CentralCounterparty database table.
 *
 */

@Entity
@Table (name="CentralCounterparty")
@DiscriminatorValue("CCP")
@PrimaryKeyJoinColumn(name = "idCentralCounterparty")

public class CentralCounterparty extends FinMktInfra implements Serializable {
    private static final long serialVersionUID = 1L;

    //bi-directional many-to-one association to CCPMembershipVersion
    @OneToMany(mappedBy = "centralCounterparty", fetch = FetchType.EAGER)
    @OrderBy("idCCPMembershipVersion")
    private Set<CCPMembershipVersion> ccpMembershipVersions;

    @JsonIgnore
    public Set<CCPMembershipVersion> getCcpMembershipVersions() {
        if (this.ccpMembershipVersions == null) {
            this.ccpMembershipVersions = new HashSet<CCPMembershipVersion>();
        }
        return ccpMembershipVersions;
    }

    public void setCcpMembershipVersions(
            Set<CCPMembershipVersion> ccpMembershipVersions) {
        this.ccpMembershipVersions = ccpMembershipVersions;
    }

    @RelationshipSetter(paramClass = CCPMembershipVersion.class)
    public CCPMembershipVersion addCcpMembershipVersion(CCPMembershipVersion version) {
        getCcpMembershipVersions().add(version);
        version.setCentralCounterparty(this);
        return version;
    }

    @RelationshipRemover(paramClass = CCPMembershipVersion.class)
    public CCPMembershipVersion removeCcpMembershipVersion(CCPMembershipVersion version) {
        getCcpMembershipVersions().remove(version);
        version.setCentralCounterparty(null);
        return version;
    }

    public CentralCounterparty() {
        this.setFinMktInfraType("CCP");
    }

    public CentralCounterparty(String shortName, String longName, String country, String region) {
        super("CCP", region, country, longName, shortName);
    }

    @Override
    public String toString() {
        return "CCP " + this.getShortName();
    }
}

