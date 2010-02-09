package net.deuce.moman.rcp.envelope;

import net.deuce.moman.model.Registry;
import net.deuce.moman.model.envelope.Bill;
import net.deuce.moman.model.Frequency;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;

public class BillEditingSupport extends EditingSupport {
	
	private CellEditor editor;
	private int column;

	public BillEditingSupport(ColumnViewer viewer, int column) {
		super(viewer);
		
		String[] values;
		
		switch (column) {
		case 0:
			editor = new CheckboxCellEditor(((TableViewer)viewer).getTable(), SWT.CHECK | SWT.READ_ONLY);
			break;
		case 2:
			values = new String[28];
			for (int i=1; i<=values.length; i++) {
				values[i-1] = Integer.toString(i);
			}
			editor = new ComboBoxCellEditor(((TableViewer)viewer).getTable(), values, SWT.READ_ONLY);
			editor.getControl().setFont(Registry.instance().getStandardFont());
			break;
		case 1:
		case 4:
			editor = new TextCellEditor(((TableViewer) viewer).getTable());
			editor.getControl().setFont(Registry.instance().getStandardFont());
			break;
		case 3:
			values = new String[Frequency.values().length];
			for (int i=0; i<Frequency.values().length; i++) {
				values[i] = Frequency.values()[i].label();
			}
			editor = new ComboBoxCellEditor(((TableViewer)viewer).getTable(), values, SWT.READ_ONLY);
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
		Bill bill = (Bill)element;
	
		switch (this.column) {
		case 0: return bill.isEnabled();
		case 1: return bill.getName();
		case 2: return bill.getDueDay()-1;
		case 3: return bill.getFrequency().ordinal();
		case 4: return Float.toString(bill.getAmount());
		default:
			break;
		}
		return null;
	}

	@Override
	protected void setValue(Object element, Object value) {
		Bill bill = (Bill)element;
	
		switch (this.column) {
		case 0: bill.setEnabled((Boolean)value);
			break;
		case 1: bill.setName((String)value);
			break;
		case 2: bill.setDueDay(((Integer)value) + 1);
			break;
		case 3: bill.setFrequency(Frequency.values()[(Integer)value]);
			break;
		case 4: bill.setAmount(new Float((String)value));
			break;
		default:
			break;
		}
		getViewer().update(element, null);
	}


}
