package net.deuce.moman.transaction.ui;

import net.deuce.moman.transaction.model.InternalTransaction;
import net.deuce.moman.transaction.ui.DateSelectionCellEditor;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.swt.widgets.Composite;

public abstract class DateSelectionEditingSupport extends EditingSupport {
	
	private CellEditor editor;

	public DateSelectionEditingSupport(ColumnViewer viewer, Composite parent) {
		super(viewer);
		editor = new DateSelectionCellEditor(parent);
	}

	@Override
	protected boolean canEdit(Object element) {
		return true;
	}

	@Override
	public CellEditor getCellEditor(Object element) {
		return editor;
	}

	@Override
	protected Object getValue(Object element) {
		return ((InternalTransaction)element).getDate();
	}

	@Override
	protected void setValue(Object element, Object value) {
		doSetValue(element, value);
	}
	
	protected abstract void doSetValue(Object element, Object value);

}
