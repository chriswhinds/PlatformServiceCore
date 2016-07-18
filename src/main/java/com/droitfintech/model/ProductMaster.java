package com.droitfintech.model;

import java.io.Serializable;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import com.fasterxml.jackson.annotation.JsonIgnore;

import com.droitfintech.model.RelationshipRemover;
import com.droitfintech.model.RelationshipSetter;

import java.util.HashSet;
import java.util.Set;


/**
 * The persistent class for the ProductMaster database table.
 *
 */
@Entity
@Table (name = "ProductMaster")
public class ProductMaster implements Serializable, Comparable<ProductMaster> {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "TDSS_GEN")
    @Column(name = "idProductMaster")
    private int idProductMaster;

    @Column(name = "assetClass")
    private String assetClass;

    @Column(name = "baseProduct")
    private String baseProduct;

    @Column(name = "subProduct")
    private String subProduct;

    @Column(name = "economicsSummary")
    private String economicsSummary;

    //bi-directional many-to-one association to MasterBase
    @OneToMany(mappedBy = "productMaster")
    @OrderBy("idMasterBase")
    private Set<MasterBase> masterBases;

    //bi-directional many-to-one association to SubProductRule
    @OneToMany(mappedBy = "productMaster")
    @OrderBy("idSubProductRules")
    private Set<SubProductRules> subProductRules;

    //bi-directional many-to-one association to ProductMasterExtension
    @OneToMany(mappedBy = "productMaster")
    @OrderBy("idProductMasterExtension")
    private Set<ProductMasterExtension> productMasterExtensions;

    public ProductMaster() {
    }

    public ProductMaster(String assetClass, String baseProduct, String subProduct) {
        this.assetClass = assetClass;
        this.baseProduct = baseProduct;
        this.subProduct = subProduct;
    }

    @XmlTransient
    public int getIdProductMaster() {
        return this.idProductMaster;
    }

    public void setIdProductMaster(int idProductMaster) {
        this.idProductMaster = idProductMaster;
    }

    public String getAssetClass() {
        return this.assetClass;
    }

    public void setAssetClass(String assetClass) {
        this.assetClass = assetClass;
    }

    public String getBaseProduct() {
        return this.baseProduct;
    }

    public void setBaseProduct(String baseProduct) {
        this.baseProduct = baseProduct;
    }

    public String getSubProduct() {
        return this.subProduct;
    }

    public void setSubProduct(String subProduct) {
        this.subProduct = subProduct;
    }

    public String getEconomicsSummary() {
        return economicsSummary;
    }

    public void setEconomicsSummary(String economicsSummary) {
        this.economicsSummary = economicsSummary;
    }

    @JsonIgnore
    public Set<MasterBase> getCCCPEligibilityMasterBases() {
        if (this.masterBases == null) {
            this.masterBases = new HashSet<MasterBase>();
        }
        return this.masterBases;
    }

    public void setCcpeligibilityMasterBases(Set<MasterBase> ccpeligibilityMasterBases) {
        this.masterBases = ccpeligibilityMasterBases;
    }

    @RelationshipSetter(paramClass = MasterBase.class)
    public MasterBase addCCPEligibilityMasterBase(MasterBase ccpeligibilityMasterBas) {
        getCCCPEligibilityMasterBases().add(ccpeligibilityMasterBas);
        ccpeligibilityMasterBas.setProductMaster(this);

        return ccpeligibilityMasterBas;
    }

    @RelationshipRemover(paramClass = MasterBase.class)
    public MasterBase removeCCPEligibilityMasterBase(MasterBase ccpeligibilityMasterBas) {
        getCCCPEligibilityMasterBases().remove(ccpeligibilityMasterBas);
        ccpeligibilityMasterBas.setProductMaster(null);

        return ccpeligibilityMasterBas;
    }

    @JsonIgnore
    public Set<SubProductRules> getSubProductRules() {
        if (this.subProductRules == null) {
            this.subProductRules = new HashSet<SubProductRules>();
        }
        return this.subProductRules;
    }

    public void setSubProductRules(Set<SubProductRules> subProductRules) {
        this.subProductRules = subProductRules;
    }

    @RelationshipSetter(paramClass = SubProductRules.class)
    public SubProductRules addSubProductRule(SubProductRules subProductRules) {
        getSubProductRules().add(subProductRules);
        subProductRules.setProductMaster(this);

        return subProductRules;
    }

    @RelationshipRemover(paramClass = SubProductRules.class)
    public SubProductRules removeSubProductRule(SubProductRules subProductRules) {
        getSubProductRules().remove(subProductRules);
        subProductRules.setProductMaster(null);

        return subProductRules;
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
        ProductMaster rhs = (ProductMaster) obj;
        return new EqualsBuilder().
                append(this.assetClass, rhs.getAssetClass()).
                append(this.baseProduct, rhs.getBaseProduct()).
                append(this.subProduct, rhs.getSubProduct()).
                isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).
                append(this.assetClass).
                append(this.baseProduct).
                append(this.subProduct).
                toHashCode();
    }


    public int compareTo(ProductMaster p) {
        return new CompareToBuilder()
                .append(assetClass, p.assetClass)
                .append(baseProduct, p.baseProduct)
                .append(subProduct, p.subProduct)
                .toComparison();
    }

    public void setMasterBases(Set<MasterBase> masterBases) {
        this.masterBases = masterBases;
    }

    @JsonIgnore
    public Set<MasterBase> getMasterBases() {
        return masterBases;
    }

    @JsonIgnore
    public String getISDATaxonomy() {
        String isdaTaxonomy = this.getAssetClass() + ":" + this.getBaseProduct();
        if (this.getSubProduct() != null && !"".equals(this.getSubProduct())) {
            isdaTaxonomy += ":" + this.getSubProduct();
        }
        return isdaTaxonomy;
    }

    @Override
    public String toString() {
        return this.getISDATaxonomy();
    }
}

