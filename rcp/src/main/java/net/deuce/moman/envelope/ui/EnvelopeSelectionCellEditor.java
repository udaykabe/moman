package net.deuce.moman.envelope.ui;

import java.util.List;

import net.deuce.moman.envelope.model.Envelope;
import net.deuce.moman.service.ServiceNeeder;
import net.deuce.moman.transaction.model.Split;
import net.deuce.moman.transaction.ui.ControlLimitingCellEditor;
import net.deuce.moman.ui.ShiftKeyAware;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class EnvelopeSelectionCellEditor extends ControlLimitingCellEditor {
	
	private boolean shiftDown;
	private ShiftKeyAware shiftKeyAwareControl;
	private Label label;
	private Shell shell;
	
	public EnvelopeSelectionCellEditor(ShiftKeyAware shiftKeyAwareControl, Composite parent) {
		super(parent);
		this.shell = parent.getShell();
		this.shiftKeyAwareControl = shiftKeyAwareControl;
	}

	public boolean isShiftDown() {
		return shiftDown;
	}


	public void setShiftDown(boolean shiftDown) {
		this.shiftDown = shiftDown;
	}
	
	@Override
	protected Control createContents(Composite cell) {
		label = new Label(cell, SWT.NONE);
		return label;
	}

	@Override
	protected void updateContents(Object value) {
	}

	@Override
	public void activate() {
		openDialogBox(label);
	}

	@Override
	protected Object openDialogBox(Control cellEditorWindow) {
		Object value = getValue();
		if (shiftDown || forceSplit(value)) {
			handleSplitSelectionDialog(value);
		} else {
			handleEnvelopeSelectionDialog(value);
		}
		return null;
	}
	
	protected boolean forceSplit(Object value) {
		return false;
	}
	
	protected Double getSplitAmount(Object value) {
		return null;
	}
	
	protected List<Split> getSplit(Object value) {
		return null;
	}
	
	protected void handleEnvelopeSelected(Object value, Envelope env) {
		Envelope envelope = (Envelope)value;
		Envelope oldParent = envelope.getParent();
		if (oldParent != null) {
			oldParent.removeChild(envelope);
		}
		envelope.setParent(env);
		env.addChild(envelope);
	}
	
	protected Envelope getInitialEnvelope(Object value) {
		Envelope envelope = (Envelope)value;
		return envelope.getParent();
	}
	
	protected void handleSplitSelected(Object value, List<Split> result) {
	}

	private void handleEnvelopeSelectionDialog(final Object value) {
		Envelope initialEnvelope = getInitialEnvelope(value);
		final EnvelopeSelectionDialog dialog = new EnvelopeSelectionDialog(shell, initialEnvelope);
		dialog.setAllowBills(true);
		dialog.create();
		dialog.open();
		if (initialEnvelope != dialog.getEnvelope()) {
			BusyIndicator.showWhile(shell.getDisplay(), new Runnable() {
				public void run() {
					handleEnvelopeSelected(value, dialog.getEnvelope());
				}
			});
		}
	}
	
	private void handleSplitSelectionDialog(final Object value) {
		List<Split> split = getSplit(value);
		final SplitSelectionDialog dialog = new SplitSelectionDialog(shell, getSplitAmount(value), split);
		
		dialog.setAllowBills(true);
		dialog.create();
		int status = dialog.open();
		final List<Split> result = dialog.getSplit();
		if (status == Window.OK) {
			if (!split.equals(result)) {
				BusyIndicator.showWhile(shell.getDisplay(), new Runnable() {
					public void run() {
						List<String> ids = ServiceNeeder.instance().getServiceContainer().startQueuingNotifications();
						try {
							handleSplitSelected(value, result);
						} finally {
							ServiceNeeder.instance().getServiceContainer().stopQueuingNotifications(ids);
						}
					}
				});
			}
		}
		shiftDown = false;
		if (shiftKeyAwareControl != null) {
			shiftKeyAwareControl.setShiftKeyDown(false);
		}
	}
}
