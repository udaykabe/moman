package net.deuce.moman.account.ui;

import net.deuce.moman.account.model.Account;
import net.deuce.moman.ui.EntityLabelProvider;

public class AccountEntityLabelProvider implements EntityLabelProvider<Account> {
	public String getLabel(Account entity) {
		return entity.getNickname();
	}
}
