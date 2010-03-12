package net.deuce.moman.transaction.ui;

import net.deuce.moman.RcpConstants;
import net.deuce.moman.entity.model.transaction.RepeatingTransaction;
import net.deuce.moman.ui.Activator;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

public class RepeatingTransactionLabelProvider implements ITableLabelProvider {

	private static final Image CHECKED = Activator.getImage("icons/checked.gif");
	private static final Image UNCHECKED = Activator.getImage("icons/unchecked.gif");

	public Image getColumnImage(Object element, int columnIndex) {
		if (columnIndex == 0) {
			if (((RepeatingTransaction) element).isEnabled()) {
				return CHECKED;
			} else {
				return UNCHECKED;
			}
		}
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		RepeatingTransaction transaction = (RepeatingTransaction) element;

		switch (columnIndex) {
		case 1:
			return transaction.getDescription();
		case 2:
			return transaction.getSplit().size() > 1 ? "Split" : transaction
					.getSplit().get(0).getEnvelope().getName();
		case 3:
			return RcpConstants.CURRENCY_VALIDATOR.format(transaction
					.getAmount());
		case 4:
			return transaction.getFrequency().label();
		case 5:
			return transaction.getCount().toString();
		case 6:
			return RcpConstants.SHORT_DATE_FORMAT.format(transaction
					.getDateDue());
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

}
