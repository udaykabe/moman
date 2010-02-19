package net.deuce.moman.transaction.model;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.deuce.moman.account.model.Account;
import net.deuce.moman.envelope.model.Envelope;
import net.deuce.moman.envelope.ui.SplitSelectionDialog;
import net.deuce.moman.model.AbstractEntity;
import net.deuce.moman.model.EntityProperty;
import net.deuce.moman.service.ServiceNeeder;
import net.deuce.moman.transaction.operation.SetAmountOperation;
import net.sf.ofx4j.domain.data.common.TransactionType;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;

public class InternalTransaction extends AbstractEntity<InternalTransaction> {

	private static final long serialVersionUID = 1L;

    public enum Properties implements EntityProperty {
        externalId(String.class), amount(Double.class), type(TransactionType.class),
        date(Date.class), description(String.class), memo(String.class),
        check(String.class), ref(String.class), balance(Double.class), status(TransactionStatus.class),
        account(Account.class), split(Map.class), initialBalance(Double.class);
        
		private Class<?> ptype;
		
		public Class<?> type() { return ptype; }
		
		private Properties(Class<?> ptype) { this.ptype = ptype; }
    }
	
	private String externalId;
	private Double amount;
	private TransactionType type;
	private Date date;
	private String description;
	private String memo;
	private String check;
	private String ref;
	private Double balance;
	private Boolean initialBalance;
	private TransactionStatus status;

	private InternalTransaction transferTransaction;
	private Account account;
	private Map<Envelope, Split> splitMap = new HashMap<Envelope, Split>();
	
	private transient InternalTransaction matchedTransaction;
	private transient String transferTransactionId;
	
	// constants for annotations
	public static final int TYPE_LENGTH = 20;
	public static final int DESCRIPTION_LENGTH = 256;
	public static final int MEMO_LENGTH = 256;
	public static final int CHECK_NUMBER_LENGTH = 20;
	public static final int REF_NUMBER_LENGTH = 256;
	public static final int EXT_ID_LENGTH = 50;

	public InternalTransaction() {
		super();
	}
	
	public boolean isEnvelopeTransfer() {
		return transferTransaction != null;
	}
	
	public String getTransferTransactionId() {
		return transferTransactionId;
	}

	public void setTransferTransactionId(String transferTransactionId) {
		this.transferTransactionId = transferTransactionId;
	}

	public InternalTransaction getTransferTransaction() {
		return transferTransaction;
	}

	public void setTransferTransaction(InternalTransaction transferTransaction) {
		this.transferTransaction = transferTransaction;
	}

	public TransactionStatus getStatus() {
		return status;
	}

	public void setStatus(TransactionStatus status) {
		if (propertyChanged(this.status, status)) {
			this.status = status;
			getMonitor().fireEntityChanged(this, Properties.status);
		}
	}

	public Double getAmount() {
		return amount;
	}
	
	public void setAmount(Double amount) {
		setAmount(amount, false);
	}
	
	private boolean adjustSplits(double newAmount) {
		if (splitMap.size() == 0) return true;
		
		List<Split> split = new LinkedList<Split>(splitMap.values());
		if (split.size() == 1) {
			split.get(0).setAmount(newAmount);
		} else {
			final SplitSelectionDialog dialog = new SplitSelectionDialog(
					Display.getCurrent().getActiveShell(), newAmount, split);
			
			dialog.setAllowBills(true);
			dialog.create();
			int status = dialog.open();
			final List<Split> result = dialog.getSplit();
			if (status == Window.OK) {
				if (!split.equals(result)) {
					BusyIndicator.showWhile(Display.getCurrent(), new Runnable() {
						public void run() {
							ServiceNeeder.instance().getServiceContainer().startQueuingNotifications();
							try {
								clearSplit();
									
								for (Split item : result) {
									if (getAmount() < 0.0) {
										item.setAmount(-item.getAmount());
									}
									addSplit(item, true);
								}
							} finally {
								ServiceNeeder.instance().getServiceContainer().stopQueuingNotifications();
							}
						}
					});
				}
			} else {
				return false;
			}
		}
		return true;
	}
	
	public void setAmount(Double amount, boolean adjust) {
		if (propertyChanged(this.amount, amount)) {
			double difference = 0.0;
			if (this.amount != null) {
				difference = this.amount - amount;
			}
			if (adjustSplits(amount)) {
				this.amount = amount;
				if (amount > 0) {
					type = TransactionType.CREDIT;
				} else if (type == null || type != TransactionType.CHECK) {
					type = TransactionType.DEBIT;
				}
				if (balance != null) {
					setBalance(balance - difference);
					for (Split split : splitMap.values()) {
						split.getEnvelope().resetBalance();
					}
					getMonitor().fireEntityChanged(this, Properties.amount);
				}
				if (adjust) {
					getTransactionService().adjustBalances(this, false);
				}
			}
		}
	}
	
	public boolean isInitialBalance() {
		return evaluateBoolean(initialBalance);
	}

	public Boolean getInitialBalance() {
		return initialBalance;
	}

	public void setInitialBalance(Boolean initialBalance) {
		this.initialBalance = initialBalance;
	}

	public boolean isMatched() {
		return matchedTransaction != null;
	}

	public void setMatchedTransaction(InternalTransaction transaction) {
		this.matchedTransaction = transaction;
	}
	
	public InternalTransaction getMatchedTransaction() {
		return matchedTransaction;
	}

	public Double getBalance() {
		return balance;
	}

	public void setBalance(Double balance) {
		if (propertyChanged(this.balance, balance)) {
			this.balance = balance;
			getMonitor().fireEntityChanged(this);
		}
	}

	public TransactionType getType() {
		return type;
	}

	public void setType(TransactionType type) {
		if (propertyChanged(this.type, type)) {
			this.type = type;
			getMonitor().fireEntityChanged(this, Properties.type);
		}
	}

	public Date getDate() {
		return date;
	}
	
	public void setDate(Date date) {
		setDate(date, false);
	}
	
	public void setDate(Date date, boolean adjust) {
		if (propertyChanged(this.date, date)) {
			this.date = date;
			getMonitor().fireEntityChanged(this, Properties.date);
			
			if (adjust) {
				getTransactionService().adjustBalances(this, false);
			}
		}
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		if (propertyChanged(this.description, description)) {
			this.description = description;
			getMonitor().fireEntityChanged(this);
		}
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		if (propertyChanged(this.memo, memo)) {
			this.memo = memo;
			getMonitor().fireEntityChanged(this);
		}
	}

	public String getCheck() {
		return check;
	}

	public void setCheck(String check) {
		if (propertyChanged(this.check, check)) {
			this.check = check;
			getMonitor().fireEntityChanged(this);
		}
	}

	public String getRef() {
		return ref;
	}

	public void setRef(String ref) {
		if (propertyChanged(this.ref, ref)) {
			this.ref = ref;
			getMonitor().fireEntityChanged(this);
		}
	}
	
	public boolean isExternal() {
		return externalId != null;
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		if (propertyChanged(this.externalId, externalId)) {
			this.externalId = externalId;
			getMonitor().fireEntityChanged(this, Properties.externalId);
		}
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		if (propertyChanged(this.account, account)) {
			this.account = account;
			getMonitor().fireEntityChanged(this);
		}
	}
	
	public Double getSplitAmount(Envelope env) {
		Split split = splitMap.get(env);
		if (split != null) {
			return split.getAmount();
		} 
		return 0.0;
	}

	public void clearSplit() {
		for (Envelope env : splitMap.keySet()) {
			env.removeTransaction(this, false);
		}
		splitMap.clear();
		getMonitor().fireEntityChanged(this, Properties.split);
	}
	
	public void addSplit(Split item) {
		addSplit(item, true);
	}
	
	public void addSplit(Envelope envelope, Double amount) {
		addSplit(new Split(envelope, amount), true);
	}
	
	public void addSplit(Envelope envelope, Double amount, boolean notifyEnvelope) {
		addSplit(new Split(envelope, amount), notifyEnvelope);
	}
	
	public void addSplit(Split split, boolean notifyEnvelope) {
		if (!splitMap.containsValue(split)) {
			if (notifyEnvelope) {
				split.getEnvelope().addTransaction(this, false);
			}
			splitMap.put(split.getEnvelope(), split);
			getMonitor().fireEntityChanged(this, Properties.split);
		}
	}

	public void removeSplit(Envelope envelope) {
		removeSplit(envelope, true);
	}
	
	public void removeSplit(Envelope envelope, boolean notifyEnvelope) {
		if (notifyEnvelope) {
			envelope.removeTransaction(this, false);
		}
		
		if (splitMap.remove(envelope) != null) {
			envelope.resetBalance();
			getMonitor().fireEntityChanged(this, Properties.split);
		}
	}
	
	public List<Split> getSplit() {
		return new LinkedList<Split>(splitMap.values());
	}
	
	public void setSplit(List<Split> splitList) {
		setSplit(splitList, true);
	}
	
	public void setSplit(List<Split> splitList, boolean notify) {
		clearSplit();
		for (Split split : splitList) {
			addSplit(split, notify);
		}
	}
	
	@Override
	public int compareTo(InternalTransaction o) {
		return compare(this, o);
	}
	
	@Override
	public int compare(InternalTransaction o1, InternalTransaction o2) {
		int dateCompare = o1.date.compareTo(o2.getDate());
		
		if (dateCompare == 0) {
			if (o1.externalId != null && o2.getExternalId() != null) {
				return o1.externalId.compareTo(o2.getExternalId());
			}
			if (o1.externalId != null) {
				return -1;
			}
			if (o2.externalId != null) {
				return 1;
			}
			return o1.description.compareTo(o2.description);
		}
		return dateCompare;
	}

	@Override
    public boolean equals(Object o) {
		if (this == o) return true;
	    if (o == null || getClass() != o.getClass()) return false;

	    InternalTransaction it = (InternalTransaction) o;

	    if (externalId != null ? !externalId.equals(it.externalId) : it.externalId != null) return false;
	    if (getId() != null ? !getId().equals(it.getId()) : it.getId() != null) return false;

	    return true;
	}

	@Override
	public int hashCode() {
	    int result = getId() != null ? getId().hashCode() : 0;
	    result = 31 * result + (externalId != null ? externalId.hashCode() : 0);
	    return result;
	}

	public void executeSetAmount(Double value) {
		SetAmountOperation operation = new SetAmountOperation(this, value);
		executeOperation(operation);
	}

	@Override
	public String toString() {
		return "InternalTransaction : " + getId() + " - " + description + " - " + amount;
	}
	
}
