package net.deuce.moman.allocation.ui;

import net.deuce.moman.Constants;
import net.deuce.moman.allocation.model.AllocationSet;

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
		AllocationSet allocationSet = (AllocationSet)element;
		
		switch (this.column) {
		case 0: return allocationSet.getName();
		default:
			break;
		}
		return null;
	}

	@Override
	protected void setValue(Object element, Object value) {
		if (value != null) {
			AllocationSet allocationSet = (AllocationSet)element;
			switch (this.column) {
			case 0: allocationSet.executeChange(AllocationSet.Properties.name, value); break;
			default:
				break;
			}
			getViewer().update(element, null);
		}
	}

}
