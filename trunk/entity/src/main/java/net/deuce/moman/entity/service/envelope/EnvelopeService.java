package net.deuce.moman.entity.service.envelope;

import java.util.List;

import net.deuce.moman.entity.model.account.Account;
import net.deuce.moman.entity.model.envelope.Envelope;
import net.deuce.moman.entity.model.transaction.InternalTransaction;
import net.deuce.moman.entity.service.EntityService;

public interface EnvelopeService extends EntityService<Envelope> {
	
	public void clearSelectedEnvelope();

	public Envelope getSelectedEnvelope();
	
	public void setSelectedEnvelope(Envelope env);
	
	public Envelope getMonthlyEnvelope();
	
	public void setMonthlyEnvelope(Envelope env);
	
	public void setRootEnvelope(Envelope envelope);
	
	public Envelope getRootEnvelope();
	
	public Envelope getSavingsGoalsEnvelope();

	public void setSavingsGoalsEnvelope(Envelope savingsGoalsEnvelope);

	public Envelope getUnassignedEnvelope();

	public void setUnassignedEnvelope(Envelope unassignedEnvelope);
	
	public Envelope getAvailableEnvelope();

	public void setAvailableEnvelope(Envelope availableEnvelope);
	
	public void moveTransaction(Account account, Envelope source,
			Envelope target, InternalTransaction transaction);
	
	public List<Envelope> getAllEnvelopes();
	
	public void transfer(Account sourceAccount, Account targetAccount,
			Envelope source, Envelope target, double amount);
	
	public void importDefaultEnvelopes();
	
	public void addDefaultEnvelope(Envelope envelope, Envelope parent);
	
	public void addEnvelope(Envelope envelope, Envelope parent);
	
	public int getNextIndex();
	
	public void removeEnvelope(Envelope envelope);
	
	public List<Envelope> getSavingsGoals();
	
	public List<Envelope> getOrderedSavingsGoals(boolean reverse);
	
	public List<Envelope> getOrderedBudgetedEnvelopes(boolean reverse);
	
	public List<Envelope> getBills();
	
	public List<Envelope> getOrderedBills(boolean reverse);
	
	public Envelope getBill(String id);
	
	public void bindEnvelopes();
	
	public double distributeToNegativeEnvelopes(Account account, Envelope env, double balance);

}
