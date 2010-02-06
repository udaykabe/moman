package net.deuce.moman.rule.ui;

import net.deuce.moman.Constants;
import net.deuce.moman.rule.model.Condition;
import net.deuce.moman.rule.model.Rule;

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
			for (int i=0; i<Condition.values().length; i++) {
				values[i] = Condition.values()[i].name();
			}
			editor = new ComboBoxCellEditor(((TableViewer)viewer).getTable(), values, SWT.READ_ONLY);
			editor.getControl().setFont(Constants.STANDARD_FONT);
			break;
		case 2:
		case 3:
			editor = new TextCellEditor(((TableViewer)viewer).getTable());
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
		Rule rule = (Rule)element;
		
		switch (this.column) {
		case 0: return rule.isEnabled();
		case 1: return rule.getCondition().ordinal();
		case 2: return rule.getExpression();
		case 3: return rule.getConversion();
		default:
			break;
		}
		return null;
	}

	@Override
	protected void setValue(Object element, Object value) {
		
		if (value != null) {

			Rule rule = (Rule)element;
			
			switch (this.column) {
			case 0: rule.executeChange(Rule.Properties.enabled, value); break;
			case 1: rule.executeChange(Rule.Properties.condition, Condition.values()[(Integer)value]); break;
			case 2: rule.executeChange(Rule.Properties.expression, value); break;
			case 3: rule.executeChange(Rule.Properties.conversion, value); break;
			default:
				break;
			}
			getViewer().update(element, null);
		}
	}

}
