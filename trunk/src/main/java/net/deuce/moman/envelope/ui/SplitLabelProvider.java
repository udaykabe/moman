package net.deuce.moman.envelope.ui;

import net.deuce.moman.Constants;
import net.deuce.moman.transaction.model.Split;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

public class SplitLabelProvider implements ITableLabelProvider {

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		Split item = (Split) element;

		switch (columnIndex) {
		case 0:
			return item.getEnvelope().getName();
		case 1:
			return Constants.CURRENCY_VALIDATOR.format(item.getAmount());
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
