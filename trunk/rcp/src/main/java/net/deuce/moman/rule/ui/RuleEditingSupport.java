package net.deuce.moman.rule.ui;

import net.deuce.moman.RcpConstants;
import net.deuce.moman.entity.model.rule.Condition;
import net.deuce.moman.entity.model.rule.Rule;
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

public class RuleEditingSupport extends EditingSupport {

	private CellEditor editor;
	private int column;

	public RuleEditingSupport(ColumnViewer viewer, int column) {
		super(viewer);

		switch (column) {
		case 0:
			editor = new CheckboxCellEditor(null, SWT.CHECK | SWT.READ_ONLY);
			break;
		case 1:
			String[] values = new String[Condition.values().length];
			for (int i = 0; i < Condition.values().length; i++) {
				values[i] = Condition.values()[i].name();
			}
			editor = new ComboBoxCellEditor(((TableViewer) viewer).getTable(),
					values, SWT.READ_ONLY);
			editor.getControl().setFont(RcpConstants.STANDARD_FONT);
			break;
		case 2:
		case 4:
			editor = new TextCellEditor(((TableViewer) viewer).getTable());
			editor.getControl().setFont(RcpConstants.STANDARD_FONT);
			break;
		case 3:
			editor = new TextCellEditor(((TableViewer) viewer).getTable());
			editor.getControl().setFont(RcpConstants.STANDARD_FONT);
			editor.setValidator(CurrencyCellEditorValidator.instance());
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
		Rule rule = (Rule) element;

		switch (this.column) {
		case 0:
			return rule.isEnabled();
		case 1:
			return rule.getCondition().ordinal();
		case 2:
			return rule.getExpression();
		case 3:
			return rule.getAmount() != null ? RcpConstants.CURRENCY_VALIDATOR
					.format(rule.getAmount()) : "";
		case 4:
			return rule.getConversion();
		default:
			break;
		}
		return null;
	}

	protected void setValue(Object element, Object value) {

		if (value != null) {

			Rule rule = (Rule) element;

			UndoAdapter undoAdapter = new EntityUndoAdapter<Rule>(rule);

			switch (this.column) {
			case 0:
				undoAdapter.executeChange(Rule.Properties.enabled, value);
				break;
			case 1:
				undoAdapter.executeChange(Rule.Properties.condition, Condition
						.values()[(Integer) value]);
				break;
			case 2:
				undoAdapter.executeChange(Rule.Properties.expression, value);
				break;
			case 3:
				String amount = (String) value;
				if (amount != null && amount.length() > 0) {
					undoAdapter.executeChange(Rule.Properties.amount,
							RcpConstants.CURRENCY_VALIDATOR.validate(amount)
									.doubleValue());
				} else {
					undoAdapter.executeChange(Rule.Properties.amount, null);
				}
				break;
			case 4:
				undoAdapter.executeChange(Rule.Properties.conversion, value);
				break;
			default:
				break;
			}
			getViewer().update(element, null);
		}
	}

}
