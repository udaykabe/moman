package net.deuce.moman.transaction.ui;

import net.deuce.moman.Constants;
import net.deuce.moman.transaction.model.InternalTransaction;
import net.deuce.moman.transaction.model.TransactionStatus;
import net.deuce.moman.ui.CurrencyCellEditorValidator;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;

public class TransactionEditingSupport extends EditingSupport {
	
	private CellEditor editor;
	private int column;

	public TransactionEditingSupport(ColumnViewer viewer, int column, TransactionStatus[] availableStatuses) {
		super(viewer);
		
		switch (column) {
		case 1:
			String[] values = new String[availableStatuses.length];
			for (int i=0; i<availableStatuses.length; i++) {
				values[i] =  availableStatuses[i].name();
			}
			editor = new ComboBoxCellEditor(((TableViewer)viewer).getTable(), values, SWT.READ_ONLY);
			editor.getControl().setFont(Constants.STANDARD_FONT);
			break;
		case 2:
		case 3:
			editor = new TextCellEditor(((TableViewer) viewer).getTable());
			editor.getControl().setFont(Constants.STANDARD_FONT);
			break;
		case 5:
		case 6:
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
		case 1: return transaction.getStatus().ordinal();
		case 2: return transaction.getCheck();
		case 3: return transaction.getDescription();
		case 5:
			double amount = Math.round(transaction.getAmount()*100.0)/100.0;
			return amount > 0.0 ? Constants.CURRENCY_VALIDATOR.format(amount) : "";
		case 6:
			amount = Math.round(transaction.getAmount()*100.0)/100.0;
			return amount <= 0.0 ? (amount < 0.0 ? Constants.CURRENCY_VALIDATOR.format(-amount) :
					Constants.CURRENCY_VALIDATOR.format(amount)) : "";
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
			case 1: transaction.executeChange(InternalTransaction.Properties.status, TransactionStatus.values()[(Integer)value]); break;
			case 2: transaction.executeChange(InternalTransaction.Properties.check, value); break;
			case 3: transaction.executeChange(InternalTransaction.Properties.description, value); break;
			case 5: transaction.executeSetAmount(Constants.CURRENCY_VALIDATOR.validate((String)value).doubleValue()); break;
			case 6: transaction.executeSetAmount(-Constants.CURRENCY_VALIDATOR.validate((String)value).doubleValue()); break;
			default:
				break;
			}
			getViewer().update(element, null);
		}
	}

}
