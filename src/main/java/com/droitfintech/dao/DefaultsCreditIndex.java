package com.droitfintech.dao;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="DefaultsCreditIndex")
public class DefaultsCreditIndex {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.TABLE, generator="TDSS_GEN")
    @Column
    private Integer idDefaultsCreditIndex;

    @Column
    private String indexLabel;

    @Column
    private Integer series;

    @Column
    private String baseProduct;

    @Column
    private String subProduct;

    @Column
    private String transactionType;

    @Column
    private String family;

    @Column
    private Boolean onTheRun;

    public Integer getIdDefaultsCreditIndex() {
        return idDefaultsCreditIndex;
    }

    public String getIndexLabel() {
        return indexLabel;
    }

    public Integer getSeries() {
        return series;
    }

    public String getBaseProduct() {
        return baseProduct;
    }

    public String getSubProduct() {
        return subProduct;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public String getFamily() {
        return family;
    }

    public Boolean getOnTheRun() {
        return onTheRun;
    }


}
