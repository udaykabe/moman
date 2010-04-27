package net.deuce.moman.envelope.ui;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import net.deuce.moman.RcpConstants;
import net.deuce.moman.entity.ServiceProvider;
import net.deuce.moman.entity.model.EntityEvent;
import net.deuce.moman.entity.model.EntityListener;
import net.deuce.moman.entity.model.Frequency;
import net.deuce.moman.entity.model.envelope.Envelope;
import net.deuce.moman.entity.model.income.Income;
import net.deuce.moman.entity.service.EntityService;
import net.deuce.moman.entity.service.envelope.EnvelopeService;
import net.deuce.moman.entity.service.income.IncomeService;
import net.deuce.moman.envelope.command.Delete;
import net.deuce.moman.ui.AbstractEntityTableView;
import net.deuce.moman.ui.SelectingTableViewer;

import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class BudgetView extends AbstractEntityTableView<Envelope> {

	public static final String ID = BudgetView.class.getName();

	public static final String BUDGET_VIEWER_NAME = "budget";

	private Text paySourcesTotalText;
	private Text paySourcesRemainderText;
	private Text budgetTotalText;

	private EnvelopeService envelopeService = ServiceProvider.instance().getEnvelopeService();

	private IncomeService incomeService = ServiceProvider.instance().getIncomeService();

	public BudgetView() {
		super();

		incomeService.addEntityListener(new EntityListener<Income>() {

			public void entityAdded(EntityEvent<Income> event) {
				refresh();
			}

			public void entityChanged(EntityEvent<Income> event) {
				refresh();
			}

			public void entityRemoved(EntityEvent<Income> event) {
				refresh();
			}
		});
	}

	protected String getViewerName() {
		return BUDGET_VIEWER_NAME;
	}

	protected EntityService<Envelope> getService() {
		return envelopeService;
	}

	public void createPartControl(Composite parent) {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		parent.setLayout(gridLayout);

		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;

		Composite topContainer = new Composite(parent, SWT.NONE);
		topContainer.setLayout(new FillLayout(SWT.HORIZONTAL));
		topContainer.setLayoutData(gridData);

		createAvailableForm(topContainer);

		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
		createTableViewer(parent, gridData);
		refresh();
	}

	protected void createAvailableForm(Composite parent) {
		final Composite container = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		container.setLayout(gridLayout);

		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;

		Label titleLabel = new Label(container, SWT.NONE);
		titleLabel.setText("Yearly Budget");

		new Label(container, SWT.NONE); // padding

		Label paySourcesTotalLabel = new Label(container, SWT.NONE);
		paySourcesTotalLabel.setText("Pay source(s) total:");

		paySourcesTotalText = new Text(container, SWT.BORDER);
		paySourcesTotalText.setEditable(false);
		paySourcesTotalText.setEnabled(false);
		paySourcesTotalText.setLayoutData(gridData);

		Label budgetTotalLabel = new Label(container, SWT.NONE);
		budgetTotalLabel.setText("Budget total: ");

		budgetTotalText = new Text(container, SWT.BORDER);
		budgetTotalText.setEditable(false);
		budgetTotalText.setEnabled(false);
		budgetTotalText.setLayoutData(gridData);

		Label paySourcesRemainderLabel = new Label(container, SWT.NONE);
		paySourcesRemainderLabel.setText("Remainder: ");

		paySourcesRemainderText = new Text(container, SWT.BORDER);
		paySourcesRemainderText.setEditable(false);
		paySourcesRemainderText.setEnabled(false);
		paySourcesRemainderText.setLayoutData(gridData);

	}

	protected TableViewer createTableViewer(Composite parent, GridData gridData) {
		final SelectingTableViewer tableViewer = new SelectingTableViewer(
				parent, SWT.MULTI | SWT.V_SCROLL | SWT.FULL_SELECTION);
		tableViewer.setComparator(new BudgetViewerComparator());
		tableViewer.getTable().setLayoutData(gridData);

		// removed for now until a better way to determine table selection
		// can be passed to the command handlers. via command parameter?
		/*
		 * tableViewer.getTable().addKeyListener(new KeyListener() {
		 * 
		 * public void keyPressed(KeyEvent e) { if (e.keyCode == SWT.BS &&
		 * e.stateMask == SWT.COMMAND) { IHandlerService handlerService =
		 * (IHandlerService) getSite().getService(IHandlerService.class); try {
		 * handlerService.executeCommand(getDeleteCommandId(), null); } catch
		 * (Exception ex) { ex.printStackTrace(); } } else if (e.keyCode == 'a'
		 * && e.stateMask == SWT.COMMAND) { tableViewer.getTable().selectAll();
		 * } }
		 * 
		 * 
		 * public void keyReleased(KeyEvent e) { } });
		 */

		TableViewerColumn column = new TableViewerColumn(tableViewer, SWT.NONE);
		column.getColumn().setText("Name");
		column.getColumn().setWidth(200);
		column.setEditingSupport(new BudgetEditingSupport(tableViewer, 0));

		column = new TableViewerColumn(tableViewer, SWT.NONE);
		column.getColumn().setText("Frequency");
		column.getColumn().setWidth(100);
		column.setEditingSupport(new BudgetEditingSupport(tableViewer, 1));

		column = new TableViewerColumn(tableViewer, SWT.RIGHT);
		column.getColumn().setText("Amount");
		column.getColumn().setWidth(100);
		column.setEditingSupport(new BudgetEditingSupport(tableViewer, 2));

		column = new TableViewerColumn(tableViewer, SWT.RIGHT);
		column.getColumn().setText("Envelope");
		column.getColumn().setWidth(100);
		column.setEditingSupport(new EnvelopeSelectionEditingSupport(
				tableViewer, null, tableViewer.getTable()));

		tableViewer.getTable().setFont(RcpConstants.STANDARD_FONT);
		tableViewer.getTable().setHeaderVisible(true);
		tableViewer.getTable().setLinesVisible(true);

		tableViewer.setContentProvider(new EnvelopeListContentProvider());
		tableViewer.setLabelProvider(new BudgetLabelProvider());

		ColumnViewerEditorActivationStrategy actSupport = createColumnViewerEditorActivationStrategy(tableViewer);
		setupTableViewerEditor(tableViewer, actSupport);

		setTableViewer(tableViewer);
		return tableViewer;
	}

	protected String getDeleteCommandId() {
		return Delete.ID;
	}

	protected List<Envelope> getEntities() {
		return envelopeService.getOrderedBudgetedEnvelopes(false);
	}

	protected void refresh() {
		super.refresh();

		double incomeTotal = 0.0;
		for (Income income : incomeService.getEntities()) {
			incomeTotal += income.getAmount() * income.getFrequency().ppy();
		}
		paySourcesTotalText.setText(RcpConstants.CURRENCY_VALIDATOR
				.format(incomeTotal));

		double budgetTotal = 0.0;
		for (Envelope env : envelopeService.getOrderedBudgetedEnvelopes(false)) {
			if (env.isEnabled()) {
				if (env.isSavingsGoal()) {
					int monthCount = calcMonthCountUntilDate(env
							.getSavingsGoalDate());
					double amountPerMonth = (env.getBudget() - env.getBalance())
							/ monthCount;
					budgetTotal += amountPerMonth * Math.min(12, monthCount);
				} else {
					budgetTotal += env.getBudget() * env.getFrequency().ppy();
				}
			}
		}
		budgetTotalText.setText(RcpConstants.CURRENCY_VALIDATOR
				.format(budgetTotal));

		paySourcesRemainderText.setText(RcpConstants.CURRENCY_VALIDATOR
				.format(incomeTotal - budgetTotal));
	}

	public void entityChanged(EntityEvent<Envelope> event) {
		refresh();
	}

	public void entityAdded(EntityEvent<Envelope> event) {
		refresh();
	}

	public void entityRemoved(EntityEvent<Envelope> event) {
		refresh();
	}

	protected SelectingTableViewer createTableViewer(Composite parent) {
		return null;
	}

	public int calcMonthCountUntilDate(Date d) {
		Frequency freq = Frequency.MONTHLY;
		Calendar cal = new GregorianCalendar();
		cal.setTime(new Date());
		cal.add(freq.getCalendarFrequency(), freq.getCardinality());

		int count = 0;
		while (d.after(cal.getTime())) {
			count++;
			cal.add(freq.getCalendarFrequency(), freq.getCardinality());
		}

		return count;
	}
}
