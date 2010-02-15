package net.deuce.moman.transaction.model;

import java.util.Date;

import net.deuce.moman.account.model.Account;
import net.deuce.moman.model.EntityFactory;
import net.deuce.moman.model.Frequency;
import net.sf.ofx4j.domain.data.common.TransactionType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RepeatingTransactionFactory extends EntityFactory<RepeatingTransaction> {
	
	@Autowired
	private TransactionFactory transactionFactory;
	
	public RepeatingTransaction buildEntity(String id, String externalId,
			Double amount, TransactionType type, Date date, String description,
			String memo, String check, String ref, Double balance,
			Account account, Date originalDateDue, Date dateDue,
			Frequency frequency, Integer count) {
		
		try {
			RepeatingTransaction entity = new RepeatingTransaction();
			entity.setId(id);
			
			transactionFactory.setTransactionProperties(entity, externalId, amount, type, date,
				description, memo, check, ref, balance, account);
			entity.setOriginalDateDue(originalDateDue);
			entity.setDateDue(dateDue);
			entity.setFrequency(frequency);
			entity.setCount(count);
			return entity;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
		
	public RepeatingTransaction newEntity(String externalId,
			Double amount, TransactionType type, Date date, String description,
			String memo, String check, String ref, Double balance,
			Account account, Date originalDateDue, Date dateDue,
			Frequency frequency, Integer count) {
		return buildEntity(createUuid(), externalId, amount, type, date,
			description, memo, check, ref, balance, account, originalDateDue,
			dateDue, frequency, count);
	}
}
