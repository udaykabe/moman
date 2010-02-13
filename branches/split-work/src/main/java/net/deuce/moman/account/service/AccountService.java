package net.deuce.moman.account.service;

import java.util.LinkedList;
import java.util.List;

import net.deuce.moman.account.model.Account;
import net.deuce.moman.operation.EntitySetterOperation;
import net.deuce.moman.service.EntityService;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ui.IWorkbench;
import org.springframework.stereotype.Service;

@Service
public class AccountService extends EntityService<Account> {

	private List<Account> selectedAccounts = new LinkedList<Account>();

	public List<Account> getSelectedAccounts() {
		return selectedAccounts;
	}
	
	public boolean doesAccountExist(Account account) {
		for (Account a : getEntities()) {
			if (a.getAccountId().equals(account.getAccountId()) &&
					a.getBankId().equals(account.getBankId())) {
				return true;
			}
		}
		
		return false;
	}
	
	public void setSelectedAccounts(List<Account> selectedAccounts) {
		this.selectedAccounts = selectedAccounts;
	}
	
	public void addSelectedAccount(Account account) {
		selectedAccounts.add(account);
	}
	
	public void removeSelectedAccount(Account account) {
		selectedAccounts.remove(account);
	}

	@Override
	protected void clearCache() {
		selectedAccounts.clear();
		super.clearCache();
	}
	
}
