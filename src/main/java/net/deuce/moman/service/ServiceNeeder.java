package net.deuce.moman.service;

import net.deuce.moman.account.model.AccountFactory;
import net.deuce.moman.account.service.AccountService;
import net.deuce.moman.allocation.model.AllocationFactory;
import net.deuce.moman.allocation.model.AllocationSetFactory;
import net.deuce.moman.allocation.service.AllocationSetService;
import net.deuce.moman.envelope.model.EnvelopeFactory;
import net.deuce.moman.envelope.service.EnvelopeService;
import net.deuce.moman.fi.model.FinancialInstitutionFactory;
import net.deuce.moman.fi.service.FinancialInstitutionService;
import net.deuce.moman.income.model.IncomeFactory;
import net.deuce.moman.income.service.IncomeService;
import net.deuce.moman.payee.model.PayeeFactory;
import net.deuce.moman.payee.service.PayeeService;
import net.deuce.moman.rule.model.RuleFactory;
import net.deuce.moman.rule.service.TransactionRuleService;
import net.deuce.moman.spring.BeanContainer;
import net.deuce.moman.transaction.model.RepeatingTransactionFactory;
import net.deuce.moman.transaction.model.TransactionFactory;
import net.deuce.moman.transaction.service.ImportService;
import net.deuce.moman.transaction.service.RepeatingTransactionService;
import net.deuce.moman.transaction.service.TransactionService;

public class ServiceNeeder {
	
	private static ServiceNeeder serviceNeeder = new ServiceNeeder();
	
	public static ServiceNeeder instance() { return serviceNeeder; }
	
	private AccountService accountService;
	private EnvelopeService envelopeService;
	private ImportService importService;
	private IncomeService incomeService;
	private FinancialInstitutionService financialInstitutionService;
	private ServiceContainer serviceContainer;
	private TransactionService transactionService;
	private TransactionRuleService transactionRuleService;
	private AllocationSetService allocationSetService;
	private PayeeService payeeService;
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
	
	protected ServiceNeeder() {
		try {
		accountService = (AccountService)BeanContainer.instance().getBean(
				"accountService", AccountService.class);
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
		serviceContainer = (ServiceContainer)BeanContainer.instance().getBean(
				"serviceContainer", ServiceContainer.class);
		
		} catch (Throwable e) {
			e.printStackTrace();
		}
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

	public ServiceContainer getServiceContainer() {
		return serviceContainer;
	}

	public void setServiceContainer(ServiceContainer serviceContainer) {
		this.serviceContainer = serviceContainer;
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
	
}
