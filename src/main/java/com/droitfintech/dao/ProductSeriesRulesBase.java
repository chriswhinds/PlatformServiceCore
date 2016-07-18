package com.droitfintech.dao;

import java.io.Serializable;

import javax.persistence.*;

import com.droitfintech.model.DeepCopyIgnore;
import com.droitfintech.model.SubProductRules;
import com.fasterxml.jackson.annotation.JsonIgnore;



/**
 * The persistent class for the ProductSeriesRulesBase database table.
 *
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "productSeriesRulesType", discriminatorType = DiscriminatorType.STRING)
@Table (name = "ProductSeriesRulesBase")
public abstract class ProductSeriesRulesBase implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "TDSS_GEN")
    @Column(name = "idProductSeriesRulesBase")
    @DeepCopyIgnore(type = DeepCopyIgnore.Type.NULL)
    private int idProductSeriesRulesBase;

    @Column(name = "series")
    private int series;

    //bi-directional many-to-one association to SubProductRule
    @ManyToOne
    @JoinColumn(name = "idSubProductRules")
    @DeepCopyIgnore(type = DeepCopyIgnore.Type.NULL)
    private SubProductRules subProductRules;

    @Column(name = "productSeriesRulesType")
    private String productSeriesRulesType;

    public ProductSeriesRulesBase() {
        this.productSeriesRulesType = this.getClass().getSimpleName();
    }

    public ProductSeriesRulesBase(SubProductRules subProductRules) {
        this.productSeriesRulesType = this.getClass().getSimpleName();

        this.subProductRules = subProductRules;
        if (subProductRules != null) {
            subProductRules.addProductSeriesRulesBase(this);
        }

    }

    public int getIdProductSeriesRulesBase() {
        return this.idProductSeriesRulesBase;
    }

    public void setIdProductSeriesRulesBase(int idProductSeriesRulesBase) {
        this.idProductSeriesRulesBase = idProductSeriesRulesBase;
    }

    public int getSeries() {
        return this.series;
    }

    public void setSeries(int series) {
        this.series = series;
    }

    @JsonIgnore
    public SubProductRules getSubProductRule() {
        return this.subProductRules;
    }

    public void setSubProductRule(SubProductRules subProductRules) {
        this.subProductRules = subProductRules;
    }

    public String getProductSeriesRulesType() {
        return productSeriesRulesType;
    }

    public void setProductSeriesRulesType(String productSeriesRulesType) {
        this.productSeriesRulesType = productSeriesRulesType;
    }
}

