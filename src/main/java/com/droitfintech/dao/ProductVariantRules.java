package com.droitfintech.dao;

import java.io.Serializable;

import javax.persistence.*;


import com.droitfintech.model.*;
import com.droitfintech.regulatory.Tenor;
import com.fasterxml.jackson.annotation.JsonIgnore;



import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;


/**
 * The persistent class for the ProductVariantRules database table.
 *
 */
@Entity
@Table(name="ProductVariantRules")
public class ProductVariantRules implements Serializable, DeepCopiable<ProductVariantRules> {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "TDSS_GEN")
    @DeepCopyIgnore(type = DeepCopyIgnore.Type.NULL)
    private int idProductVariantRules;

    @RuleTest(attributeName = "", attributeType = TradeAttribute.AttributeType.BusinessCenter, operator = ContainsAll.class,
            customConverter = "businessCalendarsAll")
    @Column(name = "businessHolidayCalendars")
    private String businessHolidayCalendarsMustContain;

    @RuleKey(attributeName = "term", attributeType = TradeAttribute.AttributeType.TenorType)
    private String eligibleMaturities;

    @RuleTest(attributeName = "fixedleg1_daycountfraction", attributeType = TradeAttribute.AttributeType.DayCountFraction, operator = MemberOf.class)
    private String fixedLegDayCountFractions;

    @RuleKey(attributeName = "fixedleg1_paymentfrequency", attributeType = TradeAttribute.AttributeType.TenorType)
    private String fixedLegPaymentFrequencies;

    @RuleTest(attributeName = "floatingleg1_compoundingmethod", attributeType = TradeAttribute.AttributeType.CompoundingMethod, operator = MemberOf.class)
    private String floatLegCompoundingMethods;

    @RuleTest(attributeName = "floatingleg1_daycountfraction", attributeType = TradeAttribute.AttributeType.DayCountFraction, operator = MemberOf.class)
    private String floatLegindexDayCountFractions;

    @RuleKey(attributeName = "floatingleg1_indextenor", attributeType = TradeAttribute.AttributeType.TenorType)
    @RuleTest(attributeName = "floatingleg1_resetfrequency", attributeType = TradeAttribute.AttributeType.TenorType, operator = MemberOf.class)
    private String floatLegIndexTenors;

    @RuleTest(attributeName = "floatingleg1_paymentfrequency", attributeType = TradeAttribute.AttributeType.TenorType, operator = MemberOf.class)
    private String floatLegPaymentFrequencies;

    // @RuleTest(attributeName="", attributeType=AttributeType, operator=MemberOf.class)
    @Column(name = "incrementLim")
    private BigDecimal increment;

    @RuleTest(attributeName = "tradenotional", attributeType = TradeAttribute.AttributeType.BigDecimalType, operator = Max.class)
    private BigDecimal maxNotional;

    @RuleTest(attributeName = "tradenotional", attributeType = TradeAttribute.AttributeType.BigDecimalType, operator = Min.class)
    private BigDecimal minNotional;

    @RuleTest(attributeName = "floatingleg1_paymentdates_businessdayconvention", attributeType = TradeAttribute.AttributeType.BusinessDayConvention,
            operator = MemberOf.class, customConverter = "paymentDateBusinessDayConventions")
    @Column(name = "paymentDateBDCs")
    private String paymentDateBusinessDayConventions;

    @RuleTest(attributeName = "blocktrade_entrymethod", attributeType = TradeAttribute.AttributeType.BlockEntryType, operator = MemberOf.class)
    private String productBlockTradeEntry;

    @RuleTest(attributeName = "clearingchoices", attributeType = TradeAttribute.AttributeType.FinMktInfraType, operator = MemberOf.class)
    private String productClearingChoices;

    @RuleTest(attributeName = "executionstyle", attributeType = TradeAttribute.AttributeType.ExecutionStyle, operator = MemberOf.class)
    private String productExecutionStyles;

    @RuleTest(attributeName = "reportingchoices", attributeType = TradeAttribute.AttributeType.FinMktInfraType, operator = MemberOf.class)
    private String productReportingChoices;

    @RuleTest(attributeName = "strategy", attributeType = TradeAttribute.AttributeType.ProductStructure, operator = MemberOf.class)
    private String productStructures;

    @RuleKey(attributeName = "productvariant", attributeType = TradeAttribute.AttributeType.ProductVariant)
    private String productVariant;

    @RuleTest(attributeName = "rollconventions_all", attributeType = TradeAttribute.AttributeType.RollConvention, operator = MemberOf.class)
    private String rollConventions;

    @RuleTest(attributeName = "spotoffset", attributeType = TradeAttribute.AttributeType.TenorType, operator = MemberOf.class)
    private String spotOffset;

    @ManyToOne
    @JoinColumn(name = "idProductCurrencyRules")
    @DeepCopyIgnore(type = DeepCopyIgnore.Type.NULL)
    private ProductCurrencyRules productCurrencyRules;

    @RuleKey(attributeName = "term", attributeType = TradeAttribute.AttributeType.TenorType)
    @OneToMany(mappedBy = "productVariantRule", orphanRemoval = true, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @OrderBy("idIMMDefinitionKeys")
    private Set<IMMDefinitionKeys> immdefinitionKeys;

    @RuleKey(attributeName = "term", attributeType = TradeAttribute.AttributeType.TenorType)
    @OneToMany(mappedBy = "productVariantRule", orphanRemoval = true, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @OrderBy("idMACDefinitionKeys")
    private Set<MACDefinitionKeys> macdefinitionKeys;

    public ProductVariantRules() {
    }

    public ProductVariantRules(ProductCurrencyRules currencyRules) {
        this.productCurrencyRules = currencyRules;
        if (productCurrencyRules != null) {
            productCurrencyRules.addProductVariantRules(this);
        }
    }

    public int getIdProductVariantRules() {
        return this.idProductVariantRules;
    }

    public void setIdProductVariantRules(int idProductVariantRules) {
        this.idProductVariantRules = idProductVariantRules;
    }

    public String getBusinessHolidayCalendarsMustContain() {
        return this.businessHolidayCalendarsMustContain;
    }

    @JsonIgnore
    public MustContain<String> getBusinessHolidayCalendarsMustContainAsRepr() {
        return ModelConversionUtil.getStringMustContain(this.getBusinessHolidayCalendarsMustContain());
    }

    public void setBusinessHolidayCalendarsMustContain(String businessHolidayCalendarsMustContain) {
        this.businessHolidayCalendarsMustContain = businessHolidayCalendarsMustContain;
    }

    public String getEligibleMaturities() {
        return this.eligibleMaturities;
    }

    @JsonIgnore
    public Set<Tenor> getEligibleMaturitiesAsTenorSet() {
        return ModelConversionUtil.getTenorSet(getEligibleMaturities());
    }

    public void setEligibleMaturities(String eligibleMaturities) {
        this.eligibleMaturities = eligibleMaturities;
    }

    public String getFixedLegDayCountFractions() {
        return this.fixedLegDayCountFractions;
    }

    @JsonIgnore
    public Set<String> getFixedLegDayCountFractionsAsSet() {
        return ModelConversionUtil.getStringSet(getFixedLegDayCountFractions());
    }

    public void setFixedLegDayCountFractions(String fixedLegDayCountFractions) {
        this.fixedLegDayCountFractions = fixedLegDayCountFractions;
    }

    public String getFixedLegPaymentFrequencies() {
        return this.fixedLegPaymentFrequencies;
    }

    @JsonIgnore
    public Set<Tenor> getFixedLegPaymentFrequenciesAsTenorSet() {
        return ModelConversionUtil.getTenorSet(getFixedLegPaymentFrequencies());
    }

    public void setFixedLegPaymentFrequencies(String fixedLegPaymentFrequencies) {
        this.fixedLegPaymentFrequencies = fixedLegPaymentFrequencies;
    }

    public String getFloatLegCompoundingMethods() {
        return this.floatLegCompoundingMethods;
    }

    @JsonIgnore
    public Set<String> getFloatLegCompoundingMethodsAsSet() {
        return ModelConversionUtil.getStringSet(getFloatLegCompoundingMethods());
    }

    public void setFloatLegCompoundingMethods(String floatLegCompoundingMethods) {
        this.floatLegCompoundingMethods = floatLegCompoundingMethods;
    }

    public String getFloatLegindexDayCountFractions() {
        return this.floatLegindexDayCountFractions;
    }

    @JsonIgnore
    public Set<String> getFloatLegindexDayCountFractionsAsSet() {
        return ModelConversionUtil.getStringSet(getFloatLegindexDayCountFractions());
    }

    public void setFloatLegindexDayCountFractions(String floatLegindexDayCountFractions) {
        this.floatLegindexDayCountFractions = floatLegindexDayCountFractions;
    }

    public String getFloatLegIndexTenors() {
        return this.floatLegIndexTenors;
    }

    @JsonIgnore
    public Set<Tenor> getFloatLegIndexTenorsAsTenorSet() {
        return ModelConversionUtil.getTenorSet(getFloatLegIndexTenors());
    }

    public void setFloatLegIndexTenors(String floatLegIndexTenors) {
        this.floatLegIndexTenors = floatLegIndexTenors;
    }

    public String getFloatLegPaymentFrequencies() {
        return this.floatLegPaymentFrequencies;
    }

    @JsonIgnore
    public Set<Tenor> getFloatLegPaymentFrequenciesAsTenorSet() {
        return ModelConversionUtil.getTenorSet(getFloatLegPaymentFrequencies());
    }

    public void setFloatLegPaymentFrequencies(String floatLegPaymentFrequencies) {
        this.floatLegPaymentFrequencies = floatLegPaymentFrequencies;
    }

    public BigDecimal getIncrement() {
        return this.increment;
    }

    public void setIncrement(BigDecimal increment) {
        this.increment = increment;
    }

    public BigDecimal getMaxNotional() {
        return this.maxNotional;
    }

    public void setMaxNotional(BigDecimal maxNotional) {
        this.maxNotional = maxNotional;
    }

    public BigDecimal getMinNotional() {
        return this.minNotional;
    }

    public void setMinNotional(BigDecimal minNotional) {
        this.minNotional = minNotional;
    }

    public String getPaymentDateBusinessDayConventions() {
        return this.paymentDateBusinessDayConventions;
    }

    @JsonIgnore
    public Set<String> getPaymentDateBusinessDayConventionsAsSet() {
        return ModelConversionUtil.getStringSet(getPaymentDateBusinessDayConventions());
    }

    public void setPaymentDateBusinessDayConventions(String paymentDateBusinessDayConventions) {
        this.paymentDateBusinessDayConventions = paymentDateBusinessDayConventions;
    }

    public String getProductBlockTradeEntry() {
        return this.productBlockTradeEntry;
    }

    @JsonIgnore
    public Set<String> getProductBlockTradeEntryAsSet() {
        return ModelConversionUtil.getStringSet(getProductBlockTradeEntry());
    }

    public void setProductBlockTradeEntry(String productBlockTradeEntry) {
        this.productBlockTradeEntry = productBlockTradeEntry;
    }

    public String getProductClearingChoices() {
        return this.productClearingChoices;
    }

    @JsonIgnore
    public Set<String> getProductClearingChoicesAsSet() {
        return ModelConversionUtil.getStringSet(getProductClearingChoices());
    }

    public void setProductClearingChoices(String productClearingChoices) {
        this.productClearingChoices = productClearingChoices;
    }

    public String getProductExecutionStyles() {
        return this.productExecutionStyles;
    }

    @JsonIgnore
    public Set<String> getProductExecutionStylesAsSet() {
        return ModelConversionUtil.getStringSet(getProductExecutionStyles());
    }

    public void setProductExecutionStyles(String productExecutionStyles) {
        this.productExecutionStyles = productExecutionStyles;
    }

    public String getProductReportingChoices() {
        return this.productReportingChoices;
    }

    @JsonIgnore
    public Set<String> getProductReportingChoicesAsSet() {
        return ModelConversionUtil.getStringSet(getProductReportingChoices());
    }

    public void setProductReportingChoices(String productReportingChoices) {
        this.productReportingChoices = productReportingChoices;
    }

    public String getProductStructures() {
        return this.productStructures;
    }

    @JsonIgnore
    public Set<String> getProductStructuresAsSet() {
        return ModelConversionUtil.getStringSet(getProductStructures());
    }

    public void setProductStructures(String productStructures) {
        this.productStructures = productStructures;
    }

    public String getProductVariant() {
        return this.productVariant;
    }

    public void setProductVariant(String productVariant) {
        this.productVariant = productVariant;
    }

    public String getRollConventions() {
        return this.rollConventions;
    }

    @JsonIgnore
    public Set<String> getRollConventionsAsSet() {
        return ModelConversionUtil.getStringSet(getRollConventions());
    }

    public void setRollConventions(String rollConventions) {
        this.rollConventions = rollConventions;
    }

    public String getSpotOffset() {
        return this.spotOffset;
    }

    public void setSpotOffset(String spotOffset) {
        this.spotOffset = spotOffset;
    }

    @JsonIgnore
    public Set<IMMDefinitionKeys> getImmdefinitionKeys() {
        if (this.immdefinitionKeys == null) {
            this.immdefinitionKeys = new LinkedHashSet<IMMDefinitionKeys>();
        }
        return this.immdefinitionKeys;
    }

    public void setImmdefinitionKeys(Set<IMMDefinitionKeys> immdefinitionKeys) {
        this.immdefinitionKeys = immdefinitionKeys;
    }

    @RelationshipSetter(paramClass = IMMDefinitionKeys.class)
    public IMMDefinitionKeys addImmdefinitionKey(IMMDefinitionKeys immdefinitionKey) {
        getImmdefinitionKeys().add(immdefinitionKey);
        immdefinitionKey.setProductVariantRule(this);

        return immdefinitionKey;
    }

    @RelationshipRemover(paramClass = IMMDefinitionKeys.class)
    public IMMDefinitionKeys removeImmdefinitionKey(IMMDefinitionKeys immdefinitionKey) {
        getImmdefinitionKeys().remove(immdefinitionKey);
        immdefinitionKey.setProductVariantRule(null);

        return immdefinitionKey;
    }

    @JsonIgnore
    public Set<MACDefinitionKeys> getMacdefinitionKeys() {
        if (this.macdefinitionKeys == null) {
            this.macdefinitionKeys = new LinkedHashSet<MACDefinitionKeys>();
        }
        return this.macdefinitionKeys;
    }

    public void setMacdefinitionKeys(Set<MACDefinitionKeys> macdefinitionKeys) {
        this.macdefinitionKeys = macdefinitionKeys;
    }

    @RelationshipSetter(paramClass = MACDefinitionKeys.class)
    public MACDefinitionKeys addMacdefinitionKey(MACDefinitionKeys macdefinitionKey) {
        getMacdefinitionKeys().add(macdefinitionKey);
        macdefinitionKey.setProductVariantRule(this);

        return macdefinitionKey;
    }

    @RelationshipRemover(paramClass = MACDefinitionKeys.class)
    public MACDefinitionKeys removeMacdefinitionKey(MACDefinitionKeys macdefinitionKey) {
        getMacdefinitionKeys().remove(macdefinitionKey);
        macdefinitionKey.setProductVariantRule(null);

        return macdefinitionKey;
    }

    @JsonIgnore
    public ProductCurrencyRules getProductCurrencyRules() {
        return productCurrencyRules;
    }

    public void setProductCurrencyRules(ProductCurrencyRules productCurrencyRules) {
        this.productCurrencyRules = productCurrencyRules;
    }

    @JsonIgnore
    public Tenor getSpotOffsetAsTenor() {
        return ModelConversionUtil.makeTenor(getSpotOffset());
    }

    @JsonIgnore
    public ProductTypeEnum getProductVariantAsEnum() {
        return ProductTypeEnum.fromValue(getProductVariant());
    }
}

