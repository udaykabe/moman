package net.deuce.moman.envelope.ui;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.deuce.moman.RcpConstants;
import net.deuce.moman.entity.ServiceProvider;
import net.deuce.moman.entity.model.EntityEvent;
import net.deuce.moman.entity.model.EntityListener;
import net.deuce.moman.entity.model.envelope.Envelope;
import net.deuce.moman.entity.model.transaction.Split;
import net.deuce.moman.entity.service.envelope.EnvelopeService;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TableViewerEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.springframework.beans.factory.annotation.Autowired;

public class SplitSelectionDialog extends TitleAreaDialog implements
		EntityListener<Split> {

	private TableViewer splitViewer;
	private List<Split> split = new LinkedList<Split>();
	private List<Envelope> invalidSelections = new LinkedList<Envelope>();
	private Label totalLabel = null;
	private Label remainingLabel = null;
	private Double amount;
	private Double remaining = 0.0;
	private boolean allowBills;

	private EnvelopeService envelopeService = ServiceProvider.instance().getEnvelopeService();

	public SplitSelectionDialog(Shell parentShell, Double amount,
			List<Split> split) {
		super(parentShell);

		this.amount = Math.abs(amount);
		this.remaining = this.amount;

		Split newSplit;
		for (Split item : split) {
			newSplit = new Split(item.getEnvelope(), Math.abs(item.getAmount()));
			this.split.add(newSplit);
			newSplit.getMonitor().addListener(this);

			this.remaining -= newSplit.getAmount();
		}

		setTitle("Envelope Split");
	}

	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
	}

	public boolean isAllowBills() {
		return allowBills;
	}

	public void setAllowBills(boolean allowBills) {
		this.allowBills = allowBills;
	}

	public void addInvalidSelection(Envelope e) {
		invalidSelections.add(e);
	}

	public void setInvalidSelection(List<Envelope> l) {
		if (l != null) {
			this.invalidSelections = new LinkedList<Envelope>(l);
		}
	}

	public List<Split> getSplit() {
		List<Split> list = new LinkedList<Split>();
		for (Split item : split) {
			item.getMonitor().removeListener(this);
			list.add(item);
		}
		return list;
	}

	public void setSplit(List<Split> split) {
		this.split.clear();
		this.split.addAll(split);
	}

	protected Point getInitialLocation(Point initialSize) {
		getShell().setSize(525, 250);
		return Display.getCurrent().getCursorLocation();
	}

	protected Control createContents(Composite parent) {
		// create the top level composite for the dialog
		Composite composite = new Composite(parent, 0);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.verticalSpacing = 0;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		applyDialogFont(composite);
		// initialize the dialog units
		initializeDialogUnits(composite);
		// create the dialog area and button bar
		dialogArea = createAddRemoveButtons(composite);
		dialogArea = createDialogArea(composite);
		createStatusBar(composite);
		buttonBar = createButtonBar(composite);

		return composite;
	}

	protected Control createDialogArea(Composite parent) {
		createSplitTable(parent);
		splitViewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		return parent;
	}

	private void updateTotals() {
		totalLabel.setText(RcpConstants.CURRENCY_VALIDATOR.format(amount));
		remainingLabel.setText(RcpConstants.CURRENCY_VALIDATOR
				.format(remaining));
	}

	private Composite createStatusBar(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		// create a layout with spacing and margins appropriate for the font
		// size.
		GridLayout layout = new GridLayout();
		layout.numColumns = 2; // this is incremented by createButton
		layout.makeColumnsEqualWidth = true;
		layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
		composite.setLayout(layout);
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING
				| GridData.VERTICAL_ALIGN_CENTER);
		composite.setLayoutData(data);
		composite.setFont(parent.getFont());

		Label label = new Label(composite, SWT.NONE);
		label.setText("Split Total: ");
		totalLabel = new Label(composite, SWT.NONE);
		totalLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		label = new Label(composite, SWT.NONE);
		label.setText("Remaining: ");
		remainingLabel = new Label(composite, SWT.NONE);
		remainingLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		updateTotals();

		return composite;
	}

	private Composite createAddRemoveButtons(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		// create a layout with spacing and margins appropriate for the font
		// size.
		GridLayout layout = new GridLayout();
		layout.numColumns = 0; // this is incremented by createButton
		layout.makeColumnsEqualWidth = true;
		layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
		composite.setLayout(layout);
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING
				| GridData.VERTICAL_ALIGN_CENTER);
		composite.setLayoutData(data);
		composite.setFont(parent.getFont());

		Button addButton = createButton(composite, 3, "Add", false);
		setButtonLayoutData(addButton);
		addButton.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {

				if (remaining == 0.0)
					return;

				final EnvelopeSelectionDialog dialog = new EnvelopeSelectionDialog(
						((Button) e.getSource()).getShell(), envelopeService
								.getRootEnvelope());

				dialog.setAllowBills(true);
				dialog.create();
				dialog.open();
				boolean containsEnvelope = false;

				for (Split item : split) {
					if (item.getEnvelope() == dialog.getEnvelope()) {
						containsEnvelope = true;
						break;
					}
				}
				if (!containsEnvelope) {
					Split splitItem = new Split(dialog.getEnvelope(), remaining);
					splitItem.getMonitor().addListener(
							SplitSelectionDialog.this);
					split.add(splitItem);
					remaining = 0.0;
					splitViewer.refresh();
					updateTotals();
				}
			}
		});

		Button removeButton = createButton(composite, 4, "Remove", false);
		setButtonLayoutData(removeButton);
		removeButton.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
			}

			@SuppressWarnings("unchecked")
			public void widgetSelected(SelectionEvent e) {
				StructuredSelection selection = (StructuredSelection) splitViewer
						.getSelection();
				if (selection.size() > 0) {
					Iterator<Split> itr = selection.iterator();
					Split item;
					while (itr.hasNext()) {
						item = itr.next();
						split.remove(item);
						remaining += item.getAmount();
					}
					splitViewer.refresh();
					updateTotals();
				}
			}
		});

		return composite;
	}

	private void createSplitTable(Composite parent) {

		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = GridData.FILL;

		splitViewer = new TableViewer(parent, SWT.MULTI | SWT.V_SCROLL
				| SWT.FULL_SELECTION);

		TableViewerColumn column = new TableViewerColumn(splitViewer,
				SWT.CENTER);
		column.getColumn().setText("Envelope");
		column.getColumn().setWidth(200);

		column = new TableViewerColumn(splitViewer, SWT.NONE);
		column.getColumn().setText("Amount");
		column.getColumn().setWidth(100);
		column.setEditingSupport(new SplitEditingSupport(splitViewer, 1));

		splitViewer.setContentProvider(new SplitContentProvider());
		splitViewer.setLabelProvider(new SplitLabelProvider());

		splitViewer.getTable().addKeyListener(new KeyListener() {

			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.BS && e.stateMask == SWT.COMMAND) {
				} else if (e.keyCode == 'a' && e.stateMask == SWT.COMMAND) {
					splitViewer.getTable().selectAll();
				}
			}

			public void keyReleased(KeyEvent e) {
			}
		});

		ColumnViewerEditorActivationStrategy actSupport = createColumnViewerEditorActivationStrategy(splitViewer);
		setupTableViewerEditor(splitViewer, actSupport);

		splitViewer.getTable().setFont(RcpConstants.STANDARD_FONT);
		splitViewer.getTable().setHeaderVisible(true);
		splitViewer.getTable().setLinesVisible(true);

		refresh();
	}

	protected void setupTableViewerEditor(TableViewer tableViewer,
			ColumnViewerEditorActivationStrategy strategy) {
		TableViewerEditor.create(tableViewer, strategy,
				ColumnViewerEditor.TABBING_HORIZONTAL
						| ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR
						| ColumnViewerEditor.TABBING_VERTICAL
						| ColumnViewerEditor.KEYBOARD_ACTIVATION);

	}

	protected ColumnViewerEditorActivationStrategy createColumnViewerEditorActivationStrategy(
			TableViewer viewer) {
		return new ColumnViewerEditorActivationStrategy(viewer) {
			protected boolean isEditorActivationEvent(
					ColumnViewerEditorActivationEvent event) {
				return event.eventType == ColumnViewerEditorActivationEvent.TRAVERSAL
						|| event.eventType == ColumnViewerEditorActivationEvent.MOUSE_DOUBLE_CLICK_SELECTION
						|| event.eventType == ColumnViewerEditorActivationEvent.PROGRAMMATIC;
			}
		};
	}

	protected void createButtonsForButtonBar(Composite parent) {

		createOkButton(parent);
		Button cancelButton = createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);

		// Add a SelectionListener
		cancelButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				setReturnCode(CANCEL);
				close();
			}
		});
	}

	protected Button createOkButton(Composite parent) {
		// increment the number of columns in the button bar
		((GridLayout) parent.getLayout()).numColumns++;
		Button button = new Button(parent, SWT.PUSH);
		button.setText(IDialogConstants.OK_LABEL);
		button.setFont(JFaceResources.getDialogFont());
		button.setData(new Integer(IDialogConstants.OK_ID));
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if (isValidInput()) {
					okPressed();
				}
			}
		});
		Shell shell = parent.getShell();
		if (shell != null) {
			shell.setDefaultButton(button);
		}
		setButtonLayoutData(button);
		return button;
	}

	private void refresh() {
		splitViewer.setInput(split);
	}

	protected boolean isValidInput() {
		if (remaining != 0.0) {
			setErrorMessage("Remaining amount must be zero.");
			return false;
		}
		return true;
	}

	protected void saveInput() {
	}

	public void entityAdded(EntityEvent<Split> event) {
		remaining -= event.getEntity().getAmount();
		updateTotals();
	}

	public void entityChanged(EntityEvent<Split> event) {
		remaining = amount;
		for (Split item : split) {
			remaining -= item.getAmount();
		}
		updateTotals();
	}

	public void entityRemoved(EntityEvent<Split> event) {
		remaining += event.getEntity().getAmount();
		updateTotals();
	}
}
