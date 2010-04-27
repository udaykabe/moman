package net.deuce.moman.account.ui;

import java.util.List;

import net.deuce.moman.entity.model.account.Account;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class AccountContentProvider implements IStructuredContentProvider {

	public Object[] getElements(Object inputElement) {
		@SuppressWarnings("unchecked")
		List<Account> accounts = (List<Account>) inputElement;
		return accounts.toArray();
	}

	public void dispose() {
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

}
