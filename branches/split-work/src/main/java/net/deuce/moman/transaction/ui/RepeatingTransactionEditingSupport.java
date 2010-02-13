package net.deuce.moman.transaction.ui;

import net.deuce.moman.Constants;
import net.deuce.moman.model.Frequency;
import net.deuce.moman.transaction.model.InternalTransaction;
import net.deuce.moman.transaction.model.RepeatingTransaction;
import net.deuce.moman.ui.CurrencyCellEditorValidator;
import net.deuce.moman.ui.NonNegativeCellEditorValidator;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;

public class RepeatingTransactionEditingSupport extends EditingSupport {
	
	private CellEditor editor;
	private int column;

	public RepeatingTransactionEditingSupport(ColumnViewer viewer, int column) {
		super(viewer);
		
		switch (column) {
		case 0:
			editor = new CheckboxCellEditor(((TableViewer)viewer).getTable(), SWT.CHECK | SWT.READ_ONLY);
			break;
		case 1:
			editor = new TextCellEditor(((TableViewer) viewer).getTable());
			editor.getControl().setFont(Constants.STANDARD_FONT);
			break;
		case 3:
			editor = new TextCellEditor(((TableViewer) viewer).getTable());
			editor.getControl().setFont(Constants.STANDARD_FONT);
			editor.setValidator(CurrencyCellEditorValidator.instance());
			break;
		case 4:
			String[] values = new String[Frequency.values().length];
			for (int i=0; i<Frequency.values().length; i++) {
				values[i] = Frequency.values()[i].label();
			}
			editor = new ComboBoxCellEditor(((TableViewer)viewer).getTable(), values, SWT.READ_ONLY);
			editor.getControl().setFont(Constants.STANDARD_FONT);
			break;
		case 5:
			editor = new TextCellEditor(((TableViewer) viewer).getTable());
			editor.getControl().setFont(Constants.STANDARD_FONT);
			editor.setValidator(NonNegativeCellEditorValidator.instance());
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
		RepeatingTransaction transaction = (RepeatingTransaction)element;
	
		switch (this.column) {
		case 0: return transaction.isEnabled();
		case 1: return transaction.getDescription();
		case 3: return Double.toString(transaction.getAmount());
		case 4: return transaction.getFrequency().ordinal();
		case 5: return transaction.getCount().toString();
		default:
			break;
		}
		return null;
	}

	@Override
	protected void setValue(Object element, Object value) {
		
		if (value != null) {

			RepeatingTransaction transaction = (RepeatingTransaction)element;
			
			switch (this.column) {
			case 0: transaction.executeChange(RepeatingTransaction.Properties.enabled, value); break;
			case 1: transaction.executeChange(InternalTransaction.Properties.description, value); break;
			case 3: transaction.executeSetAmount(Double.valueOf((String)value)); break;
			case 4: transaction.executeChange(RepeatingTransaction.Properties.frequency, Frequency.values()[(Integer)value]);
			case 5: transaction.executeChange(RepeatingTransaction.Properties.count, Integer.valueOf((String)value)); break;
			default:
				break;
			}
			getViewer().update(element, null);
		}
	}

}
