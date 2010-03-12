package net.deuce.moman.undo;

import net.deuce.moman.entity.model.transaction.InternalTransaction;
import net.deuce.moman.transaction.operation.SetAmountOperation;

public class TransactionUndoAdapter extends
		EntityUndoAdapter<InternalTransaction> {

	public TransactionUndoAdapter(InternalTransaction entity) {
		super(entity);
	}

	public void executeSetAmount(Double value) {
		SetAmountOperation operation = new SetAmountOperation(getEntity(),
				value);
		executeOperation(operation);
	}
}
