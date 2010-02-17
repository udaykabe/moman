package net.deuce.moman.transaction.model;

public enum TransactionStatus {
	open("icons/transactionStatus_Open.png"),
	cleared("icons/transactionStatus_Cleared.png"),
	reconciled("icons/transactionStatus_Reconciled.png"),
	voided("icons/transactionStatus_Voided.png"),
	pending("icons/transactionStatus_Pending.png");
	
	private String iconPath;
	
	public String iconPath() { return iconPath; }
	
	private TransactionStatus(String iconPath) { this.iconPath = iconPath; }
}