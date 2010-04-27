package net.deuce.moman.income.command;

import java.util.Date;

import net.deuce.moman.entity.ServiceProvider;
import net.deuce.moman.entity.model.Frequency;
import net.deuce.moman.entity.model.income.Income;
import net.deuce.moman.entity.model.income.IncomeFactory;
import net.deuce.moman.entity.service.income.IncomeService;
import net.deuce.moman.income.ui.IncomeView;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.springframework.beans.factory.annotation.Autowired;

public class New extends AbstractHandler {

	public static final String ID = "net.deuce.moman.income.command.new";

	private IncomeService incomeService = ServiceProvider.instance().getIncomeService();

	private IncomeFactory incomeFactory = ServiceProvider.instance().getIncomeFactory();

	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPages()[0]
					.showView(IncomeView.ID, null, IWorkbenchPage.VIEW_ACTIVATE);
			Income income = incomeFactory.newEntity("Set Name", 0.0, true,
					new Date(), Frequency.BIWEEKLY);
			incomeService.addEntity(income);

		} catch (PartInitException e) {
			e.printStackTrace();
		}

		return null;
	}

}
