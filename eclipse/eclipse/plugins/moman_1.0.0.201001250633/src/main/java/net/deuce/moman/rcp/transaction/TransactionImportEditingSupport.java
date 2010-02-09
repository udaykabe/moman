package net.deuce.moman.rcp.transaction;

import java.util.List;

import net.deuce.moman.model.Registry;
import net.deuce.moman.model.envelope.Envelope;
import net.deuce.moman.model.transaction.InternalTransaction;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;

public class TransactionImportEditingSupport extends EditingSupport {
	
	private CellEditor editor;
	private int column;

	public TransactionImportEditingSupport(ColumnViewer viewer, int column) {
		super(viewer);
		
		switch (column) {
		case 2:
			editor = new TextCellEditor(((TableViewer) viewer).getTable());
			editor.getControl().setFont(Registry.instance().getStandardFont());
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
		Transaction transaction = (Transaction)element;
		List<Envelope> split = transaction.getSplit();
	
		switch (this.column) {
		case 2: return transaction.getDescription();
		case 4: return split != null && split.size() > 0 ? split.get(0).getName() : "";
		default:
			break;
		}
		return null;
	}

	@Override
	protected void setValue(Object element, Object value) {
		Transaction transaction = (Transaction)element;
		switch (this.column) {
		case 2: transaction.setDescription((String)value); break;
		case 4: 
			//transaction.clearSplit();
			//transaction.addSplit((Envelope)value);
			break;
		default:
			break;
		}
		getViewer().update(element, null);
	}

}
