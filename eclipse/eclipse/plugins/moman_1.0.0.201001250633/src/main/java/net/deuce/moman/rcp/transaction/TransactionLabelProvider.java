package net.deuce.moman.rcp.transaction;

import java.util.List;

import net.deuce.moman.model.Registry;
import net.deuce.moman.model.envelope.Envelope;
import net.deuce.moman.model.transaction.InternalTransaction;
import net.deuce.moman.rcp.Activator;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

public class TransactionLabelProvider implements ITableLabelProvider {
	
	private static final Image CHECKED = Activator.getImageDescriptor(
			"icons/checked.gif").createImage();
	private static final Image UNCHECKED = Activator.getImageDescriptor(
			"icons/unchecked.gif").createImage();

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		if (columnIndex == 0) {
			if (((Transaction) element).isMatched()) {
				return CHECKED;
			} else {
				return UNCHECKED;
			}
		}
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		Transaction transaction = (Transaction) element;
		List<Envelope> split = transaction.getSplit();
		
		switch (columnIndex) {
		case 1: return transaction.getCheck() != null ? transaction.getCheck() : "";
		case 2: return Registry.SHORT_DATE_FORMAT.format(transaction.getDate());
		case 3: return transaction.getDescription();
		case 4: return Registry.CURRENCY_FORMAT.format(transaction.getAmount());
		case 5: return split != null && split.size() > 0 ? split.get(0).getName() : "";
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
