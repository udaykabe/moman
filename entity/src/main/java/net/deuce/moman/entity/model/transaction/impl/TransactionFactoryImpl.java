package net.deuce.moman.entity.model.transaction.impl;

import java.util.Date;

import net.deuce.moman.entity.model.account.Account;
import net.deuce.moman.entity.model.impl.EntityFactoryImpl;
import net.deuce.moman.entity.model.transaction.InternalTransaction;
import net.deuce.moman.entity.model.transaction.TransactionFactory;
import net.deuce.moman.entity.model.transaction.TransactionStatus;
import net.sf.ofx4j.domain.data.common.TransactionType;

import org.springframework.stereotype.Component;

@Component("transactionFactory")
public class TransactionFactoryImpl extends EntityFactoryImpl<InternalTransaction>
implements TransactionFactory {

	public void setTransactionProperties(InternalTransaction entity, String externalId,
			Double amount, TransactionType type, Date date, String description,
			String memo, String check, String ref, Double balance, TransactionStatus status,
			Account account) {
		
		entity.setExternalId(externalId);
		entity.setType(type);
		entity.setAmount(amount, null);
		entity.setDate(date);
		entity.setDescription(description);
		entity.setMemo(memo);
		entity.setCheck(check);
		entity.setRef(ref);
		entity.setBalance(balance);
		entity.setStatus(status);
		entity.setAccount(account);
	}
	
	public InternalTransaction buildEntity(String id, String externalId,
		Double amount, TransactionType type, Date date, String description,
		String memo, String check, String ref, Double balance, TransactionStatus status,
		Account account) {
		InternalTransaction entity = super.buildEntity(InternalTransaction.class, id);
		setTransactionProperties(entity, externalId, amount, type, date,
			description, memo, check, ref, balance, status, account);
		return entity;
	}
	
	public InternalTransaction newEntity(String externalId,
			Double amount, TransactionType type, Date date, String description,
			String memo, String check, String ref, Double balance, TransactionStatus status,
			Account account) {
		return buildEntity(createUuid(), externalId, amount, type, date,
			description, memo, check, ref, balance, status, account);
	}
}
