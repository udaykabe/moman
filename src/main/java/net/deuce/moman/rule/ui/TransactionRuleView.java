package net.deuce.moman.rule.ui;

import java.util.Iterator;
import java.util.List;

import net.deuce.moman.envelope.ui.EnvelopeSelectionDialog;
import net.deuce.moman.envelope.ui.SplitSelectionDialog;
import net.deuce.moman.rule.command.Delete;
import net.deuce.moman.rule.model.Rule;
import net.deuce.moman.service.ServiceNeeder;
import net.deuce.moman.transaction.model.Split;
import net.deuce.moman.ui.AbstractEntityTableView;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public class TransactionRuleView extends AbstractEntityTableView<Rule> {
	
	public static final String ID = TransactionRuleView.class.getName();
	
	public TransactionRuleView() {
		super(ServiceNeeder.instance().getTransactionRuleService());
	}

	@Override
	protected TableViewer createTableViewer(Composite parent) {
		TableViewer tableViewer = new TableViewer(parent, SWT.MULTI | SWT.V_SCROLL | SWT.FULL_SELECTION);
				
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

	@SuppressWarnings("unchecked")
	@Override
	protected void doubleClickHandler(int column,
			StructuredSelection selection, Shell shell) {
		Rule rule = (Rule)selection.getFirstElement();
		List<Split> split = rule.getSplit();
		
		if (rule.getAmount() == null) {
			EnvelopeSelectionDialog dialog = new EnvelopeSelectionDialog(shell, split.get(0).getEnvelope());
			dialog.setAllowBills(true);
			dialog.create();
			dialog.open();
			if (split.get(0).getEnvelope() != dialog.getEnvelope()) {
				ServiceNeeder.instance().getTransactionRuleService().startQueuingNotifications();
				try {
					rule.clearSplit();
					rule.addSplit(dialog.getEnvelope(), null);
					getViewer().refresh(rule);
				} finally {
					ServiceNeeder.instance().getTransactionRuleService().stopQueuingNotifications();
				}
			}
		} else {
			SplitSelectionDialog dialog = new SplitSelectionDialog(shell, rule.getAmount(), split);
			
			dialog.setAllowBills(true);
			dialog.create();
			if (dialog.open() == Window.OK) {
				if (!split.equals(dialog.getSplit())) {
					ServiceNeeder.instance().getTransactionRuleService().startQueuingNotifications();
					try {
						Iterator<Rule> itr = selection.iterator();
						while (itr.hasNext()) {
							rule = itr.next();
							rule.clearSplit();
							for (Split item : dialog.getSplit()) {
								if (rule.getAmount() < 0.0) {
									item.setAmount(-item.getAmount());
								}
								rule.addSplit(item);
							}
							getViewer().refresh(rule);
						}
					} finally {
						ServiceNeeder.instance().getTransactionRuleService().stopQueuingNotifications();
					}
				}
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
