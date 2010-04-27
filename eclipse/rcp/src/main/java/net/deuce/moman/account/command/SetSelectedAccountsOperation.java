/**
 * 
 */
package net.deuce.moman.account.command;

import java.util.LinkedList;
import java.util.List;

import net.deuce.moman.entity.ServiceProvider;
import net.deuce.moman.entity.model.account.Account;
import net.deuce.moman.entity.service.account.AccountService;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class SetSelectedAccountsOperation extends AbstractOperation {

	private AccountService accountService = ServiceProvider.instance().getAccountService();
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
