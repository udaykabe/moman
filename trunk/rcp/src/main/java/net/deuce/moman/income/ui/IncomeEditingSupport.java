package net.deuce.moman.income.ui;

import net.deuce.moman.RcpConstants;
import net.deuce.moman.entity.model.Frequency;
import net.deuce.moman.entity.model.income.Income;
import net.deuce.moman.ui.CurrencyCellEditorValidator;
import net.deuce.moman.undo.EntityUndoAdapter;
import net.deuce.moman.undo.UndoAdapter;

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
			editor = new CheckboxCellEditor(((TableViewer) viewer).getTable(),
					SWT.CHECK | SWT.READ_ONLY);
			break;
		case 1:
			editor = new TextCellEditor(((TableViewer) viewer).getTable());
			editor.getControl().setFont(RcpConstants.STANDARD_FONT);
			break;
		case 2:
			editor = new TextCellEditor(((TableViewer) viewer).getTable());
			editor.getControl().setFont(RcpConstants.STANDARD_FONT);
			editor.setValidator(CurrencyCellEditorValidator.instance());
			break;
		case 3:
			values = new String[Frequency.values().length];
			for (int i = 0; i < Frequency.values().length; i++) {
				values[i] = Frequency.values()[i].label();
			}
			editor = new ComboBoxCellEditor(((TableViewer) viewer).getTable(),
					values, SWT.READ_ONLY);
			editor.getControl().setFont(RcpConstants.STANDARD_FONT);
			break;
		default:
			editor = null;
		}
		this.column = column;
	}

	protected boolean canEdit(Object element) {
		return true;
	}

	protected CellEditor getCellEditor(Object element) {
		return editor;
	}

	protected Object getValue(Object element) {
		Income income = (Income) element;

		switch (this.column) {
		case 0:
			return income.isEnabled();
		case 1:
			return income.getName();
		case 2:
			return RcpConstants.CURRENCY_VALIDATOR.format(income.getAmount());
		case 3:
			return income.getFrequency().ordinal();
		default:
			break;
		}
		return null;
	}

	protected void setValue(Object element, Object value) {

		if (value != null) {

			Income income = (Income) element;

			UndoAdapter undoAdapter = new EntityUndoAdapter<Income>(income);

			switch (this.column) {
			case 0:
				undoAdapter.executeChange(Income.Properties.enabled, value);
				break;
			case 1:
				undoAdapter.executeChange(Income.Properties.name, value);
				break;
			case 2:
				undoAdapter.executeChange(Income.Properties.amount,
						RcpConstants.CURRENCY_VALIDATOR
								.validate((String) value).doubleValue());
				break;
			case 3:
				undoAdapter.executeChange(Income.Properties.frequency,
						Frequency.values()[(Integer) value]);
				break;
			default:
				break;
			}
			getViewer().update(element, null);
		}
	}

}
