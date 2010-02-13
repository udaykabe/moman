package net.deuce.moman.transaction.service;

import java.io.FileReader;
import java.util.Date;
import java.util.List;

import net.deuce.moman.account.model.Account;
import net.deuce.moman.transaction.ui.TransactionImportView;
import net.sf.ofx4j.domain.data.MessageSetType;
import net.sf.ofx4j.domain.data.ResponseEnvelope;
import net.sf.ofx4j.domain.data.ResponseMessage;
import net.sf.ofx4j.domain.data.ResponseMessageSet;
import net.sf.ofx4j.domain.data.banking.BankStatementResponse;
import net.sf.ofx4j.domain.data.banking.BankStatementResponseTransaction;
import net.sf.ofx4j.domain.data.common.StatementResponse;
import net.sf.ofx4j.domain.data.common.Transaction;
import net.sf.ofx4j.io.AggregateUnmarshaller;

import org.eclipse.swt.widgets.Shell;

public class FileImportingTransactionProcessor extends TransactionProcessor {
	
	private String file;
	
	public FileImportingTransactionProcessor(Shell shell, Account account, String file) {
		super(shell, account, false, TransactionImportView.ID);
		this.file = file;
	}

	@Override
	protected TransactionFetchResult fetchTransactions(Account account)
			throws Exception {
		
		FileReader fileReader = new FileReader(file);
    	AggregateUnmarshaller<ResponseEnvelope> unmarshaller = new AggregateUnmarshaller<ResponseEnvelope>(ResponseEnvelope.class);
    	ResponseEnvelope response = unmarshaller.unmarshal(fileReader);
    	
    	Double statementBalance = null;
    	Date lastDownloadedDate = null;
    	List<Transaction> bankTransactions = null;
    	
    	for (MessageSetType type : new MessageSetType[]{MessageSetType.banking, MessageSetType.creditcard}) {
        	ResponseMessageSet messageSet = response.getMessageSet(type);
        	if (messageSet != null && messageSet.getResponseMessages() != null) {
	        	for (ResponseMessage message : messageSet.getResponseMessages()) {
	        		if (message instanceof StatementResponse) {
	        			StatementResponse statement = (StatementResponse)message;
	        			statementBalance = statement.getLedgerBalance().getAmount();
	        			lastDownloadedDate = statement.getLedgerBalance().getAsOfDate();
	        			bankTransactions = statement.getTransactionList().getTransactions();
	        		} else if (message instanceof BankStatementResponse) {
	        			BankStatementResponse statement = (BankStatementResponse)message;
	        			statementBalance = statement.getLedgerBalance().getAmount();
	        			lastDownloadedDate = statement.getLedgerBalance().getAsOfDate();
	        			bankTransactions = statement.getTransactionList().getTransactions();
	        		} else if (message instanceof BankStatementResponseTransaction) {
	        			BankStatementResponseTransaction responseTranaction = (BankStatementResponseTransaction)message;
	        			BankStatementResponse statement = responseTranaction.getMessage();
	        			statementBalance = statement.getLedgerBalance().getAmount();
	        			lastDownloadedDate = statement.getLedgerBalance().getAsOfDate();
	        			bankTransactions = statement.getTransactionList().getTransactions();
	        		} else {
	        			System.out.println("ZZZ message: " + message.getClass().getName() + " - " + message);
	        		}
	        	}
        	}
    	}
		return new TransactionFetchResult(statementBalance, lastDownloadedDate,
				bankTransactions);
	}

}
