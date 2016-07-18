package com.droitfintech.services.adeptservice;

import com.droitfintech.clojure.TypeConverter;
import com.droitfintech.dao.EligibilityDAO;
import com.droitfintech.datadictionary.DictionaryTradeAttributeRegistry;
import com.droitfintech.licensing.LicenseService;
import com.droitfintech.marketlogic.MarketLogicService;
import com.droitfintech.model.DynamicDocument;
import com.droitfintech.model.FinMktInfraVersion;
import com.droitfintech.model.TradeContext;
import com.droitfintech.model.TradeDocument;
import com.droitfintech.services.RequestContext;
import com.droitfintech.utils.BucketedMap;
import com.droitfintech.utils.H2DataSourceFactoryImpl;
import com.droitfintech.workflow.repository.FilesystemWorkflowRepositoryService;
import com.droitfintech.workflow.service.WorkflowService;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.sql.DataSource;

/**
 * Created by christopherwhinds on 7/6/16.
 */
public class AdeptServiceCore implements AdeptServiceConstants {

	private WorkflowService workflowService;

	private MarketLogicService marketLogicService;

	private DictionaryTradeAttributeRegistry dictionaryRegistry;

	private LicenseService licenseService;

	private RequestContext defaultRequestDefault = new RequestContext();

	private LocalContainerEntityManagerFactoryBean entityManagerFactoryBean;

	private DataSource dataSource;

	public HashMap getConfigurationProperties() {
		return configurationProperties;
	}

	public void setConfigurationProperties(HashMap configurationProperties) {
		this.configurationProperties = configurationProperties;
	}

	private HashMap configurationProperties;

	/***
	 * Initionalize the Service Core
	 */
	public void initialize(){

		//Get the Adept Service specific  Properties from the yml config
		HashMap serviceProperties = (HashMap)configurationProperties.get("service properties");

		//================
		// Market Logic
		//Set Up h2 DataStore
		dataSource = H2DataSourceFactoryImpl.createDataSource(serviceProperties);
		//Initialize the Hibernate EntityManager
		EntityManager entityManager = createEntityManager();
		EligibilityDAO eligibilityDAO = new EligibilityDAO();
		eligibilityDAO.setEntityManager(entityManager);
		//Set the DOA to the MArketLogic Service
		marketLogicService = new MarketLogicService();
		marketLogicService.setEligibilityDao(eligibilityDAO);

		//================
		//Workflow Manager Iniialize
		workflowService = new WorkflowService();
		FilesystemWorkflowRepositoryService filesystemWorkflowRepositoryService = new FilesystemWorkflowRepositoryService();
		filesystemWorkflowRepositoryService.setClientSourceDir((String)serviceProperties.get(CLIENT_REPOSITORY_LOCATION));
		filesystemWorkflowRepositoryService.setSourceDir((String)serviceProperties.get(DROIT_REPOSITORY_LOCATION));
		workflowService.setRepository(filesystemWorkflowRepositoryService);


	}

	/***
	 * Create the Hibernate EntityManager
	 * @return
     */
	private EntityManager createEntityManager(){
		entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
		entityManagerFactoryBean.setDataSource(this.dataSource);
		HashMap hibernateProps = (HashMap)configurationProperties.get(HIBERNATE_PARMS);
		entityManagerFactoryBean.setPersistenceUnitName( (String)hibernateProps.get(UNIT_NAME));
		entityManagerFactoryBean.setPackagesToScan(PACKAGES_TO_SCAN);
		entityManagerFactoryBean.setJpaDialect( new HibernateJpaDialect());

		HashMap jpaVendorAdapterParms = (HashMap)hibernateProps.get(JPA_VENDOR_ADAPTOR_PARMS);
		HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
		jpaVendorAdapter.setDatabasePlatform(JPA_VENDOR_DB_PLATFORMS);
		jpaVendorAdapter.setShowSql(Boolean.parseBoolean((String)jpaVendorAdapterParms.get(JPA_VENDOR_SHOWSQL)));
		jpaVendorAdapter.setGenerateDdl(Boolean.parseBoolean((String)jpaVendorAdapterParms.get(JPA_VENDOR_GENERATE_DDL)));
		entityManagerFactoryBean.setJpaVendorAdapter(jpaVendorAdapter);

		HashMap jpaParms = (HashMap)hibernateProps.get(JPA_PROPERTIES);
		Properties jpaProperties = new Properties();
		jpaProperties.put(JPA_DIALECT,jpaProperties.get(JPA_DIALECT));
		entityManagerFactoryBean.setJpaProperties(jpaProperties);
		return entityManagerFactoryBean.getNativeEntityManagerFactory().createEntityManager();
	}
	/**
	 * Currently this call is used only by the counterparty validation tool, and so has special logic that's
	 * not intended to be used for any other type of trade processing.
     */
	public Map<String, Map<String, Object>>processTrade(Map trade, Map contraparty, Map counterparty) {


		trade = TypeConverter.convertMap(trade);

		counterparty = TypeConverter.convertMap(counterparty);
		contraparty = TypeConverter.convertMap(contraparty);

		TradeDocument document = new DynamicDocument(trade);
		TradeContext context = dictionaryRegistry.initializeContext(document);
		BucketedMap payload = new BucketedMap();

		String groupId = UUID.randomUUID().toString();
		String decisionId = UUID.randomUUID().toString();
		payload.setValue("metadata", "groupId", groupId);
		payload.setValue("metadata", "decisionId", decisionId);


		payload.setAllValuesForBucket("trade", context);
		payload.setAllValuesForBucket("contraparty", contraparty);
		payload.setAllValuesForBucket("counterparty", counterparty);


		RequestContext.ApplicationType type = defaultRequestDefault.getRequestType();

		for (FinMktInfraVersion.FinMktInfraVersionType t : licenseService.getMarketLogicWhitelist(type)) {
			payload = marketLogicService.evaluateMarketLogic(payload, t);
		}

		//NOTE - MUST VERIFY WHAT TO CALL NEXT
		payload = workflowService.process(defaultRequestDefault, payload);

		return payload.getUnderlyingMap();
	}
}
