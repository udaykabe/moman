package net.deuce.moman.transaction.ui;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public abstract class ControlLimitingCellEditor extends DialogCellEditor {
	
	private Control control;
	
	public ControlLimitingCellEditor(Composite parent) {
		super(parent);
	}
	
	public void create(Composite parent) {
		Assert.isTrue(control == null);
		control = createControl(parent);
		// See 1GD5CA6: ITPUI:ALL - TaskView.setSelection does not work
		// Control is created with getVisible()==true by default.
		// This causes composite.setFocus() to work incorrectly.
		// The cell editor's control grabs focus instead, even if it is not
		// active.
		// Make the control invisible here by default.
		deactivate();
	}

	@Override
	public Control getControl() {
		return null;
	}
	
	@Override
	public void deactivate() {
		if (control != null && !control.isDisposed()) {
			control.setVisible(false);
		}
	}

	@Override
	public void dispose() {
		if (control != null && !control.isDisposed()) {
			control.dispose();
		}
		control = null;
	}
	
	@Override
	public boolean isActivated() {
		// Use the state of the visible style bit (getVisible()) rather than the
		// window's actual visibility (isVisible()) to get correct handling when
		// an ancestor control goes invisible, see bug 85331.
		return control != null && control.getVisible();
	}
	
}
