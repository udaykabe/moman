package net.deuce.moman.account.ui;

import java.util.LinkedList;
import java.util.List;

import net.deuce.moman.RcpConstants;
import net.deuce.moman.entity.model.account.Account;
import net.deuce.moman.ui.AbstractModelDialog;

import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TableViewerEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class AvailableAccountsDialog extends AbstractModelDialog<Account> {

	private List<Account> selectedAccounts = new LinkedList<Account>();
	private List<Account> availableAccounts = new LinkedList<Account>();

	public AvailableAccountsDialog(Shell shell, List<Account> availableAccounts) {
		super(shell);
		this.availableAccounts = availableAccounts;
	}

	public List<Account> getSelectedAccounts() {
		return selectedAccounts;
	}

	protected Control createDialogArea(Composite parent) {
		GridLayout layout = new GridLayout();
		parent.setLayout(layout);
		layout.numColumns = 1;

		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;

		TableViewer tableViewer = new TableViewer(parent, SWT.SINGLE
				| SWT.V_SCROLL | SWT.FULL_SELECTION);
		tableViewer.getTable().setLayoutData(gridData);

		TableViewerColumn column = new TableViewerColumn(tableViewer,
				SWT.CENTER);
		column.getColumn().setText("Import");
		column.getColumn().setWidth(50);
		column.setEditingSupport(new AccountEditingSupport(tableViewer, 0));

		column = new TableViewerColumn(tableViewer, SWT.NONE);
		column.getColumn().setText("Nickname");
		column.getColumn().setWidth(300);
		column.setEditingSupport(new AccountEditingSupport(tableViewer, 1));

		column = new TableViewerColumn(tableViewer, SWT.NONE);
		column.getColumn().setText("Account #");
		column.getColumn().setWidth(200);

		tableViewer.getTable().setFont(RcpConstants.STANDARD_FONT);
		tableViewer.getTable().setHeaderVisible(true);
		tableViewer.getTable().setLinesVisible(true);

		tableViewer.setContentProvider(new AccountContentProvider());
		tableViewer.setLabelProvider(new AccountLabelProvider());

		ColumnViewerEditorActivationStrategy actSupport = new ColumnViewerEditorActivationStrategy(
				tableViewer) {
			protected boolean isEditorActivationEvent(
					ColumnViewerEditorActivationEvent event) {
				return event.eventType == ColumnViewerEditorActivationEvent.TRAVERSAL
						|| event.eventType == ColumnViewerEditorActivationEvent.MOUSE_CLICK_SELECTION
						|| event.eventType == ColumnViewerEditorActivationEvent.PROGRAMMATIC;
			}
		};

		TableViewerEditor.create(tableViewer, actSupport,
				ColumnViewerEditor.TABBING_HORIZONTAL
						| ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR
						| ColumnViewerEditor.TABBING_VERTICAL
						| ColumnViewerEditor.KEYBOARD_ACTIVATION);

		tableViewer.setInput(availableAccounts);
		return parent;
	}

	protected boolean isValidInput() {
		return true;
	}

	protected void saveInput() {
		for (Account account : availableAccounts) {
			if (account.isSelected()) {
				selectedAccounts.add(account);
			}
		}
	}

}
