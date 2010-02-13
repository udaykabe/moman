package net.deuce.moman.transaction.model;

import java.util.Date;

import net.deuce.moman.account.model.Account;
import net.deuce.moman.model.EntityFactory;

import org.springframework.stereotype.Component;

@Component
public class TransactionFactory extends EntityFactory<InternalTransaction> {

	protected void setTransactionProperties(InternalTransaction entity, String externalId,
			Double amount, String type, Date date, String description,
			String memo, String check, String ref, Double balance,
			Account account) {
		
		entity.setExternalId(externalId);
		entity.setType(type);
		entity.setAmount(amount);
		entity.setDate(date);
		entity.setDescription(description);
		entity.setMemo(memo);
		entity.setCheck(check);
		entity.setRef(ref);
		entity.setBalance(balance);
		entity.setAccount(account);
	}
	
	public InternalTransaction buildEntity(String id, String externalId,
		Double amount, String type, Date date, String description,
		String memo, String check, String ref, Double balance,
		Account account) {
		InternalTransaction entity = super.buildEntity(InternalTransaction.class, id);
		setTransactionProperties(entity, externalId, amount, type, date,
			description, memo, check, ref, balance, account);
		return entity;
	}
	
	public InternalTransaction newEntity(String externalId,
			Double amount, String type, Date date, String description,
			String memo, String check, String ref, Double balance,
			Account account) {
		return buildEntity(createUuid(), externalId, amount, type, date,
			description, memo, check, ref, balance, account);
	}
}
