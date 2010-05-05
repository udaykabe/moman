package net.deuce.moman.controller.command;

import net.sf.ehcache.Cache;

public abstract class AbstractJobCommandController extends AbstractCommandController {

  private Cache cache;

  public Cache getCache() {
    return cache;
  }

  public void setCache(Cache cache) {
    this.cache = cache;
  }
}