package com.droitfintech.model;

/**
 * Created by christopherwhinds on 7/8/16.
 */

import com.droitfintech.exceptions.DroitException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Set;

import java.util.Collection;

import com.droitfintech.exceptions.DroitException;
import com.droitfintech.model.TradeAttribute.AttributeType;
import com.droitfintech.model.ValidationException;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public abstract class Validator {

    private static Logger log = LoggerFactory.getLogger(Validator.class);

    public final boolean validate(String attr, Object value) {

        boolean answer = false;

        log.trace("Attempting to validate attribute '{}' with value '{}' using instance of {}...",
                attr, value, this.getClass().getSimpleName());

        try {
            answer = this.doValidate(attr, value);
        } catch (ValidationException e) {

            log.trace("NOT valid : attribute '{}' with value '{}' using instance of {}, e",
                    attr, value, this.getClass().getSimpleName());
            throw e;

        }

        log.trace("Attribute '{}' with value '{}' using instance of {} valid? {}",
                attr, value, this.getClass().getSimpleName(), answer);

        return answer;

    }

    public abstract boolean doValidate(String attr, Object value);

    private static Multimap<AttributeType, Validator> _validators;

    public static boolean validate(AttributeType type, String attr, Object value)
            throws ValidationException {
        Collection<Validator> validators = getValidators().get(type);
        for (Validator validator : validators) {
            if (!validator.validate(attr, value)) {
                // Usually we expect the validator to throw a
                // ValidationException,
                // so we probably will not get to this point in the code.
                return false;
            }
        }
        return true;
    }

    public static Set<Object> enumerate(AttributeType type) {

        Collection<Validator> validators = getValidators().get(type);

        for (Validator validator : validators) {
            if (validator instanceof EnumerationValidator) {
                return ((EnumerationValidator) validator).getEnumeration();
            }
        }

        throw new DroitException("Enumeration not supported for " + type);
    }

    private static Multimap<AttributeType, Validator> getValidators() {

        if (_validators == null) {
            initValidators();
        }
        return _validators;
    }

    private synchronized static void initValidators() {

        if (_validators != null) {
            return;
        }

        _validators = HashMultimap.create();
        _validators.put(AttributeType.BlockEntryType, new EnumerationValidator( AttributeType.BlockEntryType));
        _validators.put(AttributeType.BusinessCenter, new EnumerationValidator( AttributeType.BusinessCenter));
        _validators.put(AttributeType.BusinessDayConvention, new EnumerationValidator(AttributeType.BusinessDayConvention));
        _validators.put(AttributeType.CompoundingMethod, new EnumerationValidator(AttributeType.CompoundingMethod));
        _validators.put(AttributeType.CountryCode, new EnumerationValidator( AttributeType.CountryCode));
        _validators.put(AttributeType.Currency, new EnumerationValidator( AttributeType.Currency));
        _validators.put(AttributeType.DayCountFraction, new EnumerationValidator(AttributeType.DayCountFraction));
        _validators.put(AttributeType.DayOfWeek, new EnumerationValidator( AttributeType.DayOfWeek));
        _validators.put(AttributeType.DayType, new EnumerationValidator( AttributeType.DayType));
        _validators.put(AttributeType.EarlyTerminationType, new EnumerationValidator(AttributeType.EarlyTerminationType));
        _validators.put(AttributeType.ExecutionStyle, new EnumerationValidator( AttributeType.ExecutionStyle));
        _validators.put(AttributeType.Family, new EnumerationValidator( AttributeType.Family));
        _validators.put(AttributeType.FloatLegIndex, new EnumerationValidator( AttributeType.FloatLegIndex));
        _validators.put(AttributeType.IndexTenor, new EnumerationValidator(AttributeType.IndexTenor));
        _validators.put(AttributeType.InflationInterpolationMethod, new EnumerationValidator( AttributeType.InflationInterpolationMethod));
        _validators.put(AttributeType.InflationSource, new EnumerationValidator(AttributeType.InflationSource));
        _validators.put(AttributeType.LegType, new EnumerationValidator( AttributeType.LegType));
        _validators.put(AttributeType.OptionStyle, new EnumerationValidator( AttributeType.OptionStyle));
        _validators.put(AttributeType.PayRelativeTo, new EnumerationValidator( AttributeType.PayRelativeTo));
        _validators.put(AttributeType.ProductStructure, new EnumerationValidator(AttributeType.ProductStructure));
        _validators.put(AttributeType.ProductMasterType, new EnumerationValidator(AttributeType.ProductMasterType));
        _validators.put(AttributeType.ProductVariant, new EnumerationValidator( AttributeType.ProductVariant));
        _validators.put(AttributeType.RollConvention, new EnumerationValidator( AttributeType.RollConvention));
        _validators.put(AttributeType.SettlementMethod, new EnumerationValidator(AttributeType.SettlementMethod));
        _validators.put(AttributeType.StubType, new EnumerationValidator( AttributeType.StubType));
        _validators.put(AttributeType.TransactionType, new EnumerationValidator(AttributeType.TransactionType));
        _validators.put(AttributeType.PackageLegTypeType, new EnumerationValidator(AttributeType.PackageLegTypeType));
        _validators.put(AttributeType.ExecutionTypeType, new EnumerationValidator(AttributeType.ExecutionTypeType));
        _validators.put(AttributeType.TenorType, new TenorValidator());

    }
}
