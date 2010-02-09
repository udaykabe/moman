package net.deuce.moman.transaction.ui;

import java.util.List;

import net.deuce.moman.Constants;
import net.deuce.moman.envelope.model.Envelope;
import net.deuce.moman.transaction.model.RepeatingTransaction;
import net.deuce.moman.ui.Activator;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

public class RepeatingTransactionLabelProvider implements ITableLabelProvider {
	
	private static final Image CHECKED = Activator.getImageDescriptor(
		"icons/checked.gif").createImage();
	private static final Image UNCHECKED = Activator.getImageDescriptor(
		"icons/unchecked.gif").createImage();
	
	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		if (columnIndex == 0) {
			if (((RepeatingTransaction) element).isEnabled()) {
				return CHECKED;
			} else {
				return UNCHECKED;
			}
		}
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		RepeatingTransaction transaction = (RepeatingTransaction) element;
		List<Envelope> split = transaction.getSplit();
		
		switch (columnIndex) {
		case 1: return transaction.getDescription();
		case 2: return split != null && split.size() > 0 ? split.get(0).getName() : "";
		case 3: return Constants.CURRENCY_VALIDATOR.format(transaction.getAmount());
		case 4: return transaction.getFrequency().label();
		case 5: return transaction.getCount().toString();
		case 6: String date = Constants.SHORT_DATE_FORMAT.format(transaction.getDateDue());
			return date;
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
