package net.deuce.moman.controller.command;

import org.springframework.beans.factory.annotation.Autowired;

public abstract class EntityAccessingController extends AbstractCommandController {

  @Autowired
  private EntityAdapter entityAdapter;

  public EntityAdapter getEntityAdapter() {
    return entityAdapter;
  }

  public void setEntityAdapter(EntityAdapter entityAdapter) {
    this.entityAdapter = entityAdapter;
  }
}
