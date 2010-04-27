package net.deuce.moman.transaction.ui;

import net.deuce.moman.RcpConstants;
import net.deuce.moman.entity.model.transaction.InternalTransaction;
import net.deuce.moman.ui.Activator;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

public class TransactionLabelProvider implements ITableLabelProvider,
		ITableColorProvider {

	private static final Image[] STATUS_IMAGES = {
			Activator.getImage("icons/transactionStatus_Open.png"),
			Activator.getImage("icons/transactionStatus_Cleared.png"),
			Activator.getImage("icons/transactionStatus_Reconciled.png"),
			Activator.getImage("icons/transactionStatus_Voided.png"),
			Activator.getImage("icons/transactionStatus_Pending.png"),
			};

	public Image getColumnImage(Object element, int columnIndex) {
		// In case you don't like image just return null here
		if (columnIndex == 1) {
			return STATUS_IMAGES[((InternalTransaction) element).getStatus()
					.ordinal()];
		}
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		InternalTransaction transaction = (InternalTransaction) element;

		switch (columnIndex) {
		case 0:
			return RcpConstants.SHORT_DATE_FORMAT.format(transaction.getDate());
		case 2:
			return transaction.getCheck() != null ? transaction.getCheck() : "";
		case 3:
			return transaction.getDescription();
		case 4:
			return transaction.getSplit().size() > 1 ? "Split" : transaction
					.getSplit().get(0).getEnvelope().getName();
		case 5:
			double amount = Math.round(transaction.getAmount() * 100.0) / 100.0;
			return amount > 0.0 ? RcpConstants.CURRENCY_VALIDATOR
					.format(amount) : "";
		case 6:
			amount = Math.round(transaction.getAmount() * 100.0) / 100.0;
			return amount <= 0.0 ? (amount < 0.0 ? RcpConstants.CURRENCY_VALIDATOR
					.format(-amount)
					: RcpConstants.CURRENCY_VALIDATOR.format(amount))
					: "";
		case 7:
			if (transaction.getBalance() != null) {
				return RcpConstants.CURRENCY_VALIDATOR.format(transaction
						.getBalance());
			}
		default:
			break;
		}
		return "";
	}

	public void addListener(ILabelProviderListener listener) {
	}

	public void dispose() {
	}

	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	public void removeListener(ILabelProviderListener listener) {
	}

	public Color getBackground(Object element, int columnIndex) {

		return null;
	}

	public Color getForeground(Object element, int columnIndex) {

		InternalTransaction transaction = (InternalTransaction) element;
		if (transaction.getBalance() != null) {
			if (columnIndex == 5 && transaction.getBalance() < 0) {
				return Display.getCurrent().getSystemColor(SWT.COLOR_RED);
			}
		}
		return null;
	}

}
