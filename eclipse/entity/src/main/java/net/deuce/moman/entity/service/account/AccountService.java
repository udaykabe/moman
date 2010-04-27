package net.deuce.moman.entity.service.account;

import java.util.List;

import net.deuce.moman.entity.model.account.Account;
import net.deuce.moman.entity.service.EntityService;

public interface AccountService extends EntityService<Account> {

	public List<Account> getSelectedAccounts();
	
	public boolean doesAccountExist(Account account);
	
	public void setSelectedAccounts(List<Account> selectedAccounts);
	
	public void addSelectedAccount(Account account);
	
	public void removeSelectedAccount(Account account);

}
