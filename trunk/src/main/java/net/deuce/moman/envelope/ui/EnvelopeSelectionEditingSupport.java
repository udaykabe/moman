package net.deuce.moman.envelope.ui;

import net.deuce.moman.ui.ShiftKeyAware;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.swt.widgets.Composite;

public class EnvelopeSelectionEditingSupport extends EditingSupport {
	
	private CellEditor editor;

	public EnvelopeSelectionEditingSupport(ColumnViewer viewer, ShiftKeyAware shiftKeyAwareControl, Composite parent) {
		super(viewer);
		editor = new EnvelopeSelectionCellEditor(shiftKeyAwareControl, parent);
	}

	@Override
	protected boolean canEdit(Object element) {
		return true;
	}

	@Override
	public CellEditor getCellEditor(Object element) {
		return editor;
	}

	@Override
	protected Object getValue(Object element) {
		return element;
	}

	@Override
	protected void setValue(Object element, Object value) {
	}

}
