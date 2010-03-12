package net.deuce.moman.entity;

import net.deuce.moman.entity.model.account.AccountBuilder;
import net.deuce.moman.entity.model.account.AccountFactory;
import net.deuce.moman.entity.model.allocation.AllocationFactory;
import net.deuce.moman.entity.model.allocation.AllocationSetBuilder;
import net.deuce.moman.entity.model.allocation.AllocationSetFactory;
import net.deuce.moman.entity.model.envelope.EnvelopeBuilder;
import net.deuce.moman.entity.model.envelope.EnvelopeFactory;
import net.deuce.moman.entity.model.fi.FinancialInstitutionBuilder;
import net.deuce.moman.entity.model.fi.FinancialInstitutionFactory;
import net.deuce.moman.entity.model.income.IncomeBuilder;
import net.deuce.moman.entity.model.income.IncomeFactory;
import net.deuce.moman.entity.model.payee.PayeeBuilder;
import net.deuce.moman.entity.model.payee.PayeeFactory;
import net.deuce.moman.entity.model.rule.RuleFactory;
import net.deuce.moman.entity.model.rule.TransactionRuleBuilder;
import net.deuce.moman.entity.model.transaction.RepeatingTransactionBuilder;
import net.deuce.moman.entity.model.transaction.RepeatingTransactionFactory;
import net.deuce.moman.entity.model.transaction.TransactionBuilder;
import net.deuce.moman.entity.model.transaction.TransactionFactory;
import net.deuce.moman.entity.service.ServiceManager;
import net.deuce.moman.entity.service.account.AccountService;
import net.deuce.moman.entity.service.allocation.AllocationSetService;
import net.deuce.moman.entity.service.envelope.EnvelopeService;
import net.deuce.moman.entity.service.fi.FinancialInstitutionService;
import net.deuce.moman.entity.service.income.IncomeService;
import net.deuce.moman.entity.service.payee.PayeeService;
import net.deuce.moman.entity.service.preference.PreferenceService;
import net.deuce.moman.entity.service.rule.TransactionRuleService;
import net.deuce.moman.entity.service.transaction.ImportService;
import net.deuce.moman.entity.service.transaction.RepeatingTransactionService;
import net.deuce.moman.entity.service.transaction.TransactionService;
import net.deuce.moman.entity.spring.BeanContainer;

public class ServiceProvider {
	
	private static ServiceProvider serviceProvider = new ServiceProvider();
	
	public static ServiceProvider instance() { return serviceProvider; }
	
	private AccountService accountService;
	private EnvelopeService envelopeService;
	private ImportService importService;
	private IncomeService incomeService;
	private FinancialInstitutionService financialInstitutionService;
	private ServiceManager serviceManager;
	private TransactionService transactionService;
	private TransactionRuleService transactionRuleService;
	private AllocationSetService allocationSetService;
	private PayeeService payeeService;
	private PreferenceService preferenceService;
	private RepeatingTransactionService repeatingTransactionService;
	
	private AccountFactory accountFactory;
	private EnvelopeFactory envelopeFactory;
	private IncomeFactory incomeFactory;
	private FinancialInstitutionFactory financialInstitutionFactory;
	private TransactionFactory transactionFactory;
	private AllocationSetFactory allocationSetFactory;
	private AllocationFactory allocationFactory;
	private RuleFactory ruleFactory;
	private PayeeFactory payeeFactory;
	private RepeatingTransactionFactory repeatingTransactionFactory;
	
	private AccountBuilder accountBuilder;
	private EnvelopeBuilder envelopeBuilder;
	private AllocationSetBuilder allocationSetBuilder;
	private FinancialInstitutionBuilder financialInstitutionBuilder;
	private IncomeBuilder incomeBuilder;
	private PayeeBuilder payeeBuilder;
	private TransactionRuleBuilder transactionRuleBuilder;
	private RepeatingTransactionBuilder repeatingTransactionBuilder;
	private TransactionBuilder transactionBuilder;
	
	protected ServiceProvider() {
		try {
		accountService = (AccountService)BeanContainer.instance().getBean(
				"accountService", AccountService.class);
		serviceManager = (ServiceManager)BeanContainer.instance().getBean(
				"serviceManager", ServiceManager.class);
		envelopeService = (EnvelopeService)BeanContainer.instance().getBean(
				"envelopeService", EnvelopeService.class);
		importService = (ImportService)BeanContainer.instance().getBean(
				"importService", ImportService.class);
		incomeService = (IncomeService)BeanContainer.instance().getBean(
				"incomeService", IncomeService.class);
		financialInstitutionService = (FinancialInstitutionService)BeanContainer.instance().getBean(
				"financialInstitutionService", FinancialInstitutionService.class);
		transactionService = (TransactionService)BeanContainer.instance().getBean(
				"transactionService", TransactionService.class);
		transactionRuleService = (TransactionRuleService)BeanContainer.instance().getBean(
				"transactionRuleService", TransactionRuleService.class);
		payeeService = (PayeeService)BeanContainer.instance().getBean(
				"payeeService", PayeeService.class);
		allocationSetService = (AllocationSetService)BeanContainer.instance().getBean(
				"allocationSetService", AllocationSetService.class);
		repeatingTransactionService = (RepeatingTransactionService)BeanContainer.instance().getBean(
				"repeatingTransactionService", RepeatingTransactionService.class);
		preferenceService = (PreferenceService)BeanContainer.instance().getBean(
				"preferenceService", PreferenceService.class);
		accountFactory = (AccountFactory)BeanContainer.instance().getBean(
				"accountFactory", AccountFactory.class);
		envelopeFactory = (EnvelopeFactory)BeanContainer.instance().getBean(
				"envelopeFactory", EnvelopeFactory.class);
		incomeFactory = (IncomeFactory)BeanContainer.instance().getBean(
				"incomeFactory", IncomeFactory.class);
		financialInstitutionFactory = (FinancialInstitutionFactory)BeanContainer.instance().getBean(
				"financialInstitutionFactory", FinancialInstitutionFactory.class);
		transactionFactory = (TransactionFactory)BeanContainer.instance().getBean(
				"transactionFactory", TransactionFactory.class);
		ruleFactory = (RuleFactory)BeanContainer.instance().getBean(
				"ruleFactory", RuleFactory.class);
		payeeFactory = (PayeeFactory)BeanContainer.instance().getBean(
				"payeeFactory", PayeeFactory.class);
		allocationSetFactory = (AllocationSetFactory)BeanContainer.instance().getBean(
				"allocationSetFactory", AllocationSetFactory.class);
		allocationFactory = (AllocationFactory)BeanContainer.instance().getBean(
				"allocationFactory", AllocationFactory.class);
		repeatingTransactionFactory = (RepeatingTransactionFactory)BeanContainer.instance().getBean(
				"repeatingTransactionFactory", RepeatingTransactionFactory.class);
		
		accountBuilder = (AccountBuilder)BeanContainer.instance().getBean(
				"accountBuilder", AccountBuilder.class);
		envelopeBuilder = (EnvelopeBuilder)BeanContainer.instance().getBean(
				"envelopeBuilder", EnvelopeBuilder.class);
		allocationSetBuilder = (AllocationSetBuilder)BeanContainer.instance().getBean(
				"allocationSetBuilder", AllocationSetBuilder.class);
		financialInstitutionBuilder = (FinancialInstitutionBuilder)BeanContainer.instance().getBean(
				"financialInstitutionBuilder", FinancialInstitutionBuilder.class);
		incomeBuilder = (IncomeBuilder)BeanContainer.instance().getBean(
				"incomeBuilder", IncomeBuilder.class);
		payeeBuilder = (PayeeBuilder)BeanContainer.instance().getBean(
				"payeeBuilder", PayeeBuilder.class);
		transactionRuleBuilder = (TransactionRuleBuilder)BeanContainer.instance().getBean(
				"transactionRuleBuilder", TransactionRuleBuilder.class);
		repeatingTransactionBuilder = (RepeatingTransactionBuilder)BeanContainer.instance().getBean(
				"repeatingTransactionBuilder", RepeatingTransactionBuilder.class);
		transactionBuilder = (TransactionBuilder)BeanContainer.instance().getBean(
				"transactionBuilder", TransactionBuilder.class);
		
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	public PreferenceService getPreferenceService() {
		return preferenceService;
	}

	public void setPreferenceService(
			PreferenceService preferenceService) {
		this.preferenceService = preferenceService;
	}
	public RepeatingTransactionService getRepeatingTransactionService() {
		return repeatingTransactionService;
	}

	public void setRepeatingTransactionService(
			RepeatingTransactionService repeatingTransactionService) {
		this.repeatingTransactionService = repeatingTransactionService;
	}

	public RepeatingTransactionFactory getRepeatingTransactionFactory() {
		return repeatingTransactionFactory;
	}

	public void setRepeatingTransactionFactory(
			RepeatingTransactionFactory repeatingTransactionFactory) {
		this.repeatingTransactionFactory = repeatingTransactionFactory;
	}

	public AccountService getAccountService() {
		return accountService;
	}

	public void setAccountService(AccountService accountService) {
		this.accountService = accountService;
	}

	public EnvelopeService getEnvelopeService() {
		return envelopeService;
	}

	public void setEnvelopeService(EnvelopeService envelopeService) {
		this.envelopeService = envelopeService;
	}

	public ImportService getImportService() {
		return importService;
	}

	public void setImportService(ImportService importService) {
		this.importService = importService;
	}

	public IncomeService getIncomeService() {
		return incomeService;
	}

	public void setIncomeService(IncomeService incomeService) {
		this.incomeService = incomeService;
	}

	public FinancialInstitutionService getFinancialInstitutionService() {
		return financialInstitutionService;
	}

	public void setFinancialInstitutionService(
			FinancialInstitutionService financialInstitutionService) {
		this.financialInstitutionService = financialInstitutionService;
	}

	public ServiceManager getServiceManager() {
		return serviceManager;
	}

	public void setServiceManager(ServiceManager serviceManager) {
		this.serviceManager = serviceManager;
	}

	public TransactionService getTransactionService() {
		return transactionService;
	}

	public void setTransactionService(TransactionService transactionService) {
		this.transactionService = transactionService;
	}

	public TransactionRuleService getTransactionRuleService() {
		return transactionRuleService;
	}

	public void setTransactionRuleService(
			TransactionRuleService transactionRuleService) {
		this.transactionRuleService = transactionRuleService;
	}

	public AccountFactory getAccountFactory() {
		return accountFactory;
	}

	public void setAccountFactory(AccountFactory accountFactory) {
		this.accountFactory = accountFactory;
	}

	public EnvelopeFactory getEnvelopeFactory() {
		return envelopeFactory;
	}

	public void setEnvelopeFactory(EnvelopeFactory envelopeFactory) {
		this.envelopeFactory = envelopeFactory;
	}

	public IncomeFactory getIncomeFactory() {
		return incomeFactory;
	}

	public void setIncomeFactory(IncomeFactory incomeFactory) {
		this.incomeFactory = incomeFactory;
	}

	public FinancialInstitutionFactory getFinancialInstitutionFactory() {
		return financialInstitutionFactory;
	}

	public void setFinancialInstitutionFactory(
			FinancialInstitutionFactory financialInstitutionFactory) {
		this.financialInstitutionFactory = financialInstitutionFactory;
	}

	public TransactionFactory getTransactionFactory() {
		return transactionFactory;
	}

	public void setTransactionFactory(TransactionFactory transactionFactory) {
		this.transactionFactory = transactionFactory;
	}

	public RuleFactory getRuleFactory() {
		return ruleFactory;
	}

	public void setRuleFactory(RuleFactory ruleFactory) {
		this.ruleFactory = ruleFactory;
	}

	public PayeeService getPayeeService() {
		return payeeService;
	}

	public void setPayeeService(PayeeService payeeService) {
		this.payeeService = payeeService;
	}

	public PayeeFactory getPayeeFactory() {
		return payeeFactory;
	}

	public void setPayeeFactory(PayeeFactory payeeFactory) {
		this.payeeFactory = payeeFactory;
	}

	public AllocationSetService getAllocationSetService() {
		return allocationSetService;
	}

	public void setAllocationSetService(AllocationSetService allocationSetService) {
		this.allocationSetService = allocationSetService;
	}

	public AllocationSetFactory getAllocationSetFactory() {
		return allocationSetFactory;
	}

	public void setAllocationSetFactory(AllocationSetFactory allocationSetFactory) {
		this.allocationSetFactory = allocationSetFactory;
	}

	public AllocationFactory getAllocationFactory() {
		return allocationFactory;
	}

	public void setAllocationFactory(AllocationFactory allocationFactory) {
		this.allocationFactory = allocationFactory;
	}

	public AccountBuilder getAccountBuilder() {
		return accountBuilder;
	}

	public void setAccountBuilder(AccountBuilder accountBuilder) {
		this.accountBuilder = accountBuilder;
	}

	public EnvelopeBuilder getEnvelopeBuilder() {
		return envelopeBuilder;
	}

	public void setEnvelopeBuilder(EnvelopeBuilder envelopeBuilder) {
		this.envelopeBuilder = envelopeBuilder;
	}

	public AllocationSetBuilder getAllocationSetBuilder() {
		return allocationSetBuilder;
	}

	public void setAllocationSetBuilder(AllocationSetBuilder allocationSetBuilder) {
		this.allocationSetBuilder = allocationSetBuilder;
	}

	public FinancialInstitutionBuilder getFinancialInstitutionBuilder() {
		return financialInstitutionBuilder;
	}

	public void setFinancialInstitutionBuilder(
			FinancialInstitutionBuilder financialInstitutionBuilder) {
		this.financialInstitutionBuilder = financialInstitutionBuilder;
	}

	public IncomeBuilder getIncomeBuilder() {
		return incomeBuilder;
	}

	public void setIncomeBuilder(IncomeBuilder incomeBuilder) {
		this.incomeBuilder = incomeBuilder;
	}

	public PayeeBuilder getPayeeBuilder() {
		return payeeBuilder;
	}

	public void setPayeeBuilder(PayeeBuilder payeeBuilder) {
		this.payeeBuilder = payeeBuilder;
	}

	public TransactionRuleBuilder getTransactionRuleBuilder() {
		return transactionRuleBuilder;
	}

	public void setTransactionRuleBuilder(
			TransactionRuleBuilder transactionRuleBuilder) {
		this.transactionRuleBuilder = transactionRuleBuilder;
	}

	public RepeatingTransactionBuilder getRepeatingTransactionBuilder() {
		return repeatingTransactionBuilder;
	}

	public void setRepeatingTransactionBuilder(
			RepeatingTransactionBuilder repeatingTransactionBuilder) {
		this.repeatingTransactionBuilder = repeatingTransactionBuilder;
	}

	public TransactionBuilder getTransactionBuilder() {
		return transactionBuilder;
	}

	public void setTransactionBuilder(TransactionBuilder transactionBuilder) {
		this.transactionBuilder = transactionBuilder;
	}
	
}
