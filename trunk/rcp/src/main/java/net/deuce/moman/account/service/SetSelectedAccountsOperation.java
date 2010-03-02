/**
 * 
 */
package net.deuce.moman.account.service;

import java.util.LinkedList;
import java.util.List;

import net.deuce.moman.account.model.Account;
import net.deuce.moman.service.ServiceNeeder;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class SetSelectedAccountsOperation extends AbstractOperation {
	
	private AccountService accountService = ServiceNeeder.instance().getAccountService();
	private List<Account> oldAccounts = null;
	private List<Account> newAccounts = null;
	
	public SetSelectedAccountsOperation(List<Account> oldAccounts,
			List<Account> newAccounts) {
		
		super("Set Selected Accounts");
		
		if (oldAccounts != null) {
			this.oldAccounts = new LinkedList<Account>();
			this.oldAccounts.addAll(oldAccounts);
		}
		if (newAccounts != null) {
			this.newAccounts = new LinkedList<Account>();
			this.newAccounts.addAll(newAccounts);
		}
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		
		try {
			this.accountService.setSelectedAccounts(newAccounts);
			return Status.OK_STATUS;
		} catch (Throwable t) {
			t.printStackTrace();
			return new Status(Status.ERROR, "", t.getMessage(), t);
		}
		
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		try {
			this.accountService.setSelectedAccounts(newAccounts);
			return Status.OK_STATUS;
		} catch (Throwable t) {
			t.printStackTrace();
			return new Status(Status.ERROR, "", t.getMessage(), t);
		}
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		try {
			this.accountService.setSelectedAccounts(oldAccounts);
			return Status.OK_STATUS;
		} catch (Throwable t) {
			t.printStackTrace();
			return new Status(Status.ERROR, "", t.getMessage(), t);
		}
	}
	
}