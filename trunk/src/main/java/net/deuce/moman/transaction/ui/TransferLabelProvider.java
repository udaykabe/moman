package net.deuce.moman.transaction.ui;

import net.deuce.moman.Constants;
import net.deuce.moman.transaction.model.InternalTransaction;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

public class TransferLabelProvider implements ITableLabelProvider, ITableColorProvider {
	
	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		InternalTransaction transaction = (InternalTransaction) element;
		
		switch (columnIndex) {
		case 0: return Constants.SHORT_DATE_FORMAT.format(transaction.getDate());
		case 1: return transaction.getDescription();
		case 2: return transaction.getSplit() != null && transaction.getSplit().size() > 0 ?
				transaction.getSplit().get(0).getEnvelope().getName() : "";
		case 3: return Constants.CURRENCY_VALIDATOR.format(transaction.getAmount());
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

	@Override
	public Color getBackground(Object element, int columnIndex) {
		
		return null;
	}

	@Override
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
