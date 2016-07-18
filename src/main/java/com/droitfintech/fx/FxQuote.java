package com.droitfintech.fx;

import java.math.BigDecimal;
import java.util.Date;

public class FxQuote {

    private String baseCurrency;
    private String quoteCurrency;
    private BigDecimal rate;
    private Date effectiveDate;

    public FxQuote(String baseCurrency, String quoteCurrency, BigDecimal rate, Date effectiveDate) {
        this.baseCurrency = baseCurrency;
        this.quoteCurrency = quoteCurrency;
        this.rate = rate;
        this.effectiveDate = effectiveDate;
    }

    public FxQuote() {}

    public String getBaseCurrency() {
        return baseCurrency;
    }

    public String getQuoteCurrency() {
        return quoteCurrency;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public Date getEffectiveDate() {
        return effectiveDate;
    }

    public void setBaseCurrency(String baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    public void setQuoteCurrency(String quoteCurrency) {
        this.quoteCurrency = quoteCurrency;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }
}