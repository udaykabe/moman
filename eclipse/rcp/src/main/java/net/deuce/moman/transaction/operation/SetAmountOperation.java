package net.deuce.moman.transaction.operation;

import net.deuce.moman.entity.model.transaction.InternalTransaction;
import net.deuce.moman.entity.model.transaction.SplitSelectionHandler;
import net.deuce.moman.transaction.ui.SwtSplitSelectionHandler;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class SetAmountOperation extends AbstractOperation {

	private InternalTransaction transaction;
	private Double oldValue;
	private Double newValue;
	private SplitSelectionHandler splitSelectionHandler = new SwtSplitSelectionHandler();

	public SetAmountOperation(InternalTransaction transaction, Double newValue) {
		super("");
		setLabel("Set Transaction Amount");

		if (transaction == null) {
			throw new RuntimeException("Missing parameter 'transaction'");
		}

		this.transaction = transaction;
		this.newValue = newValue;
	}

	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		try {

			oldValue = transaction.getAmount();
			transaction.setAmount(newValue, true, splitSelectionHandler);
			return Status.OK_STATUS;
		} catch (Throwable t) {
			t.printStackTrace();
			return new Status(Status.ERROR, "", t.getMessage(), t);
		}
	}

	public IStatus redo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		try {
			transaction.setAmount(newValue, true, splitSelectionHandler);
			return Status.OK_STATUS;
		} catch (Throwable t) {
			t.printStackTrace();
			return new Status(Status.ERROR, "", t.getMessage(), t);
		}
	}

	public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		try {
			transaction.setAmount(oldValue, true, splitSelectionHandler);
			return Status.OK_STATUS;
		} catch (Throwable t) {
			t.printStackTrace();
			return new Status(Status.ERROR, "", t.getMessage(), t);
		}
	}

}
