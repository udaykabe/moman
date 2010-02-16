package net.deuce.moman.transaction.ui;

import net.deuce.moman.Constants;
import net.deuce.moman.transaction.model.InternalTransaction;
import net.deuce.moman.ui.Activator;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

public class TransactionImportLabelProvider implements ITableLabelProvider {
	
	private static final Image CHECKED = Activator.getImageDescriptor(
			"icons/checked.gif").createImage();
	private static final Image UNCHECKED = Activator.getImageDescriptor(
			"icons/unchecked.gif").createImage();

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		if (columnIndex == 0) {
			if (((InternalTransaction) element).isMatched()) {
				return CHECKED;
			} else {
				return UNCHECKED;
			}
		}
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		InternalTransaction transaction = (InternalTransaction) element;
		
		switch (columnIndex) {
		case 1: return Constants.SHORT_DATE_FORMAT.format(transaction.getDate());
		case 2: return transaction.getCheck() != null ? transaction.getCheck() : "";
		case 3: return transaction.getDescription();
		case 4: return transaction.getSplit().size() > 1 ? "Split" : transaction.getSplit().get(0).getEnvelope().getName();
		case 5:
			double amount = Math.round(transaction.getAmount()*100.0)/100.0;
			return amount > 0.0 ? Constants.CURRENCY_VALIDATOR.format(amount) : "";
		case 6:
			amount = Math.round(transaction.getAmount()*100.0)/100.0;
			return amount <= 0.0 ? (amount < 0.0 ? Constants.CURRENCY_VALIDATOR.format(-amount) :
					Constants.CURRENCY_VALIDATOR.format(amount)) : "";
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
