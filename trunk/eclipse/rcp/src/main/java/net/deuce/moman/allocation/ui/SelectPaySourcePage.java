package net.deuce.moman.allocation.ui;

import java.util.List;

import net.deuce.moman.entity.ServiceProvider;
import net.deuce.moman.entity.model.income.Income;
import net.deuce.moman.entity.service.income.IncomeService;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class SelectPaySourcePage extends WizardPage {

	private Combo paySourceCombo;
	private Income paySource;

	private IncomeService incomeService = ServiceProvider.instance().getIncomeService();

	public SelectPaySourcePage() {
		super("Select Pay Source");
		setTitle("Select Pay Source");
	}

	public Income getPaySource() {
		return paySource;
	}

	public void createControl(Composite parent) {

		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		container.setLayout(layout);

		// layout.horizontalAlignment = GridData.FILL;

		// The text fields will grow with the size of the dialog
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;

		Label label = new Label(container, SWT.NONE);
		label.setText("Select a pay source:");
		paySourceCombo = new Combo(container, SWT.READ_ONLY | SWT.SIMPLE);

		final List<Income> paySources = incomeService.getOrderedEntities(false);

		for (Income income : paySources) {
			paySourceCombo.add(income.getName());
		}

		paySourceCombo.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				int index = paySourceCombo.getSelectionIndex();
				paySource = paySources.get(index);
			}
		});

		if (paySources.size() > 0) {
			paySourceCombo.select(0);
			paySource = paySources.get(0);
		}

		setControl(container);
		setPageComplete(true);
	}

}
