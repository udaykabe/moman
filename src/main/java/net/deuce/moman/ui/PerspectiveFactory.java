package net.deuce.moman.ui;

import net.deuce.moman.account.ui.AccountView;
import net.deuce.moman.allocation.ui.AllocationView;
import net.deuce.moman.envelope.ui.BillView;
import net.deuce.moman.envelope.ui.BudgetView;
import net.deuce.moman.envelope.ui.EnvelopeSpendingView;
import net.deuce.moman.envelope.ui.EnvelopeView;
import net.deuce.moman.envelope.ui.SavingsGoalsView;
import net.deuce.moman.income.ui.IncomeView;
import net.deuce.moman.rule.ui.TransactionRuleView;
import net.deuce.moman.transaction.ui.RegisterView;
import net.deuce.moman.transaction.ui.RepeatingTransactionView;
import net.deuce.moman.transaction.ui.TransactionImportView;
import net.deuce.moman.transaction.ui.TransferView;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class PerspectiveFactory implements IPerspectiveFactory {

	@Override
	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(false);
		layout.addStandaloneView(AccountView.ID, true, IPageLayout.LEFT, 0.25f, editorArea);
		
		IFolderLayout envelopeFolder = layout.createFolder("net.deuce.moman.rcp.view.envelopees", IPageLayout.TOP, 0.6f, editorArea);
		envelopeFolder.addView(EnvelopeView.ID);
		envelopeFolder.addView(IncomeView.ID);
		envelopeFolder.addView(BillView.ID);
		envelopeFolder.addView(AllocationView.ID);
		envelopeFolder.addView(SavingsGoalsView.ID);
		envelopeFolder.addView(BudgetView.ID);
		envelopeFolder.addView(EnvelopeSpendingView.ID);
		
		IFolderLayout transactionsFolder = layout.createFolder("net.deuce.moman.rcp.view.transactions", IPageLayout.BOTTOM, 0.4f, editorArea);
		transactionsFolder.addView(RegisterView.ID);
		transactionsFolder.addView(TransactionImportView.ID);
		transactionsFolder.addView(TransactionRuleView.ID);
		transactionsFolder.addView(TransferView.ID);
		transactionsFolder.addView(RepeatingTransactionView.ID);
		
		// prevent all views from being closed
		layout.getViewLayout(AccountView.ID).setCloseable(false);
		layout.getViewLayout(EnvelopeView.ID).setCloseable(false);
		layout.getViewLayout(IncomeView.ID).setCloseable(false);
		layout.getViewLayout(BillView.ID).setCloseable(false);
		layout.getViewLayout(RegisterView.ID).setCloseable(false);
		layout.getViewLayout(TransactionImportView.ID).setCloseable(false);
		layout.getViewLayout(TransactionRuleView.ID).setCloseable(false);
		layout.getViewLayout(EnvelopeSpendingView.ID).setCloseable(false);
	}

}