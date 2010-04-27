package net.deuce.moman.account.ui;

import net.deuce.moman.RcpConstants;
import net.deuce.moman.entity.model.account.Account;
import net.deuce.moman.ui.Activator;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

public class AccountLabelProvider implements ITableLabelProvider {

	private static final Image CHECKED = Activator.getImage("icons/checked.gif");
	private static final Image UNCHECKED = Activator.getImage("icons/unchecked.gif");
	
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

	public String getColumnText(Object element, int columnIndex) {
		Account account = (Account) element;
		switch (columnIndex) {
		case 1:
			return account.getNickname()
					+ " - "
					+ RcpConstants.CURRENCY_VALIDATOR.format(account
							.getBalance());
		case 2:
			return account.getAccountId();
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
