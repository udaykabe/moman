package net.deuce.moman.allocation.ui;

import net.deuce.moman.Constants;
import net.deuce.moman.allocation.model.Allocation;
import net.deuce.moman.allocation.model.AmountType;
import net.deuce.moman.allocation.model.LimitType;
import net.deuce.moman.ui.Activator;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

public class AllocationLabelProvider implements ITableLabelProvider {
	
	private static final Image CHECKED = Activator.getImageDescriptor(
		"icons/checked.gif").createImage();
	private static final Image UNCHECKED = Activator.getImageDescriptor(
		"icons/unchecked.gif").createImage();

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		// In case you don't like image just return null here
		if (columnIndex == 0) {
			if (((Allocation) element).isEnabled()) {
				return CHECKED;
			} else {
				return UNCHECKED;
			}
		}
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		Allocation allocation = (Allocation) element;
		switch (columnIndex) {
		case 1: 
			AmountType type = allocation.getAmountType();
			if (type == AmountType.FIXED || type == AmountType.REMAINDER) {
				return Constants.CURRENCY_VALIDATOR.format(allocation.getAmount());
			}
			return Constants.PERCENT_VALIDATOR.format(allocation.getAmount());
		case 2: return allocation.getAmountType().label();
		case 3: return allocation.getEnvelope().getName();
		case 4: 
			LimitType lType = allocation.getLimitType();
			if (lType == LimitType.DEPOSIT_PERCENT) {
				return Constants.PERCENT_VALIDATOR.format(allocation.getLimit());
			}
			return Constants.CURRENCY_VALIDATOR.format(allocation.getLimit());
		case 5: return allocation.getLimitType().label();
		case 6: return Constants.CURRENCY_VALIDATOR.format(allocation.getProposed());
		case 7: return Constants.CURRENCY_VALIDATOR.format(allocation.getRemainder());
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
