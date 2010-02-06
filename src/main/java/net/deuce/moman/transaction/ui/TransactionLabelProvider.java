package net.deuce.moman.transaction.ui;

import java.util.List;

import net.deuce.moman.Constants;
import net.deuce.moman.envelope.model.Envelope;
import net.deuce.moman.service.ServiceNeeder;
import net.deuce.moman.transaction.model.InternalTransaction;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

public class TransactionLabelProvider implements ITableLabelProvider, ITableColorProvider {
	
	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		InternalTransaction transaction = (InternalTransaction) element;
		List<Envelope> split = transaction.getSplit();
		
		switch (columnIndex) {
		case 0: return Constants.SHORT_DATE_FORMAT.format(transaction.getDate());
		case 1: return transaction.getCheck() != null ? transaction.getCheck() : "";
		case 2: return transaction.getDescription();
		case 3: return split != null && split.size() > 0 ? split.get(0).getName() : "";
		case 4: return Constants.CURRENCY_VALIDATOR.format(transaction.getAmount());
		case 5: 
			if (ServiceNeeder.instance().getEnvelopeService().getSelectedEnvelope() == null) {
				return Constants.CURRENCY_VALIDATOR.format(transaction.getBalance());
			}
			return "";
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