package net.deuce.moman.rule.ui;

import net.deuce.moman.Constants;
import net.deuce.moman.rule.model.Rule;
import net.deuce.moman.ui.Activator;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

public class RuleLabelProvider implements ITableLabelProvider {
	
	private static final Image CHECKED = Activator.getImageDescriptor(
			"icons/checked.gif").createImage();
	private static final Image UNCHECKED = Activator.getImageDescriptor(
			"icons/unchecked.gif").createImage();

	@Override
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

	@Override
	public String getColumnText(Object element, int columnIndex) {
		Rule rule = (Rule) element;
		
		switch (columnIndex) {
		case 1: return rule.getCondition().name();
		case 2: return rule.getExpression();
		case 3: return rule.getAmount() != null ? Constants.CURRENCY_VALIDATOR.format(rule.getAmount()) : "";
		case 4: return rule.getConversion();
		case 5: return rule.getSplit().size() > 1 ? "Split" : rule.getSplit().get(0).getEnvelope().getName();
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
