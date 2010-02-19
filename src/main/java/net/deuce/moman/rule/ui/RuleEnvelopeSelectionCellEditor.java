package net.deuce.moman.rule.ui;

import net.deuce.moman.envelope.model.Envelope;
import net.deuce.moman.envelope.ui.EnvelopeSelectionCellEditor;
import net.deuce.moman.rule.model.Rule;
import net.deuce.moman.ui.ShiftKeyAware;

import org.eclipse.swt.widgets.Composite;

public class RuleEnvelopeSelectionCellEditor extends EnvelopeSelectionCellEditor {
	
	public RuleEnvelopeSelectionCellEditor(ShiftKeyAware shiftKeyAwareControl, Composite parent) {
		super(shiftKeyAwareControl, parent);
	}

	@Override
	protected void handleEnvelopeSelected(Object data, Envelope env) {
		Rule rule = (Rule)data;
		rule.setEnvelope(env);
	}
	
	@Override
	protected Envelope getInitialEnvelope(Object data) {
		Rule rule = (Rule)data;
		return rule.getEnvelope();
	}

}
