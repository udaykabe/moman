package net.deuce.moman.allocation.command;

import net.deuce.moman.allocation.model.AllocationSet;
import net.deuce.moman.command.AbstractEntityHandler;

public abstract class AbstractAllocationSetHandler extends AbstractEntityHandler<AllocationSet> {

	public AbstractAllocationSetHandler(boolean multiSelection) {
		super(multiSelection);
		setMultiSelection(true);
	}

}
