package net.deuce.moman.allocation.command;

import net.deuce.moman.command.AbstractEntityHandler;
import net.deuce.moman.entity.model.allocation.AllocationSet;

public abstract class AbstractAllocationSetHandler extends
		AbstractEntityHandler<AllocationSet> {

	public AbstractAllocationSetHandler(boolean multiSelection) {
		super(multiSelection);
		setMultiSelection(true);
	}

}
