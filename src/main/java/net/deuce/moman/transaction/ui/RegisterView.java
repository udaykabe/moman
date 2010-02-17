package net.deuce.moman.transaction.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

public class RegisterView extends ViewPart {
	
	public static final String ID = RegisterView.class.getName();
	
	private TransactionComposite register;
	
	@Override
	public void createPartControl(Composite parent) {
		register = new TransactionComposite(parent, true, getSite(), true, SWT.NONE);
		register.setLayoutData(new GridData(GridData.FILL_BOTH));
	}

	@Override
	public void setFocus() {
		register.setFocus();
	}


}
