package net.deuce.moman.transaction.ui;

import net.deuce.moman.RcpConstants;
import net.deuce.moman.entity.model.transaction.InternalTransaction;
import net.deuce.moman.entity.model.transaction.TransactionStatus;
import net.deuce.moman.ui.CurrencyCellEditorValidator;
import net.deuce.moman.undo.TransactionUndoAdapter;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;

public class TransactionEditingSupport extends EditingSupport {

	private CellEditor editor;
	private int column;

	public TransactionEditingSupport(ColumnViewer viewer, int column,
			TransactionStatus[] availableStatuses) {
		super(viewer);

		switch (column) {
		case 1:
			String[] values = new String[availableStatuses.length];
			for (int i = 0; i < availableStatuses.length; i++) {
				values[i] = availableStatuses[i].name();
			}
			editor = new ComboBoxCellEditor(((TableViewer) viewer).getTable(),
					values, SWT.READ_ONLY);
			editor.getControl().setFont(RcpConstants.STANDARD_FONT);
			break;
		case 2:
		case 3:
			editor = new TextCellEditor(((TableViewer) viewer).getTable());
			editor.getControl().setFont(RcpConstants.STANDARD_FONT);
			break;
		case 5:
		case 6:
			editor = new TextCellEditor(((TableViewer) viewer).getTable());
			editor.getControl().setFont(RcpConstants.STANDARD_FONT);
			editor.setValidator(CurrencyCellEditorValidator.instance());
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
		InternalTransaction transaction = (InternalTransaction) element;

		switch (this.column) {
		case 1:
			return transaction.getStatus().ordinal();
		case 2:
			return transaction.getCheck();
		case 3:
			return transaction.getDescription();
		case 5:
			double amount = Math.round(transaction.getAmount() * 100.0) / 100.0;
			return amount > 0.0 ? RcpConstants.CURRENCY_VALIDATOR
					.format(amount) : "";
		case 6:
			amount = Math.round(transaction.getAmount() * 100.0) / 100.0;
			return amount <= 0.0 ? (amount < 0.0 ? RcpConstants.CURRENCY_VALIDATOR
					.format(-amount)
					: RcpConstants.CURRENCY_VALIDATOR.format(amount))
					: "";
		default:
			break;
		}
		return null;
	}

	protected void setValue(Object element, Object value) {

		if (value != null) {

			InternalTransaction transaction = (InternalTransaction) element;

			TransactionUndoAdapter undoAdapter = new TransactionUndoAdapter(
					transaction);

			switch (this.column) {
			case 1:
				undoAdapter.executeChange(
						InternalTransaction.Properties.status,
						TransactionStatus.values()[(Integer) value]);
				break;
			case 2:
				undoAdapter.executeChange(InternalTransaction.Properties.check,
						value);
				break;
			case 3:
				undoAdapter.executeChange(
						InternalTransaction.Properties.description, value);
				break;
			case 5:
				undoAdapter.executeSetAmount(RcpConstants.CURRENCY_VALIDATOR
						.validate((String) value).doubleValue());
				break;
			case 6:
				undoAdapter.executeSetAmount(-RcpConstants.CURRENCY_VALIDATOR
						.validate((String) value).doubleValue());
				break;
			default:
				break;
			}
			getViewer().update(element, null);
		}
	}

}
