package net.deuce.moman.rule.ui;

import net.deuce.moman.envelope.model.Envelope;
import net.deuce.moman.envelope.ui.EnvelopeSelectionDialog;
import net.deuce.moman.rule.command.Delete;
import net.deuce.moman.rule.model.Rule;
import net.deuce.moman.service.ServiceNeeder;
import net.deuce.moman.ui.AbstractEntityTableView;
import net.deuce.moman.ui.SelectingTableViewer;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public class TransactionRuleView extends AbstractEntityTableView<Rule> {
	
	public static final String ID = TransactionRuleView.class.getName();
	
	public TransactionRuleView() {
		super(ServiceNeeder.instance().getTransactionRuleService());
	}

	@Override
	protected SelectingTableViewer createTableViewer(Composite parent) {
		SelectingTableViewer tableViewer = new SelectingTableViewer(parent, SWT.MULTI | SWT.V_SCROLL | SWT.FULL_SELECTION);
				
        TableViewerColumn column = new TableViewerColumn(tableViewer, SWT.CENTER);
 		column.getColumn().setText("Enabled");
 		column.getColumn().setAlignment(SWT.LEFT);
 	    column.getColumn().setWidth(50);
 	    column.setEditingSupport(new RuleEditingSupport(tableViewer, 0));
		
        column = new TableViewerColumn(tableViewer, SWT.LEFT);
 		column.getColumn().setText("Condition");
 	    column.getColumn().setWidth(100);
 	    column.setEditingSupport(new RuleEditingSupport(tableViewer, 1));
		
        column = new TableViewerColumn(tableViewer, SWT.CENTER);
 		column.getColumn().setText("Expression");
 		column.getColumn().setAlignment(SWT.LEFT);
 	    column.getColumn().setWidth(200);
 	    column.setEditingSupport(new RuleEditingSupport(tableViewer, 2));
		
        column = new TableViewerColumn(tableViewer, SWT.CENTER);
 		column.getColumn().setText("Amount");
 		column.getColumn().setAlignment(SWT.LEFT);
 	    column.getColumn().setWidth(200);
 	    column.setEditingSupport(new RuleEditingSupport(tableViewer, 3));
		
        column = new TableViewerColumn(tableViewer, SWT.CENTER);
 		column.getColumn().setText("Converted Value");
 		column.getColumn().setAlignment(SWT.LEFT);
 	    column.getColumn().setWidth(200);
 	    column.setEditingSupport(new RuleEditingSupport(tableViewer, 4));
		
 		column = new TableViewerColumn(tableViewer, SWT.LEFT);
 		column.getColumn().setText("Use This Envelope");
 	    column.getColumn().setWidth(400);
		
	    tableViewer.setContentProvider(new RuleContentProvider());
	    tableViewer.setLabelProvider(new RuleLabelProvider());
	    
		return tableViewer;
	}
	
	@Override
	protected int getNewEntitySelectionColumn() {
		return 2;
	}
	
	@Override
	protected void doubleClickHandler(int column,
			StructuredSelection selection, Shell shell) {
		Rule rule = (Rule)selection.getFirstElement();
		Envelope env = rule.getEnvelope();
		
		EnvelopeSelectionDialog dialog = new EnvelopeSelectionDialog(shell, env);
		dialog.setAllowBills(true);
		dialog.create();
		dialog.open();
		if (env != dialog.getEnvelope()) {
			ServiceNeeder.instance().getTransactionRuleService().startQueuingNotifications();
			try {
				rule.setEnvelope(dialog.getEnvelope());
				getViewer().refresh(rule);
			} finally {
				ServiceNeeder.instance().getTransactionRuleService().stopQueuingNotifications();
			}
		}
	}

	@Override
	protected String getDeleteCommandId() {
		return Delete.ID;
	}

	@Override
	protected int[] getDoubleClickableColumns() {
		return new int[]{5};
	}

}
