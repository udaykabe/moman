package net.deuce.moman.envelope.ui;

import net.deuce.moman.Constants;
import net.deuce.moman.envelope.model.Envelope;
import net.deuce.moman.model.Frequency;
import net.deuce.moman.service.ServiceNeeder;
import net.deuce.moman.ui.CurrencyCellEditorValidator;

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
		case 0:
			editor = new TextCellEditor(((TreeViewer) viewer).getTree());
			editor.getControl().setFont(Constants.STANDARD_FONT);
			break;
		case 2:
			editor = new TextCellEditor(((TreeViewer) viewer).getTree());
			editor.getControl().setFont(Constants.STANDARD_FONT);
			editor.setValidator(CurrencyCellEditorValidator.instance());
			break;
		case 3:
			values = new String[Frequency.values().length];
			for (int i=0; i<Frequency.values().length; i++) {
				values[i] = Frequency.values()[i].label();
			}
			editor = new ComboBoxCellEditor(((TreeViewer)viewer).getTree(), values, SWT.READ_ONLY);
			editor.getControl().setFont(Constants.STANDARD_FONT);
			break;
		default:
			editor = null;
		}
		this.column = column;
	}

	@Override
	protected boolean canEdit(Object element) {
		Envelope envelope = (Envelope)element;
		
		if (envelope == ServiceNeeder.instance().getEnvelopeService().getRootEnvelope()) return false;
		
		if (column == 2 && envelope.hasChildren()) return false;
		
		return (column != 0 || envelope.isEditable());
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		return editor;
	}

	@Override
	protected Object getValue(Object element) {
		Envelope env = (Envelope)element;
	
		switch (this.column) {
		case 0: return env.getName();
		case 2: return Double.toString(env.getBudget());
		case 3: return env.getFrequency().ordinal();
		default:
			break;
		}
		return null;
	}

	@Override
	protected void setValue(Object element, Object value) {
	
		if (value != null) {
			
			Envelope env = (Envelope)element;
			
			switch (this.column) {
			case 0: env.executeChange(Envelope.Properties.name, value);
				break;
			case 2: env.executeChange(Envelope.Properties.budget, new Double((String)value));
				break;
			case 3: env.executeChange(Envelope.Properties.frequency, Frequency.values()[(Integer)value]);
				break;
			default:
				break;
			}
			getViewer().update(element, null);
		}
	}


}
