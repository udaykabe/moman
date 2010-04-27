package net.deuce.moman.entity.model.transaction;

import java.util.Date;

import net.deuce.moman.entity.model.EntityFactory;
import net.deuce.moman.entity.model.account.Account;
import net.sf.ofx4j.domain.data.common.TransactionType;

public interface TransactionFactory extends EntityFactory<InternalTransaction> {

	public InternalTransaction buildEntity(String id, String externalId,
		Double amount, TransactionType type, Date date, String description,
		String memo, String check, String ref, Double balance, TransactionStatus status,
		Account account);
	
	public InternalTransaction newEntity(String externalId,
			Double amount, TransactionType type, Date date, String description,
			String memo, String check, String ref, Double balance, TransactionStatus status,
			Account account);
	
	public void setTransactionProperties(InternalTransaction entity, String externalId,
			Double amount, TransactionType type, Date date, String description,
			String memo, String check, String ref, Double balance, TransactionStatus status,
			Account account);
}
