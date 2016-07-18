package com.droitfintech.dao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="EMIRClearingCredit")
public class EMIRClearingCredit {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.TABLE, generator="TDSS_GEN")
    @Column (name="idEMIRClearingCredit")
    private int idEMIRClearingCredit;

    @Column
    private String entryIntoForce;

    @Column
    private String assetClass;

    @Column
    private String baseProduct;

    @Column
    private String subProduct;

    @Column
    private String currency;

    @Column
    private String transactionType;

    @Column
    private String family;

    @Column
    private String minSeries;

    @Column
    private String term;

    @Column
    private String category1Offset;

    @Column
    private String category1Obligation;

    @Column
    private String category2Offset;

    @Column
    private String category2Obligation;

    @Column
    private String category3Offset;

    @Column
    private String category3Obligation;

    @Column
    private String category4Offset;

    @Column
    private String category4Obligation;

    public int getIdEMIRClearingCredit() {
        return idEMIRClearingCredit;
    }

    public void setIdEMIRClearingCredit(int idEMIRClearingCredit) {
        this.idEMIRClearingCredit = idEMIRClearingCredit;
    }

    public String getEntryIntoForce() {
        return entryIntoForce;
    }

    public void setEntryIntoForce(String entryIntoForce) {
        this.entryIntoForce = entryIntoForce;
    }

    public String getAssetClass() {
        return assetClass;
    }

    public void setAssetClass(String assetClass) {
        this.assetClass = assetClass;
    }

    public String getBaseProduct() {
        return baseProduct;
    }

    public void setBaseProduct(String baseProduct) {
        this.baseProduct = baseProduct;
    }

    public String getSubProduct() {
        return subProduct;
    }

    public void setSubProduct(String subProduct) {
        this.subProduct = subProduct;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public String getMinSeries() {
        return minSeries;
    }

    public void setMinSeries(String minSeries) {
        this.minSeries = minSeries;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getCategory1Offset() {
        return category1Offset;
    }

    public void setCategory1Offset(String category1Offset) {
        this.category1Offset = category1Offset;
    }

    public String getCategory1Obligation() {
        return category1Obligation;
    }

    public void setCategory1Obligation(String category1Obligation) {
        this.category1Obligation = category1Obligation;
    }

    public String getCategory2Offset() {
        return category2Offset;
    }

    public void setCategory2Offset(String category2Offset) {
        this.category2Offset = category2Offset;
    }

    public String getCategory2Obligation() {
        return category2Obligation;
    }

    public void setCategory2Obligation(String category2Obligation) {
        this.category2Obligation = category2Obligation;
    }

    public String getCategory3Offset() {
        return category3Offset;
    }

    public void setCategory3Offset(String category3Offset) {
        this.category3Offset = category3Offset;
    }

    public String getCategory3Obligation() {
        return category3Obligation;
    }

    public void setCategory3Obligation(String category3Obligation) {
        this.category3Obligation = category3Obligation;
    }

    public String getCategory4Offset() {
        return category4Offset;
    }

    public void setCategory4Offset(String category4Offset) {
        this.category4Offset = category4Offset;
    }

    public String getCategory4Obligation() {
        return category4Obligation;
    }

    public void setCategory4Obligation(String category4Obligation) {
        this.category4Obligation = category4Obligation;
    }

}
