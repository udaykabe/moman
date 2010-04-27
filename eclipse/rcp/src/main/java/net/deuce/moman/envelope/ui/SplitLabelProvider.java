package net.deuce.moman.envelope.ui;

import net.deuce.moman.RcpConstants;
import net.deuce.moman.entity.model.transaction.Split;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

public class SplitLabelProvider implements ITableLabelProvider {

	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		Split item = (Split) element;

		switch (columnIndex) {
		case 0:
			return item.getEnvelope().getName();
		case 1:
			return RcpConstants.CURRENCY_VALIDATOR.format(item.getAmount());
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
