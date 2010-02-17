package net.deuce.moman.envelope.ui;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import net.deuce.moman.Constants;
import net.deuce.moman.envelope.command.Delete;
import net.deuce.moman.envelope.model.Envelope;
import net.deuce.moman.envelope.service.EnvelopeService;
import net.deuce.moman.income.model.Income;
import net.deuce.moman.income.service.IncomeService;
import net.deuce.moman.model.EntityEvent;
import net.deuce.moman.model.EntityListener;
import net.deuce.moman.model.Frequency;
import net.deuce.moman.service.ServiceContainer;
import net.deuce.moman.service.ServiceNeeder;
import net.deuce.moman.ui.AbstractEntityTableView;
import net.deuce.moman.ui.SelectingTableViewer;

import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class BudgetView extends AbstractEntityTableView<Envelope> {
	
	public static final String ID = BudgetView.class.getName();
	
	private ServiceContainer serviceContainer;
	private EnvelopeService envelopeService;
	private IncomeService incomeService;
	private Text paySourcesTotalText;
	private Text paySourcesRemainderText;
	private Text budgetTotalText;

	public BudgetView() {
		super(ServiceNeeder.instance().getEnvelopeService());
		envelopeService = ServiceNeeder.instance().getEnvelopeService();
		incomeService = ServiceNeeder.instance().getIncomeService();
		serviceContainer = ServiceNeeder.instance().getServiceContainer();
		
		incomeService.addEntityListener(new EntityListener<Income>() {
			@Override
			public void entityAdded(EntityEvent<Income> event) {
				refresh();
			}
			@Override
			public void entityChanged(EntityEvent<Income> event) {
				refresh();
			}
			@Override
			public void entityRemoved(EntityEvent<Income> event) {
				refresh();
			}
		});
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
		
		new Label(container, SWT.NONE); //padding
		
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
		final SelectingTableViewer tableViewer = new SelectingTableViewer(parent, SWT.MULTI | SWT.V_SCROLL | SWT.FULL_SELECTION);    
		tableViewer.setComparator(new BudgetViewerComparator());
		tableViewer.getTable().setLayoutData(gridData);
		
		tableViewer.addDoubleClickListener(getDoubleClickListener(parent.getShell()));
		
		// removed for now until a better way to determine table selection
		// can be passed to the command handlers.  via command parameter?
		/*
		tableViewer.getTable().addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.BS && e.stateMask == SWT.COMMAND) {
					IHandlerService handlerService = (IHandlerService) getSite().getService(IHandlerService.class);
					try {
						handlerService.executeCommand(getDeleteCommandId(), null);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				} else if (e.keyCode == 'a' && e.stateMask == SWT.COMMAND) {
					tableViewer.getTable().selectAll();
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}
 		});
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
 	    
 		tableViewer.getTable().setFont(Constants.STANDARD_FONT);
 		tableViewer.getTable().setHeaderVisible(true);
 		tableViewer.getTable().setLinesVisible(true);
 		
	    tableViewer.setContentProvider(new EnvelopeListContentProvider());
	    tableViewer.setLabelProvider(new BudgetLabelProvider());
	    
	    ColumnViewerEditorActivationStrategy actSupport = createColumnViewerEditorActivationStrategy(tableViewer);
 		setupTableViewerEditor(tableViewer, actSupport);
		
	    setTableViewer(tableViewer);
		return tableViewer;
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void doubleClickHandler(int column, StructuredSelection selection, Shell shell) {
		Envelope parentEnvelope = null;
		Iterator<Envelope> itr = selection.iterator();
		while (itr.hasNext()) {
			Envelope env = itr.next();
			if (parentEnvelope == null) {
				parentEnvelope = env.getParent();
			} else if (parentEnvelope != env.getParent()) {
				parentEnvelope = envelopeService.getRootEnvelope();
				break;
			}
		}
		EnvelopeSelectionDialog dialog = new EnvelopeSelectionDialog(shell, parentEnvelope);
		
		dialog.create();
		dialog.open();
		if (parentEnvelope != dialog.getEnvelope()) {
			
			serviceContainer.startQueuingNotifications();
			try {
				itr = selection.iterator();
				while (itr.hasNext()) {
					Envelope env = itr.next();
					Envelope oldParent = env.getParent();
					if (oldParent != null) {
						oldParent.removeChild(env);
					}
					env.setParent(dialog.getEnvelope());
					dialog.getEnvelope().addChild(env);
				}
			} finally {
				serviceContainer.stopQueuingNotifications();
			}
		}
	}

	@Override
	protected String getDeleteCommandId() {
		return Delete.ID;
	}

	@Override
	protected int[] getDoubleClickableColumns() {
		return new int[]{3};
	}

	protected IDoubleClickListener getDoubleClickListener(Shell shell) {
		return super.getDoubleClickListener(shell);
	}

	@Override
	protected List<Envelope> getEntities() {
		return envelopeService.getOrderedBudgetedEnvelopes(false);
	}

	@Override
	protected void refresh() {
		super.refresh();
		
		double incomeTotal = 0.0;
		for (Income income : incomeService.getEntities()) {
			incomeTotal += income.getAmount() * income.getFrequency().ppy();
		}
		paySourcesTotalText.setText(Constants.CURRENCY_VALIDATOR.format(incomeTotal));
		
		double budgetTotal = 0.0;
		for (Envelope env : envelopeService.getOrderedBudgetedEnvelopes(false)) {
			if (env.isEnabled()) {
				if (env.isSavingsGoal()) {
					int monthCount = calcMonthCountUntilDate(env.getSavingsGoalDate());
					double amountPerMonth = (env.getBudget() - env.getBalance()) / monthCount;
					budgetTotal += amountPerMonth * Math.min(12, monthCount);
				} else {
					budgetTotal += env.getBudget() * env.getFrequency().ppy();
				}
			}
		}
		budgetTotalText.setText(Constants.CURRENCY_VALIDATOR.format(budgetTotal));
		
		paySourcesRemainderText.setText(Constants.CURRENCY_VALIDATOR.format(incomeTotal - budgetTotal));
	}

	@Override
	public void entityChanged(EntityEvent<Envelope> event) {
		refresh();
	}

	@Override
	public void entityAdded(EntityEvent<Envelope> event) {
		refresh();
	}

	@Override
	public void entityRemoved(EntityEvent<Envelope> event) {
		refresh();
	}

	@Override
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
