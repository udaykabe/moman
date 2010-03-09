package net.deuce.moman.transaction.operation;

import net.deuce.moman.transaction.model.InternalTransaction;

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

	public SetAmountOperation(InternalTransaction transaction, Double newValue) {
		super("");
		setLabel("Set Transaction Amount");
		
		if (transaction == null) {
			throw new RuntimeException("Missing parameter 'transaction'");
		}

		this.transaction = transaction;
		this.newValue = newValue;
	}
	
	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		try {
			
			oldValue = transaction.getAmount();
			transaction.setAmount(newValue, true);
			return Status.OK_STATUS;
		} catch (Throwable t) {
			t.printStackTrace();
			return new Status(Status.ERROR, "", t.getMessage(), t);
		}
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		try {
			transaction.setAmount(newValue, true);
			return Status.OK_STATUS;
		} catch (Throwable t) {
			t.printStackTrace();
			return new Status(Status.ERROR, "", t.getMessage(), t);
		}
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		try {
			transaction.setAmount(oldValue, true);
			return Status.OK_STATUS;
		} catch (Throwable t) {
			t.printStackTrace();
			return new Status(Status.ERROR, "", t.getMessage(), t);
		}
	}

}
