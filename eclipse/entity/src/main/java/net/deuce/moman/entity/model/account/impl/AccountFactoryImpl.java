package net.deuce.moman.entity.model.account.impl;

import net.deuce.moman.entity.model.account.Account;
import net.deuce.moman.entity.model.account.AccountFactory;
import net.deuce.moman.entity.model.impl.EntityFactoryImpl;
import net.sf.ofx4j.domain.data.common.AccountStatus;

import org.springframework.stereotype.Component;

@Component("accountFactory")
public class AccountFactoryImpl extends EntityFactoryImpl<Account> implements AccountFactory {

	public Account buildEntity(String id, Boolean selected,
			String nickname, String bankId, String accountId,
			String username, String password, AccountStatus status,
			Boolean supportsDownloading, Double balance, Double onlineBalance,
			Double lastReconciledEndingBalance) {
		Account entity = super.buildEntity(Account.class, id);
		entity.setSelected(selected);
		entity.setNickname(nickname);
		entity.setBankId(bankId);
		entity.setAccountId(accountId);
		entity.setUsername(username);
		entity.setPassword(password);
		entity.setStatus(status);
		entity.setSupportsDownloading(supportsDownloading);
		entity.setBalance(balance);
		entity.setOnlineBalance(onlineBalance);
		entity.setLastReconciledEndingBalance(lastReconciledEndingBalance);
		return entity;
	}
	
	public Account newEntity(Boolean selected,
			String nickname, String bankId, String accountId,
			String username, String password, AccountStatus status,
			Boolean supportsDownloading, Double balance, Double onlineBalance,
			Double lastReconciledEndingBalance) {
		return buildEntity(createUuid(), selected, nickname,
			bankId, accountId, username, password, status,
			supportsDownloading, balance, onlineBalance, lastReconciledEndingBalance);
	}
}
