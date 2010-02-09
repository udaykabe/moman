package net.deuce.moman.model.transaction;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import net.deuce.moman.model.EntityMonitor;
import net.deuce.moman.model.MomanEntity;
import net.deuce.moman.model.account.Account;
import net.deuce.moman.model.envelope.Envelope;

public class Transaction extends MomanEntity implements Comparable<Transaction> {

	private static final long serialVersionUID = 1L;

	private String externalId;
	private double amount;
	private String type;
	private Date date;
	private String description;
	private String memo;
	private String check;
	private String ref;
	private double balance;

	private Account account;
	private List<Envelope> split = new LinkedList<Envelope>();
	
	private transient EntityMonitor<Transaction> monitor = new EntityMonitor<Transaction>();
	
	private transient boolean matched = false;
	
	// constants for annotations
	public static final int TYPE_LENGTH = 20;
	public static final int DESCRIPTION_LENGTH = 256;
	public static final int MEMO_LENGTH = 256;
	public static final int CHECK_NUMBER_LENGTH = 20;
	public static final int REF_NUMBER_LENGTH = 256;
	public static final int EXT_ID_LENGTH = 50;

	
	public void setMonitor(EntityMonitor<Transaction> monitor) {
		this.monitor = monitor;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		if (propertyChanged(this.amount, amount)) {
			this.amount = amount;
			monitor.fireEntityChanged(this);
		}
	}
	
	public boolean isMatched() {
		return matched;
	}

	public void setMatched(boolean matched) {
		this.matched = matched;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		if (propertyChanged(this.balance, balance)) {
			this.balance = balance;
			monitor.fireEntityChanged(this);
		}
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		if (propertyChanged(this.type, type)) {
			this.type = type;
			monitor.fireEntityChanged(this);
		}
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		if (propertyChanged(this.date, date)) {
			this.date = date;
			monitor.fireEntityChanged(this);
		}
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		if (propertyChanged(this.description, description)) {
			this.description = description;
			monitor.fireEntityChanged(this);
		}
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		if (propertyChanged(this.memo, memo)) {
			this.memo = memo;
			monitor.fireEntityChanged(this);
		}
	}

	public String getCheck() {
		return check;
	}

	public void setCheck(String check) {
		if (propertyChanged(this.check, check)) {
			this.check = check;
			monitor.fireEntityChanged(this);
		}
	}

	public String getRef() {
		return ref;
	}

	public void setRef(String ref) {
		if (propertyChanged(this.ref, ref)) {
			this.ref = ref;
			monitor.fireEntityChanged(this);
		}
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		if (propertyChanged(this.externalId, externalId)) {
			this.externalId = externalId;
			monitor.fireEntityChanged(this);
		}
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		if (propertyChanged(this.account, account)) {
			this.account = account;
			monitor.fireEntityChanged(this);
		}
	}

	public void clearSplit() {
		split.clear();
		monitor.fireEntityChanged(this);
	}

	public void addSplit(Envelope envelope) {
		split.add(envelope);
		monitor.fireEntityChanged(this);
	}

	public void removeSplit(Envelope envelope) {
		split.remove(envelope);
		monitor.fireEntityChanged(this);
	}
	
	public List<Envelope> getSplit() {
		return split;
	}

	@Override
	public int compareTo(Transaction o) {
		return externalId.compareTo(o.getExternalId());
	}
}
