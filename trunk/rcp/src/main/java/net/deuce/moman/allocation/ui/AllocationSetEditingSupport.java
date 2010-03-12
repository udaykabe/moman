package net.deuce.moman.allocation.ui;

import net.deuce.moman.RcpConstants;
import net.deuce.moman.entity.model.allocation.AllocationSet;
import net.deuce.moman.undo.EntityUndoAdapter;
import net.deuce.moman.undo.UndoAdapter;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;

public class AllocationSetEditingSupport extends EditingSupport {

	private CellEditor editor;
	private int column;

	public AllocationSetEditingSupport(ColumnViewer viewer, int column) {
		super(viewer);

		switch (column) {
		case 0:
			editor = new TextCellEditor(((TableViewer) viewer).getTable());
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
		AllocationSet allocationSet = (AllocationSet) element;

		switch (this.column) {
		case 0:
			return allocationSet.getName();
		default:
			break;
		}
		return null;
	}

	protected void setValue(Object element, Object value) {
		if (value != null) {
			AllocationSet allocationSet = (AllocationSet) element;
			UndoAdapter undoAdapter = new EntityUndoAdapter<AllocationSet>(
					allocationSet);

			switch (this.column) {
			case 0:
				undoAdapter.executeChange(AllocationSet.Properties.name, value);
				break;
			default:
				break;
			}
			getViewer().update(element, null);
		}
	}

}
