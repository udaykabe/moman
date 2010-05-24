package net.deuce.moman;

import net.deuce.moman.om.Envelope;

public class EnvelopeCrud extends EntityCrud<Envelope> {

  @Override
  protected String getCollectionName() {
    return "envelopes";
  }

  @Override
  protected String getEntityName() {
    return "envelope";
  }

  @Override
  protected String getCreatePath() {
    String[] properties = {
        "name", "TEST",
        "frequency", "MONTHLY",
        "budget", "0.0",
        "dueDay", "1",
        "index", "5",
    };

    StringBuffer sb = new StringBuffer();
    for (String s : properties) {
      sb.append('/').append(s);
    }
    return sb.toString();
  }

  @Override
  protected String getEditPropertyName() {
    return "name";
  }

  @Override
  protected String getEditPropertyValue() {
    return "New Name";
  }
}