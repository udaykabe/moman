/**
 * 
 */
package net.deuce.moman.account.service;

import net.deuce.moman.account.model.Account;
import net.deuce.moman.service.ServiceNeeder;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class RemoveSelectedAccountOperation extends AbstractOperation {
	
	private AccountService accountService = ServiceNeeder.instance().getAccountService();
	private Account account = null;
	
	public RemoveSelectedAccountOperation(Account account) {
		super("Remove Selected Account");
		
		if (account == null) {
			throw new RuntimeException("Missing parameter 'account'");
		}
		this.account = account;
	}

	@Override
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

	@Override
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

	@Override
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