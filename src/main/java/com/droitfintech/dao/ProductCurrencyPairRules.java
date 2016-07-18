package com.droitfintech.dao;

import java.io.Serializable;

import javax.persistence.*;

import com.droitfintech.model.DeepCopyIgnore;
import com.droitfintech.model.RuleKey;
import com.droitfintech.model.SubProductRules;
import com.droitfintech.model.TradeAttribute;
import com.fasterxml.jackson.annotation.JsonIgnore;



/**
 * The persistent class for the ProductCurrencyPairRules database table.
 *
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "productCurrencyPairRulesType", discriminatorType = DiscriminatorType.STRING)
@Table(name="ProductCurrencyPairRules")
public abstract class ProductCurrencyPairRules implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "TDSS_GEN")
    @Column(name = "idProductCurrencyPairRules")
    @DeepCopyIgnore(type = DeepCopyIgnore.Type.NULL)
    private int idProductCurrencyPairRules;

    @RuleKey(attributeName = "basecurrency", attributeType = TradeAttribute.AttributeType.Currency)
    @Column(name = "baseCurrency")
    private String baseCurrency;

    @RuleKey(attributeName = "countercurrency", attributeType = TradeAttribute.AttributeType.Currency)
    @Column(name = "counterCurrency")
    private String counterCurrency;

    @Column(name = "productCurrencyPairRulesType")
    private String productCurrencyPairRulesType;

    //bi-directional many-to-one association to SubProductRule
    @ManyToOne
    @JoinColumn(name = "idSubProductRules")
    @DeepCopyIgnore(type = DeepCopyIgnore.Type.NULL)
    private SubProductRules subProductRules;

    public ProductCurrencyPairRules() {
        this.productCurrencyPairRulesType = this.getClass().getSimpleName();
    }

    public ProductCurrencyPairRules(SubProductRules subProductRules) {
        this.productCurrencyPairRulesType = this.getClass().getSimpleName();
        this.subProductRules = subProductRules;
        if (subProductRules != null) {
            subProductRules.addProductCurrencyPairRule(this);
        }
    }

    public int getIdProductCurrencyPairRules() {
        return this.idProductCurrencyPairRules;
    }

    public void setIdProductCurrencyPairRules(int idProductCurrencyPairRules) {
        this.idProductCurrencyPairRules = idProductCurrencyPairRules;
    }

    public String getBaseCurrency() {
        return this.baseCurrency;
    }

    public void setBaseCurrency(String baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    public String getCounterCurrency() {
        return this.counterCurrency;
    }

    public void setCounterCurrency(String counterCurrency) {
        this.counterCurrency = counterCurrency;
    }

    @JsonIgnore
    public SubProductRules getSubProductRule() {
        return this.subProductRules;
    }

    public void setSubProductRule(SubProductRules subProductRules) {
        this.subProductRules = subProductRules;
    }

    @JsonIgnore
    public String getProductCurrencyPairRulesType() {
        return productCurrencyPairRulesType;
    }

    public void setProductCurrencyPairRulesType(
            String productCurrencyPairRulesType) {
        this.productCurrencyPairRulesType = productCurrencyPairRulesType;
    }
}

