package net.deuce.moman.allocation.ui;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Set;
import java.util.TreeSet;

import net.deuce.moman.allocation.model.Allocation;
import net.deuce.moman.allocation.model.AllocationFactory;
import net.deuce.moman.allocation.model.AllocationSet;
import net.deuce.moman.allocation.model.AmountType;
import net.deuce.moman.allocation.model.LimitType;
import net.deuce.moman.allocation.operation.CreateAllocationOperation;
import net.deuce.moman.allocation.service.AllocationSetService;
import net.deuce.moman.envelope.model.Envelope;
import net.deuce.moman.envelope.service.EnvelopeService;
import net.deuce.moman.income.model.Income;
import net.deuce.moman.model.Frequency;
import net.deuce.moman.operation.CreateEntityOperation;
import net.deuce.moman.service.ServiceNeeder;

import org.eclipse.jface.wizard.Wizard;

public class BuildProfileWizard extends Wizard {
	
	private SelectPaySourcePage selectPaySourcePage;
	private AllocationSetService allocationSetService;
	private EnvelopeService envelopeService;
	private AllocationFactory allocationFactory;
	
	public BuildProfileWizard() {
		super();
		setNeedsProgressMonitor(true);
		allocationSetService = ServiceNeeder.instance().getAllocationSetService();
		envelopeService = ServiceNeeder.instance().getEnvelopeService();
		allocationFactory = ServiceNeeder.instance().getAllocationFactory();
	}
	
	@Override
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

	@Override
	public boolean performFinish() {
		
		Income paySource = selectPaySourcePage.getPaySource();
		if (paySource == null) return true;
		
		AllocationSet allocationSet = ServiceNeeder.instance().getAllocationSetFactory().newEntity(getNewName(paySource), paySource);
		allocationSet.setIncome(paySource);
		new CreateEntityOperation<AllocationSet, AllocationSetService>(allocationSet, allocationSetService).execute();
		
		double amount, limit;
		LimitType limitType;
		int paycheckCount;
		
		Set<Envelope> list = new TreeSet<Envelope>(Envelope.SAVINGS_GOAL_COMPARATOR);
		list.addAll(envelopeService.getBills());
		list.addAll(envelopeService.getSavingsGoals());
		list.addAll(envelopeService.getEntities());
		
		for (Envelope env : list) {
			
			if (((env.isBill() || env.isSavingsGoal()) && !env.isEnabled()) || env.getBudget() == null || env.getBudget() == 0.0) continue;
			
			amount = limit = 0.0;
			limitType = LimitType.TARGET_ENVELOPE_BALANCE;
			
			if (env.isSavingsGoal() && env.getBalance() < env.getBudget()) {
				paycheckCount = paySource.calcPaycheckCountUntilDate(env.getSavingsGoalDate());
				if (paycheckCount > 0) {
					amount = (env.getBudget() - env.getBalance()) / paycheckCount;
				}
			} else {
				amount = env.getBudget()/Math.round(paySource.getFrequency().ppy()/env.getFrequency().ppy());
			}
			limit = env.getBudget();
			if (limit < amount) {
				limitType = LimitType.NONE;
				limit = 0.0;
			}
			
			Allocation allocation = allocationFactory.newEntity(
					allocationSet.getAllocations().size(), true, amount, AmountType.FIXED,
					env, limit, limitType);
			
			new CreateAllocationOperation(allocationSet, allocation).execute();
		}
		return true;
	}
	
}
