package net.deuce.moman.envelope.ui;

import net.deuce.moman.RcpConstants;
import net.deuce.moman.entity.model.envelope.Envelope;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

public class BudgetLabelProvider implements ITableLabelProvider {

	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		Envelope bill = (Envelope) element;

		switch (columnIndex) {
		case 0:
			return bill.getName();
		case 1:
			return bill.getFrequency().label();
		case 2:
			return RcpConstants.CURRENCY_VALIDATOR.format(bill.getAmount());
		case 3:
			return bill.getParent().getName();
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
