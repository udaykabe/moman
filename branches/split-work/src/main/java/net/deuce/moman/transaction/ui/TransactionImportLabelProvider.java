package net.deuce.moman.transaction.ui;

import java.util.List;

import net.deuce.moman.Constants;
import net.deuce.moman.envelope.model.Envelope;
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
		List<Envelope> split = transaction.getSplit();
		
		switch (columnIndex) {
		case 1: return Constants.SHORT_DATE_FORMAT.format(transaction.getDate());
		case 2: return transaction.getCheck() != null ? transaction.getCheck() : "";
		case 3: return transaction.getDescription();
		case 4: return split != null && split.size() > 0 ? split.get(0).getName() : "";
		case 5: return Constants.CURRENCY_VALIDATOR.format(transaction.getAmount());
		default:
			break;
        }
		return "";
	}

	@Override
	public void addListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

}
