package com.droitfintech.dao;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


import com.droitfintech.model.*;
import com.droitfintech.regulatory.Tenor;
import com.fasterxml.jackson.annotation.JsonIgnore;


import java.math.BigDecimal;
import java.util.Set;


/**
 * The persistent class for the ProductCurrencyFloatingLegRules database table.
 *
 */
@Entity
@Table(name="PrdCurFloatingLegRules")
public class ProductCurrencyFloatingLegRules implements Serializable, DeepCopiable<ProductCurrencyFloatingLegRules> {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.TABLE, generator="TDSS_GEN")
    @Column(name="idPrdCurrFloLegRules")
    @DeepCopyIgnore(type= DeepCopyIgnore.Type.NULL)
    private int idProductCurrencyFloatLegRules;

    @RuleTest(attributeName="floatingleg1_calculationperiodfrequency", attributeType= TradeAttribute.AttributeType.TenorType, operator=MemberOf.class)
    @Column(name="floLegCalcFreq")
    private String floatLegCalculationFrequencies;

    @RuleTest(attributeName="floatingleg1_calculationperioddates_businesscenters", attributeType= TradeAttribute.AttributeType.BusinessCenter,
            operator=ContainsAll.class, customConverter="mustContainOrHandler")
    @Column(name="floLegCalcPerdCalMustContain")
    private String floatLegCalculationPeriodCalendarsMustContain;

    @RuleTest(attributeName="floatingleg1_floatingrateindex", attributeType= TradeAttribute.AttributeType.FloatLegIndex, operator=MemberOf.class)
    @Column(name="floatLegIndices")
    private String floatLegIndices;

    @RuleTest(attributeName="floatingleg1_initialnotionalvalue", attributeType= TradeAttribute.AttributeType.BigDecimalType, operator=Max.class)
    @Column(name="floatLegMaxNotional")
    private BigDecimal floatLegMaxNotional;

    @RuleTest(attributeName="floatingleg1_stublengths", attributeType= TradeAttribute.AttributeType.TenorType, operator=Max.class)
    @Column(name="floatLegMaxStub")
    private String floatLegMaxStub;

    @RuleTest(attributeName="floatingleg1_initialnotionalvalue", attributeType= TradeAttribute.AttributeType.BigDecimalType, operator=Min.class)
    @Column(name="floatLegMinNotional")
    private BigDecimal floatLegMinNotional;

    @RuleTest(attributeName="floatingleg1_stublengths", attributeType= TradeAttribute.AttributeType.TenorType, operator=Min.class)
    @Column(name="floatLegMinStub")
    private String floatLegMinStub;

    @RuleTest(attributeName="floatingleg1_paymentdates_businesscenters", attributeType= TradeAttribute.AttributeType.BusinessCenter,
            operator=ContainsAll.class, customConverter="mustContainOrHandler")
    @Column(name="floLegPmntDtCalMustContain")
    private String floatLegPaymentDateCalendarsMustContain;

    @RuleTest(attributeName="floatingleg1_paymentfrequency", attributeType= TradeAttribute.AttributeType.TenorType, operator=MemberOf.class)
    @Column(name="floatLegPaymentFrequencies")
    private String floatLegPaymentFrequencies;

    @RuleTest(attributeName="floatingleg1_calculationperiodfrequency_rollconvention", attributeType= TradeAttribute.AttributeType.RollConvention, operator=MemberOf.class)
    @Column(name="floatLegRollConventions")
    private String floatLegRollConventions;

    @RuleTest(attributeName="floatingleg1_terminationdate_businesscenters", attributeType= TradeAttribute.AttributeType.BusinessCenter,
            operator=ContainsAll.class, customConverter="mustContainOrHandler")
    @Column(name="floLegTerDtCalMustContain")
    private String floatLegTerminationDateCalendarsMustContain;

    public ProductCurrencyFloatingLegRules() {
    }

    public int getIdProductCurrencyFloatLegRules() {
        return this.idProductCurrencyFloatLegRules;
    }

    public void setIdProductCurrencyFloatLegRules(int idProductCurrencyFloatLegRules) {
        this.idProductCurrencyFloatLegRules = idProductCurrencyFloatLegRules;
    }

    public String getFloatLegCalculationFrequencies() {
        return this.floatLegCalculationFrequencies;
    }

    @JsonIgnore
    public Set<Tenor> getFloatLegCalculationFrequenciesAsTenorSet() {
        return ModelConversionUtil.getTenorSet(this.getFloatLegCalculationFrequencies());
    }

    public void setFloatLegCalculationFrequencies(String floatLegCalculationFrequencies) {
        this.floatLegCalculationFrequencies = floatLegCalculationFrequencies;
    }

    public String getFloatLegCalculationPeriodCalendarsMustContain() {
        return this.floatLegCalculationPeriodCalendarsMustContain;
    }

    @JsonIgnore
    public MustContain<String> getFloatLegCalculationPeriodCalendarsMustContainAsRepr() {
        return ModelConversionUtil.getStringMustContain(this.getFloatLegCalculationPeriodCalendarsMustContain());
    }

    public void setFloatLegCalculationPeriodCalendarsMustContain(String floatLegCalculationPeriodCalendarsMustContain) {
        this.floatLegCalculationPeriodCalendarsMustContain = floatLegCalculationPeriodCalendarsMustContain;
    }

    public String getFloatLegIndices() {
        return this.floatLegIndices;
    }

    @JsonIgnore
    public Set<String> getFloatLegIndicesAsSet() {
        return ModelConversionUtil.getStringSet(this.getFloatLegIndices());
    }

    public void setFloatLegIndices(String floatLegIndices) {
        this.floatLegIndices = floatLegIndices;
    }

    public BigDecimal getFloatLegMaxNotional() {
        return this.floatLegMaxNotional;
    }

    public void setFloatLegMaxNotional(BigDecimal floatLegMaxNotional) {
        this.floatLegMaxNotional = floatLegMaxNotional;
    }

    public String getFloatLegMaxStub() {
        return this.floatLegMaxStub;
    }

    @JsonIgnore
    public Tenor getFloatLegMaxStubAsTenor() {
        return ModelConversionUtil.makeTenor(this.getFloatLegMaxStub());
    }

    public void setFloatLegMaxStub(String floatLegMaxStub) {
        this.floatLegMaxStub = floatLegMaxStub;
    }

    public BigDecimal getFloatLegMinNotional() {
        return this.floatLegMinNotional;
    }

    public void setFloatLegMinNotional(BigDecimal floatLegMinNotional) {
        this.floatLegMinNotional = floatLegMinNotional;
    }

    public String getFloatLegMinStub() {
        return this.floatLegMinStub;
    }

    @JsonIgnore
    public Tenor getFloatLegMinStubAsTenor() {
        return ModelConversionUtil.makeTenor(this.getFloatLegMinStub());
    }

    public void setFloatLegMinStub(String floatLegMinStub) {
        this.floatLegMinStub = floatLegMinStub;
    }

    public String getFloatLegPaymentDateCalendarsMustContain() {
        return this.floatLegPaymentDateCalendarsMustContain;
    }

    @JsonIgnore
    public MustContain<String> getFloatLegPaymentDateCalendarsMustContainAsRepr() {
        return ModelConversionUtil.getStringMustContain(this.getFloatLegPaymentDateCalendarsMustContain());
    }

    public void setFloatLegPaymentDateCalendarsMustContain(String floatLegPaymentDateCalendarsMustContain) {
        this.floatLegPaymentDateCalendarsMustContain = floatLegPaymentDateCalendarsMustContain;
    }

    public String getFloatLegPaymentFrequencies() {
        return this.floatLegPaymentFrequencies;
    }

    @JsonIgnore
    public Set<Tenor> getFloatLegPaymentFrequenciesAsTenorSet() {
        return ModelConversionUtil.getTenorSet(this.getFloatLegPaymentFrequencies());
    }

    public void setFloatLegPaymentFrequencies(String floatLegPaymentFrequencies) {
        this.floatLegPaymentFrequencies = floatLegPaymentFrequencies;
    }

    public String getFloatLegRollConventions() {
        return this.floatLegRollConventions;
    }

    @JsonIgnore
    public Set<String> getFloatLegRollConventionsAsSet() {
        return ModelConversionUtil.getStringSet(this.getFloatLegRollConventions());
    }

    public void setFloatLegRollConventions(String floatLegRollConventions) {
        this.floatLegRollConventions = floatLegRollConventions;
    }

    public String getFloatLegTerminationDateCalendarsMustContain() {
        return this.floatLegTerminationDateCalendarsMustContain;
    }

    @JsonIgnore
    public MustContain<String> getFloatLegTerminationDateCalendarsMustContainAsRepr() {
        return ModelConversionUtil.getStringMustContain(this.getFloatLegTerminationDateCalendarsMustContain());
    }

    public void setFloatLegTerminationDateCalendarsMustContain(String floatLegTerminationDateCalendarsMustContain) {
        this.floatLegTerminationDateCalendarsMustContain = floatLegTerminationDateCalendarsMustContain;
    }

}
