package net.deuce.moman;

import net.deuce.moman.om.User;

public class UserCrud extends EntityCrud<User> {

  @Override
  protected String getCollectionName() {
    return "users";
  }

  @Override
  protected String getEntityName() {
    return "user";
  }

  @Override
  protected String getCreatePath() {
    return "/username/test/password/wee";
  }

  @Override
  protected String getEditPropertyName() {
    return "username";
  }

  @Override
  protected String getEditPropertyValue() {
    return "New username";
  }
}