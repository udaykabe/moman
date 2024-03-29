package net.deuce.moman.ui;

import net.deuce.moman.action.Actions;
import net.deuce.moman.menu.RecentlyOpenedFilesMenu;

import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IOperationHistoryListener;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.OperationHistoryEvent;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IContributionManagerOverrides;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.keys.IBindingService;

public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

	private static final String OS_MACOSX = "macosx"; //$NON-NLS-1$

	private IWorkbenchAction newFileAction;
	private IWorkbenchAction openFileAction;
	private IWorkbenchAction saveFileAction;
	private IWorkbenchAction saveFileAsAction;
	private IWorkbenchAction exitAction;

    private IWorkbenchAction importAction;
	private IWorkbenchAction exportAction;

	private IWorkbenchAction undoAction;
	private IWorkbenchAction redoAction;
	private IWorkbenchAction findTransactionAction;

	private IWorkbenchAction newAllocationProfileAction;
	private IWorkbenchAction newAccountAction;
	private IWorkbenchAction editAccountAction;
	private IWorkbenchAction reconcileAccountAction;
	private IWorkbenchAction downloadTransactionsAction;
	private IWorkbenchAction forceDownloadTransactionsAction;

	private IWorkbenchAction newPaySourceAction;

	private IWorkbenchAction newEnvelopeAction;

	private IWorkbenchAction newBillAction;
	private IWorkbenchAction newSavingsGoalAction;

    private IWorkbenchAction newTransactionAction;
	private IWorkbenchAction newRepeatingTransactionAction;
	private IWorkbenchAction newTransactionRuleAction;

	private IWorkbenchAction accountViewAction;
	private IWorkbenchAction envelopeViewAction;
	private IWorkbenchAction paySourceViewAction;
	private IWorkbenchAction billViewAction;
	private IWorkbenchAction allocationViewAction;
	private IWorkbenchAction budgetViewAction;
	private IWorkbenchAction cashFlowReportViewAction;
	private IWorkbenchAction spendingReportViewAction;
	private IWorkbenchAction savingsGoalsViewAction;
	private IWorkbenchAction registerViewAction;
	private IWorkbenchAction importViewAction;
	private IWorkbenchAction importRuleViewAction;
	private IWorkbenchAction transferViewAction;
	private IWorkbenchAction repeatingTransactionViewAction;

	private RecentlyOpenedFilesMenu recentFilesMenu;

	private int menuAboutToShowCount = 0;

	// private IContributionItem perspectivesList;

	public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
		super(configurer);
	}

	protected void makeActions(IWorkbenchWindow window) {
		try {
			newFileAction = Actions.NEW_FILE_ACTION.create(window);
			register(newFileAction);

			openFileAction = Actions.OPEN_FILE_ACTION.create(window);
			register(openFileAction);

			saveFileAction = Actions.SAVE_FILE_ACTION.create(window);
			register(saveFileAction);

			saveFileAsAction = Actions.SAVE_AS_FILE_ACTION.create(window);
			register(saveFileAsAction);

			recentFilesMenu = new RecentlyOpenedFilesMenu();

			importAction = Actions.IMPORT_TRANSACTIONS_ACTION.create(window);
			register(importAction);

			exportAction = Actions.EXPORT_TRANSACTIONS_ACTION.create(window);
			register(exportAction);

			exitAction = ActionFactory.QUIT.create(window);
			register(exitAction);

			undoAction = ActionFactory.UNDO.create(window);
			register(undoAction);

			redoAction = ActionFactory.REDO.create(window);
			register(redoAction);

			findTransactionAction = Actions.FIND_TRANSACTION_ACTION
					.create(window);
			register(findTransactionAction);

			newAccountAction = Actions.NEW_ACCOUNT_ACTION.create(window);
			register(newAccountAction);

			newAllocationProfileAction = Actions.NEW_ALLOCATION_PROFILE_ACTION
					.create(window);
			register(newAllocationProfileAction);

			editAccountAction = Actions.EDIT_ACCOUNT_ACTION.create(window);
			register(editAccountAction);

			reconcileAccountAction = Actions.RECONCILE_ACCOUNT_ACTION
					.create(window);
			register(reconcileAccountAction);

			downloadTransactionsAction = Actions.DOWNLOAD_TRANSACTIONS_ACTION
					.create(window);
			register(downloadTransactionsAction);

			forceDownloadTransactionsAction = Actions.DOWNLOAD_TRANSACTIONS_FULL_ACTION
					.create(window);
			register(forceDownloadTransactionsAction);

			newTransactionAction = Actions.NEW_TRANSACTION_ACTION
					.create(window);
			register(newTransactionAction);

			newRepeatingTransactionAction = Actions.NEW_REPEATING_TRANSACTION_ACTION
					.create(window);
			register(newRepeatingTransactionAction);

			newEnvelopeAction = Actions.NEW_ENVELOPE_ACTION.create(window);
			register(newEnvelopeAction);

			newPaySourceAction = Actions.NEW_PAY_SOURCE_ACTION.create(window);
			register(newPaySourceAction);

			newBillAction = Actions.NEW_BILL_ACTION.create(window);
			register(newBillAction);

			newSavingsGoalAction = Actions.NEW_SAVINGS_GOAL_ACTION
					.create(window);
			register(newSavingsGoalAction);

            IWorkbenchAction fundNegativeEnvelopesAction = Actions.FUND_NEGATIVE_ENVELOPES_ACTION
                    .create(window);
			register(fundNegativeEnvelopesAction);

			newTransactionRuleAction = Actions.NEW_TRANSACTION_RULE_ACTION
					.create(window);
			register(newTransactionRuleAction);

			accountViewAction = Actions.NAVIGATE_ACCOUNTS_ACTION.create(window);
			register(accountViewAction);

			envelopeViewAction = Actions.NAVIGATE_ENVELOPES_ACTION
					.create(window);
			register(envelopeViewAction);

			paySourceViewAction = Actions.NAVIGATE_PAY_SOURCES_ACTION
					.create(window);
			register(paySourceViewAction);

			billViewAction = Actions.NAVIGATE_BILLS_ACTION.create(window);
			register(billViewAction);

			allocationViewAction = Actions.NAVIGATE_ALLOCATION_ACTION
					.create(window);
			register(allocationViewAction);

			savingsGoalsViewAction = Actions.NAVIGATE_SAVINGS_GOALS_ACTION
					.create(window);
			register(savingsGoalsViewAction);

			budgetViewAction = Actions.NAVIGATE_BUDGET_ACTION.create(window);
			register(budgetViewAction);

			cashFlowReportViewAction = Actions.NAVIGATE_CASH_FLOW_REPORT_GOALS_ACTION
					.create(window);
			register(cashFlowReportViewAction);

			spendingReportViewAction = Actions.NAVIGATE_SPENDING_REPORT_ACTION
					.create(window);
			register(spendingReportViewAction);

			registerViewAction = Actions.NAVIGATE_REGISTER_ACTION
					.create(window);
			register(registerViewAction);

			importViewAction = Actions.NAVIGATE_IMPORTS_ACTION.create(window);
			register(importViewAction);

			importRuleViewAction = Actions.NAVIGATE_IMPORT_RULES_ACTION
					.create(window);
			register(importRuleViewAction);

			transferViewAction = Actions.NAVIGATE_TRANSFER_ACTION
					.create(window);
			register(transferViewAction);

			repeatingTransactionViewAction = Actions.NAVIGATE_REPEATING_TRANSACTION_ACTION
					.create(window);
			register(repeatingTransactionViewAction);

            IWorkbenchAction aboutAction = ActionFactory.ABOUT.create(window);
			// aboutAction.setImageDescriptor(
			// AbstractUIPlugin.imageDescriptorFromPlugin( "moman.application",
			// ImageKeys.ABOUT ) );
			register(aboutAction);

            IWorkbenchAction preferencesAction = ActionFactory.PREFERENCES.create(window);
			// preferencesAction.setImageDescriptor(
			// AbstractUIPlugin.imageDescriptorFromPlugin(
			// Application.PLUGIN_ID,
			// ImageKeys.SHOW_PREFERENCES ) );
			register(preferencesAction);

            IWorkbenchAction helpAction = ActionFactory.HELP_CONTENTS.create(window);
			register(helpAction);

            IWorkbenchAction dynamicHelpAction = ActionFactory.DYNAMIC_HELP.create(window);
			register(dynamicHelpAction);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	public void fillActionBars(int flags) {
		super.fillActionBars(flags);
	}

	protected void fillMenuBar(final IMenuManager menuBar) {

		try {
			// Getting the OS
			String os = Platform.getOS();

			// Creating menus
			final MenuManager fileMenu = new MenuManager(
					Messages.getString("ApplicationActionBarAdvisor.file"), "net.deuce.moman.menu.file"); //$NON-NLS-1$
			MenuManager editMenu = new MenuManager(
					Messages.getString("ApplicationActionBarAdvisor.edit"), "net.deuce.moman.edit"); //$NON-NLS-1$
			MenuManager accountMenu = new MenuManager(
					Messages.getString("ApplicationActionBarAdvisor.account"), "net.deuce.moman.menu.account"); //$NON-NLS-1$
			MenuManager toolsMenu = new MenuManager(
					Messages.getString("ApplicationActionBarAdvisor.tools"), "net.deuce.moman.menu.tools"); //$NON-NLS-1$
			MenuManager navigateMenu = new MenuManager(
					Messages.getString("ApplicationActionBarAdvisor.navigate"), "net.deuce.moman.menu.navigate"); //$NON-NLS-1$
			MenuManager helpMenu = new MenuManager(
					Messages.getString("ApplicationActionBarAdvisor.help"), IWorkbenchActionConstants.M_HELP); //$NON-NLS-1$
			MenuManager hiddenMenu = new MenuManager(
					"Hidden", "org.apache.directory.studio.rcp.hidden"); //$NON-NLS-1$ //$NON-NLS-2$
			hiddenMenu.setVisible(false);

			// Adding menus
			menuBar.add(fileMenu);
			menuBar.add(editMenu);
			menuBar.add(accountMenu);
			menuBar.add(toolsMenu);
			menuBar.add(navigateMenu);
			menuBar.add(helpMenu);

			// Populating File Menu
			fileMenu.add(newFileAction);
			fileMenu.add(openFileAction);
			fileMenu.add(new Separator());
			fileMenu.add(saveFileAction);
			fileMenu.add(saveFileAsAction);
			fileMenu.add(new Separator());
			fileMenu.add(recentFilesMenu);

			if (ApplicationActionBarAdvisor.OS_MACOSX.equalsIgnoreCase(os)) {
				// We hide the exit (quit) action, it will be added by the
				// "Carbon" plugin
				hiddenMenu.add(exitAction);
			} else {
				fileMenu.add(new Separator());
				fileMenu.add(exitAction);
			}

			editMenu.add(undoAction);
			editMenu.add(redoAction);

			IWorkbench workbench = Activator.getDefault().getWorkbench();
			final IUndoContext undoContext = workbench.getOperationSupport()
					.getUndoContext();
			final IOperationHistory operHistory = workbench
					.getOperationSupport().getOperationHistory();
			workbench.getOperationSupport().getUndoContext();
			operHistory
					.addOperationHistoryListener(new IOperationHistoryListener() {

						public void historyNotification(
								OperationHistoryEvent event) {
							switch (event.getEventType()) {
							case OperationHistoryEvent.DONE:
							case OperationHistoryEvent.REDONE:
							case OperationHistoryEvent.UNDONE:
								redoAction.setEnabled(operHistory
										.canRedo(undoContext));
								undoAction.setEnabled(operHistory
										.canUndo(undoContext));
								break;
							}
						}
					});

			accountMenu.add(newAccountAction);
			accountMenu.add(editAccountAction);
			accountMenu.add(reconcileAccountAction);
			accountMenu.add(new Separator());
			accountMenu.add(downloadTransactionsAction);
			accountMenu.add(forceDownloadTransactionsAction);
			accountMenu.add(importAction);
			accountMenu.add(exportAction);

			toolsMenu.add(newTransactionAction);
			toolsMenu.add(newRepeatingTransactionAction);
			toolsMenu.add(newEnvelopeAction);
			toolsMenu.add(newBillAction);
			toolsMenu.add(newSavingsGoalAction);
			toolsMenu.add(newPaySourceAction);
			toolsMenu.add(newTransactionRuleAction);
			toolsMenu.add(newAllocationProfileAction);
			toolsMenu.add(new Separator());
			toolsMenu.add(findTransactionAction);

			navigateMenu.add(accountViewAction);
			navigateMenu.add(envelopeViewAction);
			navigateMenu.add(paySourceViewAction);
			navigateMenu.add(billViewAction);
			navigateMenu.add(allocationViewAction);
			navigateMenu.add(savingsGoalsViewAction);
			navigateMenu.add(budgetViewAction);
			navigateMenu.add(cashFlowReportViewAction);
			navigateMenu.add(spendingReportViewAction);
			navigateMenu.add(registerViewAction);
			navigateMenu.add(importViewAction);
			navigateMenu.add(importRuleViewAction);
			navigateMenu.add(transferViewAction);
			navigateMenu.add(repeatingTransactionViewAction);

			((MenuManager) menuBar)
					.setOverrides(new IContributionManagerOverrides() {
						public Integer getAccelerator(IContributionItem item) {
							return null;
						}

						public String getAcceleratorText(IContributionItem item) {
							return null;
						}

						public Boolean getEnabled(IContributionItem item) {
							return null;
						}

						public String getText(IContributionItem item) {
							return null;
						}

						public Boolean getVisible(IContributionItem item) {
							return null;
						}
					});

			accountMenu.addMenuListener(new IMenuListener() {

				public void menuAboutToShow(IMenuManager manager) {
					// System.out.println("ZZZ override: " +
					// menuBar.getOverrides());
					// for (int i=0; i<menuBar.getItems().length; i++) {
					// IContributionItem item = menuBar.getItems()[i];
					// System.out.println("ZZZ item: " + item.getId());
					// }

					if (menuAboutToShowCount++ == 1) {

						for (int i = 0; i < fileMenu.getMenu().getItemCount(); i++) {
							fileMenu.getMenu().getItem(i).addSelectionListener(
									new SelectionListener() {

										public void widgetDefaultSelected(
												SelectionEvent e) {
											System.out.println();
										}

										public void widgetSelected(
												SelectionEvent e) {
											System.out.println();
										}

									});
						}
					}
					IWorkbench workbench = PlatformUI.getWorkbench();
					IBindingService bindingService = (IBindingService) workbench
							.getAdapter(IBindingService.class);
					System.out.println(bindingService.getActiveScheme());
					// for(Binding binding : bindingService.getBindings()) {
					// System.out.println(binding);
					// }
				}

			});
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

}
