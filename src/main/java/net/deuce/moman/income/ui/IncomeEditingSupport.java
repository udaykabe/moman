package net.deuce.moman.income.ui;

import net.deuce.moman.Constants;
import net.deuce.moman.income.model.Income;
import net.deuce.moman.model.Frequency;
import net.deuce.moman.ui.CurrencyCellEditorValidator;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;

public class IncomeEditingSupport extends EditingSupport {
	
	private CellEditor editor;
	private int column;

	public IncomeEditingSupport(ColumnViewer viewer, int column) {
		super(viewer);
		
		String[] values;
		
		switch (column) {
		case 0:
			editor = new CheckboxCellEditor(((TableViewer)viewer).getTable(), SWT.CHECK | SWT.READ_ONLY);
			break;
		case 1:
			editor = new TextCellEditor(((TableViewer) viewer).getTable());
			editor.getControl().setFont(Constants.STANDARD_FONT);
			break;
		case 2:
			editor = new TextCellEditor(((TableViewer) viewer).getTable());
			editor.getControl().setFont(Constants.STANDARD_FONT);
			editor.setValidator(CurrencyCellEditorValidator.instance());
			break;
		case 3:
			values = new String[Frequency.values().length];
			for (int i=0; i<Frequency.values().length; i++) {
				values[i] = Frequency.values()[i].label();
			}
			editor = new ComboBoxCellEditor(((TableViewer)viewer).getTable(), values, SWT.READ_ONLY);
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
		Income income = (Income)element;
	
		switch (this.column) {
		case 0: return income.isEnabled();
		case 1: return income.getName();
		case 2: return Double.toString(income.getAmount());
		case 3: return income.getFrequency().ordinal();
		default:
			break;
		}
		return null;
	}

	@Override
	protected void setValue(Object element, Object value) {
		
		if (value != null) {

			Income income = (Income)element;
		
			switch (this.column) {
			case 0: income.executeChange(Income.Properties.enabled, value);
				break;
			case 1: income.executeChange(Income.Properties.name, value);
				break;
			case 2: income.executeChange(Income.Properties.amount, new Double((String)value));
				break;
			case 3: income.executeChange(Income.Properties.frequency, Frequency.values()[(Integer)value]);
				break;
			default:
				break;
			}
			getViewer().update(element, null);
		}
	}


}
