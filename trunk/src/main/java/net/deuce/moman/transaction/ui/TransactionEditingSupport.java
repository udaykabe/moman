package net.deuce.moman.transaction.ui;

import net.deuce.moman.Constants;
import net.deuce.moman.transaction.model.InternalTransaction;
import net.deuce.moman.ui.CurrencyCellEditorValidator;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;

public class TransactionEditingSupport extends EditingSupport {
	
	private CellEditor editor;
	private int column;

	public TransactionEditingSupport(ColumnViewer viewer, int column) {
		super(viewer);
		
		switch (column) {
		case 1:
		case 2:
			editor = new TextCellEditor(((TableViewer) viewer).getTable());
			editor.getControl().setFont(Constants.STANDARD_FONT);
			break;
		case 4:
			editor = new TextCellEditor(((TableViewer) viewer).getTable());
			editor.getControl().setFont(Constants.STANDARD_FONT);
			editor.setValidator(CurrencyCellEditorValidator.instance());
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
		case 1: return transaction.getCheck();
		case 2: return transaction.getDescription();
		case 4: return Constants.CURRENCY_VALIDATOR.format(transaction.getAmount());
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
			case 1: transaction.executeChange(InternalTransaction.Properties.check, value); break;
			case 2: transaction.executeChange(InternalTransaction.Properties.description, value); break;
			case 4: transaction.executeSetAmount(new Double((String)value)); break;
			default:
				break;
			}
			getViewer().update(element, null);
		}
	}

}
