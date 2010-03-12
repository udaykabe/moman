package net.deuce.moman.entity.model.allocation;

import net.deuce.moman.entity.model.EntityBuilder;

import org.dom4j.Element;

public interface AllocationSetBuilder extends EntityBuilder<AllocationSet> {
	
	public void parseAllocationXml(Element e, AllocationSet allocationSet);
	
}
