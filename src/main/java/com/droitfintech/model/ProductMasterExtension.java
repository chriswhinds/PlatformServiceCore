package com.droitfintech.model;

/**
 * Created by christopherwhinds on 7/8/16.
 */

import javax.persistence.*;
import java.io.Serializable;

import javax.persistence.*;

/**
 * The persistence class for the ProductMasterExtension table
 * @author nathanbrei
 *
 */

@Entity
@Table(name = "ProductMasterExtension")
public class ProductMasterExtension implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy= GenerationType.TABLE, generator="TDSS_GEN")
    @Column (name="idProductMasterExtension")
    private int idProductMasterExtension;

    @ManyToOne
    @JoinColumn(name="idProductMaster")
    private ProductMaster productMaster;

    @Column (name="transactionType")
    private String transactionType;

    @Column (name="settlementType")
    private String settlementType;

    public ProductMaster getProductMaster() {
        return productMaster;
    }

    public void setProductMaster(ProductMaster productMaster) {
        this.productMaster = productMaster;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getSettlementType() {
        return settlementType;
    }

    public void setSettlementType(String settlementType) {
        this.settlementType = settlementType;
    }

}