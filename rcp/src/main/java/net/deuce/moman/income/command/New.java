package net.deuce.moman.income.command;

import java.util.Date;

import net.deuce.moman.income.model.Income;
import net.deuce.moman.income.service.IncomeService;
import net.deuce.moman.income.ui.IncomeView;
import net.deuce.moman.model.Frequency;
import net.deuce.moman.service.ServiceNeeder;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class New extends AbstractHandler {

	public static final String ID = "net.deuce.moman.income.command.new";

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPages()[0].showView(IncomeView.ID,null,IWorkbenchPage.VIEW_ACTIVATE);
			Income income = ServiceNeeder.instance().getIncomeFactory().newEntity(
				"Set Name", 0.0, true, new Date(), Frequency.BIWEEKLY);
			IncomeService incomeService = ServiceNeeder.instance().getIncomeService();
			incomeService.addEntity(income);
			
		} catch (PartInitException e) {
			e.printStackTrace();
		}

		return null;
	}

}
