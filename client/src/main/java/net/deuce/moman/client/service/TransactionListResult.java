package net.deuce.moman.client.service;

import net.deuce.moman.client.model.TransactionClient;

import java.util.List;

public class TransactionListResult {

  private List<TransactionClient> transactions;
  private int totalSize;
  private int pageSize;

  public TransactionListResult(int pageSize, int totalSize, List<TransactionClient> transactions) {
    this.pageSize = pageSize;
    this.totalSize = totalSize;
    this.transactions = transactions;
  }

  public int getPageSize() {
    return pageSize;
  }

  public void setPageSize(int pageSize) {
    this.pageSize = pageSize;
  }

  public int getTotalSize() {
    return totalSize;
  }

  public void setTotalSize(int totalSize) {
    this.totalSize = totalSize;
  }

  public List<TransactionClient> getTransactions() {
    return transactions;
  }

  public void setTransactions(List<TransactionClient> transactions) {
    this.transactions = transactions;
  }
}
