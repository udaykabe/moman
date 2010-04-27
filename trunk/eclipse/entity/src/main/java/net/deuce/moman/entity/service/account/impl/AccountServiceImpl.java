package net.deuce.moman.entity.service.account.impl;

import java.util.LinkedList;
import java.util.List;

import net.deuce.moman.entity.model.account.Account;
import net.deuce.moman.entity.service.account.AccountService;
import net.deuce.moman.entity.service.impl.EntityServiceImpl;

import org.springframework.stereotype.Component;

@Component("accountService")
public class AccountServiceImpl extends EntityServiceImpl<Account> implements AccountService {

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

	
	public void clearCache() {
		selectedAccounts.clear();
		super.clearCache();
	}
	
}
