package net.deuce.moman.envelope.service;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.deuce.moman.account.model.Account;
import net.deuce.moman.envelope.model.Envelope;
import net.deuce.moman.envelope.model.EnvelopeFactory;
import net.deuce.moman.rule.model.Rule;
import net.deuce.moman.rule.service.TransactionRuleService;
import net.deuce.moman.service.EntityService;
import net.deuce.moman.transaction.model.InternalTransaction;
import net.deuce.moman.transaction.model.TransactionFactory;
import net.deuce.moman.transaction.model.TransactionStatus;
import net.deuce.moman.transaction.service.TransactionService;
import net.sf.ofx4j.domain.data.common.TransactionType;

import org.eclipse.jface.viewers.TableViewer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EnvelopeService extends EntityService<Envelope> {
	
	@Autowired
	private TransactionService transactionService;

	@Autowired
	private TransactionRuleService transactionRuleService;
	
	@Autowired
	private EnvelopeFactory envelopeFactory;

	@Autowired
	private TransactionFactory transactionFactory;

	private Map<String, Envelope> bills = new HashMap<String, Envelope>();
	private Map<String, Envelope> savingsGoals = new HashMap<String, Envelope>();
	private Envelope defaultRootEnvelope;
	
	/*
	private Envelope defaultAvailableEnvelope;
	private Envelope defaultUnassignedEnvelope;
	private Envelope defaultMonthlyEnvelope;
	*/
	private Envelope rootEnvelope;
	private Envelope unassignedEnvelope;
	private Envelope availableEnvelope;
	private Envelope selectedEnvelope;
	private Envelope monthlyEnvelope;
	private Envelope savingsGoalsEnvelope;
	private TableViewer billViewer;
	private TableViewer savingsGoalViewer;
	private int maxIndex = 0;
	
	public EnvelopeService() {
		setSingleChange(true);
	}
	
	public TableViewer getBillViewer() {
		return billViewer;
	}

	public void setBillViewer(TableViewer billViewer) {
		this.billViewer = billViewer;
	}

	public TableViewer getSavingsGoalViewer() {
		return savingsGoalViewer;
	}

	public void setSavingsGoalViewer(TableViewer savingsGoalViewer) {
		this.savingsGoalViewer = savingsGoalViewer;
	}
	
	public void clearSelectedEnvelope() {
		if (selectedEnvelope != null) {
			selectedEnvelope.setSelected(false);
		}
	}

	public Envelope getSelectedEnvelope() {
		return selectedEnvelope;
	}
	
	public void setSelectedEnvelope(Envelope env) {
		this.selectedEnvelope = env;
	}
	
	public Envelope getMonthlyEnvelope() {
		return monthlyEnvelope;
	}
	
	public void setMonthlyEnvelope(Envelope env) {
		this.monthlyEnvelope = env;
	}
	
	public void setRootEnvelope(Envelope envelope) {
		this.rootEnvelope = envelope;
	}
	
	public Envelope getRootEnvelope() {
		return rootEnvelope;
	}
	
	public Envelope getSavingsGoalsEnvelope() {
		return savingsGoalsEnvelope;
	}

	public void setSavingsGoalsEnvelope(Envelope savingsGoalsEnvelope) {
		this.savingsGoalsEnvelope = savingsGoalsEnvelope;
	}

	public Envelope getUnassignedEnvelope() {
		return unassignedEnvelope;
	}

	public void setUnassignedEnvelope(Envelope unassignedEnvelope) {
		this.unassignedEnvelope = unassignedEnvelope;
	}
	
	public Envelope getAvailableEnvelope() {
		return availableEnvelope;
	}

	public void setAvailableEnvelope(Envelope availableEnvelope) {
		this.availableEnvelope = availableEnvelope;
	}
	
	public void moveTransaction(Account account, Envelope source,
			Envelope target, InternalTransaction transaction) {
		
	}
	
	private void addEnvelopeToList(Envelope env, List<Envelope> list) {
		list.add(env);
		for (Envelope child : env.getChildren()) {
			addEnvelopeToList(child, list);
		}
	}
	
	public List<Envelope> getAllEnvelopes() {
		List<Envelope> envelopes = new LinkedList<Envelope>();
		/*
		envelopes.add(availableEnvelope);
		envelopes.add(monthlyEnvelope);
		envelopes.add(unassignedEnvelope);
		envelopes.add(savingsGoalsEnvelope);
		envelopes.add(unassignedEnvelope);
		*/
		addEnvelopeToList(rootEnvelope, envelopes);
		return envelopes;
	}
	
	public void transfer(Account sourceAccount, Account targetAccount,
			Envelope source, Envelope target, double amount) {
		
		Date date = new Date();
		
		// source transaction
		InternalTransaction sTransaction = transactionFactory.newEntity(
				null, -amount, TransactionType.XFER, date,
				"Transfer to " + target.getName(), null, null, null,
				null, TransactionStatus.reconciled, sourceAccount);
		sTransaction.addSplit(source, -amount);
		transactionService.addEntity(sTransaction);
		
		// target transaction
		InternalTransaction tTransaction = transactionFactory.newEntity(
				null, amount, TransactionType.XFER, date,
				"Transfer from " + source.getName(), null, null, null,
				null, TransactionStatus.reconciled, targetAccount);
		tTransaction.addSplit(target, amount);
		transactionService.addEntity(tTransaction);
		
		sTransaction.setTransferTransaction(tTransaction);
		tTransaction.setTransferTransaction(sTransaction);
		
		fireEntityChanged(source);
		fireEntityChanged(target);
	}
	
	public void importDefaultEnvelopes() {
//		importDefaultEnvelope(defaultAvailableEnvelope, null);
//		importDefaultEnvelope(defaultUnassignedEnvelope, null);
//		importDefaultEnvelope(defaultMonthlyEnvelope, null);
		importDefaultEnvelope(defaultRootEnvelope, null);
	}
	
	private void importDefaultEnvelope(Envelope envelope, Envelope parent) {
		Envelope clone = envelopeFactory.cloneEnvelope(envelope, parent);
		addEnvelope(clone, parent);
		for (Envelope child : envelope.getChildren()) {
			importDefaultEnvelope(child, clone);
		}
	}
	
	public void addDefaultEnvelope(Envelope envelope, Envelope parent) {
		if (parent != null) {
			envelope.setParent(envelope);
			parent.addChild(envelope);
		}
		if (envelope.isRoot()) {
			defaultRootEnvelope = envelope;
			/*
		} else if (envelope.isMonthly()) {
			defaultMonthlyEnvelope = envelope;
		} else if (envelope.isAvailable()) {
			defaultAvailableEnvelope = envelope;
		} else if (envelope.isUnassigned()) {
			defaultUnassignedEnvelope = envelope;
			*/
		}
	}
	
	public void addEnvelope(Envelope envelope, Envelope parent) {
		
		if (parent != null) {
			
			/*
			for (Envelope child : parent.getChildren()) {
				if (child.getName().equals(envelope.getName())) {
					throw new RuntimeException("Duplicate envelope name: " + envelope.getName());
				}
			}
			*/
			envelope.setParent(parent);
			parent.addChild(envelope);
		}
		
		if (envelope.isRoot()) {
			rootEnvelope = envelope;
		}
		
		if (envelope.isAvailable()) {
			availableEnvelope = envelope;
		}
		
		if (envelope.isUnassigned()) {
			unassignedEnvelope = envelope;
		}
		
		if (envelope.isMonthly()) {
			monthlyEnvelope = envelope;
		}
		
		if (envelope.isSavingsGoals()) {
			savingsGoalsEnvelope = envelope;
		}
		
		if (envelope.isBill()) {
			bills.put(envelope.getId(), envelope);
		}
		
		if (envelope.isSavingsGoal()) {
			savingsGoals.put(envelope.getId(), envelope);
		}
		
		if (envelope.getIndex() > maxIndex) {
			maxIndex = envelope.getIndex();
		}
		
		super.addEntity(envelope);
	}
	
	public synchronized int getNextIndex() {
		return ++maxIndex;
	}
	
	public void removeEnvelope(Envelope envelope) {
		
		if (!envelope.isEditable()) return;
		
		Envelope parent = envelope.getParent();
		if (parent != null) {
			parent.removeChild(envelope);
		}
		List<Envelope> children = new LinkedList<Envelope>(envelope.getChildren());
		
		for (Envelope child : children) {
			removeEnvelope(child);
		}
		
		for (Rule rule : transactionRuleService.getEntities()) {
			if (rule.getEnvelope() == envelope) {
				rule.setEnvelope(unassignedEnvelope);
			}
		}
		
		if (envelope.isBill()) {
			bills.remove(envelope.getId());
		}
		
		if (envelope.isSavingsGoal()) {
			savingsGoals.remove(envelope.getId());
		}
		
		super.removeEntity(envelope);
	}
	
	public List<Envelope> getSavingsGoals() {
		return new LinkedList<Envelope>(savingsGoals.values());
	}
	
	public List<Envelope> getOrderedSavingsGoals(boolean reverse) {
		List<Envelope> list = getSavingsGoals();
		if (list.size() > 0) {
			Envelope entity = list.get(0);
			Comparator<Envelope> comparator = reverse ? entity.getReverseComparator() :
				entity.getForwardComparator();
			Collections.sort(list, comparator);
		}
		return list;
	}
	
	public List<Envelope> getOrderedBudgetedEnvelopes(boolean reverse) {
		List<Envelope> list = new LinkedList<Envelope>();
		for (Envelope env : getEntities()) {
			if (env.isEnabled() && env.getBudget() != null && env.getBudget() > 0.0) {
				list.add(env);
			}
		}
		
		if (list.size() > 0) {
			Envelope entity = list.get(0);
			Comparator<Envelope> comparator = reverse ? entity.getReverseComparator() :
				entity.getForwardComparator();
			Collections.sort(list, comparator);
		}
		return list;
	}
	
	public List<Envelope> getBills() {
		return new LinkedList<Envelope>(bills.values());
	}
	
	public List<Envelope> getOrderedBills(boolean reverse) {
		List<Envelope> list = getBills();
		if (list.size() > 0) {
			Envelope entity = list.get(0);
			Comparator<Envelope> comparator = reverse ? entity.getReverseComparator() :
				entity.getForwardComparator();
			Collections.sort(list, comparator);
		}
		return list;
	}
	
	@Override
	public Envelope findEntity(String id) {
		Envelope env = super.findEntity(id);
		if (env == null) {
			env = bills.get(id);
		}
		return env;
	}
	
	public Envelope getBill(String id) {
		Envelope bill = bills.get(id);
		if (bill == null) {
			throw new RuntimeException("No bill exists with ID " + id);
		}
		return bill;
	}
	
	public void bindEnvelopes() {
		Envelope parent;
		for (Envelope env : getEntities()) {
			
			if (env.getParentId() != null) {
				parent = getEntity(env.getParentId());
				env.setParent(parent);
				parent.addChild(env);
				
			}
			
			if (env.isBill()) {
				bills.put(env.getId(), env);
			}
			
			if (env.isSavingsGoal()) {
				savingsGoals.put(env.getId(), env);
			}
			
			env.clearDirty();
		}
	}
	
	public double distributeToNegativeEnvelopes(Account account, Envelope env, double balance) {
		if (!env.isAvailable() && !env.isUnassigned()) {
			if (balance > 0) {
				if (!env.hasChildren()) {
					if (env.getBalance() < 0) {
						double transferAmount = 0;
						if (balance > -env.getBalance()) {
							transferAmount = -env.getBalance();
						} else {
							transferAmount = balance;
						}
						transfer(account, account, getAvailableEnvelope(), env, transferAmount);
						return balance-transferAmount;
					}
				} else {
					for (Envelope child : env.getChildren()) {
						balance = distributeToNegativeEnvelopes(account, child, balance);
					}
				}
			}
		}
		return balance;
	}

	@Override
	protected void clearCache() {
		bills.clear();
		savingsGoals.clear();
		rootEnvelope = null;
		monthlyEnvelope = null;
		unassignedEnvelope = null;
		availableEnvelope = null;
		selectedEnvelope = null;
		savingsGoalsEnvelope = null;
		super.clearCache();
	}
}
