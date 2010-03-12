package net.deuce.moman.transaction.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

public class TransferView extends ViewPart {

	public static final String ID = TransferView.class.getName();

	private TransferComposite register;

	public void createPartControl(Composite parent) {
		register = new TransferComposite(parent, SWT.NONE);
		register.setLayoutData(new GridData(GridData.FILL_BOTH));
	}

	public void setFocus() {
		register.setFocus();
	}
}
