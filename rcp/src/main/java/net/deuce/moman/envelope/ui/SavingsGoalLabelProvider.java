package net.deuce.moman.envelope.ui;

import net.deuce.moman.Constants;
import net.deuce.moman.envelope.model.Envelope;
import net.deuce.moman.ui.Activator;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

public class SavingsGoalLabelProvider implements ITableLabelProvider {

	private static final Image CHECKED = Activator.getImageDescriptor(
			"icons/checked.gif").createImage();
	private static final Image UNCHECKED = Activator.getImageDescriptor(
			"icons/unchecked.gif").createImage();

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		if (columnIndex == 0) {
			if (((Envelope) element).isEnabled()) {
				return CHECKED;
			} else {
				return UNCHECKED;
			}
		}
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		Envelope goal = (Envelope) element;

		switch (columnIndex) {
		case 1:
			return goal.getName();
		case 2:
			return Constants.CURRENCY_VALIDATOR.format(goal.getAmount());
		case 3:
			return Constants.SHORT_DATE_FORMAT.format(goal.getSavingsGoalDate());
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
