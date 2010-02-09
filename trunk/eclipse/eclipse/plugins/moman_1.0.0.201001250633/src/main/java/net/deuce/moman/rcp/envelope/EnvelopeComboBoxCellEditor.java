package net.deuce.moman.rcp.envelope;

import java.util.LinkedList;
import java.util.List;

import net.deuce.moman.model.Registry;
import net.deuce.moman.model.envelope.Envelope;

import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.swt.widgets.Composite;

public class EnvelopeComboBoxCellEditor extends ComboBoxCellEditor {

	public EnvelopeComboBoxCellEditor(Composite parent,  int style) {
		super(parent, new String[0], style);
		
		List<String> items = new LinkedList<String>();
		for (Envelope e : Registry.instance().getEnvelopes()) {
			items.add(e.getName());
		}
		setItems(items.toArray(new String[items.size()]));
	}

}
