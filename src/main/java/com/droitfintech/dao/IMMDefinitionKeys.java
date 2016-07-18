package com.droitfintech.dao;

import java.io.Serializable;

import javax.persistence.*;

import com.droitfintech.model.*;
import com.droitfintech.regulatory.Tenor;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import com.fasterxml.jackson.annotation.JsonIgnore;



import java.util.Date;
import java.util.Objects;


/**
 * The persistent class for the IMMDefinitionKeys database table.
 *
 */
@Entity
@Table(name="IMMDefinitionKeys")
public class IMMDefinitionKeys implements Serializable, DeepCopiable<IMMDefinitionKeys> {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "TDSS_GEN")
    @DeepCopyIgnore(type = DeepCopyIgnore.Type.NULL)
    private int idIMMDefinitionKeys;

    @Temporal(TemporalType.DATE)
    private Date effectiveDate;

    @Temporal(TemporalType.DATE)
    private Date maturityDate;

    @RuleKey(attributeName = "term", attributeType = TradeAttribute.AttributeType.TenorType)
    private String term;

    //bi-directional many-to-one association to ProductVariantRule
    @ManyToOne
    @JoinColumn(name = "idProductVariantRules")
    @DeepCopyIgnore(type = DeepCopyIgnore.Type.NULL)
    private ProductVariantRules productVariantRule;

    public IMMDefinitionKeys() {
    }

    public IMMDefinitionKeys(ProductVariantRules parent) {
        this.productVariantRule = parent;
        if (parent != null) {
            parent.addImmdefinitionKey(this);
        }
    }

    public IMMDefinitionKeys(Date effectiveDate, Tenor term) {
        this.effectiveDate = effectiveDate;
        this.term = term.toDbValue();
    }

    public int getIdIMMDefinitionKeys() {
        return this.idIMMDefinitionKeys;
    }

    public void setIdIMMDefinitionKeys(int idIMMDefinitionKeys) {
        this.idIMMDefinitionKeys = idIMMDefinitionKeys;
    }

    public Date getEffectiveDate() {
        return this.effectiveDate;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public Date getMaturityDate() {
        return this.maturityDate;
    }

    public void setMaturityDate(Date maturityDate) {
        this.maturityDate = maturityDate;
    }

    public String getTerm() {
        return this.term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    @JsonIgnore
    public ProductVariantRules getProductVariantRule() {
        return this.productVariantRule;
    }

    public void setProductVariantRule(ProductVariantRules productVariantRule) {
        this.productVariantRule = productVariantRule;
    }

    @JsonIgnore
    public Tenor getTermAsTenor() {
        return ModelConversionUtil.makeTenor(getTerm());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        IMMDefinitionKeys rhs = (IMMDefinitionKeys) obj;
        return Objects.equals(getTermAsTenor(), rhs.getTermAsTenor()) && Objects.equals(getEffectiveDate(), rhs.getEffectiveDate());
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getTermAsTenor())
                .append(effectiveDate).toHashCode();
    }
}

