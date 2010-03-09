package net.deuce.moman.transaction.service;

import java.util.Collection;

import net.deuce.moman.transaction.model.InternalTransaction;
import net.deuce.moman.util.RandomAccessLinkedListOFF;

public class TransactionListOFF extends RandomAccessLinkedListOFF<InternalTransaction> {
	
	private static final long serialVersionUID = 1L;
	private boolean allowingTransfers = true;

	public TransactionListOFF() {
		super();
	}

	public TransactionListOFF(Collection<InternalTransaction> c) {
		super(c);
	}

	public boolean isAllowingTransfers() {
		return allowingTransfers;
	}

	public void setAllowingTransfers(boolean allowingTransfers) {
		this.allowingTransfers = allowingTransfers;
	}

	@Override
	protected boolean filter(InternalTransaction entity) {
		return (!allowingTransfers && entity.isEnvelopeTransfer()) ||
			(allowingTransfers && !entity.isEnvelopeTransfer());
	}

}
