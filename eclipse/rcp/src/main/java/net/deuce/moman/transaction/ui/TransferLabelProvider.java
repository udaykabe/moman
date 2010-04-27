package net.deuce.moman.transaction.ui;

import net.deuce.moman.RcpConstants;
import net.deuce.moman.entity.model.transaction.InternalTransaction;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

public class TransferLabelProvider implements ITableLabelProvider,
		ITableColorProvider {

	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		InternalTransaction transaction = (InternalTransaction) element;

		switch (columnIndex) {
		case 0:
			return RcpConstants.SHORT_DATE_FORMAT.format(transaction.getDate());
		case 1:
			return transaction.getDescription();
		case 2:
			return transaction.getSplit() != null
					&& transaction.getSplit().size() > 0 ? transaction
					.getSplit().get(0).getEnvelope().getName() : "";
		case 3:
			return RcpConstants.CURRENCY_VALIDATOR.format(transaction
					.getAmount());
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
