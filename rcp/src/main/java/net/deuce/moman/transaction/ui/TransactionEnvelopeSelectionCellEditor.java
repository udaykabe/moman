package net.deuce.moman.transaction.ui;

import java.util.List;

import net.deuce.moman.envelope.model.Envelope;
import net.deuce.moman.envelope.ui.EnvelopeSelectionCellEditor;
import net.deuce.moman.transaction.model.InternalTransaction;
import net.deuce.moman.transaction.model.Split;
import net.deuce.moman.ui.ShiftKeyAware;

import org.eclipse.swt.widgets.Composite;

public class TransactionEnvelopeSelectionCellEditor extends EnvelopeSelectionCellEditor {
	
	public TransactionEnvelopeSelectionCellEditor(ShiftKeyAware shiftKeyAwareControl, Composite parent) {
		super(shiftKeyAwareControl, parent);
	}

	@Override
	protected void handleEnvelopeSelected(Object data, Envelope env) {
		InternalTransaction transaction = (InternalTransaction)data;
		transaction.clearSplit();
		transaction.addSplit(env, transaction.getAmount(), true);
	}
	
	@Override
	protected Envelope getInitialEnvelope(Object data) {
		InternalTransaction transaction = (InternalTransaction)data;
		return transaction.getSplit().get(0).getEnvelope();
	}

	@Override
	protected boolean forceSplit(Object data) {
		InternalTransaction transaction = (InternalTransaction)data;
		return transaction.getSplit().size() > 1;
	}
	
	@Override
	protected Double getSplitAmount(Object value) {
		return ((InternalTransaction)value).getAmount();
	}
	
	@Override
	protected List<Split> getSplit(Object value) {
		return ((InternalTransaction)value).getSplit();
	}

	@Override
	protected void handleSplitSelected(Object data, List<Split> result) {
		InternalTransaction transaction = (InternalTransaction)data;

		transaction.clearSplit();
		
		for (Split item : result) {
			if (transaction.getAmount() < 0.0) {
				item.setAmount(-item.getAmount());
			}
			transaction.addSplit(item, true);
		}	
		
	}
	
}
