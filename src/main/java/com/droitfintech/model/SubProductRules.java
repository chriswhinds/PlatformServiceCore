package com.droitfintech.model;

/**
 * Created by christopherwhinds on 7/8/16.
 */

import com.droitfintech.dao.ProductCurrencyPairRules;
import com.droitfintech.dao.ProductCurrencyRules;
import com.droitfintech.dao.ProductSeriesRulesBase;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import java.io.Serializable;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashSet;
import java.util.Set;


/**
 * The persistent class for the SubProductRules database table.
 *
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "subProductRulesType", discriminatorType = DiscriminatorType.STRING)
@Table (name = "SubProductRules")
public abstract class SubProductRules implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "TDSS_GEN")
    @Column(name = "idSubProductRules")
    @DeepCopyIgnore(type = DeepCopyIgnore.Type.NULL)
    private int idSubProductRules;

    @Column(name = "ruleImplementationComplex")
    @RuleTest(attributeName = "complexRules", attributeType = TradeAttribute.AttributeType.StringType,
            operator = MemberOf.class, customConverter = "complexRuleConverter")
    private String ruleImplementationComplex = "";

    @Column(name = "ruleImplementationSimple")
    private String ruleImplementationSimple = "";

    @Column(name = "subProductRulesType")
    private String subProductRulesType;

    //bi-directional many-to-one association to ProductCurrencyPairRule
    @OneToMany(mappedBy = "subProductRules", orphanRemoval = true, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @OrderBy("idProductCurrencyPairRules")
    private Set<ProductCurrencyPairRules> productCurrencyPairRules;

    //bi-directional many-to-one association to ProductCurrencyRule
    @OneToMany(mappedBy = "subProductRules", orphanRemoval = true, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @OrderBy("idProductCurrencyRules")
    private Set<ProductCurrencyRules> productCurrencyRules;

    //bi-directional many-to-one association to ProductSeriesRulesBase
    @OneToMany(mappedBy = "subProductRules", orphanRemoval = true, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @OrderBy("idProductSeriesRulesBase")
    private Set<ProductSeriesRulesBase> productSeriesRulesBases;

    //bi-directional many-to-one association to ProductMaster
    @ManyToOne
    @JoinColumn(name = "idProductMaster")
    @DeepCopyIgnore(type = DeepCopyIgnore.Type.REFERENCE)
    @RuleKey(attributeName = "product", attributeType = TradeAttribute.AttributeType.ProductMasterType)
    private ProductMaster productMaster;

    //bi-directional many-to-one association to CentralCounterparty
    @ManyToOne
    @JoinColumn(name = "idFinMktInfraVersion")
    @DeepCopyIgnore(type = DeepCopyIgnore.Type.NULL)
    private FinMktInfraVersion finMktInfraVersion;

    public SubProductRules() {
        this(null, null);
    }

    public SubProductRules(FinMktInfraVersion ccpVersion) {
        this(null, ccpVersion);
    }

    public SubProductRules(ProductMaster product, FinMktInfraVersion ccpVersion) {
        this.subProductRulesType = this.getClass().getSimpleName();

        this.productMaster = product;
        if (product != null) {
            product.addSubProductRule(this);
        }

        this.finMktInfraVersion = ccpVersion;
        if (ccpVersion != null) {
            ccpVersion.addSubProductRule(this);
        }
        this.setProductCurrencyPairRules(new HashSet<ProductCurrencyPairRules>());
        this.setProductCurrencyRules(new HashSet<ProductCurrencyRules>());
        this.setProductSeriesRulesBases(new HashSet<ProductSeriesRulesBase>());
    }

    @JsonIgnore
    public FinMktInfraVersion getFinMktInfraVersion() {
        return finMktInfraVersion;
    }

    public void setFinMktInfraVersion(
            FinMktInfraVersion centralCounterpartyVersion) {
        this.finMktInfraVersion = centralCounterpartyVersion;
    }

    public int getIdSubProductRules() {
        return this.idSubProductRules;
    }

    @JsonIgnore
    public Set<String> getRuleImplementations() {
        Set<String> simple = ModelConversionUtil.getStringSet(this.ruleImplementationSimple);
        simple.addAll(ModelConversionUtil.getStringSet(this.ruleImplementationComplex));
        return simple;
    }

    public void setIdSubProductRules(int idSubProductRules) {
        this.idSubProductRules = idSubProductRules;
    }

    public String getRuleImplementationComplex() {
        return this.ruleImplementationComplex;
    }

    public void setRuleImplementationComplex(String ruleImplementationComplex) {
        this.ruleImplementationComplex = ruleImplementationComplex;
    }

    public String getRuleImplementationSimple() {
        return this.ruleImplementationSimple;
    }

    public void setRuleImplementationSimple(String ruleImplementationSimple) {
        this.ruleImplementationSimple = ruleImplementationSimple;
    }

    @JsonIgnore
    public String getSubProductRulesType() {
        return this.subProductRulesType;
    }

    public void setSubProductRulesType(String subProductRulesType) {
        this.subProductRulesType = subProductRulesType;
    }

    @JsonIgnore
    public Set<ProductCurrencyPairRules> getProductCurrencyPairRules() {
        return this.productCurrencyPairRules;
    }

    public void setProductCurrencyPairRules(Set<ProductCurrencyPairRules> productCurrencyPairRules) {
        this.productCurrencyPairRules = productCurrencyPairRules;
    }

    @RelationshipSetter(paramClass = ProductCurrencyPairRules.class)
    public ProductCurrencyPairRules addProductCurrencyPairRule(ProductCurrencyPairRules productCurrencyPairRules) {
        getProductCurrencyPairRules().add(productCurrencyPairRules);
        productCurrencyPairRules.setSubProductRule(this);

        return productCurrencyPairRules;
    }

    @RelationshipRemover(paramClass = ProductCurrencyPairRules.class)
    public ProductCurrencyPairRules removeProductCurrencyPairRule(ProductCurrencyPairRules productCurrencyPairRules) {
        getProductCurrencyPairRules().remove(productCurrencyPairRules);
        productCurrencyPairRules.setSubProductRule(null);

        return productCurrencyPairRules;
    }

    @JsonIgnore
    public Set<ProductCurrencyRules> getProductCurrencyRules() {
        return this.productCurrencyRules;
    }

    public void setProductCurrencyRules(Set<ProductCurrencyRules> productCurrencyRules) {
        this.productCurrencyRules = productCurrencyRules;
    }

    @RelationshipSetter(paramClass = ProductCurrencyRules.class)
    public ProductCurrencyRules addProductCurrencyRule(ProductCurrencyRules productCurrencyRules) {
        getProductCurrencyRules().add(productCurrencyRules);
        productCurrencyRules.setSubProductRule(this);

        return productCurrencyRules;
    }

    @RelationshipRemover(paramClass = ProductCurrencyRules.class)
    public ProductCurrencyRules removeProductCurrencyRule(ProductCurrencyRules productCurrencyRules) {
        getProductCurrencyRules().remove(productCurrencyRules);
        productCurrencyRules.setSubProductRule(null);

        return productCurrencyRules;
    }

    @JsonIgnore
    public Set<ProductSeriesRulesBase> getProductSeriesRulesBases() {
        return this.productSeriesRulesBases;
    }

    public void setProductSeriesRulesBases(Set<ProductSeriesRulesBase> productSeriesRulesBases) {
        this.productSeriesRulesBases = productSeriesRulesBases;
    }

    @RelationshipSetter(paramClass = ProductSeriesRulesBase.class)
    public ProductSeriesRulesBase addProductSeriesRulesBase(ProductSeriesRulesBase productSeriesRulesBas) {
        getProductSeriesRulesBases().add(productSeriesRulesBas);
        productSeriesRulesBas.setSubProductRule(this);

        return productSeriesRulesBas;
    }

    @RelationshipRemover(paramClass = ProductSeriesRulesBase.class)
    public ProductSeriesRulesBase removeProductSeriesRulesBase(ProductSeriesRulesBase productSeriesRulesBas) {
        getProductSeriesRulesBases().remove(productSeriesRulesBas);
        productSeriesRulesBas.setSubProductRule(null);

        return productSeriesRulesBas;
    }

    public ProductMaster getProductMaster() {
        return this.productMaster;
    }

    public void setProductMaster(ProductMaster productMaster) {
        // Don't try to handle bidirectionality here.
        // Instead, ProductMaster.addSubProductRule() takes care of that; always
        // set the relationship using that method instead.
        this.productMaster = productMaster;
    }

    @JsonIgnore
    public FinMktInfra getFinMktInfra() {
        return (FinMktInfra) this.getFinMktInfraVersion().getFinMktInfra();
    }
}

