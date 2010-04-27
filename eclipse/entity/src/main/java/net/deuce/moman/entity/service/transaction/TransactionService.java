package net.deuce.moman.entity.service.transaction;

import java.util.List;

import net.deuce.moman.entity.model.account.Account;
import net.deuce.moman.entity.model.transaction.InternalTransaction;
import net.deuce.moman.entity.service.EntityService;
import net.deuce.moman.util.DataDateRange;

public interface TransactionService extends EntityService<InternalTransaction> {

	public void bindTransferTransactions();
	
	public List<InternalTransaction> getCustomTransactionList();

	public void setCustomTransactionList(List<InternalTransaction> customTransactionList);

	public InternalTransaction findTransactionByExternalId(String id);
	
	public void addExternalTransactionReference(InternalTransaction transaction);
	
	public void removeExternalTransactionReference(InternalTransaction transaction);
	
	public void removeAccountTransactions(Account account);
	
	public List<InternalTransaction> getAccountTransactions(Account account, DataDateRange dateRange, boolean reverse);
	
	public List<InternalTransaction> getAccountTransactions(Account account, boolean reverse);
	
	public List<InternalTransaction> getRegisterTransactions(boolean reverse, boolean allowingTransfers);
	
	public List<InternalTransaction> getUnreconciledTransactions(Account account, boolean reverse);
	
	public void adjustBalances(InternalTransaction transaction, boolean remove);
	
	public InternalTransaction getInitialBalanceTransaction(Account account);
	
	public void addEntity(InternalTransaction transaction, boolean updateBalances);
	
	public void addEntity(InternalTransaction transaction, boolean updateBalances, boolean notify);
	
}
