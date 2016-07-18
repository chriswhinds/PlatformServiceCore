package com.droitfintech.dao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="PackageTradesRules")
public class PackageTradesRules {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.TABLE, generator="TDSS_GEN")
    @Column (name="idPackageTradeRule")
    private int idPackageTradeRule;

    @Column
    private String assetClass;

    @Column
    private String packageType;

    @Column
    private String packageLegType;

    @Column
    private String matLeg1;

    @Column
    private String matLeg2;

    @Column
    private String clearingMandated;

    @Column
    private String reliefDate;

    public int getIdPackageTradeRule() {
        return idPackageTradeRule;
    }

    public void setIdPackageTradeRule(int idPackageTradeRule) {
        this.idPackageTradeRule = idPackageTradeRule;
    }

    public String getAssetClass() {
        return assetClass;
    }

    public void setAssetClass(String assetClass) {
        this.assetClass = assetClass;
    }

    public String getPackageType() {
        return packageType;
    }

    public void setPackageType(String packageType) {
        this.packageType = packageType;
    }

    public String getPackageLegType() {
        return packageLegType;
    }

    public void setPackageLegType(String packageLegType) {
        this.packageLegType = packageLegType;
    }

    public String getMatLeg1() {
        return matLeg1;
    }

    public void setMatLeg1(String matLeg1) {
        this.matLeg1 = matLeg1;
    }

    public String getMatLeg2() {
        return matLeg2;
    }

    public void setMatLeg2(String matLeg2) {
        this.matLeg2 = matLeg2;
    }

    public String getClearingMandated() {
        return clearingMandated;
    }

    public void setClearingMandated(String clearingMandated) {
        this.clearingMandated = clearingMandated;
    }

    public String getReliefDate() {
        return reliefDate;
    }

    public void setReliefDate(String reliefDate) {
        this.reliefDate = reliefDate;
    }

}
