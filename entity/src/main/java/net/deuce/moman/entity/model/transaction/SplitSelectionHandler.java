package net.deuce.moman.entity.model.transaction;

import java.util.List;

public interface SplitSelectionHandler {

	public boolean handleSplitSelection(InternalTransaction transaction,
			double newAmount, List<Split> split);
}
