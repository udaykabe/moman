package net.deuce.moman.income.ui;

import net.deuce.moman.income.model.Income;
import net.deuce.moman.service.ServiceNeeder;
import net.deuce.moman.ui.AbstractSelectEntityDialog;
import net.deuce.moman.ui.EntityLabelProvider;

import org.eclipse.swt.widgets.Shell;

public class SelectPaySourceDialog extends AbstractSelectEntityDialog<Income> {
	
	public SelectPaySourceDialog(Shell shell) {
		super(shell, ServiceNeeder.instance().getIncomeService());
	}

	@Override
	protected String getEntityTitle() {
		return "Select a Pay Source:";
	}

	@Override
	protected EntityLabelProvider<Income> getEntityLabelProvider() {
		return new EntityLabelProvider<Income>() {
			@Override
			public String getLabel(Income entity) {
				return entity.getName();
			}
		};
	}
	
}
