package net.deuce.moman.transaction.service;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;

import net.deuce.moman.account.model.Account;
import net.deuce.moman.account.service.AccountService;
import net.deuce.moman.envelope.model.Envelope;
import net.deuce.moman.envelope.service.EnvelopeService;
import net.deuce.moman.service.EntityService;
import net.deuce.moman.transaction.model.InternalTransaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionService extends EntityService<InternalTransaction> {

	private Map<String, InternalTransaction> transactionsByExternalId = new HashMap<String, InternalTransaction>();
	private Map<Account, List<InternalTransaction>> accountTransactions = new HashMap<Account, List<InternalTransaction>>();
	private Map<Account, InternalTransaction> initialBalanceTransactions = new HashMap<Account, InternalTransaction>();

	@Autowired
	private AccountService accountService;

	@Autowired
	private EnvelopeService envelopeService;
	
	public void bindTransferTransactions() {
		for (InternalTransaction it : getEntities()) {
			if (it.getTransferTransactionId() != null && 
					(it.getTransferTransaction() == null || 
							!it.getTransferTransaction().getId().equals(it.getTransferTransactionId()))) {
				it.setTransferTransaction(getEntity(it.getTransferTransactionId()));
			}
		}
	}
	
	public InternalTransaction findTransactionByExternalId(String id) {
		return transactionsByExternalId.get(id);
	}
	
	public void addExternalTransactionReference(InternalTransaction transaction) {
		if (transaction.getExternalId() != null) {
			transactionsByExternalId.put(transaction.getExternalId(), transaction);
		}
	}
	
	public void removeExternalTransactionReference(InternalTransaction transaction) {
		if (transaction.getExternalId() != null) {
			transactionsByExternalId.remove(transaction.getExternalId());
		}
	}
	
	public void removeAccountTransactions(Account account) {
		boolean queuingNotifications = isQueuingNotifications();
		
		if (!queuingNotifications) {
			startQueuingNotifications();
		}
		try {
			for (InternalTransaction it : new LinkedList<InternalTransaction>(__getAccountTransactions(account))) {
				removeEntity(it);
			}
		} finally {
			if (!queuingNotifications) {
				stopQueuingNotifications();
			}
		}
	}
	
	public List<InternalTransaction> getAccountTransactions(Account account, boolean reverse) {
		LinkedList<InternalTransaction> list = new LinkedList<InternalTransaction>(__getAccountTransactions(account));
		if (list.size() > 0) {
			InternalTransaction it = list.get(0);
			Comparator<InternalTransaction> comparator = reverse ? it.getReverseComparator() :
				it.getForwardComparator();
			Collections.sort(list, comparator);
		}
		return list;
	}
	
	private List<InternalTransaction> __getAccountTransactions(Account account) {
		List<InternalTransaction> list = accountTransactions.get(account);
		if (list == null) {
			list = new LinkedList<InternalTransaction>();
			accountTransactions.put(account, list);
		}
		return list;
	}
	
	public List<InternalTransaction> getRegisterTransactions(boolean reverse, boolean allowingTransfers) {
		List<InternalTransaction> list = new LinkedList<InternalTransaction>(__getRegisterTransactions());
		if (list.size() > 0) {
			
			// filter
			ListIterator<InternalTransaction> itr = list.listIterator();
			while (itr.hasNext()) {
				InternalTransaction it = itr.next();
				if ((!allowingTransfers && it.isEnvelopeTransfer()) ||
						(allowingTransfers && !it.isEnvelopeTransfer())) {
					itr.remove();
				}
			}
			
			// sort
			if (list.size() > 1) {
				InternalTransaction it = list.get(0);
				Comparator<InternalTransaction> comparator = reverse ? it.getReverseComparator() :
					it.getForwardComparator();
				Collections.sort(list, comparator);
			}
		}
		return list;
	}
	
	private void addEnvelopeTransactions(List<InternalTransaction> list, Envelope env) {
		list.addAll(env.getTransactions());
		for (Envelope child : env.getChildren()) {
			addEnvelopeTransactions(list, child);
		}
	}
	
	private List<InternalTransaction> __getRegisterTransactions() {
		
		List<Account> selectedAccounts = accountService.getSelectedAccounts();
		Envelope selectedEnvelope = envelopeService.getSelectedEnvelope();
		
		if (selectedAccounts.size() == 0 && selectedEnvelope == null) {
			return getEntities();
		}
		
		List<InternalTransaction> list = new LinkedList<InternalTransaction>();
		
		if (selectedEnvelope != null) {
			addEnvelopeTransactions(list, selectedEnvelope);
			return list;
		}
		
		for (Entry<Account, List<InternalTransaction>> entry : accountTransactions.entrySet()) {
			if (selectedAccounts.size() == 0 || selectedAccounts.contains(entry.getKey())) {
				list.addAll(entry.getValue());
			}
		}
		return list;
	}
	
	public void adjustBalances(InternalTransaction transaction, boolean remove) {
		
		Double balance = null;
		
		boolean foundStartingPlace = false;
		Account account = transaction.getAccount();
		
		for (InternalTransaction it : getAccountTransactions(account, false)) {
			if (it == transaction) {
				if (balance == null) {
					balance = 0.0;
				}
				foundStartingPlace = true;
			}
			
			if (foundStartingPlace) {
				if (!remove) {
					try {
					balance += it.getAmount();
					} catch (Exception e) {
						e.printStackTrace();
					}
					it.setBalance(balance);
					
					// viewer might not be set during initialization
//					if (getViewer() != null) {
//						getViewer().refresh(it);
//					}
				}
			} else {
				balance = it.getBalance();
			}
			
		}
		
		/*
		Double balance = null;
		
		boolean foundStartingPlace = false;
		Account account = transaction.getAccount();
		
		for (InternalTransaction it : getAccountTransactions(account, false)) {
			if (it == transaction) {
				if (balance == null) {
					balance = account.getInitialBalance();
				}
				foundStartingPlace = true;
			}
			
			if (foundStartingPlace) {
				if (!remove) {
					try {
					balance += it.getAmount();
					} catch (Exception e) {
						e.printStackTrace();
					}
					it.setBalance(balance);
					
					// viewer might not be set during initialization
//					if (getViewer() != null) {
//						getViewer().refresh(it);
//					}
				}
			} else {
				balance = it.getBalance();
			}
			
		}
		*/
	}
	
	public InternalTransaction getInitialBalanceTransaction(Account account) {
		return initialBalanceTransactions.get(account);
	}
	
	@Override
	public void addEntity(InternalTransaction transaction) {
		addEntity(transaction, true);
	}
	
	public void addEntity(InternalTransaction transaction, boolean updateBalances) {
		addEntity(transaction, updateBalances, true);
	}
	
	public void addEntity(InternalTransaction transaction, boolean updateBalances, boolean notify) {
		addExternalTransactionReference(transaction);
		__getAccountTransactions(transaction.getAccount()).add(transaction);
		if (transaction.isInitialBalance()) {
			initialBalanceTransactions.put(transaction.getAccount(), transaction);
		}
		super.addEntity(transaction, false);
		
		if (updateBalances) {
			adjustBalances(transaction, false);
		}
		if (notify) {
			fireEntityAdded(transaction);
		}
	}
	
	@Override
	public void removeEntity(InternalTransaction transaction) {
		removeExternalTransactionReference(transaction);
		
		for (Envelope env : new LinkedList<Envelope>(transaction.getSplit())) {
			env.removeTransaction(transaction);
		}
		
		__getAccountTransactions(transaction.getAccount()).remove(transaction);
		
		adjustBalances(transaction, true);
		super.removeEntity(transaction);
	}
	
	@Override
	protected void clearCache() {
		transactionsByExternalId.clear();
		accountTransactions.clear();
		super.clearCache();
	}
}
