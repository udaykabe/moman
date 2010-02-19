package net.deuce.moman.transaction.ui;

import java.util.Date;

import net.deuce.moman.transaction.model.RepeatingTransaction;
import net.deuce.moman.ui.DateSelectionEditingSupport;

import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.swt.widgets.Composite;

public class RepeatingTransactionDateSelectionEditingSupport extends DateSelectionEditingSupport {
	
	public RepeatingTransactionDateSelectionEditingSupport(ColumnViewer viewer, Composite parent) {
		super(viewer, parent);
	}

	@Override
	protected void doSetValue(Object element, Object value) {
		RepeatingTransaction transaction = (RepeatingTransaction)element;
		transaction.setDateDue((Date)value);
	}

	@Override
	protected Object doGetValue(Object element) {
		return ((RepeatingTransaction)element).getDateDue();
	}

}
