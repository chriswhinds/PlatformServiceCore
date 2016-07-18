package com.droitfintech.fx;

import java.math.BigDecimal;

public class FxConversion {

    private BigDecimal result;
    private FxQuote quote;

    public FxConversion(BigDecimal result, FxQuote quote) {
        this.result = result;
        this.quote = quote;
    }

    public BigDecimal getResult() {
        return result;
    }

    public FxQuote getQuote() {
        return quote;
    }
}