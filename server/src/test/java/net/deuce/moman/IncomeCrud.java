package net.deuce.moman;

import net.deuce.moman.om.Account;

public class IncomeCrud extends EntityCrud<Account> {

  @Override
  protected String getCollectionName() {
    return "accounts";
  }

  @Override
  protected String getEntityName() {
    return "account";
  }

  @Override
  protected String getCreatePath() {
    return "/nickname/Checking/status/ACTIVE/financialInstitution/281f5b66-df4a-30a8-a7c7-908e8e55c61b/bankId/122100024/accountId/721051696/username/fruptichase/password/cqopklm7/supportsDownloading/true";
  }

  @Override
  protected String getEditPropertyName() {
    return "nickname";
  }

  @Override
  protected String getEditPropertyValue() {
    return "New Nickname";
  }
}