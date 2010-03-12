package net.deuce.moman.rule.ui;

import net.deuce.moman.ui.ShiftKeyAware;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.swt.widgets.Composite;

public class RuleEnvelopeSelectionEditingSupport extends EditingSupport {

	private CellEditor editor;

	public RuleEnvelopeSelectionEditingSupport(ColumnViewer viewer,
			ShiftKeyAware shiftKeyAwareControl, Composite parent) {
		super(viewer);
		editor = new RuleEnvelopeSelectionCellEditor(shiftKeyAwareControl,
				parent);
	}

	protected boolean canEdit(Object element) {
		return true;
	}

	public CellEditor getCellEditor(Object element) {
		return editor;
	}

	protected Object getValue(Object element) {
		return element;
	}

	protected void setValue(Object element, Object value) {
	}

}
