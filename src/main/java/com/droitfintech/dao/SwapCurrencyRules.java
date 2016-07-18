package com.droitfintech.dao;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.droitfintech.model.DeepCopiable;
import com.droitfintech.model.DeepCopyIgnore;
import com.droitfintech.model.ModelConversionUtil;
import com.droitfintech.regulatory.Tenor;
import com.fasterxml.jackson.annotation.JsonIgnore;


@Entity
@Table (name = "SwapCurrencyRules")
public class SwapCurrencyRules implements Serializable, DeepCopiable<SwapCurrencyRules> {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "TDSS_GEN")
    @Column (name = "idSwapCurrencyRules")
    @DeepCopyIgnore(type= DeepCopyIgnore.Type.NULL)
    private int idSwapCurrencyRules;

    @Column (name = "eligibleMaturities")
    private String eligibleMaturities;

    @Column (name = "maxForwardStart")
    private String maxForwardStart;

    @Column (name = "minNotional")
    private BigDecimal minNotional;

    @Column (name = "inc")
    private BigDecimal increment;


    // ProductExecution fields

    @Column (name = "productClearingChoices")
    private String productClearingChoices;

    @Column (name = "productReportingChoices")
    private String productReportingChoices;

    @Column (name = "productStructures")
    private String productStructures;

    @Column (name = "productExecutionStyles")
    private String productExecutionStyles;

    @Column (name = "productBlockTradeEntry")
    private String productBlockTradeEntry;


    // Constructors

    public SwapCurrencyRules() {
    }

    // Getters and setters

    public int getIdSwapCurrencyRules() {
        return idSwapCurrencyRules;
    }

    public void setIdSwapCurrencyRules(int idSwapCurrencyRules) {
        this.idSwapCurrencyRules = idSwapCurrencyRules;
    }

    public String getEligibleMaturities() {
        return eligibleMaturities;
    }

    public void setEligibleMaturities(String eligibleMaturities) {
        this.eligibleMaturities = eligibleMaturities;
    }

    public String getMaxForwardStart() {
        return maxForwardStart;
    }

    public void setMaxForwardStart(String maxForwardStart) {
        this.maxForwardStart = maxForwardStart;
    }

    public BigDecimal getMinNotional() {
        return minNotional;
    }

    public void setMinNotional(BigDecimal minNotional) {
        this.minNotional = minNotional;
    }

    public BigDecimal getIncrement() {
        return increment;
    }

    public void setIncrement(BigDecimal increment) {
        this.increment = increment;
    }

    public String getProductClearingChoices() {
        return productClearingChoices;
    }

    public void setProductClearingChoices(String productClearingChoices) {
        this.productClearingChoices = productClearingChoices;
    }

    public String getProductReportingChoices() {
        return productReportingChoices;
    }

    public void setProductReportingChoices(String productReportingChoices) {
        this.productReportingChoices = productReportingChoices;
    }

    public String getProductStructures() {
        return productStructures;
    }

    public void setProductStructures(String productStructures) {
        this.productStructures = productStructures;
    }

    public String getProductExecutionStyles() {
        return productExecutionStyles;
    }

    public void setProductExecutionStyles(String productExecutionStyles) {
        this.productExecutionStyles = productExecutionStyles;
    }

    public String getProductBlockTradeEntry() {
        return productBlockTradeEntry;
    }

    public void setProductBlockTradeEntry(String productBlockTradeEntry) {
        this.productBlockTradeEntry = productBlockTradeEntry;
    }


    // Typed getters

    @JsonIgnore
    public Set<Tenor> getEligibleMaturitiesAsTenorSet() {
        return ModelConversionUtil.getTenorSet(this.getEligibleMaturities());
    }

    @JsonIgnore
    public Tenor getMaxForwardStartAsTenor() {
        return ModelConversionUtil.makeTenor(this.getMaxForwardStart());
    }

    @JsonIgnore
    public Set<String> getProductClearingChoicesAsSet() {
        return ModelConversionUtil.getStringSet(this.getProductClearingChoices());
    }

    @JsonIgnore
    public Set<String> getProductReportingChoicesAsSet() {
        return ModelConversionUtil.getStringSet(this.getProductReportingChoices());
    }

    @JsonIgnore
    public Set<String> getProductStructuresAsSet() {
        return ModelConversionUtil.getStringSet(this.getProductStructures());
    }

    @JsonIgnore
    public Set<String> getProductExecutionStylesAsSet() {
        return ModelConversionUtil.getStringSet(this.getProductExecutionStyles());
    }

    @JsonIgnore
    public Set<String> getProductBlockTradeEntryAsSet() {
        return ModelConversionUtil.getStringSet(this.getProductBlockTradeEntry());
    }

}