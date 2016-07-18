package com.droitfintech.dao;

import java.io.Serializable;

import javax.persistence.*;

import com.droitfintech.model.*;
import com.droitfintech.regulatory.Tenor;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import com.fasterxml.jackson.annotation.JsonIgnore;


import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;


/**
 * The persistent class for the MACDefinitionKeys database table.
 *
 */
@Entity
@Table(name="MACDefinitionKeys")
public class MACDefinitionKeys implements Serializable, DeepCopiable<MACDefinitionKeys> {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "TDSS_GEN")
    @DeepCopyIgnore(type = DeepCopyIgnore.Type.NULL)
    private int idMACDefinitionKeys;

    private BigDecimal coupon;

    @Temporal(TemporalType.DATE)
    private Date effectiveDate;

    @Temporal(TemporalType.DATE)
    private Date maturityDate;

    @RuleKey(attributeName = "term", attributeType = TradeAttribute.AttributeType.TenorType)
    private String term;

    @OneToMany(mappedBy = "macdefinitionKey", orphanRemoval = true, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @OrderBy("idMACDefinitionKeyIdMap")
    private Set<MACDefinitionKeyIdMap> macdefinitionKeyIdMaps;

    @ManyToOne
    @JoinColumn(name = "idProductVariantRules")
    @DeepCopyIgnore(type = DeepCopyIgnore.Type.NULL)
    private ProductVariantRules productVariantRule;

    public MACDefinitionKeys() {
    }

    public MACDefinitionKeys(ProductVariantRules parent) {
        this.productVariantRule = parent;
        if (parent != null) {
            parent.addMacdefinitionKey(this);
        }
    }

    public MACDefinitionKeys(Date effectiveDate, Tenor term, BigDecimal coupon) {
        this.effectiveDate = effectiveDate;
        this.term = term.toDbValue();
        this.coupon = coupon;
    }

    public int getIdMACDefinitionKeys() {
        return this.idMACDefinitionKeys;
    }

    public void setIdMACDefinitionKeys(int idMACDefinitionKeys) {
        this.idMACDefinitionKeys = idMACDefinitionKeys;
    }

    public BigDecimal getCoupon() {
        return this.coupon;
    }

    public void setCoupon(BigDecimal coupon) {
        this.coupon = coupon;
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
    public Set<MACDefinitionKeyIdMap> getMacdefinitionKeyIdMaps() {
        if (this.macdefinitionKeyIdMaps == null) {
            this.macdefinitionKeyIdMaps = new LinkedHashSet<MACDefinitionKeyIdMap>();
        }
        return this.macdefinitionKeyIdMaps;
    }

    public void setMacdefinitionKeyIdMaps(Set<MACDefinitionKeyIdMap> macdefinitionKeyIdMaps) {
        this.macdefinitionKeyIdMaps = macdefinitionKeyIdMaps;
    }

    @RelationshipSetter(paramClass = MACDefinitionKeyIdMap.class)
    public MACDefinitionKeyIdMap addMacdefinitionKeyIdMap(MACDefinitionKeyIdMap macdefinitionKeyIdMap) {
        getMacdefinitionKeyIdMaps().add(macdefinitionKeyIdMap);
        macdefinitionKeyIdMap.setMacdefinitionKey(this);

        return macdefinitionKeyIdMap;
    }

    @RelationshipRemover(paramClass = MACDefinitionKeyIdMap.class)
    public MACDefinitionKeyIdMap removeMacdefinitionKeyIdMap(MACDefinitionKeyIdMap macdefinitionKeyIdMap) {
        getMacdefinitionKeyIdMaps().remove(macdefinitionKeyIdMap);
        macdefinitionKeyIdMap.setMacdefinitionKey(null);

        return macdefinitionKeyIdMap;
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
        MACDefinitionKeys rhs = (MACDefinitionKeys) obj;
        return Objects.equals(getTermAsTenor(), rhs.getTermAsTenor()) && Objects.equals(getEffectiveDate(), rhs.getEffectiveDate())
                && ((coupon == null && rhs.coupon == null) ||
                (
                        (coupon != null && rhs.coupon != null) &&
                                (coupon.compareTo(rhs.coupon) == 0)
                ));
    }

    @Override
    public int hashCode() {
        BigDecimal hashCoupon = coupon == null ? coupon : coupon.setScale(15, BigDecimal.ROUND_FLOOR);
        return new HashCodeBuilder(17, 37)
                .append(term)
                .append(effectiveDate)
                .append(hashCoupon).toHashCode();
    }
}

