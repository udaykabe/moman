package net.deuce.moman.allocation.ui;

import java.util.Set;
import java.util.TreeSet;

import net.deuce.moman.allocation.operation.CreateAllocationOperation;
import net.deuce.moman.entity.ServiceProvider;
import net.deuce.moman.entity.model.allocation.Allocation;
import net.deuce.moman.entity.model.allocation.AllocationFactory;
import net.deuce.moman.entity.model.allocation.AllocationSet;
import net.deuce.moman.entity.model.allocation.AllocationSetFactory;
import net.deuce.moman.entity.model.allocation.AmountType;
import net.deuce.moman.entity.model.allocation.LimitType;
import net.deuce.moman.entity.model.envelope.Envelope;
import net.deuce.moman.entity.model.income.Income;
import net.deuce.moman.entity.service.allocation.AllocationSetService;
import net.deuce.moman.entity.service.envelope.EnvelopeService;
import net.deuce.moman.operation.CreateEntityOperation;

import org.eclipse.jface.wizard.Wizard;

public class BuildProfileWizard extends Wizard {

	private SelectPaySourcePage selectPaySourcePage;

	private AllocationSetService allocationSetService = ServiceProvider.instance().getAllocationSetService();

	private AllocationSetFactory allocationSetFactory = ServiceProvider.instance().getAllocationSetFactory();

	private EnvelopeService envelopeService = ServiceProvider.instance().getEnvelopeService();

	private AllocationFactory allocationFactory = ServiceProvider.instance().getAllocationFactory();

	public BuildProfileWizard() {
		super();
		setNeedsProgressMonitor(true);
	}

	public void addPages() {
		selectPaySourcePage = new SelectPaySourcePage();
		addPage(selectPaySourcePage);
	}

	private String getNewName(Income income) {

		String name = income.getName();
		String originalName = name;
		int count = 2;

		while (allocationSetService.doesNameExist(name)) {
			name = originalName + count++;
		}
		return name;
	}

	public boolean performFinish() {

		Income paySource = selectPaySourcePage.getPaySource();
		if (paySource == null)
			return true;

		AllocationSet allocationSet = allocationSetFactory.newEntity(
				getNewName(paySource), paySource);
		allocationSet.setIncome(paySource);
		new CreateEntityOperation<AllocationSet, AllocationSetService>(
				allocationSet, allocationSetService).execute();

		double amount, limit;
		LimitType limitType;
		int paycheckCount;

		Set<Envelope> list = new TreeSet<Envelope>(
				Envelope.SAVINGS_GOAL_COMPARATOR);
		list.addAll(envelopeService.getBills());
		list.addAll(envelopeService.getSavingsGoals());
		list.addAll(envelopeService.getEntities());

		for (Envelope env : list) {

			if (((env.isBill() || env.isSavingsGoal()) && !env.isEnabled())
					|| env.getBudget() == null || env.getBudget() == 0.0)
				continue;

			amount = limit = 0.0;
			limitType = LimitType.TARGET_ENVELOPE_BALANCE;

			if (env.isSavingsGoal() && env.getBalance() < env.getBudget()) {
				paycheckCount = paySource.calcPaycheckCountUntilDate(env
						.getSavingsGoalDate());
				if (paycheckCount > 0) {
					amount = (env.getBudget() - env.getBalance())
							/ paycheckCount;
				}
			} else {
				amount = env.getBudget()
						/ Math.round(paySource.getFrequency().ppy()
								/ env.getFrequency().ppy());
			}
			limit = env.getBudget();
			if (limit < amount) {
				limitType = LimitType.NONE;
				limit = 0.0;
			}

			Allocation allocation = allocationFactory.newEntity(allocationSet
					.getAllocations().size(), true, amount, AmountType.FIXED,
					env, limit, limitType);

			new CreateAllocationOperation(allocationSet, allocation).execute();
		}
		return true;
	}

}
