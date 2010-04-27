package net.deuce.moman.transaction.ui;

import java.lang.reflect.InvocationTargetException;

import net.deuce.moman.entity.ServiceProvider;
import net.deuce.moman.entity.model.account.Account;
import net.deuce.moman.entity.model.transaction.InternalTransaction;
import net.deuce.moman.entity.service.transaction.AbstractTransactionProcessor;
import net.deuce.moman.entity.service.transaction.TransactionService;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractSwtTransactionProcessor extends
		AbstractTransactionProcessor {

	private Shell shell;
	private String focusedView;
	private IProgressMonitor progressMonitor;

	private TransactionService transactionService = ServiceProvider.instance().getTransactionService();

	public AbstractSwtTransactionProcessor(Shell shell, Account account,
			boolean force, String focusedView) {
		super(account, force);
		this.shell = shell;
		this.focusedView = focusedView;
	}

	protected void doFetchTransactions() {
		ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);
		try {
			dialog.run(true, true, new IRunnableWithProgress() {

				public void run(IProgressMonitor monitor) {
					monitor.beginTask("Downloading transactions from "
							+ getAccount().getFinancialInstitution().getName(),
							100);
					monitor.worked(1);
					try {
						progressMonitor = monitor;
						Thread progressWorker = new Thread(
								AbstractSwtTransactionProcessor.this);
						progressWorker.start();
						setResult(fetchTransactions());
						monitor.done();
					} catch (Exception e) {
						e.printStackTrace();
						setException(e);
						monitor.done();
					} finally {
						progressMonitor = null;
					}
				}
			});
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	protected void doHandleException(Exception e) {
		String msg;
		if (e.getCause() != null) {
			msg = e.getCause().getMessage();
		} else {
			msg = e.getMessage();
		}
		MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error",
				"Error communicating with the bank: " + msg);
	}

	protected void doProcessTransactions() {
		ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);
		try {
			dialog.run(true, true, new IRunnableWithProgress() {

				public void run(IProgressMonitor monitor) {
					try {
						setProcessedTransactions(processTransactions(
								getAccount(), getResult(), monitor));
						monitor.done();
					} catch (Exception e) {
						e.printStackTrace();
						setException(e);
						monitor.done();
					}
				}
			});

			getAccount().setLastDownloadDate(
					getResult().getLastDownloadedDate());
			getAccount().setOnlineBalance(getResult().getStatementBalance());
			if (getProcessedTransactions() != null
					&& getProcessedTransactions().size() > 0) {
				InternalTransaction firstTransaction = getProcessedTransactions()
						.get(getProcessedTransactions().size() - 1);
				if (firstTransaction.isMatched()) {
					firstTransaction = firstTransaction.getMatchedTransaction();
				}
				transactionService.adjustBalances(firstTransaction, false);
			}

		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	protected void finishUp() {
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPages()[0]
					.showView(focusedView, null, IWorkbenchPage.VIEW_ACTIVATE);
		} catch (PartInitException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public void run() {
		try {
			while (progressMonitor != null) {
				progressMonitor.worked(1);
				Thread.sleep(1000);
			}
		} catch (InterruptedException e) {
		}
	}
}
