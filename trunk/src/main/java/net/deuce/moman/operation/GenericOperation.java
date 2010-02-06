package net.deuce.moman.operation;

import net.deuce.moman.ui.Activator;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;

public abstract class GenericOperation extends AbstractOperation {
	
	public GenericOperation() {
		super("");
	}
	
	protected abstract void doExecute(IProgressMonitor monitor);
	protected abstract void doUndo(IProgressMonitor monitor);
	protected abstract void doRedo(IProgressMonitor monitor);
	
	@Override
	public IStatus execute(final IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		try {
			BusyIndicator.showWhile(Display.getCurrent(), new Runnable() {
				@Override
				public void run() {
					doExecute(monitor);
				}
			});
			return Status.OK_STATUS;
		} catch (Throwable t) {
			t.printStackTrace();
			return new Status(Status.ERROR, "", t.getMessage(), t);
		}
	}

	@Override
	public IStatus redo(final IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		try {
			BusyIndicator.showWhile(Display.getCurrent(), new Runnable() {
				@Override
				public void run() {
					doRedo(monitor);
				}
			});
			return Status.OK_STATUS;
		} catch (Throwable t) {
			t.printStackTrace();
			return new Status(Status.ERROR, "", t.getMessage(), t);
		}
	}

	@Override
	public IStatus undo(final IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		try {
			BusyIndicator.showWhile(Display.getCurrent(), new Runnable() {
				@Override
				public void run() {
					doUndo(monitor);
				}
			});
			return Status.OK_STATUS;
		} catch (Throwable t) {
			t.printStackTrace();
			return new Status(Status.ERROR, "", t.getMessage(), t);
		}
	}

	public void execute() {
		
		IWorkbench workbench = Activator.getDefault().getWorkbench();
		IOperationHistory operHistory = 
		workbench.getOperationSupport().getOperationHistory();
		IUndoContext myContext = 
		workbench.getOperationSupport().getUndoContext();
		this.addContext(myContext);
		try {
			operHistory.execute(this, new NullProgressMonitor(), null);
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}
}
