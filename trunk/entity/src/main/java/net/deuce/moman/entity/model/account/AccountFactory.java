package net.deuce.moman.entity.model.account;

import net.deuce.moman.entity.model.EntityFactory;
import net.sf.ofx4j.domain.data.common.AccountStatus;

public interface AccountFactory extends EntityFactory<Account> {

	public Account buildEntity(String id, Boolean selected,
			String nickname, String bankId, String accountId,
			String username, String password, AccountStatus status,
			Boolean supportsDownloading, Double balance, Double onlineBalance,
			Double lastReconciledEndingBalance);
	
	public Account newEntity(Boolean selected,
			String nickname, String bankId, String accountId,
			String username, String password, AccountStatus status,
			Boolean supportsDownloading, Double balance, Double onlineBalance,
			Double lastReconciledEndingBalance);
}
