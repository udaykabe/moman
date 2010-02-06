package net.deuce.moman.allocation.ui;

import java.util.LinkedList;
import java.util.List;

import net.deuce.moman.Constants;
import net.deuce.moman.account.model.Account;
import net.deuce.moman.account.service.AccountService;
import net.deuce.moman.allocation.command.Delete;
import net.deuce.moman.allocation.command.DeleteSet;
import net.deuce.moman.allocation.model.Allocation;
import net.deuce.moman.allocation.model.AllocationSet;
import net.deuce.moman.allocation.model.AmountType;
import net.deuce.moman.allocation.model.LimitType;
import net.deuce.moman.allocation.service.AllocationSetService;
import net.deuce.moman.envelope.model.Envelope;
import net.deuce.moman.envelope.service.EnvelopeService;
import net.deuce.moman.envelope.ui.EnvelopeSelectionDialog;
import net.deuce.moman.model.EntityEvent;
import net.deuce.moman.model.EntityListener;
import net.deuce.moman.model.EntityMonitor;
import net.deuce.moman.service.ServiceNeeder;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TableViewerEditor;
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
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.ViewPart;

public class AllocationView extends ViewPart implements EntityListener<AllocationSet> {
	
	public static final String ID = AllocationView.class.getName();
	
	private TableViewer profileListViewer;
	private TableViewer profileViewer;
	private AllocationSetService allocationSetService;
	private EnvelopeService envelopeService;
	private AccountService accountService;
	private boolean editingAllocation = false;
	private AllocationSet allocationSet = null;
	private Sash sash;
	private Text availableAmountText;
	private Text allocationAmountText;
	EntityMonitor<Allocation> allocationMonitor = new EntityMonitor<Allocation>();

	public AllocationView() {
		allocationSetService = ServiceNeeder.instance().getAllocationSetService();
		accountService = ServiceNeeder.instance().getAccountService();
		envelopeService = ServiceNeeder.instance().getEnvelopeService();
		allocationSetService.addEntityListener(this);
		
		envelopeService.addEntityListener(new EntityListener<Envelope>() {
			@Override
			public void entityAdded(EntityEvent<Envelope> event) {
			}
			@Override
			public void entityChanged(EntityEvent<Envelope> event) {
				resetAvailableAmounts();
				adjustAllocations();
			}
			@Override
			public void entityRemoved(EntityEvent<Envelope> event) {
			}
		});
		
		allocationMonitor.addListener(new EntityListener<Allocation>() {
			@Override
			public void entityAdded(EntityEvent<Allocation> event) {
			}

			@Override
			public void entityChanged(EntityEvent<Allocation> event) {
				if (event != null && event.getEntity() != null) {
					if (Allocation.Properties.amount.equals(event.getProperty()) ||
							Allocation.Properties.limit.equals(event.getProperty()) ||
							Allocation.Properties.amountType.equals(event.getProperty()) ||
							Allocation.Properties.limitType.equals(event.getProperty())) {
						adjustAllocations();
					} else if (Allocation.Properties.proposed.equals(event.getProperty()) ||
							Allocation.Properties.remainder.equals(event.getProperty())) {
						refreshProfile();
					}
				}
			}

			@Override
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
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			@Override
			public void widgetSelected(SelectionEvent e) {
				distributeFunds(parent.getShell());
			}
		});
	}
	
	protected void distributeFunds(Shell shell) {
		StructuredSelection selection = (StructuredSelection)profileListViewer.getSelection();
		
		if (selection.size() == 0) {
			MessageDialog.openInformation(shell, "Select an allocation profile",
					"Please select one allocation profile.");
			return;
		}
		
		if (selection.size() > 1) {
			MessageDialog.openError(shell, "Error",
					"Please select only one allocation profile.");
			return;
		}
		
		final AllocationSet allocationSet = (AllocationSet)selection.getFirstElement();
		
		selection = (StructuredSelection)accountService.getViewer().getSelection();
		
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
		
		final Account account = (Account)selection.getFirstElement();
		final Envelope available = envelopeService.getAvailableEnvelope();
		
		if (allocationSet.getAllocations().size() > 0) {
			
			BusyIndicator.showWhile(shell.getDisplay(), new Runnable() {
				
				public void run() {
					ServiceNeeder.instance().getServiceContainer().startQueuingNotifications();
					try {
						for (Allocation allocation : allocationSet.getAllocations()) {
							envelopeService.transfer(account, account, available, allocation.getEnvelope(), allocation.getProposed());
						}
					} finally {
						ServiceNeeder.instance().getServiceContainer().stopQueuingNotifications();
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
				if (allocationAmountText.getText().length() == 0 &&
						Constants.CURRENCY_VALIDATOR.isValid(e.text)) {
					return;
				}
				if (e.text == null || e.text.length() == 0) return;
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
				
				if (!Constants.CURRENCY_VALIDATOR.isValid(text+c)) {
					e.doit = false;
					return;
				}
				
				double value = Constants.CURRENCY_VALIDATOR.validate(text+c).doubleValue();
				e.doit = value < envelopeService.getAvailableEnvelope().getBalance();
			}
		});
		
		allocationAmountText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				adjustAllocations();
			}
		});
		
		resetAvailableAmounts();
	}
	
	private void adjustAllocations() {
		if (allocationSet != null) {
			double depositAmount = 0.0;
			if (!"$".equals(allocationAmountText.getText()) &&
					allocationAmountText.getText() != null && allocationAmountText.getText().length() > 0) {
				depositAmount = Constants.CURRENCY_VALIDATOR.validate(allocationAmountText.getText()).doubleValue();
			}
			double allocationAmount = 0.0;
			double available = depositAmount;
			List<Allocation> allocations = allocationSet.getAllocations();
			List<Allocation> percentRemainderAllocations = new LinkedList<Allocation>();
			for (Allocation allocation : allocations) {
				
				if (available > 0) {
					AmountType atype = allocation.getAmountType();
					LimitType ltype = allocation.getLimitType();
					
					// allocation distribution
					allocationAmount = 0.0;
					if (atype == AmountType.FIXED) {
						if (allocation.getAmount() < available) {
							allocationAmount = allocation.getAmount();
						} else {
							allocationAmount = available;
						}
					} else if (atype == AmountType.REMAINDER) {
						percentRemainderAllocations.add(allocation);
					} else if (atype == AmountType.DEPOSIT_PERCENT) {
						allocationAmount = allocation.getAmount()*depositAmount;
					} else if (atype == AmountType.REMAINDER_PERCENT) {
						percentRemainderAllocations.add(allocation);
					} 
					
					// enforce limiters
					allocationAmount = limitAllocation(allocation, allocationAmount, depositAmount);
					
					allocation.setProposed(allocationAmount);
					
					available -= allocationAmount;
				} else {
					allocation.setProposed(0.0);
				}
			}
			
			double remainderAmount = available;
			for (Allocation allocation : percentRemainderAllocations) {
				if (available > 0) {
					allocationAmount = 0.0;
					if (allocation.getAmountType() == AmountType.REMAINDER_PERCENT) {
						allocationAmount = Math.min(allocation.getAmount()*remainderAmount, available);
						allocationAmount = limitAllocation(allocation, allocationAmount, depositAmount);
					} else if (allocation.getAmountType() == AmountType.REMAINDER) {
						allocationAmount = limitAllocation(allocation, available, depositAmount);
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
		}
	}
	
	private double limitAllocation(Allocation allocation, double amount, double depositAmount) {
		LimitType type = allocation.getLimitType();
		if (type == LimitType.FIXED) {
			return Math.min(amount, allocation.getLimit());
		} else if (type == LimitType.DEPOSIT_PERCENT) {
			return Math.min(amount, allocation.getLimit()*depositAmount);
		} else if (type == LimitType.TARGET_ENVELOPE_BALANCE) {
			return Math.min(amount, allocation.getLimit() - allocation.getEnvelope().getBalance());
		}
		return amount;
	}
	
	protected void resetAvailableAmounts() {
		availableAmountText.setText(Constants.CURRENCY_VALIDATOR.format(
				envelopeService.getAvailableEnvelope().getBalance()));
		try {
		allocationAmountText.setText("");
		} catch (Throwable t) {
			t.printStackTrace();
		}
		allocationAmountText.setText(Constants.CURRENCY_VALIDATOR.format(
				envelopeService.getAvailableEnvelope().getBalance()));
	}
	
	protected void createTableContainer(Composite parent, GridData parentLayoutData) {
		final Composite container = new Composite(parent, SWT.NONE);
		container.setLayoutData(parentLayoutData);
		FormLayout form = new FormLayout ();
		container.setLayout(form);
		
		//sash = new Sash (container, SWT.VERTICAL);
		
		profileListViewer = createProfileListViewer(container);
		profileViewer = createProfileViewer(container);
		
		FormData profileListData = new FormData();
		profileListData.left = new FormAttachment(0, 0);
		//profileListData.right = new FormAttachment(sash, 0);
		profileListData.top = new FormAttachment(0, 0);
		profileListData.bottom = new FormAttachment(100, 0);
		profileListViewer.getTable().setLayoutData(profileListData);

		final int limit = 10, percent = 10;
		//final FormData sashData = new FormData ();
		//sashData.left = new FormAttachment (percent, 0);
		//sashData.top = new FormAttachment (0, 0);
		//sashData.bottom = new FormAttachment (100, 0);
		//sash.setLayoutData (sashData);
		/*
		sash.addListener(SWT.Selection, new Listener () {
			public void handleEvent(Event e) {
				Rectangle sashRect = sash.getBounds();
				Rectangle shellRect = container.getClientArea();
				int right = shellRect.width - sashRect.width - limit;
				e.x = Math.max(Math.min(e.x, right), limit);
				if (e.x != sashRect.x)  {
					sashData.left = new FormAttachment(0, e.x);
					container.layout();
				}
			}
		});
		*/
		
		FormData profileData = new FormData();
//		profileData.left = new FormAttachment(sash, 0);
		profileData.right = new FormAttachment(100, 0);
		profileData.top = new FormAttachment(0, 0);
		profileData.bottom = new FormAttachment(100, 0);
		profileViewer.getTable().setLayoutData(profileData);

		if (allocationSetService.getEntities().size() > 0) {
			StructuredSelection selection = new StructuredSelection(allocationSetService.getEntities().get(0));
			profileListViewer.setSelection(selection);
		}
	}
	
	private TableViewer createProfileListViewer(Composite composite) {
		final TableViewer tableViewer = new TableViewer(composite, SWT.MULTI | SWT.V_SCROLL | SWT.FULL_SELECTION);
		
		TableViewerColumn column = new TableViewerColumn(tableViewer, SWT.NONE);
 		column.getColumn().setText("name");
 	    column.getColumn().setWidth(100);
 	    column.setEditingSupport(new AllocationSetEditingSupport(tableViewer, 0));
 	    
	    tableViewer.setContentProvider(new AllocationSetContentProvider());
	    tableViewer.setLabelProvider(new AllocationSetLabelProvider());
	    
	    tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				StructuredSelection selection = (StructuredSelection) event.getSelection();
				if (selection.size() == 1) {
					allocationSet = (AllocationSet) selection.getFirstElement();
				} else {
					allocationSet = null;
				}
				adjustAllocations();
				refreshProfile();
			}
	    });
	    
	    ColumnViewerEditorActivationStrategy strategy = new ColumnViewerEditorActivationStrategy(tableViewer) {
			protected boolean isEditorActivationEvent(ColumnViewerEditorActivationEvent event) {
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
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.BS && e.stateMask == SWT.COMMAND) {
					IHandlerService handlerService = (IHandlerService) getSite().getService(IHandlerService.class);
					try {
						handlerService.executeCommand(DeleteSet.ID, null);
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
		
		tableViewer.getTable().setFont(Constants.STANDARD_FONT);
 		tableViewer.getTable().setHeaderVisible(false);
 		tableViewer.getTable().setLinesVisible(false);
 		
 		allocationSetService.setViewer(tableViewer);
		
		return tableViewer;
	}
	
	private TableViewer createProfileViewer(Composite composite) {
		final TableViewer tableViewer = new TableViewer (composite, SWT.BORDER);
		
		TableViewerColumn column = new TableViewerColumn(tableViewer, SWT.CENTER);
 		column.getColumn().setText("Enabled");
 		column.getColumn().setAlignment(SWT.CENTER);
 	    column.getColumn().setWidth(50);
 	    column.setEditingSupport(new AllocationEditingSupport(tableViewer, 0));
		
 	    AllocationEditingSupport editingSupport = new AllocationEditingSupport(tableViewer, 1); 
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

		tableViewer.addDoubleClickListener(getDoubleClickListener(composite.getShell()));

	    ColumnViewerEditorActivationStrategy strategy = new ColumnViewerEditorActivationStrategy(tableViewer) {
			protected boolean isEditorActivationEvent(ColumnViewerEditorActivationEvent event) {
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
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.BS && e.stateMask == SWT.COMMAND) {
					IHandlerService handlerService = (IHandlerService) getSite().getService(IHandlerService.class);
					try {
						handlerService.executeCommand(Delete.ID, null);
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
		
		tableViewer.getTable().setFont(Constants.STANDARD_FONT);
 		tableViewer.getTable().setHeaderVisible(true);
 		tableViewer.getTable().setLinesVisible(true);
 		
 		allocationSetService.setAllocationViewer(tableViewer);
	    
 		int operations = DND.DROP_MOVE|DND.DROP_COPY;
		Transfer[] transferTypes = new Transfer[]{AllocationTransfer.getInstance()};
		tableViewer.addDragSupport(operations, transferTypes , new AllocationDragListener(tableViewer));
		tableViewer.addDropSupport(operations, transferTypes, new AllocationDropListener(tableViewer));
		
 		return tableViewer;
	}
	
	protected void doubleClickHandler(int column,
			StructuredSelection selection, Shell shell) {
		Allocation allocation = (Allocation)selection.getFirstElement();
		
		Envelope envelope = allocation.getEnvelope();
		EnvelopeSelectionDialog dialog = new EnvelopeSelectionDialog(shell, envelope);
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
			@Override
			public void doubleClick(DoubleClickEvent event) {
				int[] columns = new int[]{3};
				if (editingAllocation) return;
				editingAllocation = true;
				
				try {
					Point cursorLocation = Display.getCurrent().getCursorLocation();
					Rectangle tableBounds = profileViewer.getTable().getBounds();
					Rectangle parentBounds = profileViewer.getTable().getParent().getParent().getParent().getBounds();
					Rectangle shellBounds = Display.getCurrent().getActiveShell().getBounds();
					//Rectangle sashBounds = sash.getBounds();
					
					int x = cursorLocation.x;
					
					for (int i=0; i<columns.length; i++) {
						Rectangle bounds = profileViewer.getTable().getItem(0).getBounds(columns[i]);
						int minThreshold = tableBounds.x+parentBounds.x+shellBounds.x+bounds.x;
						int maxThreshold = tableBounds.x+parentBounds.x+shellBounds.x+bounds.x+bounds.width;
				
						if (x >= minThreshold && x <= maxThreshold) {
							StructuredSelection selection = (StructuredSelection)profileViewer.getSelection();
							doubleClickHandler(columns[i], selection, shell);
						}
					}
				} finally {
					editingAllocation = false;
				}
			}

		};
	}
	
	@Override
	public void setFocus() {
		profileListViewer.getControl().setFocus();		
	}
	
	private void refreshProfileList() {
		profileListViewer.setInput(allocationSetService.getOrderedEntities(false));
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
		treeViewer.setInput(envelopeFactory.createTopLevelEnvelope());
		
		List<Envelope> expandedElements = new LinkedList<Envelope>();
		for (Envelope env : envelopeService.getEntities()) {
			if (env.isExpanded()) {
				expandedElements.add(env);
			}
		}
		treeViewer.setExpandedElements(expandedElements.toArray());
		*/
	}

	@Override
	public void entityAdded(EntityEvent<AllocationSet> event) {
		refreshProfileList();
	}

	@Override
	public void entityChanged(EntityEvent<AllocationSet> event) {
		
		if (event != null && event.getEntity() != null && AllocationSet.Properties.allocations.equals(event.getProperty())) {
			refreshProfile();
		} else {
			refreshProfileList();
		}
	}

	@Override
	public void entityRemoved(EntityEvent<AllocationSet> event) {
		refreshProfileList();
	}

}
