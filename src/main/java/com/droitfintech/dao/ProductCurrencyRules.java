package com.droitfintech.dao;
import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.*;

import com.droitfintech.exceptions.DroitException;
import com.droitfintech.model.*;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.joda.time.LocalDate;



import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;


/**
 * The persistent class for the ProductCurrencyRules database table.
 *
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "productCurrencyRulesType", discriminatorType = DiscriminatorType.STRING)
@Table (name="ProductCurrencyRules")
public abstract class ProductCurrencyRules implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.TABLE, generator="TDSS_GEN")
    @Column (name="idProductCurrencyRules")
    @DeepCopyIgnore(type= DeepCopyIgnore.Type.NULL)
    private int idProductCurrencyRules;

    @RuleKey(attributeName="currency", attributeType= TradeAttribute.AttributeType.Currency)
    @Column (name="currency")
    private String currency;

    @Column (name="productCurrencyRulesType")
    private String productCurrencyRulesType;

    //bi-directional many-to-one association to ProductCurrencyFloatingIndexRulesBase
    @OneToMany(mappedBy="productCurrencyRules", orphanRemoval=true, fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    @OrderBy("idProductCurrencyFloatingIndexRulesBase")
    private Set<ProductCurrencyFloatingIndexRulesBase> productCurrencyFloatingIndexRulesBases;

    @OneToMany(mappedBy="productCurrencyRules", orphanRemoval=true, fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    @OrderBy("idProductVariantRules")
    private Set<ProductVariantRules> productVariantRules;

    //bi-directional many-to-one association to SubProductRule
    @ManyToOne
    @JoinColumn(name="idSubProductRules")
    @DeepCopyIgnore(type= DeepCopyIgnore.Type.NULL)
    private SubProductRules subProductRules;

    public ProductCurrencyRules() {
        this.productCurrencyRulesType = this.getClass().getSimpleName();
    }

    public ProductCurrencyRules(SubProductRules subProductRules) {
        this.productCurrencyRulesType = this.getClass().getSimpleName();

        this.subProductRules = subProductRules;
        if (subProductRules != null) {
            subProductRules.addProductCurrencyRule(this);
        }

        this.setProductCurrencyFloatingIndexRulesBases(new HashSet<ProductCurrencyFloatingIndexRulesBase>());
    }

    public int getIdProductCurrencyRules() {
        return this.idProductCurrencyRules;
    }

    public void setIdProductCurrencyRules(int idProductCurrencyRules) {
        this.idProductCurrencyRules = idProductCurrencyRules;
    }

    public String getCurrency() {
        return this.currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @JsonIgnore
    public String getProductCurrencyRulesType() {
        return this.productCurrencyRulesType;
    }

    public void setProductCurrencyRulesType(String productCurrencyRulesType) {
        this.productCurrencyRulesType = productCurrencyRulesType;
    }

    @JsonIgnore
    public Set<ProductCurrencyFloatingIndexRulesBase> getProductCurrencyFloatingIndexRulesBases() {
        if (this.productCurrencyFloatingIndexRulesBases == null) {
            this.productCurrencyFloatingIndexRulesBases = new HashSet<ProductCurrencyFloatingIndexRulesBase>();
        }
        return this.productCurrencyFloatingIndexRulesBases;
    }

    public void setProductCurrencyFloatingIndexRulesBases(Set<ProductCurrencyFloatingIndexRulesBase> productCurrencyFloatingIndexRulesBases) {
        this.productCurrencyFloatingIndexRulesBases = productCurrencyFloatingIndexRulesBases;
    }

    @JsonIgnore
    public Set<ProductVariantRules> getProductVariantRules() {
        if (this.productVariantRules == null) {
            this.productVariantRules = new HashSet<ProductVariantRules>();
        }
        return this.productVariantRules;
    }

    public void setProductVariantRules(Set<ProductVariantRules> productVariantRules) {
        this.productVariantRules = productVariantRules;
    }

    @RelationshipSetter(paramClass=ProductCurrencyFloatingIndexRulesBase.class)
    public ProductCurrencyFloatingIndexRulesBase addProductCurrencyFloatingIndexRulesBase(ProductCurrencyFloatingIndexRulesBase productCurrencyFloatingIndexRulesBas) {
        getProductCurrencyFloatingIndexRulesBases().add(productCurrencyFloatingIndexRulesBas);
        productCurrencyFloatingIndexRulesBas.setProductCurrencyRule(this);

        return productCurrencyFloatingIndexRulesBas;
    }

    @RelationshipRemover(paramClass=ProductCurrencyFloatingIndexRulesBase.class)
    public ProductCurrencyFloatingIndexRulesBase removeProductCurrencyFloatingIndexRulesBase(ProductCurrencyFloatingIndexRulesBase productCurrencyFloatingIndexRulesBas) {
        getProductCurrencyFloatingIndexRulesBases().remove(productCurrencyFloatingIndexRulesBas);
        productCurrencyFloatingIndexRulesBas.setProductCurrencyRule(null);

        return productCurrencyFloatingIndexRulesBas;
    }

    @RelationshipSetter(paramClass=ProductVariantRules.class)
    public ProductVariantRules addProductVariantRules(ProductVariantRules productVariantRules) {
        getProductVariantRules().add(productVariantRules);
        productVariantRules.setProductCurrencyRules(this);

        return productVariantRules;
    }

    @RelationshipRemover(paramClass=ProductVariantRules.class)
    public ProductVariantRules removeProductVariantRules(ProductVariantRules productVariantRules) {
        getProductVariantRules().remove(productVariantRules);
        productVariantRules.setProductCurrencyRules(null);

        return productVariantRules;
    }

    @JsonIgnore
    public SubProductRules getSubProductRule() {
        return this.subProductRules;
    }

    public void setSubProductRule(SubProductRules subProductRules) {
        this.subProductRules = subProductRules;
    }

    @Override
    public String toString() {
        return super.toString() + this.getCurrency();
    }

    @JsonIgnore
    private Map<ProductTypeEnum, Set<ProductVariantRules>> getVariantRulesByVariant() {
        Map<ProductTypeEnum, Set<ProductVariantRules>> res = new HashMap<ProductTypeEnum, Set<ProductVariantRules>>();
        if (this.getProductVariantRules() != null) {
            for (ProductVariantRules r: this.getProductVariantRules()) {
                ProductTypeEnum type = r.getProductVariantAsEnum();
                if (!res.containsKey(type)) {
                    res.put(type, new HashSet<ProductVariantRules>());
                }
                res.get(type).add(r);
            }
        }
        return res;
    }

    /**
     * Performs the cascading matching against the productVariantRules by variant.
     * First checks MAC, then IMM, then Standard, then Custom.
     * @param trade
     * @return
     */
    public ProductVariantRules findMatchingProductVariant(ProductVariantTrade trade) {
        Map<ProductTypeEnum, Set<ProductVariantRules>> rulesByVariant = getVariantRulesByVariant();
        if (rulesByVariant.containsKey(ProductTypeEnum.MAC)) {
            for (ProductVariantRules rules: rulesByVariant.get(ProductTypeEnum.MAC)) {
                if (rules.getFloatLegIndexTenorsAsTenorSet().containsAll(trade.getFloatLegIndexTenors())
                        && rules.getFixedLegPaymentFrequenciesAsTenorSet().containsAll(trade.getFixedLegPaymentFrequencies())
                        && rules.getMacdefinitionKeys().contains(new MACDefinitionKeys(
                        trade.getEffectiveDate(), trade.getTerm(), trade.getFixedLegCoupon()))
                        ) {
                    return rules;
                }
            }
        }
        if (rulesByVariant.containsKey(ProductTypeEnum.IMM)) {
            for (ProductVariantRules rules: rulesByVariant.get(ProductTypeEnum.IMM)) {
                if (rules.getFloatLegIndexTenorsAsTenorSet().containsAll(trade.getFloatLegIndexTenors())
                        && rules.getFixedLegPaymentFrequenciesAsTenorSet().containsAll(trade.getFixedLegPaymentFrequencies())
                        && rules.getImmdefinitionKeys().contains(
                        new IMMDefinitionKeys(trade.getEffectiveDate(), trade.getTerm()))
                        ) {
                    return rules;
                }
            }
        }
        // Only spot-starting trades are considered standard.
        if (trade.getSpotStarting() && rulesByVariant.containsKey(ProductTypeEnum.STANDARD)) {
            for (ProductVariantRules rules: rulesByVariant.get(ProductTypeEnum.STANDARD)) {
                if (rules.getFloatLegIndexTenorsAsTenorSet().containsAll(trade.getFloatLegIndexTenors())
                        && rules.getFixedLegPaymentFrequenciesAsTenorSet().containsAll(trade.getFixedLegPaymentFrequencies())
                        && rules.getEligibleMaturitiesAsTenorSet().contains(trade.getTerm())) {
                    return rules;
                }
            }
        }
        if (rulesByVariant.containsKey(ProductTypeEnum.CUSTOM)) {
            Set<ProductVariantRules> customRules = rulesByVariant.get(ProductTypeEnum.CUSTOM);
            DroitException.assertThat(customRules.size() == 1,
                    StringUtils.join(new Object[] {
                                    "We don't expect to find more than one catch-all ProductTypeEnum.CUSTOM, but we've found",
                                    customRules.size(),
                                    "for",
                                    this.getSubProductRule().getFinMktInfraVersion().toString(),
                                    this.getSubProductRule().getProductMaster().toString(),
                                    this.getCurrency()},
                            ' '
                    ));
            for (ProductVariantRules rules: customRules) {
                return rules;
            }
        }
        return null;
    }

}

