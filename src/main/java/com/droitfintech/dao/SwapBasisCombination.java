package com.droitfintech.dao;

import com.droitfintech.model.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;

import java.io.Serializable;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;



/**
 * The persistent class for the SwapBasisCombination database table.
 *
 */
@Entity
@Table(name = "SwapBasisCombination")
public class SwapBasisCombination implements Serializable, DeepCopiable<SwapBasisCombination> {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "TDSS_GEN")
    @Column(name = "idSwapBasisCombination")
    @DeepCopyIgnore(type = DeepCopyIgnore.Type.NULL)
    private int idSwapBasisCombination;

    @RuleKey(attributeName = "floatingleg1_floatingrateindex", attributeType = TradeAttribute.AttributeType.FloatLegIndex)
    @Column(name = "floatingIndex1")
    private String floatingIndex1;

    @RuleKey(attributeName = "floatingleg2_floatingrateindex", attributeType = TradeAttribute.AttributeType.FloatLegIndex)
    @Column(name = "floatingIndex2")
    private String floatingIndex2;

    @RuleKey(attributeName = "floatingleg1_indextenor", attributeType = TradeAttribute.AttributeType.TenorType)
    @Column(name = "floatingIndexTenor1")
    private String floatingIndexTenor1;

    @RuleKey(attributeName = "floatingleg2_indextenor", attributeType = TradeAttribute.AttributeType.TenorType)
    @Column(name = "floatingIndexTenor2")
    private String floatingIndexTenor2;

    @RuleTest(attributeName = "term", attributeType = TradeAttribute.AttributeType.TenorType, operator = Min.class)
    @Column(name = "legMinTerm")
    private String legMinTerm;

    @RuleTest(attributeName = "term", attributeType = TradeAttribute.AttributeType.TenorType, operator = Max.class)
    @Column(name = "legMaxTerm")
    private String legMaxTerm;

    @RuleTest(attributeName = "residualterm", attributeType = TradeAttribute.AttributeType.TenorType, operator = Min.class)
    @Column(name = "legMinResidualTerm")
    private String legMinResidualTerm;

    @RuleTest(attributeName = "residualterm", attributeType = TradeAttribute.AttributeType.TenorType, operator = Max.class)
    @Column(name = "legMaxResidualTerm")
    private String legMaxResidualTerm;

    // bi-directional many-to-one association to BasisCurrencyRule
    @ManyToOne
    @JoinColumn(name = "idBasisCurrencyRules")
    @DeepCopyIgnore(type = DeepCopyIgnore.Type.NULL)
    private BasisCurrencyRules basisCurrencyRules;

    public SwapBasisCombination() {
    }

    public SwapBasisCombination(BasisCurrencyRules currencyRules) {
        this.basisCurrencyRules = currencyRules;
        if (currencyRules != null) {
            currencyRules.addSwapBasisCombination(this);
        }
    }

    public int getIdSwapBasisCombination() {
        return this.idSwapBasisCombination;
    }

    public void setIdSwapBasisCombination(int idSwapBasisCombination) {
        this.idSwapBasisCombination = idSwapBasisCombination;
    }

    public String getFloatingIndex1() {
        return this.floatingIndex1;
    }

    public void setFloatingIndex1(String floatingIndex1) {
        this.floatingIndex1 = floatingIndex1;
    }

    public String getFloatingIndex2() {
        return this.floatingIndex2;
    }

    public void setFloatingIndex2(String floatingIndex2) {
        this.floatingIndex2 = floatingIndex2;
    }

    public String getFloatingIndexTenor1() {
        return this.floatingIndexTenor1;
    }

    public void setFloatingIndexTenor1(String floatingIndexTenor1) {
        this.floatingIndexTenor1 = floatingIndexTenor1;
    }

    public String getFloatingIndexTenor2() {
        return this.floatingIndexTenor2;
    }

    public void setFloatingIndexTenor2(String floatingIndexTenor2) {
        this.floatingIndexTenor2 = floatingIndexTenor2;
    }

    @JsonIgnore
    public BasisCurrencyRules getBasisCurrencyRule() {
        return this.basisCurrencyRules;
    }

    public void setBasisCurrencyRule(BasisCurrencyRules basisCurrencyRules) {
        this.basisCurrencyRules = basisCurrencyRules;
    }

    public String getLegMinTerm() {
        return legMinTerm;
    }

    public void setLegMinTerm(String legMinTerm) {
        this.legMinTerm = legMinTerm;
    }

    public String getLegMaxTerm() {
        return legMaxTerm;
    }

    public void setLegMaxTerm(String legMaxTerm) {
        this.legMaxTerm = legMaxTerm;
    }

    public String getLegMinResidualTerm() {
        return legMinResidualTerm;
    }

    public void setLegMinResidualTerm(String legMinResidualTerm) {
        this.legMinResidualTerm = legMinResidualTerm;
    }

    public String getLegMaxResidualTerm() {
        return legMaxResidualTerm;
    }

    public void setLegMaxResidualTerm(String legMaxResidualTerm) {
        this.legMaxResidualTerm = legMaxResidualTerm;
    }
}

