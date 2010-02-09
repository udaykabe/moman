package net.deuce.moman.rcp.transaction;

import net.deuce.moman.model.transaction.InternalTransaction;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;

public class TransactionEditingSupport extends EditingSupport {
	
	private CellEditor editor;
	private int column;

	public TransactionEditingSupport(ColumnViewer viewer, int column) {
		super(viewer);
		
		switch (column) {
		case 0:
			editor = new CheckboxCellEditor(((TableViewer)viewer).getTable(), SWT.CHECK | SWT.READ_ONLY);
			break;
		default:
			editor = null;
		}
		this.column = column;
	}

	@Override
	protected boolean canEdit(Object element) {
		return true;
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		return editor;
	}

	@Override
	protected Object getValue(Object element) {
		Transaction t = (Transaction)element;
		
		switch (this.column) {
		case 0: return t.isMatched();
		default:
			break;
		}
		return null;
	}

	@Override
	protected void setValue(Object element, Object value) {
		Transaction t = (Transaction)element;
		switch (this.column) {
		case 0: t.setMatched((Boolean)value); break;
		default:
			break;
		}
		getViewer().update(element, null);
	}

}
