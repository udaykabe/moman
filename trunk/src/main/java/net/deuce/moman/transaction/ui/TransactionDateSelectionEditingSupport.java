package net.deuce.moman.transaction.ui;

import java.util.Date;

import net.deuce.moman.transaction.model.InternalTransaction;
import net.deuce.moman.ui.DateSelectionEditingSupport;

import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.swt.widgets.Composite;

public class TransactionDateSelectionEditingSupport extends DateSelectionEditingSupport {
	
	public TransactionDateSelectionEditingSupport(ColumnViewer viewer, Composite parent) {
		super(viewer, parent);
	}

	@Override
	protected void doSetValue(Object element, Object value) {
		InternalTransaction transaction = (InternalTransaction)element;
		transaction.setDate((Date)value);
	}

	@Override
	protected Object doGetValue(Object element) {
		return ((InternalTransaction)element).getDate();
	}

}
