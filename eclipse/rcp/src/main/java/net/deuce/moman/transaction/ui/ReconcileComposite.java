package net.deuce.moman.transaction.ui;

import java.util.List;

import net.deuce.moman.entity.model.account.Account;
import net.deuce.moman.entity.model.transaction.InternalTransaction;
import net.deuce.moman.entity.model.transaction.TransactionStatus;

import org.eclipse.swt.widgets.Composite;

public class ReconcileComposite extends TransactionComposite {

	private TransactionStatus[] availableStatuses;
	private Account account;

	public ReconcileComposite(Composite parent, int style) {
		super(parent, false, true, false, style);
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		if (this.account == account)
			return;
		if ((this.account != null && !this.account.equals(account))
				|| (account != null && !account.equals(this.account))) {
			this.account = account;
			refresh();
		}
	}

	protected TransactionStatus[] getAvailableTransactionStatuses() {
		if (availableStatuses == null) {
			availableStatuses = new TransactionStatus[] {
					TransactionStatus.open, TransactionStatus.cleared };
		}
		return availableStatuses;
	}

	protected List<InternalTransaction> getEntities() {
		return getService().getUnreconciledTransactions(account, false);
	}

}
