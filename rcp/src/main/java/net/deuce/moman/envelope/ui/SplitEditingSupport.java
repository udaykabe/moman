package net.deuce.moman.envelope.ui;

import net.deuce.moman.Constants;
import net.deuce.moman.transaction.model.Split;
import net.deuce.moman.ui.CurrencyCellEditorValidator;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;

public class SplitEditingSupport extends EditingSupport {
	
	private CellEditor editor;
	private int column;

	public SplitEditingSupport(ColumnViewer viewer, int column) {
		super(viewer);
		
		switch (column) {
		case 1:
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
		Split item = (Split)element;
	
		switch (this.column) {
		case 1: return Constants.CURRENCY_VALIDATOR.format(item.getAmount());
		default:
			break;
		}
		return null;
	}

	@Override
	protected void setValue(Object element, Object value) {
		if (value != null) {

			Split item = (Split)element;
		
			switch (this.column) {
			case 1: item.setAmount(Constants.CURRENCY_VALIDATOR.validate((String)value).doubleValue());
				break;
			default:
				break;
			}
			getViewer().update(element, null);
		}
	}


}
