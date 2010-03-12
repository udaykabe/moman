package net.deuce.moman.entity.service.transaction;

import java.util.Date;
import java.util.List;

import net.sf.ofx4j.domain.data.common.Transaction;

public class TransactionFetchResult {

	private Double statementBalance;
	private Date lastDownloadedDate;
	private List<Transaction> bankTransactions;
	
	public TransactionFetchResult(Double statementBalance,
			Date lastDownloadedDate, List<Transaction> bankTransactions) {
		super();
		this.statementBalance = statementBalance;
		this.lastDownloadedDate = lastDownloadedDate;
		this.bankTransactions = bankTransactions;
	}
	
	public Double getStatementBalance() {
		return statementBalance;
	}
	
	public Date getLastDownloadedDate() {
		return lastDownloadedDate;
	}
	
	public List<Transaction> getBankTransactions() {
		return bankTransactions;
	}

}
