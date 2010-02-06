package net.deuce.moman.transaction.ui;

import net.deuce.moman.Constants;
import net.deuce.moman.transaction.model.InternalTransaction;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;

public class TransactionImportEditingSupport extends EditingSupport {
	
	private CellEditor editor;
	private int column;

	public TransactionImportEditingSupport(ColumnViewer viewer, int column) {
		super(viewer);
		
		switch (column) {
		case 0:
			editor = new CheckboxCellEditor(((TableViewer)viewer).getTable(), SWT.CHECK | SWT.READ_ONLY);
			break;
		case 2:
			editor = new TextCellEditor(((TableViewer) viewer).getTable());
			editor.getControl().setFont(Constants.STANDARD_FONT);
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
		InternalTransaction transaction = (InternalTransaction)element;
	
		switch (this.column) {
		case 0: return transaction.isMatched();
		case 2: return transaction.getDescription();
		default:
			break;
		}
		return null;
	}

	@Override
	protected void setValue(Object element, Object value) {
		
		if (value != null) {

			InternalTransaction transaction = (InternalTransaction)element;
			
			switch (this.column) {
	//		case 0: transaction.setMatched((String)value); break;
			case 2: transaction.executeChange(InternalTransaction.Properties.description, value); break;
			default:
				break;
			}
			getViewer().update(element, null);
		}
	}

}
