package com.droitfintech.dao;

import java.io.Serializable;

import javax.persistence.*;


import com.droitfintech.model.*;
import com.droitfintech.regulatory.Tenor;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * The persistent class for the BasisCurrencyRules database table.
 *
 */
@Entity
@Table (name="BasisCurrencyRules")
@DiscriminatorValue("BasisCurrencyRules")
@PrimaryKeyJoinColumn(name = "idBasisCurrencyRules")
public class BasisCurrencyRules extends ProductCurrencyRules implements TradingWeekable, Serializable, SwapCalendarFeatureConstrainedLevel, DeepCopiable<BasisCurrencyRules> {
    private static final long serialVersionUID = 1L;

    // bi-directional many-to-one association to ProductCurrencyFloatingLegRule
    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "idPrdCurrFloLegRules")
    private ProductCurrencyFloatingLegRules productCurrencyFloatingLegRules;

    @RuleTest(attributeName="additionalpayment_submissiondate_paymentdate_offsets",
            operator=Min.class,
            attributeType= TradeAttribute.AttributeType.TenorType)
    @Column (name="swpFeePmntMinSubDtOffset")
    private String swapFeePaymentMinSubmissionDateOffset;

    @RuleTest(attributeName="term", attributeType= TradeAttribute.AttributeType.TenorType, operator= Min.class)
    @Column (name="legMinTerm")
    private String legMinTerm;

    @RuleTest(attributeName="term", attributeType= TradeAttribute.AttributeType.TenorType, operator= Max.class)
    @Column (name="legMaxTerm")
    private String legMaxTerm;

    @RuleTest(attributeName="residualterm", attributeType= TradeAttribute.AttributeType.TenorType, operator=Min.class)
    @Column (name="legMinResidualTerm")
    private String legMinResidualTerm;

    @RuleTest(attributeName="residualterm", attributeType= TradeAttribute.AttributeType.TenorType, operator=Max.class)
    @Column (name="legMaxResidualTerm")
    private String legMaxResidualTerm;

    // bi-directional many-to-one association to SwapBasisCombination
    @OneToMany(mappedBy = "basisCurrencyRules", orphanRemoval = true, fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    @OrderBy("idSwapBasisCombination")
    private Set<SwapBasisCombination> swapBasisCombinations;

    // The ProductExecution cols
    // Removed since the rule is redundant to the key-defined productvariant allowances, and were causing a problem
    // for the MAT data
    //@RuleTest(attributeName="productvariant", attributeType=AttributeType.ProductVariant, operator=MemberOf.class)
    @Column (name = "productTypes")
    private String productTypes;

    @RuleTest(attributeName="executiondate", attributeType= TradeAttribute.AttributeType.TradingHoursType, operator=CustomPredicate.class,
            customConverter="tradingDays", customRuleName="checkExecutionDateTimeInTradingHours")
    @Column (name = "tradingDays")
    private String tradingDays;

    @Column (name = "tradingHrsStartTimeBC")
    private String tradingHrsStartTimeBC;

    @Column (name = "tradingHrsEndTimeBC")
    private String tradingHrsEndTimeBC;

    @Column (name = "singleBcTradingDays")
    private String singleBcTradingDays;

    @Column (name = "tradingHrsSingleBC")
    private String tradingHrsSingleBC;

    // The SwapCurrencyRules foreign keys
    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name="idIMMSwapCurrencyRules")
    private SwapCurrencyRules immSwapCurrencyRules;

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name="idStandardSwapCurrencyRules")
    private SwapCurrencyRules standardSwapCurrencyRules;

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name="idCustomSwapCurrencyRules")
    private SwapCurrencyRules customSwapCurrencyRules;

    public BasisCurrencyRules() {
    }

    public BasisCurrencyRules(SubProductRules subProductRules) {
        super(subProductRules);
    }

    public String getSwapFeePaymentMinSubmissionDateOffset() {
        return this.swapFeePaymentMinSubmissionDateOffset;
    }

    public void setSwapFeePaymentMinSubmissionDateOffset(
            String swapFeePaymentMinSubmissionDateOffset) {
        this.swapFeePaymentMinSubmissionDateOffset = swapFeePaymentMinSubmissionDateOffset;
    }

    @JsonIgnore
    public ProductCurrencyFloatingLegRules getProductCurrencyFloatingLegRule() {
        return this.productCurrencyFloatingLegRules;
    }

    public void setProductCurrencyFloatingLegRule(
            ProductCurrencyFloatingLegRules productCurrencyFloatingLegRules) {
        this.productCurrencyFloatingLegRules = productCurrencyFloatingLegRules;
    }

    public ProductCurrencyFloatingLegRules getProductCurrencyFloatingLegRules() {
        return productCurrencyFloatingLegRules;
    }

    public void setProductCurrencyFloatingLegRules(
            ProductCurrencyFloatingLegRules productCurrencyFloatingLegRules) {
        this.productCurrencyFloatingLegRules = productCurrencyFloatingLegRules;
    }

    @JsonIgnore
    public Set<SwapBasisCombination> getSwapBasisCombinations() {
        if (this.swapBasisCombinations == null) {
            this.swapBasisCombinations = new HashSet<SwapBasisCombination>();
        }
        return this.swapBasisCombinations;
    }

    public void setSwapBasisCombinations(
            Set<SwapBasisCombination> swapBasisCombinations) {
        this.swapBasisCombinations = swapBasisCombinations;
    }

    @RelationshipSetter(paramClass=SwapBasisCombination.class)
    public SwapBasisCombination addSwapBasisCombination(
            SwapBasisCombination swapBasisCombination) {
        getSwapBasisCombinations().add(swapBasisCombination);
        swapBasisCombination.setBasisCurrencyRule(this);

        return swapBasisCombination;
    }

    @RelationshipRemover(paramClass=SwapBasisCombination.class)
    public SwapBasisCombination removeSwapBasisCombination(
            SwapBasisCombination swapBasisCombination) {
        getSwapBasisCombinations().remove(swapBasisCombination);
        swapBasisCombination.setBasisCurrencyRule(null);

        return swapBasisCombination;
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

    public String getProductTypes() {
        return productTypes;
    }

    public void setProductTypes(String productTypes) {
        this.productTypes = productTypes;
    }

    public String getTradingHrsStartTimeBC() {
        return tradingHrsStartTimeBC;
    }

    public void setTradingHrsStartTimeBC(String tradingHrsStartTimeBC) {
        this.tradingHrsStartTimeBC = tradingHrsStartTimeBC;
    }

    public String getTradingHrsEndTimeBC() {
        return tradingHrsEndTimeBC;
    }

    public void setTradingHrsEndTimeBC(String tradingHrsEndTimeBC) {
        this.tradingHrsEndTimeBC = tradingHrsEndTimeBC;
    }

    public String getTradingDays() {
        return tradingDays;
    }

    public void setTradingDays(String tradingDays) {
        this.tradingDays = tradingDays;
    }

    public SwapCurrencyRules getImmSwapCurrencyRules() {
        return immSwapCurrencyRules;
    }

    @RelationshipSetter(paramClass=SwapCurrencyRules.class)
    public void setImmSwapCurrencyRules(SwapCurrencyRules immSwapCurrencyRules) {
        this.immSwapCurrencyRules = immSwapCurrencyRules;
    }

    @RelationshipRemover(paramClass=SwapCurrencyRules.class)
    public void removeImmSwapCurrencyRules(SwapCurrencyRules immSwapCurrencyRules) {
        this.immSwapCurrencyRules = null;
    }

    public SwapCurrencyRules getStandardSwapCurrencyRules() {
        return standardSwapCurrencyRules;
    }

    @RelationshipSetter(paramClass=SwapCurrencyRules.class)
    public void setStandardSwapCurrencyRules(
            SwapCurrencyRules standardSwapCurrencyRules) {
        this.standardSwapCurrencyRules = standardSwapCurrencyRules;
    }

    @RelationshipRemover(paramClass=SwapCurrencyRules.class)
    public void removeStandardSwapCurrencyRules(SwapCurrencyRules standardSwapCurrencyRules) {
        this.standardSwapCurrencyRules = null;
    }

    public SwapCurrencyRules getCustomSwapCurrencyRules() {
        return customSwapCurrencyRules;
    }

    @RelationshipSetter(paramClass=SwapCurrencyRules.class)
    public void setCustomSwapCurrencyRules(
            SwapCurrencyRules customSwapCurrencyRules) {
        this.customSwapCurrencyRules = customSwapCurrencyRules;
    }

    @RelationshipRemover(paramClass=SwapCurrencyRules.class)
    public void removeCustomSwapCurrencyRules(SwapCurrencyRules customSwapCurrencyRules) {
        this.customSwapCurrencyRules = null;
    }

    // Typed getters
    @JsonIgnore
    public Tenor getSwapFeePaymentMinSubmissionDateOffsetAsTenor() {
        return ModelConversionUtil.makeTenor(this.getSwapFeePaymentMinSubmissionDateOffset());
    }

    @JsonIgnore
    public Set<String> getProductTypesAsSet() {
        return ModelConversionUtil.getStringSet(this.getProductTypes());
    }

    @JsonIgnore
    public TreeSet<TradingDayHours> getTradingDaysAsStartEndTimeSet() {
        return ModelConversionUtil.getTradingDayHoursSet(this.getTradingDays());
    }

    public String getSingleBcTradingDays() {
        return singleBcTradingDays;
    }

    public void setSingleBcTradingDays(String singleBcTradingDays) {
        this.singleBcTradingDays = singleBcTradingDays;
    }

    public String getTradingHrsSingleBC() {
        return tradingHrsSingleBC;
    }

    public void setTradingHrsSingleBC(String tradingHrsSingleBC) {
        this.tradingHrsSingleBC = tradingHrsSingleBC;
    }

    @JsonIgnore
    public TreeSet<TradingDayHours> getSingleBcTradingDaysAsStartEndTimeSet() {
        return ModelConversionUtil.getTradingDayHoursSet(this.getSingleBcTradingDays());
    }

    /* (non-Javadoc)
     * @see com.droitfintech.tdss.model.eligibility.TradingWeekable#getTradingWeekHoursSet()
     */

    @JsonIgnore
    public TradingWeekHoursSet getTradingWeekHoursSet() {

        TreeSet<TradingDayHours> singleHours = this.getSingleBcTradingDaysAsStartEndTimeSet();
        TreeSet<TradingDayHours> multiHours = this.getTradingDaysAsStartEndTimeSet();

        if (singleHours.isEmpty() && multiHours.isEmpty()) {
            return null;
        }

        TradingWeekHoursSet res = new TradingWeekHoursSet();

        TradingWeekHours single = new TradingWeekHours();
        single.setTradingDayHours(singleHours);
        single.setSingleBC(this.getTradingHrsSingleBC());
        res.setSingleBcTradingHours(single);

        TradingWeekHours multiple = new TradingWeekHours();
        multiple.setTradingDayHours(multiHours);
        multiple.setStartTimeBC(this.getTradingHrsStartTimeBC());
        multiple.setEndTimeBC(this.getTradingHrsEndTimeBC());
        res.setMultipleBcTradingHours(multiple);

        return res;
    }

}
