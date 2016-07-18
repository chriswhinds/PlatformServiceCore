package com.droitfintech.dao;

import java.io.Serializable;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.droitfintech.model.DeepCopiable;
import com.droitfintech.model.DeepCopyIgnore;
import com.droitfintech.model.DeepCopyIgnore.Type;


/**
 * The persistent class for the MACDefinitionKeyIdMap database table.
 *
 */
@Entity
public class MACDefinitionKeyIdMap implements Serializable, DeepCopiable<MACDefinitionKeyIdMap> {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "TDSS_GEN")
    @DeepCopyIgnore(type = Type.NULL)
    private int idMACDefinitionKeyIdMap;

    private String ccp;

    private String cusip;

    private String isin;

    //bi-directional many-to-one association to MACDefinitionKey
    @ManyToOne
    @JoinColumn(name = "idMACDefinitionKeys")
    @DeepCopyIgnore(type = Type.NULL)
    private MACDefinitionKeys macdefinitionKey;

    public MACDefinitionKeyIdMap() {
    }

    public MACDefinitionKeyIdMap(MACDefinitionKeys parent) {
        this.macdefinitionKey = parent;
        if (parent != null) {
            parent.addMacdefinitionKeyIdMap(this);
        }
    }

    public int getIdMACDefinitionKeyIdMap() {
        return this.idMACDefinitionKeyIdMap;
    }

    public void setIdMACDefinitionKeyIdMap(int idMACDefinitionKeyIdMap) {
        this.idMACDefinitionKeyIdMap = idMACDefinitionKeyIdMap;
    }

    public String getCcp() {
        return this.ccp;
    }

    public void setCcp(String ccp) {
        this.ccp = ccp;
    }

    public String getCusip() {
        return this.cusip;
    }

    public void setCusip(String cusip) {
        this.cusip = cusip;
    }

    public String getIsin() {
        return this.isin;
    }

    public void setIsin(String isin) {
        this.isin = isin;
    }

    @JsonIgnore
    public MACDefinitionKeys getMacdefinitionKey() {
        return this.macdefinitionKey;
    }

    public void setMacdefinitionKey(MACDefinitionKeys macdefinitionKey) {
        this.macdefinitionKey = macdefinitionKey;
    }
}

