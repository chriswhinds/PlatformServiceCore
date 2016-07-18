package com.droitfintech.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;
import java.util.TreeSet;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.droitfintech.model.TradingDayHours;
import com.droitfintech.model.TradingWeekHours;
import com.droitfintech.model.TradingWeekHoursSet;
import com.droitfintech.model.ProductTypeEnum;
import com.droitfintech.model.CustomPredicate;
import com.droitfintech.model.MemberOf;

import com.droitfintech.model.TradeAttribute.AttributeType;
import com.droitfintech.model.DeepCopiable;
import com.droitfintech.model.DeepCopyIgnore;
import com.droitfintech.model.ModelConversionUtil;
import com.droitfintech.model.DeepCopyIgnore.Type;
import com.droitfintech.model.RuleTest;



/**
 * The persistent class for the ProductMaster database table.
 *
 */
@Entity
@Table(name = "ProductExecution")
public class ProductExecution implements Serializable, DeepCopiable<ProductExecution>, TradingWeekable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "TDSS_GEN")
    @Column(name = "idProductExecution")
    @DeepCopyIgnore(type = Type.NULL)
    private int idProductExecution;

    // @RuleTest(attributeName="productvariant", attributeType=AttributeType.ProductVariant, operator=MemberOf.class,
    //		excludeFromLevel="exclude")
    @Column(name = "productTypes")
    private String productTypes;

    @RuleTest(attributeName = "executiondate", attributeType = AttributeType.TradingHoursType, operator = CustomPredicate.class,
            customConverter = "tradingDays", customRuleName = "checkExecutionDateTimeInTradingHours",
            excludeFromLevel = "exclude")
    @Column(name = "tradingDays")
    private String tradingDays;

    @RuleTest(attributeName = "clearingchoices", attributeType = AttributeType.FinMktInfraType, operator = MemberOf.class,
            excludeFromLevel = "exclude")
    @Column(name = "productClearingChoices")
    private String productClearingChoices;

    @RuleTest(attributeName = "reportingchoices", attributeType = AttributeType.FinMktInfraType, operator = MemberOf.class,
            excludeFromLevel = "exclude")
    @Column(name = "productReportingChoices")
    private String productReportingChoices;

    @RuleTest(attributeName = "strategy", attributeType = AttributeType.ProductStructure, operator = MemberOf.class,
            excludeFromLevel = "exclude")
    @Column(name = "productStructures")
    private String productStructures;

    @RuleTest(attributeName = "executionstyle", attributeType = AttributeType.ExecutionStyle, operator = MemberOf.class)
    @Column(name = "productExecutionStyles")
    private String productExecutionStyles;

    @RuleTest(attributeName = "blocktrade_entrymethod", attributeType = AttributeType.BlockEntryType, operator = MemberOf.class,
            excludeFromLevel = "exclude")
    @Column(name = "productBlockTradeEntry")
    private String productBlockTradeEntry;

    @Column(name = "tradingHrsStartTimeBC")
    private String tradingHrsStartTimeBC;

    @Column(name = "tradingHrsEndTimeBC")
    private String tradingHrsEndTimeBC;

    @Column(name = "singleBcTradingDays")
    private String singleBcTradingDays;

    @Column(name = "tradingHrsSingleBC")
    private String tradingHrsSingleBC;

    public ProductExecution() {
    }

    public ProductExecution(String clearingChoices,
                            String reportingChoices,
                            String types,
                            String structures,
                            String executionStyles,
                            String blockTradeEntry) {

        this.productClearingChoices = clearingChoices;
        this.productReportingChoices = reportingChoices;
        this.productTypes = types;
        this.productStructures = structures;
        this.productExecutionStyles = executionStyles;
        this.productBlockTradeEntry = blockTradeEntry;
    }

    public int getIdProductExecution() {
        return idProductExecution;
    }

    public void setIdProductExecution(int idProductExecution) {
        this.idProductExecution = idProductExecution;
    }


    public String getProductClearingChoices() {
        return productClearingChoices;
    }

    public String getTradingDays() {
        return tradingDays;
    }

    public void setTradingDays(String tradingDays) {
        this.tradingDays = tradingDays;
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

    public String getProductTypes() {
        return productTypes;
    }

    public void setProductTypes(String productTypes) {
        this.productTypes = productTypes;
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

    // Typed getters

    @JsonIgnore
    public Set<ProductTypeEnum> getProductTypesAsSet() {
        return ModelConversionUtil.getEnumSet(this.getProductTypes(), ProductTypeEnum.class);
    }

    @JsonIgnore
    public TreeSet<TradingDayHours> getTradingDaysAsStartEndTimeSet() {
        return ModelConversionUtil.getTradingDayHoursSet(this.getTradingDays());
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
        String singleBC = this.getTradingHrsSingleBC();
        if (singleBC == null) {
            singleBC = this.getTradingHrsStartTimeBC() == null ? this.getTradingHrsEndTimeBC() : this.getTradingHrsStartTimeBC();
        }
        single.setSingleBC(singleBC);
        res.setSingleBcTradingHours(single);

        TradingWeekHours multiple = new TradingWeekHours();
        multiple.setTradingDayHours(multiHours);
        multiple.setStartTimeBC(this.getTradingHrsStartTimeBC());
        multiple.setEndTimeBC(this.getTradingHrsEndTimeBC());
        res.setMultipleBcTradingHours(multiple);

        return res;
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
}


