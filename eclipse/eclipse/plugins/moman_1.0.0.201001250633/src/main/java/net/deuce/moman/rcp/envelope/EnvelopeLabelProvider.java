package net.deuce.moman.rcp.envelope;

import net.deuce.moman.model.Registry;
import net.deuce.moman.model.envelope.BaseEnvelopeContentProvider;
import net.deuce.moman.model.envelope.Envelope;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableFontProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

public class EnvelopeLabelProvider extends BaseEnvelopeContentProvider implements ITableLabelProvider, ITableFontProvider {
	
	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		Envelope env = getEnvelope(element);
		switch (columnIndex) {
		case 0: return env.getName();
		case 1: return Float.toString(env.getBalance());
		}
		return "";
	}

	@Override
	public void addListener(ILabelProviderListener listener) {
	}

	@Override
	public void dispose() {
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
	}

	@Override
	public Font getFont(Object element, int columnIndex) {
		return Registry.instance().getStandardFont();
	}

}
