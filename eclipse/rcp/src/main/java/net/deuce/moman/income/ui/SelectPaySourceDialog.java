package net.deuce.moman.income.ui;

import net.deuce.moman.entity.ServiceProvider;
import net.deuce.moman.entity.model.income.Income;
import net.deuce.moman.entity.service.EntityService;
import net.deuce.moman.entity.service.income.IncomeService;
import net.deuce.moman.ui.AbstractSelectEntityDialog;
import net.deuce.moman.ui.EntityLabelProvider;

import org.eclipse.swt.widgets.Shell;
import org.springframework.beans.factory.annotation.Autowired;

public class SelectPaySourceDialog extends AbstractSelectEntityDialog<Income> {

	private IncomeService incomeService = ServiceProvider.instance().getIncomeService();

	public SelectPaySourceDialog(Shell shell) {
		super(shell);
	}

	protected EntityService<Income> getService() {
		return incomeService;
	}

	protected String getEntityTitle() {
		return "Select a Pay Source:";
	}

	protected EntityLabelProvider<Income> getEntityLabelProvider() {
		return new EntityLabelProvider<Income>() {

			public String getLabel(Income entity) {
				return entity.getName();
			}
		};
	}

}
