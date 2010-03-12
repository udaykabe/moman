package net.deuce.moman.allocation.ui;

import java.util.LinkedList;
import java.util.List;

import net.deuce.moman.RcpConstants;
import net.deuce.moman.account.ui.AccountView;
import net.deuce.moman.allocation.command.Delete;
import net.deuce.moman.allocation.command.DeleteSet;
import net.deuce.moman.entity.ServiceProvider;
import net.deuce.moman.entity.model.EntityEvent;
import net.deuce.moman.entity.model.EntityListener;
import net.deuce.moman.entity.model.EntityMonitor;
import net.deuce.moman.entity.model.account.Account;
import net.deuce.moman.entity.model.allocation.Allocation;
import net.deuce.moman.entity.model.allocation.AllocationSet;
import net.deuce.moman.entity.model.allocation.AmountType;
import net.deuce.moman.entity.model.allocation.LimitType;
import net.deuce.moman.entity.model.envelope.Envelope;
import net.deuce.moman.entity.model.income.Income;
import net.deuce.moman.entity.model.transaction.InternalTransaction;
import net.deuce.moman.entity.service.ServiceManager;
import net.deuce.moman.entity.service.allocation.AllocationSetService;
import net.deuce.moman.entity.service.envelope.EnvelopeService;
import net.deuce.moman.entity.service.income.IncomeService;
import net.deuce.moman.entity.service.transaction.TransactionService;
import net.deuce.moman.envelope.ui.EnvelopeSelectionDialog;
import net.deuce.moman.ui.SelectingTableViewer;
import net.deuce.moman.ui.ViewerRegistry;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TableViewerEditor;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.ViewPart;

public class AllocationView extends ViewPart implements
		EntityListener<AllocationSet> {

	public static final String ID = AllocationView.class.getName();

	public static final String ALLOCATION_SET_VIEWER_NAME = "allocationSet";
	public static final String ALLOCATION_VIEWER_NAME = "allocation";

	private AllocationSetService allocationSetService = ServiceProvider.instance().getAllocationSetService();

	private EnvelopeService envelopeService = ServiceProvider.instance().getEnvelopeService();

    private ServiceManager serviceManager = ServiceProvider.instance().getServiceManager();

	private ViewerRegistry viewerRegistry = ViewerRegistry.instance();

	private SelectingTableViewer profileListViewer;
	private SelectingTableViewer profileViewer;
	private boolean editingAllocation = false;
	private AllocationSet allocationSet = null;
	private Text availableAmountText;
	private Text allocationAmountText;
	private Text paySourceRemainderText;
	private Text allocationSetTotalText;
    private Text paySourceAmountText;
	private EntityMonitor<Allocation> allocationMonitor = new EntityMonitor<Allocation>();

	public AllocationView() {
		allocationSetService.addEntityListener(this);

		envelopeService.addEntityListener(new EntityListener<Envelope>() {

			public void entityAdded(EntityEvent<Envelope> event) {
			}

			public void entityChanged(EntityEvent<Envelope> event) {
				resetAvailableAmounts();
				adjustAllocations();
			}

			public void entityRemoved(EntityEvent<Envelope> event) {
			}
		});

        IncomeService incomeService = ServiceProvider.instance().getIncomeService();
        incomeService.addEntityListener(new EntityListener<Income>() {

			public void entityAdded(EntityEvent<Income> event) {
			}

			public void entityChanged(EntityEvent<Income> event) {
				resetAvailableAmounts();
				adjustAllocations();
			}

			public void entityRemoved(EntityEvent<Income> event) {
			}

		});

        TransactionService transactionService = ServiceProvider.instance().getTransactionService();
        transactionService
				.addEntityListener(new EntityListener<InternalTransaction>() {

					public void entityAdded(
							EntityEvent<InternalTransaction> event) {
						resetAvailableAmounts();
						adjustAllocations();
					}

					public void entityChanged(
							EntityEvent<InternalTransaction> event) {
						resetAvailableAmounts();
						adjustAllocations();
					}

					public void entityRemoved(
							EntityEvent<InternalTransaction> event) {
						resetAvailableAmounts();
						adjustAllocations();
					}

				});

		allocationMonitor.addListener(new EntityListener<Allocation>() {

			public void entityAdded(EntityEvent<Allocation> event) {
			}

			public void entityChanged(EntityEvent<Allocation> event) {
				if (event != null && event.getEntity() != null) {
					if (Allocation.Properties.amount
							.equals(event.getProperty())
							|| Allocation.Properties.limit.equals(event
									.getProperty())
							|| Allocation.Properties.amountType.equals(event
									.getProperty())
							|| Allocation.Properties.limitType.equals(event
									.getProperty())) {
						adjustAllocations();
					} else if (Allocation.Properties.proposed.equals(event
							.getProperty())
							|| Allocation.Properties.remainder.equals(event
									.getProperty())) {
						refreshProfile();
					}
				}
			}

			public void entityRemoved(EntityEvent<Allocation> event) {
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
		createExecutionButton(topContainer);

		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
		createTableContainer(parent, gridData);
		refresh();
	}

	protected void createExecutionButton(final Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new RowLayout(SWT.HORIZONTAL));

		Button execute = new Button(container, SWT.PUSH);
		execute.setText("Distribute Funds");
		execute.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				distributeFunds(parent.getShell());
			}
		});

		Button build = new Button(container, SWT.PUSH);
		build.setText("Build Profile");
		build.setToolTipText("Build profile based on bills and pay source");
		build.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				buildProfile(parent.getShell());
			}
		});

	}

	protected void buildProfile(Shell shell) {
		BuildProfileWizard wizard = new BuildProfileWizard();
		WizardDialog dialog = new WizardDialog(shell, wizard);
		dialog.open();
	}

	protected void distributeFunds(Shell shell) {
		StructuredSelection selection = (StructuredSelection) profileListViewer
				.getSelection();

		if (selection.size() == 0) {
			MessageDialog.openInformation(shell,
					"Select an allocation profile",
					"Please select one allocation profile.");
			return;
		}

		if (selection.size() > 1) {
			MessageDialog.openError(shell, "Error",
					"Please select only one allocation profile.");
			return;
		}

		final AllocationSet allocationSet = (AllocationSet) selection
				.getFirstElement();

		selection = (StructuredSelection) viewerRegistry.getViewer(
				AccountView.ACCOUNT_VIEWER_NAME).getSelection();

		if (selection.size() == 0) {
			MessageDialog.openInformation(shell, "Select an account",
					"Please select one account.");
			return;
		}

		if (selection.size() > 1) {
			MessageDialog.openError(shell, "Error",
					"Please select only one account.");
			return;
		}

		final Account account = (Account) selection.getFirstElement();
		final Envelope available = envelopeService.getAvailableEnvelope();

		if (allocationSet.getAllocations().size() > 0) {

			BusyIndicator.showWhile(shell.getDisplay(), new Runnable() {

				public void run() {
					List<String> ids = serviceManager
							.startQueuingNotifications();
					try {
						for (Allocation allocation : allocationSet
								.getAllocations()) {
							envelopeService.transfer(account, account,
									available, allocation.getEnvelope(),
									allocation.getProposed());
						}
					} finally {
						serviceManager.stopQueuingNotifications(ids);
					}
				}
			});
		}

	}

	protected void createAvailableForm(Composite parent) {
		final Composite container = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		container.setLayout(gridLayout);

		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;

        Label paySourceAmountLabel = new Label(container, SWT.NONE);
		paySourceAmountLabel.setText("Pay source amount:");

		paySourceAmountText = new Text(container, SWT.BORDER);
		paySourceAmountText.setEditable(false);
		paySourceAmountText.setEnabled(false);
		paySourceAmountText.setLayoutData(gridData);

		Label allocationSetTotalLabel = new Label(container, SWT.NONE);
		allocationSetTotalLabel.setText("Allocation Set Total: ");

		allocationSetTotalText = new Text(container, SWT.BORDER);
		allocationSetTotalText.setEditable(false);
		allocationSetTotalText.setEnabled(false);
		allocationSetTotalText.setLayoutData(gridData);

		Label paySourceRemainderLabel = new Label(container, SWT.NONE);
		paySourceRemainderLabel.setText("Pay Source Remainder: ");

		paySourceRemainderText = new Text(container, SWT.BORDER);
		paySourceRemainderText.setEditable(false);
		paySourceRemainderText.setEnabled(false);
		paySourceRemainderText.setLayoutData(gridData);

		Label availableLabel = new Label(container, SWT.NONE);
		availableLabel.setText("Available funds:");
		availableAmountText = new Text(container, SWT.BORDER);
		availableAmountText.setEditable(false);
		availableAmountText.setEnabled(false);
		availableAmountText.setLayoutData(gridData);

		Label allocationAmountLabel = new Label(container, SWT.NONE);
		allocationAmountLabel.setText("Allocation Amount:");
		allocationAmountText = new Text(container, SWT.BORDER);
		allocationAmountText.setLayoutData(gridData);
		allocationAmountText.addListener(SWT.Verify, new Listener() {
			public void handleEvent(Event e) {
				e.doit = true;
				if (allocationAmountText.getText().length() == 0
						&& RcpConstants.CURRENCY_VALIDATOR.isValid(e.text)) {
					return;
				}
				if (e.text == null || e.text.length() == 0)
					return;
				char c = e.text.charAt(0);
				if (c != '$' && ('0' > c || c > '9') && c != '.') {
					e.doit = false;
					return;
				}

				String text = allocationAmountText.getText();
				if (c == '$' && text.length() == 0) {
					return;
				}

				if (c == '$' && text.length() > 0) {
					e.doit = false;
					return;
				}

				if (!RcpConstants.CURRENCY_VALIDATOR.isValid(text + c)) {
					e.doit = false;
					return;
				}

				double value = RcpConstants.CURRENCY_VALIDATOR.validate(
						text + c).doubleValue();
				e.doit = value < envelopeService.getAvailableEnvelope()
						.getBalance();
			}
		});

		allocationAmountText.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				adjustAllocations();
			}
		});

		resetAvailableAmounts();
	}

	private void adjustAllocations() {
		if (allocationSet != null) {
			double depositAmount = 0.0;
			if (!"$".equals(allocationAmountText.getText())
					&& allocationAmountText.getText() != null
					&& allocationAmountText.getText().length() > 0) {
				depositAmount = RcpConstants.CURRENCY_VALIDATOR.validate(
						allocationAmountText.getText()).doubleValue();
			}
			double paySourceRemainder = allocationSet.getIncome() != null ? allocationSet
					.getIncome().getAmount()
					: 0.0;
			double allocationAmount = 0.0;
			double allocationSetTotal = 0.0;
			double available = depositAmount;
			List<Allocation> allocations = allocationSet.getAllocations();
			List<Allocation> percentRemainderAllocations = new LinkedList<Allocation>();
			List<Allocation> savingsGoals = new LinkedList<Allocation>();

			// check for adjusted savings goals
			for (Allocation allocation : allocations) {
				if (allocation.isEnabled()) {
					Envelope env = allocation.getEnvelope();
					if (env.isSavingsGoal()) {
						if (env.getSavingsGoalOverrideAmount() == null) {
							int paycheckCount = allocationSet.getIncome()
									.calcPaycheckCountUntilDate(
											env.getSavingsGoalDate());
							if (paycheckCount > 0) {
								allocation.setAmount((env.getBudget() - env
										.getBalance())
										/ paycheckCount);
							}
						} else {
							allocation.setAmount(env
									.getSavingsGoalOverrideAmount());
						}
					}
				}
			}

			for (Allocation allocation : allocations) {

				if (allocation.isEnabled() && available > 0) {
					AmountType atype = allocation.getAmountType();

					// allocation distribution
					allocationAmount = 0.0;
					if (allocation.getEnvelope().isSavingsGoal()
							&& allocation.getEnvelope()
									.getSavingsGoalOverrideAmount() == null) {
						savingsGoals.add(allocation);
						continue;
					}

					if (atype == AmountType.FIXED) {
						if (allocation.getAmount() < available) {
							allocationAmount = allocation.getAmount();
						} else {
							allocationAmount = available;
						}
					} else if (atype == AmountType.REMAINDER) {
						percentRemainderAllocations.add(allocation);
					} else if (atype == AmountType.DEPOSIT_PERCENT) {
						allocationAmount = allocation.getAmount()
								* depositAmount;
					} else if (atype == AmountType.REMAINDER_PERCENT) {
						percentRemainderAllocations.add(allocation);
					}

					// enforce limiters
					allocationAmount = limitAllocation(allocation,
							allocationAmount, depositAmount);

					allocation.setProposed(allocationAmount);

					available -= allocationAmount;

				} else {
					allocation.setProposed(0.0);
				}

				if (allocationSet.getIncome() != null) {
					paySourceRemainder -= allocation.getAmount();
				}

				allocationSetTotal += allocation.getAmount();
			}

			double remainderAmount = available;

			remainderAmount = available;
			for (Allocation allocation : percentRemainderAllocations) {
				if (allocation.isEnabled() && available > 0) {
					allocationAmount = 0.0;
					if (allocation.getAmountType() == AmountType.REMAINDER_PERCENT) {
						allocationAmount = Math.min(allocation.getAmount()
								* remainderAmount, available);
						allocationAmount = limitAllocation(allocation,
								allocationAmount, depositAmount);
					} else if (allocation.getAmountType() == AmountType.REMAINDER) {
						allocationAmount = limitAllocation(allocation,
								available, depositAmount);
					}
					allocation.setProposed(allocationAmount);
					available -= allocationAmount;
				}
			}

			// set remainders
			available = depositAmount;
			for (Allocation allocation : allocations) {
				available -= allocation.getProposed();
				allocation.setRemainder(available);
			}

			allocationSetTotalText.setText(RcpConstants.CURRENCY_VALIDATOR
					.format(allocationSetTotal));
			paySourceRemainderText.setText(RcpConstants.CURRENCY_VALIDATOR
					.format(paySourceRemainder));
		}
	}

	private double limitAllocation(Allocation allocation, double amount,
			double depositAmount) {
		LimitType type = allocation.getLimitType();
		if (type == LimitType.FIXED) {
			return Math.min(amount, allocation.getLimit());
		} else if (type == LimitType.DEPOSIT_PERCENT) {
			return Math.min(amount, allocation.getLimit() * depositAmount);
		} else if (type == LimitType.TARGET_ENVELOPE_BALANCE) {
			return Math.min(amount, allocation.getLimit()
					- allocation.getEnvelope().getBalance());
		}
		return amount;
	}

	protected void resetAvailableAmounts() {
		availableAmountText.setText(RcpConstants.CURRENCY_VALIDATOR
				.format(envelopeService.getAvailableEnvelope().getBalance()));
		try {
			allocationAmountText.setText("");
		} catch (Throwable t) {
			t.printStackTrace();
		}
		allocationAmountText.setText(RcpConstants.CURRENCY_VALIDATOR
				.format(envelopeService.getAvailableEnvelope().getBalance()));
	}

	protected void createTableContainer(Composite parent,
			GridData parentLayoutData) {
		final Composite container = new Composite(parent, SWT.NONE);
		container.setLayoutData(parentLayoutData);
		FormLayout form = new FormLayout();
		container.setLayout(form);

		profileListViewer = createProfileListViewer(container);
		profileViewer = createProfileViewer(container);

		FormData profileListData = new FormData();
		profileListData.left = new FormAttachment(0, 0);
		profileListData.top = new FormAttachment(0, 0);
		profileListData.bottom = new FormAttachment(100, 0);
		profileListViewer.getTable().setLayoutData(profileListData);

		FormData profileData = new FormData();
		profileData.right = new FormAttachment(100, 0);
		profileData.top = new FormAttachment(0, 0);
		profileData.bottom = new FormAttachment(100, 0);
		profileViewer.getTable().setLayoutData(profileData);

		if (allocationSetService.getEntities().size() > 0) {
			StructuredSelection selection = new StructuredSelection(
					allocationSetService.getEntities().get(0));
			profileListViewer.setSelection(selection);
		}
	}

	private SelectingTableViewer createProfileListViewer(Composite composite) {
		final SelectingTableViewer tableViewer = new SelectingTableViewer(
				composite, SWT.MULTI | SWT.V_SCROLL | SWT.FULL_SELECTION);

		TableViewerColumn column = new TableViewerColumn(tableViewer, SWT.NONE);
		column.getColumn().setText("name");
		column.getColumn().setWidth(100);
		column
				.setEditingSupport(new AllocationSetEditingSupport(tableViewer,
						0));

		tableViewer.setContentProvider(new AllocationSetContentProvider());
		tableViewer.setLabelProvider(new AllocationSetLabelProvider());

		tableViewer
				.addSelectionChangedListener(new ISelectionChangedListener() {

					public void selectionChanged(SelectionChangedEvent event) {
						StructuredSelection selection = (StructuredSelection) event
								.getSelection();
						if (selection.size() == 1) {
							allocationSet = (AllocationSet) selection
									.getFirstElement();
							if (allocationSet.getIncome() != null) {
								paySourceAmountText
										.setText(RcpConstants.CURRENCY_VALIDATOR
												.format(allocationSet
														.getIncome()
														.getAmount()));
							}
						} else {
							allocationSet = null;
						}
						adjustAllocations();
						refreshProfile();
					}
				});

		ColumnViewerEditorActivationStrategy strategy = new ColumnViewerEditorActivationStrategy(
				tableViewer) {
			protected boolean isEditorActivationEvent(
					ColumnViewerEditorActivationEvent event) {
				return event.eventType == ColumnViewerEditorActivationEvent.TRAVERSAL
						|| event.eventType == ColumnViewerEditorActivationEvent.MOUSE_DOUBLE_CLICK_SELECTION
						|| event.eventType == ColumnViewerEditorActivationEvent.PROGRAMMATIC;
			}
		};

		TableViewerEditor.create(tableViewer, strategy,
				ColumnViewerEditor.TABBING_HORIZONTAL
						| ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR
						| ColumnViewerEditor.TABBING_VERTICAL
						| ColumnViewerEditor.KEYBOARD_ACTIVATION);

		tableViewer.getTable().addKeyListener(new KeyListener() {

			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.BS && e.stateMask == SWT.COMMAND) {
					IHandlerService handlerService = (IHandlerService) getSite()
							.getService(IHandlerService.class);
					try {
						handlerService.executeCommand(DeleteSet.ID, null);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				} else if (e.keyCode == 'a' && e.stateMask == SWT.COMMAND) {
					tableViewer.getTable().selectAll();
				}
			}

			public void keyReleased(KeyEvent e) {
			}
		});

		tableViewer.getTable().setFont(RcpConstants.STANDARD_FONT);
		tableViewer.getTable().setHeaderVisible(false);
		tableViewer.getTable().setLinesVisible(false);

		viewerRegistry.registerViewer(ALLOCATION_SET_VIEWER_NAME, tableViewer);

		return tableViewer;
	}

	private SelectingTableViewer createProfileViewer(Composite composite) {
		final SelectingTableViewer tableViewer = new SelectingTableViewer(
				composite, SWT.MULTI | SWT.V_SCROLL | SWT.FULL_SELECTION);

		TableViewerColumn column = new TableViewerColumn(tableViewer,
				SWT.CENTER);
		column.getColumn().setText("Enabled");
		column.getColumn().setAlignment(SWT.CENTER);
		column.getColumn().setWidth(50);
		column.setEditingSupport(new AllocationEditingSupport(tableViewer, 0));

		AllocationEditingSupport editingSupport = new AllocationEditingSupport(
				tableViewer, 1);
		editingSupport.setAllocationMonitor(allocationMonitor);
		column = new TableViewerColumn(tableViewer, SWT.LEFT);
		column.getColumn().setText("Amount");
		column.getColumn().setWidth(100);
		column.setEditingSupport(editingSupport);

		column = new TableViewerColumn(tableViewer, SWT.LEFT);
		column.getColumn().setText("Amount Type");
		column.getColumn().setWidth(100);
		column.setEditingSupport(new AllocationEditingSupport(tableViewer, 2));

		column = new TableViewerColumn(tableViewer, SWT.LEFT);
		column.getColumn().setText("Envelope");
		column.getColumn().setWidth(150);

		editingSupport = new AllocationEditingSupport(tableViewer, 4);
		editingSupport.setAllocationMonitor(allocationMonitor);
		column = new TableViewerColumn(tableViewer, SWT.LEFT);
		column.getColumn().setText("Limit");
		column.getColumn().setWidth(100);
		column.setEditingSupport(editingSupport);

		column = new TableViewerColumn(tableViewer, SWT.LEFT);
		column.getColumn().setText("Limit Type");
		column.getColumn().setWidth(100);
		column.setEditingSupport(new AllocationEditingSupport(tableViewer, 5));

		column = new TableViewerColumn(tableViewer, SWT.RIGHT);
		column.getColumn().setText("Allocation");
		column.getColumn().setWidth(100);

		column = new TableViewerColumn(tableViewer, SWT.RIGHT);
		column.getColumn().setText("Remaining");
		column.getColumn().setWidth(100);

		tableViewer.setContentProvider(new AllocationContentProvider());
		tableViewer.setLabelProvider(new AllocationLabelProvider());

		tableViewer.addDoubleClickListener(getDoubleClickListener(composite
				.getShell()));

		ColumnViewerEditorActivationStrategy strategy = new ColumnViewerEditorActivationStrategy(
				tableViewer) {
			protected boolean isEditorActivationEvent(
					ColumnViewerEditorActivationEvent event) {
				return event.eventType == ColumnViewerEditorActivationEvent.TRAVERSAL
						|| event.eventType == ColumnViewerEditorActivationEvent.MOUSE_DOUBLE_CLICK_SELECTION
						|| event.eventType == ColumnViewerEditorActivationEvent.PROGRAMMATIC;
			}
		};

		TableViewerEditor.create(tableViewer, strategy,
				ColumnViewerEditor.TABBING_HORIZONTAL
						| ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR
						| ColumnViewerEditor.TABBING_VERTICAL
						| ColumnViewerEditor.KEYBOARD_ACTIVATION);

		tableViewer.getTable().addKeyListener(new KeyListener() {

			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.BS && e.stateMask == SWT.COMMAND) {
					IHandlerService handlerService = (IHandlerService) getSite()
							.getService(IHandlerService.class);
					try {
						handlerService.executeCommand(Delete.ID, null);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				} else if (e.keyCode == 'a' && e.stateMask == SWT.COMMAND) {
					tableViewer.getTable().selectAll();
				}
			}

			public void keyReleased(KeyEvent e) {
			}
		});

		tableViewer.getTable().setFont(RcpConstants.STANDARD_FONT);
		tableViewer.getTable().setHeaderVisible(true);
		tableViewer.getTable().setLinesVisible(true);

		viewerRegistry.registerViewer(ALLOCATION_VIEWER_NAME, tableViewer);

		int operations = DND.DROP_MOVE | DND.DROP_COPY;
		Transfer[] transferTypes = new Transfer[] { AllocationTransfer
				.getInstance() };
		tableViewer.addDragSupport(operations, transferTypes,
				new AllocationDragListener(tableViewer));
		tableViewer.addDropSupport(operations, transferTypes,
				new AllocationDropListener(tableViewer));

		return tableViewer;
	}

	protected void doubleClickHandler(int column,
			StructuredSelection selection, Shell shell) {
		Allocation allocation = (Allocation) selection.getFirstElement();

		Envelope envelope = allocation.getEnvelope();
		EnvelopeSelectionDialog dialog = new EnvelopeSelectionDialog(shell,
				envelope);
		dialog.setAllowBills(true);
		dialog.create();
		dialog.open();
		if (envelope != dialog.getEnvelope()) {
			allocation.setEnvelope(dialog.getEnvelope());
			profileViewer.refresh(allocation);
		}
	}

	protected IDoubleClickListener getDoubleClickListener(final Shell shell) {
		return new IDoubleClickListener() {

			public void doubleClick(DoubleClickEvent event) {
				int[] columns = new int[] { 3 };
				if (editingAllocation)
					return;
				editingAllocation = true;

				try {
					Point cursorLocation = Display.getCurrent()
							.getCursorLocation();
					Rectangle tableBounds = profileViewer.getTable()
							.getBounds();
					Rectangle parentBounds = profileViewer.getTable()
							.getParent().getParent().getParent().getBounds();
					Rectangle shellBounds = Display.getCurrent()
							.getActiveShell().getBounds();

					int x = cursorLocation.x;

					for (int i = 0; i < columns.length; i++) {
						Rectangle bounds = profileViewer.getTable().getItem(0)
								.getBounds(columns[i]);
						int minThreshold = tableBounds.x + parentBounds.x
								+ shellBounds.x + bounds.x;
						int maxThreshold = tableBounds.x + parentBounds.x
								+ shellBounds.x + bounds.x + bounds.width;

						if (x >= minThreshold && x <= maxThreshold) {
							StructuredSelection selection = (StructuredSelection) profileViewer
									.getSelection();
							doubleClickHandler(columns[i], selection, shell);
						}
					}
				} finally {
					editingAllocation = false;
				}
			}

		};
	}

	public void setFocus() {
		profileListViewer.getControl().setFocus();
	}

	private void refreshProfileList() {
		profileListViewer.setInput(allocationSetService
				.getOrderedEntities(false));
	}

	private void refreshProfile() {
		if (allocationSet != null) {
			for (Allocation allocation : allocationSet.getAllocations()) {
				allocation.setMonitor(allocationMonitor);
			}
			profileViewer.setInput(allocationSet.getAllocations());
		} else {
			profileViewer.setInput(null);
		}
	}

	private void refresh() {
		refreshProfileList();
		refreshProfile();
		/*
		 * treeViewer.setInput(envelopeFactory.createTopLevelEnvelope());
		 * 
		 * List<Envelope> expandedElements = new LinkedList<Envelope>(); for
		 * (Envelope env : envelopeService.getEntities()) { if
		 * (env.isExpanded()) { expandedElements.add(env); } }
		 * treeViewer.setExpandedElements(expandedElements.toArray());
		 */
	}

	public void entityAdded(EntityEvent<AllocationSet> event) {
		refreshProfileList();
		setFocus();
		if (event != null && event.getEntity() != null) {
			profileListViewer.setSelection(new StructuredSelection(
					new Object[] { event.getEntity() }));
			profileListViewer.reveal(event.getEntity());

			TableItem item = profileListViewer.getTable().getSelection()[0];
			Rectangle bounds = item.getBounds(0);
			ViewerCell viewerCell = profileListViewer.getCell(new Point(
					bounds.x, bounds.y));
			if (viewerCell != null) {
				profileListViewer.activateInitialCellEditor(viewerCell);
			}
		} else {
			refresh();
		}
	}

	public void entityChanged(EntityEvent<AllocationSet> event) {

		if (event != null
				&& event.getEntity() != null
				&& AllocationSet.Properties.allocations.equals(event
						.getProperty())) {
			refreshProfile();
		} else {
			refreshProfileList();
		}
	}

	public void entityRemoved(EntityEvent<AllocationSet> event) {
		refreshProfileList();
	}

}
