package com.droitfintech.dao;

import java.math.BigDecimal;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.droitfintech.regulatory.Tenor;
import com.droitfintech.model.ProductMaster;
import com.droitfintech.model.ModelConversionUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table (name="DefaultsInterestRate")
public class DefaultsInterestRate implements DefaultsInterestRateInterface {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.TABLE, generator="TDSS_GEN")
    @Column (name="idDefaultsInterestRate")
    private int idDefaultsInterestRate;

    @ManyToOne
    @JoinColumn(name="idProductMaster")
    private ProductMaster productMaster;

    @Column
    private String currency;

    @Column
    private String floatIndex;

    @Column
    private String indexTenor;

    @Column
    private String floatPayFrequency;

    @Column
    private String fixedPayFrequency;

    @Column
    private String floatDayCount;

    @Column
    private String fixedDayCount;

    @Column
    private String businessDayConvention;

    @Column
    private String holidayCalendars;

    @Column
    private String maturity;

    @Column
    private BigDecimal notional;

    @Column
    private BigDecimal fixedCoupon;

    public int getIdDefaultsInterestRate() {
        return idDefaultsInterestRate;
    }

    /* (non-Javadoc)
     * @see com.droitfintech.tdss.model.defaults.DefaultsInterestRateInterface#getProductMaster()
     */

    public ProductMaster getProductMaster() {
        return productMaster;
    }

    /* (non-Javadoc)
     * @see com.droitfintech.tdss.model.defaults.DefaultsInterestRateInterface#getCurrency()
     */

    public String getCurrency() {
        return currency;
    }


    public String getFloatIndex() {
        return floatIndex;
    }

    public String getIndexTenor() {
        return indexTenor;
    }

    public String getFloatPayFrequency() {
        return floatPayFrequency;
    }

    public String getFixedPayFrequency() {
        return fixedPayFrequency;
    }

    /* (non-Javadoc)
     * @see com.droitfintech.tdss.model.defaults.DefaultsInterestRateInterface#getFloatDayCount()
     */

    public String getFloatDayCount() {
        return floatDayCount;
    }

    /* (non-Javadoc)
     * @see com.droitfintech.tdss.model.defaults.DefaultsInterestRateInterface#getFixedDayCount()
     */

    public String getFixedDayCount() {
        return fixedDayCount;
    }

    /* (non-Javadoc)
     * @see com.droitfintech.tdss.model.defaults.DefaultsInterestRateInterface#getBusinessDayConvention()
     */

    public String getBusinessDayConvention() {
        return businessDayConvention;
    }

    public String getHolidayCalendars() {
        return holidayCalendars;
    }

    public String getMaturity() {
        return maturity;
    }

    /* (non-Javadoc)
     * @see com.droitfintech.tdss.model.defaults.DefaultsInterestRateInterface#getNotional()
     */

    public BigDecimal getNotional() {
        return notional;
    }

    /* (non-Javadoc)
     * @see com.droitfintech.tdss.model.defaults.DefaultsInterestRateInterface#getFixedCoupon()
     */

    public BigDecimal getFixedCoupon() {
        return fixedCoupon;
    }


    @JsonIgnore
    public Tenor getIndexTenorAsTenor() {
        return ModelConversionUtil.makeTenor(this.indexTenor);
    }


    @JsonIgnore
    public Tenor getFloatPayFrequencyAsTenor() {
        return ModelConversionUtil.makeTenor(this.floatPayFrequency);
    }


    @JsonIgnore
    public Tenor getFixedPayFrequencyAsTenor() {
        return ModelConversionUtil.makeTenor(this.fixedPayFrequency);
    }


    @JsonIgnore
    public Set<String> getHolidayCalendarsAsSet() {
        return ModelConversionUtil.makeSet(String.class, this.holidayCalendars);
    }


    @JsonIgnore
    public Tenor getMaturityAsTenor() {
        return ModelConversionUtil.makeTenor(this.maturity);
    }

}
