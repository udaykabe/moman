package net.deuce.moman.allocation.ui;

import net.deuce.moman.RcpConstants;
import net.deuce.moman.entity.model.allocation.Allocation;
import net.deuce.moman.entity.model.allocation.AmountType;
import net.deuce.moman.entity.model.allocation.LimitType;
import net.deuce.moman.ui.Activator;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

public class AllocationLabelProvider implements ITableLabelProvider {

	private static final Image CHECKED = Activator.getImage("icons/checked.gif");
	private static final Image UNCHECKED = Activator.getImage("icons/unchecked.gif");

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

	public String getColumnText(Object element, int columnIndex) {
		Allocation allocation = (Allocation) element;
		switch (columnIndex) {
		case 1:
			AmountType type = allocation.getAmountType();
			if (type == AmountType.FIXED || type == AmountType.REMAINDER) {
				return RcpConstants.CURRENCY_VALIDATOR.format(allocation
						.getAmount());
			}
			return RcpConstants.PERCENT_VALIDATOR
					.format(allocation.getAmount());
		case 2:
			return allocation.getAmountType().label();
		case 3:
			return allocation.getEnvelope().getName();
		case 4:
			LimitType lType = allocation.getLimitType();
			if (lType == LimitType.DEPOSIT_PERCENT) {
				return RcpConstants.PERCENT_VALIDATOR.format(allocation
						.getLimit());
			}
			return RcpConstants.CURRENCY_VALIDATOR
					.format(allocation.getLimit());
		case 5:
			return allocation.getLimitType().label();
		case 6:
			return RcpConstants.CURRENCY_VALIDATOR.format(allocation
					.getProposed());
		case 7:
			return RcpConstants.CURRENCY_VALIDATOR.format(allocation
					.getRemainder());
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
