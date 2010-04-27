package net.deuce.moman.envelope.ui;

import net.deuce.moman.RcpConstants;
import net.deuce.moman.entity.model.envelope.Envelope;
import net.deuce.moman.ui.Activator;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

public class BillLabelProvider implements ITableLabelProvider {

	private static final Image CHECKED = Activator.getImage("icons/checked.gif");
	private static final Image UNCHECKED = Activator.getImage("icons/unchecked.gif");

	public Image getColumnImage(Object element, int columnIndex) {
		if (columnIndex == 0) {
			if (((Envelope) element).isEnabled()) {
				return CHECKED;
			} else {
				return UNCHECKED;
			}
		}
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		Envelope bill = (Envelope) element;

		switch (columnIndex) {
		case 1:
			return bill.getName();
		case 2:
			return Integer.toString(bill.getDueDay());
		case 3:
			return bill.getFrequency().label();
		case 4:
			return RcpConstants.CURRENCY_VALIDATOR.format(bill.getAmount());
		case 5:
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
