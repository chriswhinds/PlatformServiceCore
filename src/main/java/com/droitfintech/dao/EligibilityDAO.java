package com.droitfintech.dao;

import com.droitfintech.exceptions.DroitException;
import com.droitfintech.dao.DefaultsCreditIndex;

import com.droitfintech.model.FinMktInfra;
import com.droitfintech.model.FinMktInfraVersion;
import com.droitfintech.model.FinMktInfraVersion.FinMktInfraVersionType;
import com.droitfintech.model.ProductMaster;


import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;


public class EligibilityDAO {

    private String marketLogicVersion = null;

    public EligibilityDAO() {
        ;
    }

    @PersistenceContext(unitName="TDSS")
    private EntityManager em;

    private EntityManager getEntityManager() {
        return this.em;
    }

    public void setEntityManager(EntityManager m){
        this.em = m;
    }

    public List<FinMktInfraVersion> getAllFinMktInfraVersions() {
        TypedQuery<FinMktInfraVersion> query = em.createQuery(
                "from FinMktInfraVersion",
                FinMktInfraVersion.class);
        return query.getResultList();
    }

    public CentralCounterparty getCentralCounterparty(String shortName) {
        EntityManager entityManager = getEntityManager();
        TypedQuery<CentralCounterparty> query = entityManager.createQuery(
                "from CentralCounterparty where shortName=?1",
                CentralCounterparty.class);
        query.setParameter(1, shortName);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            throw new RuntimeException("No result found for "+shortName, e);
        }
    }


    public Regulator getRegulator(String shortName) {
        EntityManager entityManager = getEntityManager();
        TypedQuery<Regulator> query = entityManager.createQuery(
                "from Regulator where shortName=?1",
                Regulator.class);
        query.setParameter(1, shortName);
        return query.getSingleResult();
    }

    public SwapExecutionFacility getSwapExecutionFacility(String shortName) {
        EntityManager entityManager = getEntityManager();
        TypedQuery<SwapExecutionFacility> query = entityManager.createQuery(
                "from SwapExecutionFacility where shortName=?1",
                SwapExecutionFacility.class);
        query.setParameter(1, shortName);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            throw new RuntimeException("No result found for "+shortName, e);
        }
    }

    public List<CentralCounterparty> getAllCentralCounterparties() {
        EntityManager entityManager = getEntityManager();
        TypedQuery<CentralCounterparty> query = entityManager.createQuery(
                "from CentralCounterparty",
                CentralCounterparty.class);
        return query.getResultList();
    }

    public List<SwapExecutionFacility> getAllSwapExecutionFacilities() {
        EntityManager entityManager = getEntityManager();
        TypedQuery<SwapExecutionFacility> query = entityManager.createQuery(
                "from SwapExecutionFacility",
                SwapExecutionFacility.class);
        return query.getResultList();
    }

    public List<Regulator> getAllRegulators() {
        EntityManager entityManager = getEntityManager();
        TypedQuery<Regulator> query = entityManager.createQuery(
                "from Regulator",
                Regulator.class);
        return query.getResultList();
    }

    public List<ReportingJurisdictionDetails> getAllReportingJurisdictionDetails() {
        return em.createQuery("from ReportingJurisdictionDetails", ReportingJurisdictionDetails.class).getResultList();
    }

    public List<SEFBlockSizePhase> getAllSEFBlockSizePhases() {
        return em.createQuery("from SEFBlockSizePhase", SEFBlockSizePhase.class).getResultList();
    }

    public List<IRCurrencyGroups> getAllIRCurrencyGroups() {
        return em.createQuery("from IRCurrencyGroups ", IRCurrencyGroups.class).getResultList();
    }

    public List<IRBlockRules> getAllIRBlockRules() {
        return em.createQuery("from IRBlockRules", IRBlockRules.class).getResultList();
    }

    public List<SpreadCategories> getAllSpreadCategories() {
        return em.createQuery("from SpreadCategories", SpreadCategories.class).getResultList();
    }

    public List<CreditBlockRules> getAllCreditBlockRules() {
        return em.createQuery("from CreditBlockRules", CreditBlockRules.class).getResultList();
    }

    public List<FXBlockRules> getAllFXBlockRules() {
        return em.createQuery("from FXBlockRules", FXBlockRules.class).getResultList();
    }

    public List<ClearingMandatePhase> getAllClearingMandatePhases() {
        TypedQuery<ClearingMandatePhase> query = em.createQuery("from ClearingMandatePhase", ClearingMandatePhase.class);
        return query.getResultList();
    }

    public <T> List<T> getEntitiesByClass(Class<T> clazz) {
        EntityManager entityManager = getEntityManager();
        Cache cache = entityManager.getEntityManagerFactory().getCache();
        cache.evictAll();
        TypedQuery<T> query = entityManager.createQuery(
                "from "+ clazz.getSimpleName(), clazz);
        return query.getResultList();
    }

    public List<Date> getMATApprovalDates(String shortName) {

        return getVersionDates(shortName, FinMktInfraVersionType.MAT);

    }

    public List<Date> getVersionDates(String shortName, FinMktInfraVersionType feedType) {
        return this.em.createQuery(
                "select distinct v.versionDate from FinMktInfraVersion as v "
                        + "join v.finMktInfra as c where c.shortName=?1 and v.versionType=?2 order by v.versionDate",
                Date.class)
                .setParameter(1, shortName)
                .setParameter(2, feedType.toString())
                .getResultList();
    }

    public FinMktInfraVersion getFinMktInfraVersion(String shortName, Date versionDate, String versionType) {
        EntityManager entityManager = this.getEntityManager();
        TypedQuery<FinMktInfraVersion> query = entityManager.createQuery(
                "select v from FinMktInfraVersion as v join v.finMktInfra as c where c.shortName=?1 and v.versionDate=?2 and v.versionType=?3",
                FinMktInfraVersion.class);
        query.setParameter(1, shortName);
        query.setParameter(2, versionDate, TemporalType.DATE);
        query.setParameter(3, versionType);
        List<FinMktInfraVersion> versions = query.getResultList();
        FinMktInfraVersion res = null;
        for (FinMktInfraVersion v: versions) {
            if (res == null || res.getVersionNumber().doubleValue() < v.getVersionNumber().doubleValue()) {
                res = v;
            }
        }
        return res;
    }

    public List<FinMktInfraVersion> getFinMktInfraVersions(String shortName, String versionType, boolean ascendingByDate) {
        EntityManager entityManager = this.getEntityManager();
        String orderOption = (ascendingByDate) ? "ASC" : "DESC";
        TypedQuery<FinMktInfraVersion> query = entityManager.createQuery(
                "select v from FinMktInfraVersion v join v.finMktInfra as c where c.shortName=?1 and v.versionType=?2 order by v.versionDate "+orderOption,
                FinMktInfraVersion.class);
        query.setParameter(1, shortName);
        query.setParameter(2, versionType);
        return query.getResultList();
    }

    public List<FinMktInfraVersion> getAllListedProductVersions(boolean ascendingByDate) {
        EntityManager entityManager = this.getEntityManager();
        String orderOption = (ascendingByDate) ? "ASC" : "DESC";
        TypedQuery<FinMktInfraVersion> query = entityManager.createQuery(
                "select v from FinMktInfraVersion v where v.versionType=?1 order by v.versionDate " + orderOption,
                FinMktInfraVersion.class);
        query.setParameter(1, FinMktInfraVersionType.ListedProduct.name());
        return query.getResultList();
    }

    public List<FinMktInfraVersion> getAllMATVersions(boolean ascendingByDate) {
        EntityManager entityManager = this.getEntityManager();
        String orderOption = (ascendingByDate) ? "ASC" : "DESC";
        TypedQuery<FinMktInfraVersion> query = entityManager.createQuery(
                "select v from FinMktInfraVersion v where v.versionType=?1 order by v.versionDate " + orderOption,
                FinMktInfraVersion.class);
        query.setParameter(1, FinMktInfraVersionType.MAT.name());
        return query.getResultList();
    }

    public List<ProductMaster> getAllProductMasters() {
        EntityManager entityManager = getEntityManager();
        TypedQuery<ProductMaster> query = entityManager.createQuery(
                "select p from ProductMaster p order by assetClass, baseProduct, subProduct",
                ProductMaster.class);
        return query.getResultList();
    }

    public ProductMaster getProductMaster(String assetClass, String baseProduct, String subProduct) {
        EntityManager entityManager = getEntityManager();
        TypedQuery<ProductMaster> query = null;
        if (subProduct == null) {
            query = entityManager.createQuery(
                    "from ProductMaster where assetClass=?1 and baseProduct=?2 and subProduct is null",
                    ProductMaster.class);
            query.setParameter(1, assetClass).setParameter(2, baseProduct);
        }
        else {
            query = entityManager.createQuery(
                    "from ProductMaster where assetClass=?1 and baseProduct=?2 and subProduct=?3",
                    ProductMaster.class);
            query.setParameter(1, assetClass).setParameter(2, baseProduct).setParameter(3, subProduct);
        }
        ProductMaster product = query.getSingleResult();
        return product;
    }

    public FinMktInfraVersion getOrCreateCCPVersion(String ccpShortName, Date versionDate) {
        return getOrCreateInfraVersion(ccpShortName, versionDate, FinMktInfraVersionType.CCPEligibility.name(), CentralCounterparty.class);
    }

    public FinMktInfraVersion getOrCreateListedProductVersion(String shortName, Date versionDate) {
        return getOrCreateInfraVersion(shortName, versionDate, FinMktInfraVersionType.ListedProduct.name(), SwapExecutionFacility.class);
    }

    public FinMktInfraVersion getOrCreateMATVersion(String shortName, Date versionDate) {
        return getOrCreateInfraVersion(shortName, versionDate, FinMktInfraVersionType.MAT.name(), SwapExecutionFacility.class);
    }

    public FinMktInfraVersion getOrCreateInfraVersion(String shortName, Date versionDate, String versionType,
                                                      Class<? extends FinMktInfra> clazz) {

        FinMktInfraVersion theVersion = null;
        theVersion = this.getFinMktInfraVersion(shortName, versionDate, versionType);
        if (theVersion==null) {
            theVersion = createNewFinMktInfraVersion(shortName, versionDate, versionType, clazz);
        }

        return theVersion;
    }

    public FinMktInfraVersion createNewFinMktInfraVersion(String shortName, Date versionDate, String versionType, Class<? extends FinMktInfra> clazz) {
        FinMktInfraVersion theVersion = null;
        List<FinMktInfraVersion> theVersions = this.getFinMktInfraVersions(shortName, versionType, false);
        if (theVersions.isEmpty()) {
            // We assume that we will find a finmktinfra here.
            // If not, this is a typo, or we need to seed CentralCounterparty correctly; no attempt will be made to
            // auto-correct the situation.
            FinMktInfra infra = null;
            if (clazz.equals(SwapExecutionFacility.class)) {
                infra = this.getSwapExecutionFacility(shortName);
            } else if (clazz.equals(CentralCounterparty.class)) {
                infra = this.getCentralCounterparty(shortName);
            } else {
                throw new RuntimeException("Expected a FinMktInfra concrete class, but got an unrecognized class type "+clazz);
            }
            theVersion = new FinMktInfraVersion(infra, versionDate, getNextVersionNumber(infra));
        } else {
            TreeMap<Date, FinMktInfraVersion> versionsMap = new TreeMap<Date, FinMktInfraVersion>();
            for (FinMktInfraVersion version: theVersions) {
                versionsMap.put(version.getVersionDate(), version);
            }
            FinMktInfraVersion templateVersion = versionsMap.floorEntry(versionDate).getValue();
            FinMktInfra infra = templateVersion.getFinMktInfra();
            theVersion = new FinMktInfraVersion(infra, versionDate, getNextVersionNumber(infra));
        }
        theVersion.setVersionType(versionType);
        this.em.persist(theVersion);
        return theVersion;
    }

    public BigDecimal getNextVersionNumber(FinMktInfra fmi) {
        TypedQuery<BigDecimal> query = this.em.createQuery(
                "select max(v.versionNumber) from FinMktInfraVersion v where v.finMktInfra=?1",
                BigDecimal.class).setParameter(1, fmi);
        BigDecimal result = query.getSingleResult();
        return result==null ? new BigDecimal(1) : new BigDecimal(result.longValue()+1);
    }

    public ProductMaster getOrCreateProduct(String assetClass, String baseProduct, String subProduct) {
        EntityManager entityManager = getEntityManager();
        ProductMaster product = null;
        // Null subProduct if subProduct is an empty string ... TODO: uncomment at some point and standardize!!!
        subProduct = (subProduct != null && ! subProduct.trim().equals("")) ? subProduct : null;
        try {
            product = getProductMaster(assetClass, baseProduct, subProduct);
        } catch (NoResultException e) {
            // Only create a new product if we see a value for assetClass
            if (assetClass != null && !assetClass.trim().equals("")) {
                product = new ProductMaster(assetClass, baseProduct, subProduct);
                entityManager.persist(product);
            }
        }
        return product;
    }

    public List<Object[]> getDistinctPredicateAttributesAndTypes() {
        return this.em.createQuery(
                "select distinct p.tradeAttribute, p.comparisonDataType from AbstractComparisonPredicate p",
                Object[].class
        ).getResultList();
    }

    /**
     * Returns the DefaultsInterestRate object given the product and currency.
     * If none found, returns null.
     * If more than one found, returns an arbitrary single object from the list.
     *
     * @param product
     * @param currency
     * @return
     */
    public DefaultsInterestRate getIrDefaultsByCurrency(ProductMaster product, String currency) {
        DroitException.assertThat(product!=null && currency!=null,
                "Inputs to getIrDefaultsByCurrency cannot be null");
        List<DefaultsInterestRate> res = this.em.createQuery(
                "from DefaultsInterestRate where productMaster=?1 and currency=?2",
                DefaultsInterestRate.class)
                .setParameter(1, product)
                .setParameter(2, currency)
                .getResultList();
        if (res.isEmpty()) {
            return null;
        }
        return res.get(0);
    }

    public DefaultsInterestRate getIrDefaultsByCurrencyIndex(ProductMaster product, String currency, String index) {
        DroitException.assertThat(product!=null && currency!=null,
                "Inputs to getIrDefaultsByCurrency cannot be null");
        List<DefaultsInterestRate> res = this.em.createQuery(
                "from DefaultsInterestRate where productMaster=?1 and currency=?2 and floatIndex=?3",
                DefaultsInterestRate.class)
                .setParameter(1, product)
                .setParameter(2, currency)
                .setParameter(3, index)
                .getResultList();
        if (res.isEmpty()) {
            return null;
        }
        return res.get(0);
    }

    public List<String> getCurrencyListByProduct(ProductMaster product) {
        TypedQuery<String> query = this.em.createQuery(
                "select distinct currency from DefaultsInterestRate where productMaster=?1 order by currency",
                String.class);
        query.setParameter(1, product);
        return query.getResultList();

    }

    public List<String> getCurrencyListByProductAndIndex(ProductMaster product, String index) {
        TypedQuery<String> query = this.em.createQuery(
                "select distinct currency from DefaultsInterestRate where productMaster=?1 and floatIndex=?2 order by currency",
                String.class);
        query.setParameter(1, product);
        query.setParameter(2, index);

        return query.getResultList();

    }

    public List<String> getIndexListByProduct(ProductMaster product) {
        TypedQuery<String> query = this.em.createQuery(
                "select distinct d.floatIndex from DefaultsInterestRate d where productMaster=?1 order by d.floatIndex",
                String.class);
        query.setParameter(1, product);
        return query.getResultList();

    }

    public List<String> getIndexListByProductAndCurrency(ProductMaster product, String currency) {
        TypedQuery<String> query = this.em.createQuery(
                "select distinct d.floatIndex from DefaultsInterestRate d where productMaster=?1 and currency=?2 order by d.floatIndex",
                String.class);
        query.setParameter(1, product);
        query.setParameter(2, currency);

        return query.getResultList();

    }

    public List<String> getSeriesListByCreditIndex(String index) {
        TypedQuery<String> query = this.em.createQuery(
                "select cast(series as string) from DefaultsCreditIndex where indexLabel=?1 order by series desc",
                String.class);
        query.setParameter(1, index);
        return query.getResultList();

    }

    /**
     * Returns DefaulstCreditIndex given the index label and series.
     * If none found, returns null.
     * If more than one found, returns an arbitrary single object from the matching list.
     *
     * @param indexLabel
     * @param series
     * @return
     */
    public DefaultsCreditIndex getCreditIndexDefaultsByIndexSeriesTerm(String indexLabel, Integer series) {
        DroitException.assertThat(indexLabel!=null,
                "Cannot pass null index to getCreditIndexDefaultsByIndexSeriesTerm");
        List<DefaultsCreditIndex> res = null;
        if (series!=null && series!=-1) {
            res = this.em.createQuery(
                    "from DefaultsCreditIndex where indexLabel=?1 and series=?2",
                    DefaultsCreditIndex.class)
                    .setParameter(1, indexLabel)
                    .setParameter(2, series)
                    .getResultList();
        } else {
            res = this.em.createQuery(
                    "from DefaultsCreditIndex where indexLabel=?1 and onTheRun=1",
                    DefaultsCreditIndex.class)
                    .setParameter(1, indexLabel)
                    .getResultList();
        }
        if (res.isEmpty()) {
            return null;
        }
        return res.get(0);
    }

    public FinMktInfra getFinMktInfraByShortName(String shortName) {
        TypedQuery<FinMktInfra> query = this.em.createQuery(
                "select f from FinMktInfra f where f.shortName=?1",
                FinMktInfra.class);
        query.setParameter(1, shortName);
        try {
            return query.getSingleResult();
        } catch (NonUniqueResultException e) {
            throw new DroitException(
                    "Looks like the FinMKtInfra shortname " + shortName
                            + " is non-unique ... you need to resolve this.", e);
        }
    }

    public List<TradeRule> getAllRulesByVenueAndType(String shortName, FinMktInfraVersionType type) {
        TypedQuery<TradeRule> query = this.em.createQuery(
                "select distinct r from TradeRule r join r.ruleEntity as e JOIN FETCH r.tradeKey JOIN FETCH r.tradeTest where e.shortName=?1 and r.ruleType=?2 ",
                TradeRule.class);
        query.setParameter(1, shortName);
        query.setParameter(2, type);
        return query.getResultList();
    }

    public List<TradeRule> getAllRules() {
        return this.em.createQuery(
                "select r from TradeRule r",
                TradeRule.class).getResultList();
    }

    public List<TradeRule> getAllRulesByVenueTypeAndEffectiveDate(String shortName, FinMktInfraVersionType type,
                                                                  Date effectiveDate) {
        DroitException.assertThat(shortName!=null && type!=null,
                "shortName and type cannot be null for getAllRulesByVenueTypeAndEffectiveDate()");

        if (effectiveDate == null) {
            return getAllRulesByVenueAndType(shortName, type);
        }
        TypedQuery<TradeRule> query = this.em.createQuery(
                "select r from TradeRule r join r.ruleEntity as e where e.shortName=?1 and r.ruleType=?2"
                        + " and r.effectiveStart <= ?3 and (r.effectiveEnd is null or r.effectiveEnd >= ?3)",
                TradeRule.class);
        query.setParameter(1, shortName);
        query.setParameter(2, type);
        query.setParameter(3, effectiveDate);

        return query.getResultList();
    }

    public List<? extends FinMktInfra> getVenuesByType(FinMktInfraVersionType type) {
        if (type.equals(FinMktInfraVersionType.CCPEligibility)) {
            return this.getAllCentralCounterparties();
        } else if (type.equals(FinMktInfraVersionType.ListedProduct)) {
            return this.getAllSwapExecutionFacilities();
        } else if (type.equals(FinMktInfraVersionType.MAT) || type.equals(FinMktInfraVersionType.MandClearing)) {
            return this.getAllRegulators();
        } else {
            throw new DroitException("Unimplemented type for getVenuesByType(): " + type);
        }
    }

    public void persist(Object o) {
        this.getEntityManager().persist(o);
    }

    public void flush() {
        this.em.flush();
    }

    public void remove(Object o) {
        this.getEntityManager().remove(o);
    }

    public <T> T find(Class<T> clazz, Object key) {
        return this.getEntityManager().find(clazz, key);
    }

    public void refresh(Object o) {
        this.getEntityManager().refresh(o);
    }

    public List<BasisCurrencyRules> getAllBasisCurrencyRules() {
        return this.em.createQuery("from BasisCurrencyRules", BasisCurrencyRules.class)
                .getResultList();
    }

    public Object merge(Object o) {
        return this.em.merge(o);
    }

    public void deleteAllTradeRules() {
        this.em.createQuery("delete from TradeRule r").executeUpdate();
    }

    public void deleteAllTradeRulesByVenueNameAndType(String infraShortName, FinMktInfraVersionType type) {
        FinMktInfra f = this.getFinMktInfraByShortName(infraShortName);
        deleteAllTradeRulesByVenueAndType(f, type);
    }

    public void deleteAllTradeRulesByVenueAndType(FinMktInfra infra, FinMktInfraVersionType type) {
        this.em.createQuery("delete from TradeRule r where r.ruleEntity=?1 and r.ruleType=?2")
                .setParameter(1, infra).setParameter(2, type).executeUpdate();
    }

    public void deleteTradeRule(Integer id) {
        DroitException.assertThat(id != null, "Trade Rule id must not be null.");
        this.em.createQuery("delete from TradeRule r where r.idTradeRule=?1")
                .setParameter(1, id).executeUpdate();
    }

    public List<String> getTransactionTypeListByProduct(ProductMaster product) {
        TypedQuery<String> query = this.em.createQuery(
                "select distinct transactionType from ProductMasterExtension where productMaster=?1 order by transactionType",
                String.class);
        query.setParameter(1, product);
        return query.getResultList();
    }

    public List<Object[]> getRuleCounts() {
        String q = "select ruleType, shortName, count(*) from traderule"
                + " join finmktinfra on traderule.idFinMktInfra = finmktinfra.idfinMktInfra"
                + " group by shortName, ruleType;";
        return this.em.createNativeQuery(q).getResultList();
    }

    public List<MATCreditReport> getMATCreditReport() {
        List<MATCreditReport> res = this.em.createQuery(
                "from MATCreditReport",
                MATCreditReport.class)
                .getResultList();
        return res;
    }

    public List<PackageTradesRules> getPackageTradesRules() {
        List<PackageTradesRules> res = this.em.createQuery(
                "from PackageTradesRules",
                PackageTradesRules.class)
                .getResultList();
        return res;
    }

    public List<MATRatesReport> getMATRatesReport() {
        List<MATRatesReport> res = this.em.createQuery(
                "from MATRatesReport",
                MATRatesReport.class)
                .getResultList();
        return res;
    }

    public List<EMIRClearingCredit> getEMIRClearingCreditReport() {
        List<EMIRClearingCredit> res = this.em.createQuery(
                "from EMIRClearingCredit",
                EMIRClearingCredit.class)
                .getResultList();
        return res;
    }

    public List<EMIRClearingRates> getEMIRClearingRatesReport() {
        List<EMIRClearingRates> res = this.em.createQuery(
                "from EMIRClearingRates",
                EMIRClearingRates.class)
                .getResultList();
        return res;
    }

    public List<String> getCreditIndexListByProduct(ProductMaster product) {
        TypedQuery<String> query = this.em.createQuery(
                "select distinct indexLabel from DefaultsCreditIndex where subProduct=?1 order by indexLabel",
                String.class);
        query.setParameter(1, product.getSubProduct());
        return query.getResultList();

    }

    public String getMarketLogicVersion() {
        if (this.marketLogicVersion == null) {
            synchronized (this) {
                if (this.marketLogicVersion == null) {
                    this.marketLogicVersion = doGetMarketLogicVersion();
                }
            }
        }

        return this.marketLogicVersion;
    }

    private String doGetMarketLogicVersion() {
        Query query = this.em.createNativeQuery("select id from version");
        return (String) query.getSingleResult();
    }

    public String getCreditIndexByTransactionType(String transactionType) {
        TypedQuery<String> query = this.em.createQuery(
                "select distinct indexLabel from DefaultsCreditIndex where transactionType=?1",
                String.class);
        query.setParameter(1, transactionType);
        return query.getResultList().get(0);
    }
}
