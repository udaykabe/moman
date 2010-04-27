package net.deuce.moman.income.ui;

import net.deuce.moman.RcpConstants;
import net.deuce.moman.entity.model.income.Income;
import net.deuce.moman.ui.Activator;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

public class IncomeLabelProvider implements ITableLabelProvider {

	private static final Image CHECKED = Activator.getImage("icons/checked.gif");
	private static final Image UNCHECKED = Activator.getImage("icons/unchecked.gif");

	public Image getColumnImage(Object element, int columnIndex) {
		if (columnIndex == 0) {
			if (((Income) element).isEnabled()) {
				return CHECKED;
			} else {
				return UNCHECKED;
			}
		}
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		Income income = (Income) element;

		switch (columnIndex) {
		case 1:
			return income.getName();
		case 2:
			return RcpConstants.CURRENCY_VALIDATOR.format(income.getAmount());
		case 3:
			return income.getFrequency().label();
		case 4:
			return RcpConstants.SHORT_DATE_FORMAT
					.format(income.getNextPayday());
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
