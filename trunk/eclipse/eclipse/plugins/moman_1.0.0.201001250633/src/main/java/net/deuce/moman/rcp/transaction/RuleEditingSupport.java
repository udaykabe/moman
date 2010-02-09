package net.deuce.moman.rcp.transaction;

import net.deuce.moman.model.Registry;
import net.deuce.moman.model.rules.Condition;
import net.deuce.moman.model.rules.Rule;

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
			editor.getControl().setFont(Registry.instance().getStandardFont());
			break;
		case 2:
		case 3:
			editor = new TextCellEditor(((TableViewer)viewer).getTable());
			editor.getControl().setFont(Registry.instance().getStandardFont());
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
		Rule rule = (Rule)element;
		switch (this.column) {
		case 0: rule.setEnabled((Boolean)value); break;
		case 1: rule.setCondition(Condition.values()[(Integer)value]); break;
		case 2: rule.setExpression((String)value); break;
		case 3: rule.setConversion((String)value); break;
		default:
			break;
		}
		getViewer().update(element, null);
	}

}
