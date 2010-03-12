package net.deuce.moman.rule.ui;

import net.deuce.moman.entity.ServiceProvider;
import net.deuce.moman.entity.model.rule.Rule;
import net.deuce.moman.entity.service.EntityService;
import net.deuce.moman.entity.service.rule.TransactionRuleService;
import net.deuce.moman.rule.command.Delete;
import net.deuce.moman.ui.AbstractEntityTableView;
import net.deuce.moman.ui.SelectingTableViewer;

import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class TransactionRuleView extends AbstractEntityTableView<Rule> {

	public static final String ID = TransactionRuleView.class.getName();
	public static final String TRANSACTION_RULE_VIEWER_NAME = "transactionRule";

	private TransactionRuleService transactionRuleService = ServiceProvider.instance().getTransactionRuleService();

	public TransactionRuleView() {
		super();
	}

	protected String getViewerName() {
		return TRANSACTION_RULE_VIEWER_NAME;
	}

	protected EntityService<Rule> getService() {
		return transactionRuleService;
	}

	protected SelectingTableViewer createTableViewer(Composite parent) {
		SelectingTableViewer tableViewer = new SelectingTableViewer(parent,
				SWT.MULTI | SWT.V_SCROLL | SWT.FULL_SELECTION);

		TableViewerColumn column = new TableViewerColumn(tableViewer,
				SWT.CENTER);
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
		column.setEditingSupport(new RuleEnvelopeSelectionEditingSupport(
				tableViewer, null, tableViewer.getTable()));

		tableViewer.setContentProvider(new RuleContentProvider());
		tableViewer.setLabelProvider(new RuleLabelProvider());

		return tableViewer;
	}

	protected int getNewEntitySelectionColumn() {
		return 2;
	}

	protected String getDeleteCommandId() {
		return Delete.ID;
	}

}
