/**
 * 
 */
package net.deuce.moman.account.command;

import net.deuce.moman.entity.ServiceProvider;
import net.deuce.moman.entity.model.account.Account;
import net.deuce.moman.entity.service.account.AccountService;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class RemoveSelectedAccountOperation extends AbstractOperation {

	private AccountService accountService = ServiceProvider.instance().getAccountService();
	private Account account = null;

	public RemoveSelectedAccountOperation(Account account) {
		super("Remove Selected Account");

		if (account == null) {
			throw new RuntimeException("Missing parameter 'account'");
		}
		this.account = account;
	}

	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {

		try {
			accountService.removeSelectedAccount(account);
			return Status.OK_STATUS;
		} catch (Throwable t) {
			t.printStackTrace();
			return new Status(Status.ERROR, "", t.getMessage(), t);
		}

	}

	public IStatus redo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		try {
			accountService.removeSelectedAccount(account);
			return Status.OK_STATUS;
		} catch (Throwable t) {
			t.printStackTrace();
			return new Status(Status.ERROR, "", t.getMessage(), t);
		}
	}

	public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		try {
			accountService.addSelectedAccount(account);
			return Status.OK_STATUS;
		} catch (Throwable t) {
			t.printStackTrace();
			return new Status(Status.ERROR, "", t.getMessage(), t);
		}
	}

}
