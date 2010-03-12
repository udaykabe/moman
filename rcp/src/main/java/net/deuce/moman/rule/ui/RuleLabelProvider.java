package net.deuce.moman.rule.ui;

import net.deuce.moman.RcpConstants;
import net.deuce.moman.entity.model.rule.Rule;
import net.deuce.moman.ui.Activator;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

public class RuleLabelProvider implements ITableLabelProvider {

	private static final Image CHECKED = Activator.getImage("icons/checked.gif");
	private static final Image UNCHECKED = Activator.getImage("icons/unchecked.gif");

	public Image getColumnImage(Object element, int columnIndex) {
		if (columnIndex == 0) {
			if (((Rule) element).isEnabled()) {
				return CHECKED;
			} else {
				return UNCHECKED;
			}
		}
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		Rule rule = (Rule) element;

		switch (columnIndex) {
		case 1:
			return rule.getCondition().name();
		case 2:
			return rule.getExpression();
		case 3:
			return rule.getAmount() != null ? RcpConstants.CURRENCY_VALIDATOR
					.format(rule.getAmount()) : "";
		case 4:
			return rule.getConversion();
		case 5:
			return rule.getEnvelope().getName();
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
