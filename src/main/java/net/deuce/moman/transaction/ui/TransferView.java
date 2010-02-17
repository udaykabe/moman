package net.deuce.moman.transaction.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

public class TransferView extends ViewPart {
	
	public static final String ID = TransferView.class.getName();
	

	private TransferComposite register;
	
	@Override
	public void createPartControl(Composite parent) {
		register = new TransferComposite(parent, false, getSite(), SWT.NONE);
		register.setLayoutData(new GridData(GridData.FILL_BOTH));
	}

	@Override
	public void setFocus() {
		register.setFocus();
	}
}
