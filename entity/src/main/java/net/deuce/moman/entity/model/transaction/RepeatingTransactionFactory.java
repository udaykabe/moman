package net.deuce.moman.entity.model.transaction;

import java.util.Date;

import net.deuce.moman.entity.model.EntityFactory;
import net.deuce.moman.entity.model.Frequency;
import net.deuce.moman.entity.model.account.Account;
import net.sf.ofx4j.domain.data.common.TransactionType;

public interface RepeatingTransactionFactory extends EntityFactory<RepeatingTransaction> {
	
	public RepeatingTransaction buildEntity(String id, String externalId,
			Double amount, TransactionType type, Date date, String description,
			String memo, String check, String ref, Double balance,
			Account account, Date originalDateDue, Date dateDue,
			Frequency frequency, Integer count);
		
	public RepeatingTransaction newEntity(String externalId,
			Double amount, TransactionType type, Date date, String description,
			String memo, String check, String ref, Double balance,
			Account account, Date originalDateDue, Date dateDue,
			Frequency frequency, Integer count);
}
