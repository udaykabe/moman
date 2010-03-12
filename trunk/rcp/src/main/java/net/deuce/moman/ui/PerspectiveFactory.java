package net.deuce.moman.ui;

import net.deuce.moman.account.ui.AccountView;
import net.deuce.moman.allocation.ui.AllocationView;
import net.deuce.moman.envelope.ui.BillView;
import net.deuce.moman.envelope.ui.BudgetView;
import net.deuce.moman.envelope.ui.EnvelopeView;
import net.deuce.moman.envelope.ui.SavingsGoalsView;
import net.deuce.moman.income.ui.IncomeView;
import net.deuce.moman.report.CashFlowReportView;
import net.deuce.moman.report.EnvelopeBreakdownReportView;
import net.deuce.moman.report.SpendingReportView;
import net.deuce.moman.rule.ui.TransactionRuleView;
import net.deuce.moman.transaction.ui.RegisterView;
import net.deuce.moman.transaction.ui.RepeatingTransactionView;
import net.deuce.moman.transaction.ui.TransactionImportView;
import net.deuce.moman.transaction.ui.TransferView;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class PerspectiveFactory implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(false);

		layout.addStandaloneView(AccountView.ID, true, IPageLayout.LEFT, 0.25f,
				editorArea);

		IFolderLayout envelopeFolder = layout.createFolder(
				"net.deuce.moman.rcp.view.envelopees", IPageLayout.TOP, 0.6f,
				editorArea);

		envelopeFolder.addView(EnvelopeView.ID);

		String[] placeholders = new String[] { IncomeView.ID, BillView.ID,
				AllocationView.ID, SavingsGoalsView.ID, BudgetView.ID,
				CashFlowReportView.ID, SpendingReportView.ID,
				EnvelopeBreakdownReportView.ID, };

		for (String partId : placeholders) {
			envelopeFolder.addPlaceholder(partId);
		}

		IFolderLayout transactionsFolder = layout.createFolder(
				"net.deuce.moman.rcp.view.transactions", IPageLayout.BOTTOM,
				0.4f, editorArea);
		transactionsFolder.addView(RegisterView.ID);

		placeholders = new String[] { TransactionImportView.ID,
				TransactionRuleView.ID, TransferView.ID,
				RepeatingTransactionView.ID, };

		for (String partId : placeholders) {
			transactionsFolder.addPlaceholder(partId);
		}

		/*
		 * layout.getViewLayout(AccountView.ID).setCloseable(false);
		 * layout.getViewLayout(EnvelopeView.ID).setCloseable(false);
		 * layout.getViewLayout(IncomeView.ID).setCloseable(false);
		 * layout.getViewLayout(RegisterView.ID).setCloseable(false);
		 * layout.getViewLayout(TransactionImportView.ID).setCloseable(false);
		 * layout.getViewLayout(TransactionRuleView.ID).setCloseable(false);
		 * layout.getViewLayout(EnvelopeSpendingView.ID).setCloseable(false);
		 */

	}

}
