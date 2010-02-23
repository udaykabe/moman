package net.deuce.moman.action;

import net.deuce.moman.ui.Messages;

import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.internal.IWorkbenchGraphicConstants;

public class Actions {

	public static final ActionFactory NEW_FILE_ACTION = new ActionFactory(
			net.deuce.moman.command.file.New.ID,
			net.deuce.moman.command.file.New.ID) {
        
        public IWorkbenchAction create(IWorkbenchWindow window) {
            if (window == null) {
                throw new IllegalArgumentException();
            }
            MomanCommandAction action = new MomanCommandAction(window, getCommandId());
            action.setId(getId());
            action.setActionDefinitionId(getId());
            ISharedImages images = window.getWorkbench().getSharedImages();
            action.setImageDescriptor(images
                    .getImageDescriptor(ISharedImages.IMG_TOOL_NEW_WIZARD));
            action.setDisabledImageDescriptor(images
                    .getImageDescriptor(ISharedImages.IMG_TOOL_NEW_WIZARD_DISABLED));
            action.setText(Messages.getString("FileMenu.new"));
            return action;
        }
    };
    
	public static final ActionFactory OPEN_FILE_ACTION = new ActionFactory(
			net.deuce.moman.command.file.Open.ID,
			net.deuce.moman.command.file.Open.ID) {
        
        public IWorkbenchAction create(IWorkbenchWindow window) {
            if (window == null) {
                throw new IllegalArgumentException();
            }
            MomanCommandAction action = new MomanCommandAction(window, getCommandId());
            action.setId(getId());
            action.setActionDefinitionId(getId());
            ISharedImages images = window.getWorkbench().getSharedImages();
            action.setText(Messages.getString("FileMenu.open"));
            
            return action;
        }
    };
    
	public static final ActionFactory SAVE_FILE_ACTION = new ActionFactory(
			net.deuce.moman.command.file.Save.ID,
			net.deuce.moman.command.file.Save.ID) {
        
        public IWorkbenchAction create(IWorkbenchWindow window) {
            if (window == null) {
                throw new IllegalArgumentException();
            }
            MomanCommandAction action = new MomanCommandAction(window, getCommandId());
            action.setId(getId());
            action.setActionDefinitionId(getId());
            ISharedImages images = window.getWorkbench().getSharedImages();
            action.setImageDescriptor(images
                    .getImageDescriptor(ISharedImages.IMG_ETOOL_SAVE_EDIT));
            action.setDisabledImageDescriptor(images
                    .getImageDescriptor(ISharedImages.IMG_ETOOL_SAVE_EDIT_DISABLED));
            action.setText(Messages.getString("FileMenu.save"));
            return action;
        }
    };
    
	public static final ActionFactory SAVE_AS_FILE_ACTION = new ActionFactory(
			net.deuce.moman.command.file.SaveAs.ID,
			net.deuce.moman.command.file.SaveAs.ID) {
        
        public IWorkbenchAction create(IWorkbenchWindow window) {
            if (window == null) {
                throw new IllegalArgumentException();
            }
            MomanCommandAction action = new MomanCommandAction(window, getCommandId());
            action.setId(getId());
            action.setActionDefinitionId(getId());
            ISharedImages images = window.getWorkbench().getSharedImages();
            action.setImageDescriptor(images
                    .getImageDescriptor(ISharedImages.IMG_ETOOL_SAVEAS_EDIT));
            action.setDisabledImageDescriptor(images
                    .getImageDescriptor(ISharedImages.IMG_ETOOL_SAVEAS_EDIT_DISABLED));
            action.setText(Messages.getString("FileMenu.saveAs"));
            return action;
        }
    };
    
	public static final ActionFactory UNDO_ACTION = new ActionFactory(
			net.deuce.moman.command.edit.Undo.ID,
			net.deuce.moman.command.edit.Undo.ID) {
        
        public IWorkbenchAction create(IWorkbenchWindow window) {
            if (window == null) {
                throw new IllegalArgumentException();
            }
            MomanCommandAction action = new MomanCommandAction(window, getCommandId());
            action.setId(getId());
            action.setActionDefinitionId(getId());
            ISharedImages images = window.getWorkbench().getSharedImages();
            action.setImageDescriptor(images
                    .getImageDescriptor(ISharedImages.IMG_TOOL_UNDO));
            action.setDisabledImageDescriptor(images
                    .getImageDescriptor(ISharedImages.IMG_TOOL_UNDO_DISABLED));
            action.setText(Messages.getString("EditMenu.undo"));
            return action;
        }
    };
    
	public static final ActionFactory REDO_ACTION = new ActionFactory(
			net.deuce.moman.command.edit.Redo.ID,
			net.deuce.moman.command.edit.Redo.ID) {
        
        public IWorkbenchAction create(IWorkbenchWindow window) {
            if (window == null) {
                throw new IllegalArgumentException();
            }
            MomanCommandAction action = new MomanCommandAction(window, getCommandId());
            action.setId(getId());
            action.setActionDefinitionId(getId());
            ISharedImages images = window.getWorkbench().getSharedImages();
            action.setImageDescriptor(images
                    .getImageDescriptor(ISharedImages.IMG_TOOL_REDO));
            action.setDisabledImageDescriptor(images
                    .getImageDescriptor(ISharedImages.IMG_TOOL_REDO_DISABLED));
            action.setText(Messages.getString("EditMenu.redo"));
            return action;
        }
    };
    
	public static final ActionFactory NEW_ACCOUNT_ACTION = new ActionFactory(
			net.deuce.moman.account.command.New.ID,
			net.deuce.moman.account.command.New.ID) {
        
        public IWorkbenchAction create(IWorkbenchWindow window) {
            if (window == null) {
                throw new IllegalArgumentException();
            }
            MomanCommandAction action = new MomanCommandAction(window, getCommandId());
            action.setId(getId());
            action.setActionDefinitionId(getId());
            action.setText(Messages.getString("AccountMenu.new"));
            return action;
        }
    };
    
	public static final ActionFactory NEW_ALLOCATION_PROFILE_ACTION = new ActionFactory(
			net.deuce.moman.allocation.command.NewSet.ID,
			net.deuce.moman.allocation.command.NewSet.ID) {
        
        public IWorkbenchAction create(IWorkbenchWindow window) {
            if (window == null) {
                throw new IllegalArgumentException();
            }
            MomanCommandAction action = new MomanCommandAction(window, getCommandId());
            action.setId(getId());
            action.setActionDefinitionId(getId());
            action.setText(Messages.getString("ToolsMenu.newAllocationProfile"));
            return action;
        }
    };
    
	public static final ActionFactory EDIT_ACCOUNT_ACTION = new ActionFactory(
			net.deuce.moman.account.command.Edit.ID,
			net.deuce.moman.account.command.Edit.ID) {
        
        public IWorkbenchAction create(IWorkbenchWindow window) {
            if (window == null) {
                throw new IllegalArgumentException();
            }
            MomanCommandAction action = new MomanCommandAction(window, getCommandId());
            action.setId(getId());
            action.setActionDefinitionId(getId());
            action.setText(Messages.getString("AccountMenu.edit"));
            return action;
        }
    };
    
	public static final ActionFactory RECONCILE_ACCOUNT_ACTION = new ActionFactory(
			net.deuce.moman.account.command.Reconcile.ID,
			net.deuce.moman.account.command.Reconcile.ID) {
        
        public IWorkbenchAction create(IWorkbenchWindow window) {
            if (window == null) {
                throw new IllegalArgumentException();
            }
            MomanCommandAction action = new MomanCommandAction(window, getCommandId());
            action.setId(getId());
            action.setActionDefinitionId(getId());
            action.setText(Messages.getString("AccountMenu.reconcile"));
            return action;
        }
    };
    
	public static final ActionFactory DOWNLOAD_TRANSACTIONS_ACTION = new ActionFactory(
			net.deuce.moman.account.command.Import.ID,
			net.deuce.moman.account.command.Import.ID) {
        
        public IWorkbenchAction create(IWorkbenchWindow window) {
            if (window == null) {
                throw new IllegalArgumentException();
            }
            MomanCommandAction action = new MomanCommandAction(window, getCommandId());
            action.setId(getId());
            action.setActionDefinitionId(getId());
            ISharedImages images = window.getWorkbench().getSharedImages();
            action.setImageDescriptor(images
                    .getImageDescriptor(ISharedImages.IMG_ELCL_SYNCED));
            action.setDisabledImageDescriptor(images
                    .getImageDescriptor(ISharedImages.IMG_ELCL_SYNCED_DISABLED));
            action.setToolTipText(Messages.getString("AccountMenu.download.tooltip"));
            action.setText(Messages.getString("AccountMenu.download"));
            return action;
        }
    };
    
	public static final ActionFactory DOWNLOAD_TRANSACTIONS_FULL_ACTION = new ActionFactory(
			net.deuce.moman.account.command.FullImport.ID,
			net.deuce.moman.account.command.FullImport.ID) {
        
        public IWorkbenchAction create(IWorkbenchWindow window) {
            if (window == null) {
                throw new IllegalArgumentException();
            }
            MomanCommandAction action = new MomanCommandAction(window, getCommandId());
            action.setId(getId());
            action.setActionDefinitionId(getId());
            action.setToolTipText(Messages.getString("AccountMenu.fullDownload.tooltip"));
            action.setText(Messages.getString("AccountMenu.fullDownload"));
            return action;
        }
    };
    
	public static final ActionFactory IMPORT_TRANSACTIONS_ACTION = new ActionFactory(
			net.deuce.moman.account.command.ImportFile.ID,
			net.deuce.moman.account.command.ImportFile.ID) {
        
        public IWorkbenchAction create(IWorkbenchWindow window) {
            if (window == null) {
                throw new IllegalArgumentException();
            }
            MomanCommandAction action = new MomanCommandAction(window, getCommandId());
            action.setId(getId());
            action.setActionDefinitionId(getId());
            ISharedImages images = window.getWorkbench().getSharedImages();
            action.setImageDescriptor(images
                    .getImageDescriptor(IWorkbenchGraphicConstants.IMG_ETOOL_IMPORT_WIZ));
            action.setText(Messages.getString("AccountMenu.import"));
            return action;
        }
    };
    
	public static final ActionFactory EXPORT_TRANSACTIONS_ACTION = new ActionFactory(
			net.deuce.moman.account.command.ExportFile.ID,
			net.deuce.moman.account.command.ExportFile.ID) {
        
        public IWorkbenchAction create(IWorkbenchWindow window) {
            if (window == null) {
                throw new IllegalArgumentException();
            }
            MomanCommandAction action = new MomanCommandAction(window, getCommandId());
            action.setId(getId());
            action.setActionDefinitionId(getId());
            ISharedImages images = window.getWorkbench().getSharedImages();
            action.setImageDescriptor(images
                    .getImageDescriptor(IWorkbenchGraphicConstants.IMG_ETOOL_EXPORT_WIZ));
            action.setText(Messages.getString("AccountMenu.export"));
            return action;
        }
    };
    
	public static final ActionFactory NEW_TRANSACTION_ACTION = new ActionFactory(
			net.deuce.moman.transaction.command.New.ID,
			net.deuce.moman.transaction.command.New.ID) {
        
        public IWorkbenchAction create(IWorkbenchWindow window) {
            if (window == null) {
                throw new IllegalArgumentException();
            }
            MomanCommandAction action = new MomanCommandAction(window, getCommandId());
            action.setId(getId());
            action.setActionDefinitionId(getId());
            action.setText(Messages.getString("ToolsMenu.newTransaction"));
            return action;
        }
    };
    
    public static final ActionFactory NEW_REPEATING_TRANSACTION_ACTION = new ActionFactory(
			net.deuce.moman.transaction.command.NewRepeating.ID,
			net.deuce.moman.transaction.command.NewRepeating.ID) {
        
        public IWorkbenchAction create(IWorkbenchWindow window) {
            if (window == null) {
                throw new IllegalArgumentException();
            }
            MomanCommandAction action = new MomanCommandAction(window, getCommandId());
            action.setId(getId());
            action.setActionDefinitionId(getId());
            action.setText(Messages.getString("ToolsMenu.newReleatingTransaction"));
            return action;
        }
    };
    
	public static final ActionFactory NEW_ENVELOPE_ACTION = new ActionFactory(
			net.deuce.moman.envelope.command.New.ID,
			net.deuce.moman.envelope.command.New.ID) {
        
        public IWorkbenchAction create(IWorkbenchWindow window) {
            if (window == null) {
                throw new IllegalArgumentException();
            }
            MomanCommandAction action = new MomanCommandAction(window, getCommandId());
            action.setId(getId());
            action.setActionDefinitionId(getId());
            action.setText(Messages.getString("ToolsMenu.newEnvelope"));
            return action;
        }
    };
    
	public static final ActionFactory NEW_PAY_SOURCE_ACTION = new ActionFactory(
			net.deuce.moman.income.command.New.ID,
			net.deuce.moman.income.command.New.ID) {
        
        public IWorkbenchAction create(IWorkbenchWindow window) {
            if (window == null) {
                throw new IllegalArgumentException();
            }
            MomanCommandAction action = new MomanCommandAction(window, getCommandId());
            action.setId(getId());
            action.setActionDefinitionId(getId());
            action.setText(Messages.getString("ToolsMenu.newPaySource"));
            return action;
        }
    };
    
	public static final ActionFactory NEW_BILL_ACTION = new ActionFactory(
			net.deuce.moman.envelope.command.NewBill.ID,
			net.deuce.moman.envelope.command.NewBill.ID) {
        
        public IWorkbenchAction create(IWorkbenchWindow window) {
            if (window == null) {
                throw new IllegalArgumentException();
            }
            MomanCommandAction action = new MomanCommandAction(window, getCommandId());
            action.setId(getId());
            action.setActionDefinitionId(getId());
            action.setText(Messages.getString("ToolsMenu.newBill"));
            return action;
        }
    };
    
	public static final ActionFactory NEW_SAVINGS_GOAL_ACTION = new ActionFactory(
			net.deuce.moman.envelope.command.NewSavingsGoal.ID,
			net.deuce.moman.envelope.command.NewSavingsGoal.ID) {
        
        public IWorkbenchAction create(IWorkbenchWindow window) {
            if (window == null) {
                throw new IllegalArgumentException();
            }
            MomanCommandAction action = new MomanCommandAction(window, getCommandId());
            action.setId(getId());
            action.setActionDefinitionId(getId());
            action.setText(Messages.getString("ToolsMenu.newSavingsGoal"));
            return action;
        }
    };
    
	public static final ActionFactory FUND_NEGATIVE_ENVELOPES_ACTION = new ActionFactory(
			net.deuce.moman.envelope.command.FundNegativeEnvelopes.ID,
			net.deuce.moman.envelope.command.FundNegativeEnvelopes.ID) {
        
        public IWorkbenchAction create(IWorkbenchWindow window) {
            if (window == null) {
                throw new IllegalArgumentException();
            }
            MomanCommandAction action = new MomanCommandAction(window, getCommandId());
            action.setId(getId());
            action.setActionDefinitionId(getId());
            action.setText(Messages.getString("ToolsMenu.fundNegative"));
            return action;
        }
    };
    
	public static final ActionFactory NEW_TRANSACTION_RULE_ACTION = new ActionFactory(
			net.deuce.moman.rule.command.New.ID,
			net.deuce.moman.rule.command.New.ID) {
        
        public IWorkbenchAction create(IWorkbenchWindow window) {
            if (window == null) {
                throw new IllegalArgumentException();
            }
            MomanCommandAction action = new MomanCommandAction(window, getCommandId());
            action.setId(getId());
            action.setActionDefinitionId(getId());
            action.setText(Messages.getString("ToolsMenu.newTransactionRule"));
            return action;
        }
    };
    
	public static final ActionFactory FIND_TRANSACTION_ACTION = new ActionFactory(
			net.deuce.moman.transaction.command.Find.ID,
			net.deuce.moman.transaction.command.Find.ID) {
        
        public IWorkbenchAction create(IWorkbenchWindow window) {
            if (window == null) {
                throw new IllegalArgumentException();
            }
            MomanCommandAction action = new MomanCommandAction(window, getCommandId());
            action.setId(getId());
            action.setActionDefinitionId(getId());
            action.setText(Messages.getString("ToolsMenu.findTransaction"));
            return action;
        }
    };
    
	public static final ActionFactory NAVIGATE_ACCOUNTS_ACTION = new ActionFactory(
			net.deuce.moman.command.navigate.ActivateAccountView.ID,
			net.deuce.moman.command.navigate.ActivateAccountView.ID) {
        
        public IWorkbenchAction create(IWorkbenchWindow window) {
            if (window == null) {
                throw new IllegalArgumentException();
            }
            MomanCommandAction action = new MomanCommandAction(window, getCommandId());
            action.setId(getId());
            action.setActionDefinitionId(getId());
            action.setText(Messages.getString("NavigateMenu.accounts"));
            return action;
        }
    };
    
	public static final ActionFactory NAVIGATE_ENVELOPES_ACTION = new ActionFactory(
			net.deuce.moman.command.navigate.ActivateEnvelopeView.ID,
			net.deuce.moman.command.navigate.ActivateEnvelopeView.ID) {
        
        public IWorkbenchAction create(IWorkbenchWindow window) {
            if (window == null) {
                throw new IllegalArgumentException();
            }
            MomanCommandAction action = new MomanCommandAction(window, getCommandId());
            action.setId(getId());
            action.setActionDefinitionId(getId());
            action.setText(Messages.getString("NavigateMenu.envelopes"));
            return action;
        }
    };
    
	public static final ActionFactory NAVIGATE_PAY_SOURCES_ACTION = new ActionFactory(
			net.deuce.moman.command.navigate.ActivatePaySourceView.ID,
			net.deuce.moman.command.navigate.ActivatePaySourceView.ID) {
        
        public IWorkbenchAction create(IWorkbenchWindow window) {
            if (window == null) {
                throw new IllegalArgumentException();
            }
            MomanCommandAction action = new MomanCommandAction(window, getCommandId());
            action.setId(getId());
            action.setActionDefinitionId(getId());
            action.setText(Messages.getString("NavigateMenu.paySources"));
            return action;
        }
    };
    
	public static final ActionFactory NAVIGATE_BILLS_ACTION = new ActionFactory(
			net.deuce.moman.command.navigate.ActivateBillView.ID,
			net.deuce.moman.command.navigate.ActivateBillView.ID) {
        
        public IWorkbenchAction create(IWorkbenchWindow window) {
            if (window == null) {
                throw new IllegalArgumentException();
            }
            MomanCommandAction action = new MomanCommandAction(window, getCommandId());
            action.setId(getId());
            action.setActionDefinitionId(getId());
            action.setText(Messages.getString("NavigateMenu.bills"));
            return action;
        }
    };
    
   	public static final ActionFactory NAVIGATE_ALLOCATION_ACTION = new ActionFactory(
			net.deuce.moman.command.navigate.ActivateAllocationView.ID,
			net.deuce.moman.command.navigate.ActivateAllocationView.ID) {
        
        public IWorkbenchAction create(IWorkbenchWindow window) {
            if (window == null) {
                throw new IllegalArgumentException();
            }
            MomanCommandAction action = new MomanCommandAction(window, getCommandId());
            action.setId(getId());
            action.setActionDefinitionId(getId());
            action.setText(Messages.getString("NavigateMenu.allocation"));
            return action;
        }
    };
     
   	public static final ActionFactory NAVIGATE_BUDGET_ACTION = new ActionFactory(
			net.deuce.moman.command.navigate.ActivateBudgetView.ID,
			net.deuce.moman.command.navigate.ActivateBudgetView.ID) {
        
        public IWorkbenchAction create(IWorkbenchWindow window) {
            if (window == null) {
                throw new IllegalArgumentException();
            }
            MomanCommandAction action = new MomanCommandAction(window, getCommandId());
            action.setId(getId());
            action.setActionDefinitionId(getId());
            action.setText(Messages.getString("NavigateMenu.budget"));
            return action;
        }
    };
     
	public static final ActionFactory NAVIGATE_CASH_FLOW_REPORT_GOALS_ACTION = new ActionFactory(
			net.deuce.moman.command.navigate.ActivateCashFlowReportView.ID,
			net.deuce.moman.command.navigate.ActivateCashFlowReportView.ID) {
        
        public IWorkbenchAction create(IWorkbenchWindow window) {
            if (window == null) {
                throw new IllegalArgumentException();
            }
            MomanCommandAction action = new MomanCommandAction(window, getCommandId());
            action.setId(getId());
            action.setActionDefinitionId(getId());
            action.setText(Messages.getString("NavigateMenu.cashFlowReport"));
            return action;
        }
    };
        
   	public static final ActionFactory NAVIGATE_SPENDING_REPORT_ACTION = new ActionFactory(
			net.deuce.moman.command.navigate.ActivateSpendingReportView.ID,
			net.deuce.moman.command.navigate.ActivateSpendingReportView.ID) {
        
        public IWorkbenchAction create(IWorkbenchWindow window) {
            if (window == null) {
                throw new IllegalArgumentException();
            }
            MomanCommandAction action = new MomanCommandAction(window, getCommandId());
            action.setId(getId());
            action.setActionDefinitionId(getId());
            action.setText(Messages.getString("NavigateMenu.spendingReport"));
            return action;
        }
    };
     
	public static final ActionFactory NAVIGATE_SAVINGS_GOALS_ACTION = new ActionFactory(
			net.deuce.moman.command.navigate.ActivateSavingsGoalsView.ID,
			net.deuce.moman.command.navigate.ActivateSavingsGoalsView.ID) {
        
        public IWorkbenchAction create(IWorkbenchWindow window) {
            if (window == null) {
                throw new IllegalArgumentException();
            }
            MomanCommandAction action = new MomanCommandAction(window, getCommandId());
            action.setId(getId());
            action.setActionDefinitionId(getId());
            action.setText(Messages.getString("NavigateMenu.savingsGoals"));
            return action;
        }
    };
    
	public static final ActionFactory NAVIGATE_REGISTER_ACTION = new ActionFactory(
			net.deuce.moman.command.navigate.ActivateRegisterView.ID,
			net.deuce.moman.command.navigate.ActivateRegisterView.ID) {
        
        public IWorkbenchAction create(IWorkbenchWindow window) {
            if (window == null) {
                throw new IllegalArgumentException();
            }
            MomanCommandAction action = new MomanCommandAction(window, getCommandId());
            action.setId(getId());
            action.setActionDefinitionId(getId());
            action.setText(Messages.getString("NavigateMenu.register"));
            return action;
        }
    };
    
	public static final ActionFactory NAVIGATE_IMPORTS_ACTION = new ActionFactory(
			net.deuce.moman.command.navigate.ActivateImportView.ID,
			net.deuce.moman.command.navigate.ActivateImportView.ID) {
        
        public IWorkbenchAction create(IWorkbenchWindow window) {
            if (window == null) {
                throw new IllegalArgumentException();
            }
            MomanCommandAction action = new MomanCommandAction(window, getCommandId());
            action.setId(getId());
            action.setActionDefinitionId(getId());
            action.setText(Messages.getString("NavigateMenu.imports"));
            return action;
        }
    };
    
	public static final ActionFactory NAVIGATE_IMPORT_RULES_ACTION = new ActionFactory(
			net.deuce.moman.command.navigate.ActivateImportRuleView.ID,
			net.deuce.moman.command.navigate.ActivateImportRuleView.ID) {
        
        public IWorkbenchAction create(IWorkbenchWindow window) {
            if (window == null) {
                throw new IllegalArgumentException();
            }
            MomanCommandAction action = new MomanCommandAction(window, getCommandId());
            action.setId(getId());
            action.setActionDefinitionId(getId());
            action.setText(Messages.getString("NavigateMenu.importRules"));
            return action;
        }
    };
    
	public static final ActionFactory NAVIGATE_TRANSFER_ACTION = new ActionFactory(
			net.deuce.moman.command.navigate.ActivateTransferView.ID,
			net.deuce.moman.command.navigate.ActivateTransferView.ID) {
        
        public IWorkbenchAction create(IWorkbenchWindow window) {
            if (window == null) {
                throw new IllegalArgumentException();
            }
            MomanCommandAction action = new MomanCommandAction(window, getCommandId());
            action.setId(getId());
            action.setActionDefinitionId(getId());
            action.setText(Messages.getString("NavigateMenu.transfer"));
            return action;
        }
    };
    
	public static final ActionFactory NAVIGATE_REPEATING_TRANSACTION_ACTION = new ActionFactory(
			net.deuce.moman.command.navigate.ActivateRepeatingTransactionView.ID,
			net.deuce.moman.command.navigate.ActivateRepeatingTransactionView.ID) {
        
        public IWorkbenchAction create(IWorkbenchWindow window) {
            if (window == null) {
                throw new IllegalArgumentException();
            }
            MomanCommandAction action = new MomanCommandAction(window, getCommandId());
            action.setId(getId());
            action.setActionDefinitionId(getId());
            action.setText(Messages.getString("NavigateMenu.repeatingTransaction"));
            return action;
        }
    };
    
}
