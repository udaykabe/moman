package net.deuce.moman.account.ui;

import net.deuce.moman.account.model.Account;
import net.deuce.moman.ui.Activator;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

public class AccountLabelProvider implements ITableLabelProvider {
	
	private static final Image CHECKED = Activator.getImageDescriptor(
	"icons/checked.gif").createImage();
	private static final Image UNCHECKED = Activator.getImageDescriptor(
			"icons/unchecked.gif").createImage();

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		// In case you don't like image just return null here
		if (columnIndex == 0) {
			if (((Account) element).isSelected()) {
				return CHECKED;
			} else {
				return UNCHECKED;
			}
		}
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		Account account = (Account) element;
		switch (columnIndex) {
		case 1: return account.getNickname();
		case 2: return account.getAccountId();
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
