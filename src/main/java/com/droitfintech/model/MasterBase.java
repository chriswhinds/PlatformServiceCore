package com.droitfintech.model;

/**
 * Created by christopherwhinds on 7/8/16.
 */
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

import com.droitfintech.dao.ClearingMandatePhase;
import com.fasterxml.jackson.annotation.JsonIgnore;

import com.droitfintech.model.TradeAttribute.AttributeType;
import com.droitfintech.model.DeepCopyIgnore;
import com.droitfintech.model.RelationshipRemover;
import com.droitfintech.model.RelationshipSetter;
import com.droitfintech.model.RuleKey;
import com.droitfintech.model.DeepCopyIgnore.Type;


/**
 * The persistent class for the MasterBase database table.
 *
 */

@Entity
@Table (name="MasterBase")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "masterType", discriminatorType = DiscriminatorType.STRING)
public abstract class MasterBase implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "TDSS_GEN")
    @Column(name = "idMasterBase")
    @DeepCopyIgnore(type = Type.NULL)
    private int idMasterBase;

    @Column(name = "masterType")
    private String masterType;

    //bi-directional many-to-one association to CentralCounterparty
    @ManyToOne
    @JoinColumn(name = "idFinMktInfraVersion")
    @DeepCopyIgnore(type = Type.NULL)
    private FinMktInfraVersion finMktInfraVersion;

    @RuleKey(attributeName = "product", attributeType = AttributeType.ProductMasterType)
    @ManyToOne
    @JoinColumn(name = "idProductMaster")
    @DeepCopyIgnore(type = Type.REFERENCE)
    private ProductMaster productMaster;

    //unidirectional one-to-one association to ProductExecution
    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "idProductExecution")
    private ProductExecution productExecution;

    //bi-directional one-to-many association to ClearingMandatePhase
    @OneToMany(mappedBy = "masterBase", orphanRemoval = true, cascade = CascadeType.ALL)
    @OrderBy("idClearingMandatePhase")
    private Set<ClearingMandatePhase> clearingMandatePhases;

    public MasterBase() {
        this(null, null);
    }

    public MasterBase(FinMktInfraVersion version) {
        this(null, version, null);
    }

    public MasterBase(ProductMaster product, FinMktInfraVersion version) {
        this(product, version, null);
    }

    public MasterBase(ProductMaster product, FinMktInfraVersion version, ProductExecution execution) {
        this.masterType = this.getClass().getSimpleName();

        this.productMaster = product;
        if (product != null) {
            product.addCCPEligibilityMasterBase(this);
        }

        this.finMktInfraVersion = null;
        if (version != null) {
            version.addMasterBase(this);
        }

        this.productExecution = execution;
    }

    public int getIdMasterBase() {
        return idMasterBase;
    }

    public void setIdMasterBase(int idMasterBase) {
        this.idMasterBase = idMasterBase;
    }

    @JsonIgnore
    public String getMasterType() {
        return masterType;
    }

    public void setMasterType(String masterType) {
        this.masterType = masterType;
    }

    @JsonIgnore
    public FinMktInfraVersion getFinMktInfraVersion() {
        return finMktInfraVersion;
    }

    public void setFinMktInfraVersion(FinMktInfraVersion finMktInfraVersion) {
        this.finMktInfraVersion = finMktInfraVersion;
    }

    public ProductMaster getProductMaster() {
        return productMaster;
    }

    @RelationshipSetter(paramClass = ProductMaster.class)
    public void setProductMaster(ProductMaster productMaster) {
        this.productMaster = productMaster;
    }

    public ProductExecution getProductExecution() {
        return productExecution;
    }

    @RelationshipSetter(paramClass = ProductExecution.class)
    public void setProductExecution(ProductExecution productExecution) {
        this.productExecution = productExecution;
    }

    @RelationshipRemover(paramClass = ProductExecution.class)
    public void removeProductExecution(ProductExecution productExecution) {
        this.productExecution = null;
    }


    /**
     * @return the clearingMandatePhases
     */
    @JsonIgnore
    public Set<ClearingMandatePhase> getClearingMandatePhases() {
        if (this.clearingMandatePhases == null) this.clearingMandatePhases = new HashSet<ClearingMandatePhase>();
        return this.clearingMandatePhases;
    }


    /**
     * @param clearingMandatePhases the clearingMandatePhases to set
     */
    @JsonIgnore
    public void setClearingMandatePhases(Set<ClearingMandatePhase> clearingMandatePhases) {
        this.clearingMandatePhases = clearingMandatePhases;
    }


    @JsonIgnore
    public ClearingMandatePhase addClearingMandatePhase(
            ClearingMandatePhase clearingMandatePhase) {
        getClearingMandatePhases().add(clearingMandatePhase);
        clearingMandatePhase.setMasterBase(this);
        return clearingMandatePhase;
    }


    @JsonIgnore
    public ClearingMandatePhase removeClearingMandatePhase(
            ClearingMandatePhase clearingMandatePhase) {
        getClearingMandatePhases().remove(clearingMandatePhase);
        clearingMandatePhase.setMasterBase(null);
        return clearingMandatePhase;
    }
}

