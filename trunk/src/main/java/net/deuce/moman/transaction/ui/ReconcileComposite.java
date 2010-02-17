package net.deuce.moman.transaction.ui;

import java.util.List;

import net.deuce.moman.account.model.Account;
import net.deuce.moman.transaction.model.InternalTransaction;
import net.deuce.moman.transaction.model.TransactionStatus;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchSite;

public class ReconcileComposite extends TransactionComposite {
	
	private TransactionStatus[] availableStatuses;
	private Account account;

	public ReconcileComposite(Composite parent, boolean settingServiceViewer,
			IWorkbenchSite site, int style) {
		super(parent, settingServiceViewer, site, false, style);
	}
	
	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		if (this.account == account) return;
		if ( (this.account != null && !this.account.equals(account)) || (account != null && !account.equals(this.account)) ) {
			this.account = account;
			refresh();
		}
	}

	protected TransactionStatus[] getAvailableTransactionStatuses() {
		if (availableStatuses == null) {
			availableStatuses = new TransactionStatus[] { TransactionStatus.open, TransactionStatus.cleared };
		}
		return availableStatuses;
	}

	@Override
	protected List<InternalTransaction> getEntities() {
		return getService().getUnreconciledTransactions(account, false);
	}

}
