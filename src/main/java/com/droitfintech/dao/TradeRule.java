package com.droitfintech.dao;
import java.util.Collection;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;

import com.droitfintech.model.AbstractComparisonPredicate;
import com.droitfintech.model.TradePredicate;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.text.WordUtils;

import com.droitfintech.model.FinMktInfra;
import com.droitfintech.model.FinMktInfraVersion.FinMktInfraVersionType;
import com.droitfintech.model.TradeContext;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@XmlAccessorType(XmlAccessType.PROPERTY)
public class TradeRule {

    @Id
    @GeneratedValue(strategy=GenerationType.TABLE, generator="TDSS_GEN")
    protected Integer idTradeRule;

    @ManyToOne
    @JoinColumn(name="idFinMktInfra")
    protected FinMktInfra ruleEntity;

    @OneToOne(orphanRemoval=true, fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name="tradeKeyPredicateId")
    protected TradePredicate tradeKey;

    @OneToOne(orphanRemoval=true, fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name="tradeTestPredicateId")
    protected TradePredicate tradeTest;

    protected Date effectiveStart;

    protected Date effectiveEnd;

    protected String tags;

    protected String comments;

    @Enumerated(EnumType.STRING)
    protected FinMktInfraVersionType ruleType;

    public TradeRule() {
        ;
    }

    public TradeRule(TradePredicate key, TradePredicate test, FinMktInfra entity, Date effectiveStart, Date effectiveEnd, String tags, FinMktInfraVersionType type) {
        setTradeKey(key);
        setTradeTest(test);
        setRuleEntity(entity);
        setEffectiveStart(effectiveStart);
        setEffectiveEnd(effectiveEnd);
        setTags(tags);
        setRuleType(type);
    }

    @JsonIgnore
    @XmlTransient
    public FinMktInfra getRuleEntity() {
        return ruleEntity;
    }

    public void setRuleEntity(FinMktInfra ruleEntity) {
        this.ruleEntity = ruleEntity;
    }

    public TradePredicate getTradeKey() {
        return tradeKey;
    }

    public void setTradeKey(TradePredicate tradeKey) {
        this.tradeKey = tradeKey;
    }

    public TradePredicate getTradeTest() {
        return tradeTest;
    }

    public void setTradeTest(TradePredicate tradeTest) {
        this.tradeTest = tradeTest;
    }

    public Date getEffectiveStart() {
        return effectiveStart;
    }

    public void setEffectiveStart(Date effectiveStart) {
        this.effectiveStart = effectiveStart;
    }

    public Date getEffectiveEnd() {
        return effectiveEnd;
    }

    public void setEffectiveEnd(Date effectiveEnd) {
        this.effectiveEnd = effectiveEnd;
    }

    @JsonIgnore
    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {

        String id = idTradeRule==null?"Undefined":idTradeRule.toString();
        String start = effectiveStart==null?"Undefined":effectiveStart.toString();
        String end = effectiveEnd==null?"Undefined":effectiveEnd.toString();
        String keyStr = tradeKey==null?"Undefined":tradeKey.toString();
        String testStr = tradeTest==null?"Undefined":tradeTest.toString();

        return "\n" + id + "\n" + start + "->" + end + "\nKEY: " + keyStr + "\nTEST: " + testStr;
    }

    /**
     * Creates as useful a name as can be done given that our rules don't have
     * meaningful names is it is.
     * @return
     */
    @JsonIgnore
    public String getName() {
        String className = tradeTest.getClass().getSimpleName();
        String res = "";
        Collection<String> attributesInvolved = tradeTest.getAttributeSignature();
        if (attributesInvolved.size() <= 2 || !(tradeTest instanceof AbstractComparisonPredicate)) {
            for (String sig: attributesInvolved) {
                sig = WordUtils.capitalizeFully(sig, new char[]{'_'}).replaceAll("_","");
                res = res + sig;
            }
        } else {
            res = ((AbstractComparisonPredicate)tradeTest).getComparisonDataType().name();
        }
        return res + className + "Check";
    }

    @JsonIgnore
    public String getDescription() {
        // not the best description of a rule but, hey, what is?
        return this.toString();
    }

    @JsonIgnore
    public FinMktInfraVersionType getRuleType() {
        return ruleType;
    }

    public void setRuleType(FinMktInfraVersionType ruleType) {
        this.ruleType = ruleType;
    }

    public boolean isEffectiveFor(Date effectiveDate) {
        return (effectiveStart == null || effectiveStart.compareTo(effectiveDate) <= 0)
                && (effectiveEnd == null || effectiveEnd.compareTo(effectiveDate) > 0);
    }

    public TradeRule copy() {
        TradeRule res = new TradeRule(this.tradeKey.copy(), this.tradeTest.copy(), this.ruleEntity,
                this.effectiveStart, this.effectiveEnd, this.tags, this.ruleType);
        return res;
    }

    @JsonIgnore
    public RuleSignatureKey getRuleSignatureKey() {
        return new RuleSignatureKey(this);
    }

    @JsonIgnore
    public Integer getIdTradeRule() {
        return idTradeRule;
    }

    @JsonIgnore
    public String getComments() {
        return comments;
    }

    @JsonIgnore
    public void setComments(String comments) {
        this.comments = comments;
    }

    /**
     * This is a class designed to provide a rule-derived key that performs hashCode, equals
     * and compareTo based on the following traits of TradeRule: ruleEntity, ruleType,
     * tradeKey, and tradeTest. The primary use of this is to combine similar rules with adjacent
     * effective ranges.
     *
     * @author jisoo
     *
     */
    public static class RuleSignatureKey implements Comparable<RuleSignatureKey> {

        private TradeRule rule;

        public RuleSignatureKey (TradeRule rule) {
            this.rule = rule;
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder(15, 31)
                    .append(rule.ruleEntity)
                    .append(rule.ruleType)
                    .append(rule.tradeKey)
                    .append(rule.tradeTest)
                    .toHashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) { return false; }
            if (obj == this) { return true; }
            if (obj.getClass() != getClass()) {
                return false;
            }
            RuleSignatureKey rhs = (RuleSignatureKey) obj;
            return new EqualsBuilder()
                    .append(rule.ruleEntity, rhs.rule.ruleEntity)
                    .append(rule.ruleType, rhs.rule.ruleType)
                    .append(rule.tradeKey, rhs.rule.tradeKey)
                    .append(rule.tradeTest, rhs.rule.tradeTest)
                    .isEquals();
        }


        public int compareTo(RuleSignatureKey rhs) {
            return new CompareToBuilder()
                    .append(rule.ruleEntity, rhs.rule.ruleEntity)
                    .append(rule.ruleType, rhs.rule.ruleType)
                    .append(rule.tradeKey, rhs.rule.tradeKey)
                    .append(rule.tradeTest, rhs.rule.tradeTest)
                    .toComparison();
        }
    }

}
