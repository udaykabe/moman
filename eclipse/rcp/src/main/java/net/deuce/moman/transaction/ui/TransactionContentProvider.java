package net.deuce.moman.transaction.ui;

import java.util.List;

import net.deuce.moman.entity.model.transaction.InternalTransaction;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class TransactionContentProvider implements IStructuredContentProvider {

	public Object[] getElements(Object inputElement) {
		@SuppressWarnings("unchecked")
		List<InternalTransaction> transactions = (List<InternalTransaction>) inputElement;
		return transactions.toArray();
	}

	public void dispose() {
		// TODO Auto-generated method stub

	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub

	}

}
