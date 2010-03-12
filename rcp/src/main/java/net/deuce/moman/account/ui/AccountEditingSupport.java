package net.deuce.moman.account.ui;

import net.deuce.moman.RcpConstants;
import net.deuce.moman.entity.model.account.Account;
import net.deuce.moman.undo.EntityUndoAdapter;
import net.deuce.moman.undo.UndoAdapter;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;

public class AccountEditingSupport extends EditingSupport {

	private CellEditor editor;
	private int column;

	public AccountEditingSupport(ColumnViewer viewer, int column) {
		super(viewer);

		switch (column) {
		case 0:
			editor = new CheckboxCellEditor(((TableViewer) viewer).getTable(),
					SWT.CHECK);
			break;
		case 1:
			editor = new TextCellEditor(((TableViewer) viewer).getTable());
			editor.getControl().setFont(RcpConstants.STANDARD_FONT);
			break;
		default:
			editor = null;
		}
		this.column = column;
	}

	protected boolean canEdit(Object element) {
		return true;
	}

	protected CellEditor getCellEditor(Object element) {
		return editor;
	}

	protected Object getValue(Object element) {
		Account account = (Account) element;

		switch (this.column) {
		case 0:
			return account.isSelected();
		case 1:
			return account.getNickname();
		default:
			break;
		}
		return null;
	}

	protected void setValue(Object element, Object value) {
		if (value != null) {
			Account account = (Account) element;
			UndoAdapter undoAdapter = new EntityUndoAdapter<Account>(account);
			switch (this.column) {
			case 0:
				undoAdapter.executeChange(Account.Properties.selected, value);
				break;
			case 1:
				undoAdapter.executeChange(Account.Properties.nickname, value);
				break;
			default:
				break;
			}
			getViewer().update(element, null);
		}
	}

}
