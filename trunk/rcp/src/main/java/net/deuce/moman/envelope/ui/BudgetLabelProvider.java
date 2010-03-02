package net.deuce.moman.envelope.ui;

import net.deuce.moman.Constants;
import net.deuce.moman.envelope.model.Envelope;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

public class BudgetLabelProvider implements ITableLabelProvider {

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		Envelope bill = (Envelope) element;

		switch (columnIndex) {
		case 0:
			return bill.getName();
		case 1:
			return bill.getFrequency().label();
		case 2:
			return Constants.CURRENCY_VALIDATOR.format(bill.getAmount());
		case 3:
			return bill.getParent().getName();
		default:
			break;
		}
		return "";
	}

	@Override
	public void addListener(ILabelProviderListener listener) {
	}

	@Override
	public void dispose() {
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
	}

}
