package com.droitfintech.dao;

import java.io.Serializable;
import java.util.Set;







import javax.persistence.*;

import com.droitfintech.model.*;
import com.droitfintech.regulatory.Tenor;
import com.fasterxml.jackson.annotation.JsonIgnore;







/**
 * The persistent class for the ProductCurrencyFloatingIndexRulesBase database table.
 *
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "prdCurFloIdxRulesType", discriminatorType = DiscriminatorType.STRING)
@Table(name="PrdCurrfloIdxRulesBase")
public abstract class ProductCurrencyFloatingIndexRulesBase implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.TABLE, generator="TDSS_GEN")
    @Column(name="idPrdCurrFloIndxRulesBase")
    @DeepCopyIgnore(type= DeepCopyIgnore.Type.NULL)
    private int idProductCurrencyFloatingIndexRulesBase;

    @RuleKey(attributeName="floatingleg1_floatingrateindex", attributeType= TradeAttribute.AttributeType.FloatLegIndex)
    @Column(name="floatLegIndex")
    private String floatLegIndex;

    @RuleTest(attributeName="floatingleg1_daycountfraction", attributeType= TradeAttribute.AttributeType.DayCountFraction, operator=MemberOf.class)
    @Column(name="floLegIndxDayCntFrac")
    private String floatLegIndexDayCountFractions;

    @RuleTest(attributeName="floatingleg1_fixingdate_businessdayconvention", attributeType= TradeAttribute.AttributeType.BusinessDayConvention,
            operator=MemberOf.class, customConverter="fra_attribute_replacer")
    @Column(name="floLegIdxFiDtBusDayConv")
    private String floatLegIndexFixingDateBusinessDayConventions;

    @RuleTest(attributeName="floatingleg1_fixingdate_businesscenters", attributeType= TradeAttribute.AttributeType.BusinessCenter,
            operator=ContainsAll.class, customConverter="mustContainOrHandler")
    @Column(name="floLegIdxFiDtCalMustContain")
    private String floatLegIndexFixingDateCalendarsMustContain;

    @RuleTest(attributeName="floatingleg1_fixingdate_offsettenor", attributeType= TradeAttribute.AttributeType.TenorType, operator=MemberOf.class)
    @Column(name="floLegIdxFiDtOffsets")
    private String floatLegIndexFixingDateOffsets;

    @RuleTest(attributeName="floatingleg1_fixingdate_daytype", attributeType= TradeAttribute.AttributeType.DayType, operator=MemberOf.class)
    @Column(name="floatLegIndexFixingDayTypes")
    private String floatLegIndexFixingDayTypes;

    @RuleTest(attributeName="floatingleg1_floatingraterounding", attributeType= TradeAttribute.AttributeType.IntegerType, operator=MemberOf.class)
    @Column(name="floatLegIndexRounding")
    private Integer floatLegIndexRounding;

    @RuleTest(attributeName="floatingleg1_indextenor", attributeType= TradeAttribute.AttributeType.TenorType, operator=MemberOf.class)
    @Column(name="floatLegIndexTenors")
    private String floatLegIndexTenors;

    @Column(name="prdCurFloIdxRulesType")
    private String productCurrencyFloatingIndexRulesType;

    //bi-directional many-to-one association to ProductCurrencyRule
    @ManyToOne
    @JoinColumn(name="idProductCurrencyRules")
    @DeepCopyIgnore(type= DeepCopyIgnore.Type.NULL)
    private ProductCurrencyRules productCurrencyRules;

    public ProductCurrencyFloatingIndexRulesBase() {
        this.productCurrencyFloatingIndexRulesType = this.getClass().getSimpleName();
    }

    public ProductCurrencyFloatingIndexRulesBase(ProductCurrencyRules currencyRules) {
        this.productCurrencyFloatingIndexRulesType = this.getClass().getSimpleName();
        this.productCurrencyRules = currencyRules;
        if (productCurrencyRules != null) {
            productCurrencyRules.addProductCurrencyFloatingIndexRulesBase(this);
        }
    }

    public int getIdProductCurrencyFloatingIndexRulesBase() {
        return this.idProductCurrencyFloatingIndexRulesBase;
    }

    public void setIdProductCurrencyFloatingIndexRulesBase(int idProductCurrencyFloatingIndexRules) {
        this.idProductCurrencyFloatingIndexRulesBase = idProductCurrencyFloatingIndexRules;
    }

    public String getFloatLegIndex() {
        return this.floatLegIndex;
    }

    public void setFloatLegIndex(String floatLegIndex) {
        this.floatLegIndex = floatLegIndex;
    }

    public String getFloatLegIndexDayCountFractions() {
        return this.floatLegIndexDayCountFractions;
    }

    @JsonIgnore
    public Set<String> getFloatLegIndexDayCountFractionsAsSet() {
        return ModelConversionUtil.getStringSet(this.getFloatLegIndexDayCountFractions());
    }

    public void setFloatLegIndexDayCountFractions(String floatLegIndexDayCountFractions) {
        this.floatLegIndexDayCountFractions = floatLegIndexDayCountFractions;
    }

    public String getFloatLegIndexFixingDateBusinessDayConventions() {
        return this.floatLegIndexFixingDateBusinessDayConventions;
    }

    @JsonIgnore
    public Set<String> getFloatLegIndexFixingDateBusinessDayConventionsAsSet() {
        return ModelConversionUtil.getStringSet(this.getFloatLegIndexFixingDateBusinessDayConventions());
    }

    public void setFloatLegIndexFixingDateBusinessDayConventions(String floatLegIndexFixingDateBusinessDayConventions) {
        this.floatLegIndexFixingDateBusinessDayConventions = floatLegIndexFixingDateBusinessDayConventions;
    }

    public String getFloatLegIndexFixingDateCalendarsMustContain() {
        return this.floatLegIndexFixingDateCalendarsMustContain;
    }

    @JsonIgnore
    public MustContain<String> getFloatLegIndexFixingDateCalendarsMustContainAsRepr() {
        return ModelConversionUtil.getStringMustContain(this.getFloatLegIndexFixingDateCalendarsMustContain());
    }

    public void setFloatLegIndexFixingDateCalendarsMustContain(String floatLegIndexFixingDateCalendarsMustContain) {
        this.floatLegIndexFixingDateCalendarsMustContain = floatLegIndexFixingDateCalendarsMustContain;
    }

    public String getFloatLegIndexFixingDateOffsets() {
        return this.floatLegIndexFixingDateOffsets;
    }

    @JsonIgnore
    public Set<Tenor> getFloatLegIndexFixingDateOffsetsAsTenorSet() {
        return ModelConversionUtil.getTenorSet(this.getFloatLegIndexFixingDateOffsets());
    }

    public void setFloatLegIndexFixingDateOffsets(String floatLegIndexFixingDateOffsets) {
        this.floatLegIndexFixingDateOffsets = floatLegIndexFixingDateOffsets;
    }

    public String getFloatLegIndexFixingDayTypes() {
        return this.floatLegIndexFixingDayTypes;
    }

    @JsonIgnore
    public Set<String> getFloatLegIndexFixingDayTypesAsSet() {
        return ModelConversionUtil.getStringSet(this.getFloatLegIndexFixingDayTypes());
    }

    public void setFloatLegIndexFixingDayTypes(String floatLegIndexFixingDayTypes) {
        this.floatLegIndexFixingDayTypes = floatLegIndexFixingDayTypes;
    }

    public Integer getFloatLegIndexRounding() {
        return this.floatLegIndexRounding;
    }

    public void setFloatLegIndexRounding(Integer floatLegIndexRounding) {
        this.floatLegIndexRounding = floatLegIndexRounding;
    }

    public String getFloatLegIndexTenors() {
        return this.floatLegIndexTenors;
    }

    @JsonIgnore
    public Set<Tenor> getFloatLegIndexTenorsAsTenorSet() {
        return ModelConversionUtil.getTenorSet(this.getFloatLegIndexTenors());
    }

    public void setFloatLegIndexTenors(String floatLegIndexTenors) {
        this.floatLegIndexTenors = floatLegIndexTenors;
    }

    public String getProductCurrencyFloatingIndexRulesType() {
        return this.productCurrencyFloatingIndexRulesType;
    }

    public void setProductCurrencyFloatingIndexRulesType(String productCurrencyFloatingIndexRulesType) {
        this.productCurrencyFloatingIndexRulesType = productCurrencyFloatingIndexRulesType;
    }

    @JsonIgnore
    public ProductCurrencyRules getProductCurrencyRule() {
        return this.productCurrencyRules;
    }

    public void setProductCurrencyRule(ProductCurrencyRules productCurrencyRules) {
        this.productCurrencyRules = productCurrencyRules;
    }

}
