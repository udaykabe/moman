package net.deuce.moman.envelope.ui;

import net.deuce.moman.RcpConstants;
import net.deuce.moman.entity.model.transaction.Split;
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
		Split item = (Split) element;

		switch (this.column) {
		case 1:
			return RcpConstants.CURRENCY_VALIDATOR.format(item.getAmount());
		default:
			break;
		}
		return null;
	}

	protected void setValue(Object element, Object value) {
		if (value != null) {

			Split item = (Split) element;

			switch (this.column) {
			case 1:
				item.setAmount(RcpConstants.CURRENCY_VALIDATOR.validate(
						(String) value).doubleValue());
				break;
			default:
				break;
			}
			getViewer().update(element, null);
		}
	}

}
