package net.deuce.moman.account.model;

import net.deuce.moman.model.EntityFactory;
import net.sf.ofx4j.domain.data.common.AccountStatus;

import org.springframework.stereotype.Component;

@Component
public class AccountFactory extends EntityFactory<Account> {

	public Account buildEntity(String id, Boolean selected,
			String nickname, String bankId, String accountId,
			String username, String password, AccountStatus status,
			Boolean supportsDownloading) {
		Account entity = super.buildEntity(Account.class, id);
		entity.setSelected(selected);
		entity.setNickname(nickname);
		entity.setBankId(bankId);
		entity.setAccountId(accountId);
		entity.setUsername(username);
		entity.setPassword(password);
		entity.setStatus(status);
		entity.setSupportsDownloading(supportsDownloading);
		return entity;
	}
	
	public Account newEntity(Boolean selected,
			String nickname, String bankId, String accountId,
			String username, String password, AccountStatus status,
			Boolean supportsDownloading) {
		return buildEntity(createUuid(), selected, nickname,
			bankId, accountId, username, password, status,
			supportsDownloading);
	}
}
