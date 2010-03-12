package net.deuce.moman.envelope.ui;

import net.deuce.moman.RcpConstants;
import net.deuce.moman.entity.model.Frequency;
import net.deuce.moman.entity.model.envelope.Envelope;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

public class EnvelopeLabelProvider extends BaseEnvelopeContentProvider
		implements ITableLabelProvider, ITableColorProvider {

	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		Envelope env = getEnvelope(element);
		switch (columnIndex) {
		case 0:
			return env.getName();
		case 1:
			Double balance = Math.round(env.getBalance() * 100.0) / 100.0;
			return RcpConstants.CURRENCY_VALIDATOR.format(balance);
		case 2:
			return RcpConstants.CURRENCY_VALIDATOR.format(env.getBudget(true));
		case 3:
			return env.getFrequency() != Frequency.NONE ? env.getFrequency()
					.label() : "";
		}
		return "";
	}

	public void addListener(ILabelProviderListener listener) {
	}

	public void dispose() {
	}

	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	public void removeListener(ILabelProviderListener listener) {
	}

	public Color getBackground(Object element, int columnIndex) {

		return null;
	}

	public Color getForeground(Object element, int columnIndex) {

		Envelope envelope = (Envelope) element;
		double balance = envelope.getBalance();
		if (columnIndex == 1 && Math.round(balance * 100) < 0) {
			return Display.getCurrent().getSystemColor(SWT.COLOR_RED);
		}
		return null;
	}

}
