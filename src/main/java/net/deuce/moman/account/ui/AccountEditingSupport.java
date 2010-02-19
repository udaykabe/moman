package net.deuce.moman.account.ui;

import net.deuce.moman.Constants;
import net.deuce.moman.account.model.Account;

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
			editor = new CheckboxCellEditor(((TableViewer)viewer).getTable(), SWT.CHECK);
			break;
		case 1:
			editor = new TextCellEditor(((TableViewer)viewer).getTable());
			editor.getControl().setFont(Constants.STANDARD_FONT);
			break;
		default:
			editor = null;
		}
		this.column = column;
	}

	@Override
	protected boolean canEdit(Object element) {
		return true;
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		return editor;
	}

	@Override
	protected Object getValue(Object element) {
		Account account = (Account)element;
		
		switch (this.column) {
		case 0: return account.isSelected();
		case 1: return account.getNickname();
		default:
			break;
		}
		return null;
	}

	@Override
	protected void setValue(Object element, Object value) {
		if (value != null) {
			Account account = (Account)element;
			switch (this.column) {
			case 0: account.executeChange(Account.Properties.selected, value); break;
			case 1: account.executeChange(Account.Properties.nickname, value); break;
			default:
				break;
			}
			getViewer().update(element, null);
		}
	}

}
