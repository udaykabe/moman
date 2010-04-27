package net.deuce.moman.entity.service.fi;

import java.util.Date;
import java.util.List;

import net.deuce.moman.entity.model.account.Account;
import net.deuce.moman.entity.model.fi.FinancialInstitution;
import net.deuce.moman.entity.service.EntityService;
import net.deuce.moman.entity.service.transaction.TransactionFetchResult;

public interface FinancialInstitutionService extends EntityService<FinancialInstitution> {

	public boolean doesFinancialInstitutionExistByName(String name);
	
	public FinancialInstitution getFinancialInstitutionByName(String name);
	
	public void addEntity(FinancialInstitution financialInstitution);
	
	public void removeFinancialInstitution(FinancialInstitution financialInstitution);
	
	public TransactionFetchResult fetchTransactions(Account account, Date startDate, Date endDate) throws Exception;
	
	public List<Account> getAvailableAccounts(FinancialInstitution financialInstitution,
			String username, String password) throws Exception;
	
}
