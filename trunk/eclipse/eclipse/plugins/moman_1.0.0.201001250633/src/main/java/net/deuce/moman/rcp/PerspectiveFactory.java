package net.deuce.moman.rcp;

import net.deuce.moman.rcp.account.AccountView;
import net.deuce.moman.rcp.envelope.BillView;
import net.deuce.moman.rcp.envelope.EnvelopeView;
import net.deuce.moman.rcp.transaction.RegisterView;
import net.deuce.moman.rcp.transaction.TransactionImportView;
import net.deuce.moman.rcp.transaction.TransactionRuleView;

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
		envelopeFolder.addView(BillView.ID);
		
		IFolderLayout transactionsFolder = layout.createFolder("net.deuce.moman.rcp.view.transactions", IPageLayout.BOTTOM, 0.4f, editorArea);
		transactionsFolder.addView(TransactionImportView.ID);
		transactionsFolder.addView(TransactionRulesView.ID);
		transactionsFolder.addView(RegisterView.ID);
	}

}
