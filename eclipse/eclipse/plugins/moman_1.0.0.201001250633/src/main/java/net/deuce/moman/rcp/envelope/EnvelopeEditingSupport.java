package net.deuce.moman.rcp.envelope;

import net.deuce.moman.model.Registry;
import net.deuce.moman.model.envelope.Envelope;
import net.deuce.moman.model.Frequency;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;

public class EnvelopeEditingSupport extends EditingSupport {
	
	private CellEditor editor;
	private int column;

	public EnvelopeEditingSupport(ColumnViewer viewer, int column) {
		super(viewer);
		
		String[] values;
		
		switch (column) {
		case 2:
			editor = new TextCellEditor(((TreeViewer) viewer).getTree());
			editor.getControl().setFont(Registry.instance().getStandardFont());
			break;
		case 3:
			values = new String[Frequency.values().length];
			for (int i=0; i<Frequency.values().length; i++) {
				values[i] = Frequency.values()[i].label();
			}
			editor = new ComboBoxCellEditor(((TreeViewer)viewer).getTree(), values, SWT.READ_ONLY);
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
		Envelope env = (Envelope)element;
	
		switch (this.column) {
		case 2: return env.getBudget();
		case 3: return env.getFrequency().ordinal();
		default:
			break;
		}
		return null;
	}

	@Override
	protected void setValue(Object element, Object value) {
		Envelope env = (Envelope)element;
	
		switch (this.column) {
		case 2: env.setBudget(new Float((String)value));
			break;
		case 3: env.setFrequency(Frequency.values()[(Integer)value]);
			break;
		default:
			break;
		}
		getViewer().update(element, null);
	}


}
