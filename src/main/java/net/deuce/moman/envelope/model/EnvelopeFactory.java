package net.deuce.moman.envelope.model;

import net.deuce.moman.envelope.service.EnvelopeService;
import net.deuce.moman.model.EntityFactory;
import net.deuce.moman.model.Frequency;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EnvelopeFactory extends EntityFactory<Envelope> {
	
	@Autowired
	private EnvelopeService envelopeService;
	
	public Envelope buildEntity(String id, Integer index, String name,
			Frequency frequency, Double budget,  Envelope parent,
			Boolean selected, Boolean expanded, Boolean enabled, Integer dueDay) {
		Envelope entity = super.buildEntity(Envelope.class, id);
		entity.setName(name);
		entity.setIndex(index);
		entity.setFrequency(frequency);
		entity.setBudget(budget);
		entity.setParent(parent);
		entity.setSelected(selected);
		entity.setExpanded(expanded);
		entity.setEnabled(enabled);
		entity.setDueDay(dueDay);
		return entity;
	}
	
	public Envelope newEntity(Integer index, String name, Frequency frequency, Double budget, 
			Envelope parent, Boolean selected, Boolean expanded,
			Boolean enabled, Integer dueDay) {
		Envelope entity = buildEntity(createUuid(), index, name, frequency, budget, 
				parent, selected, expanded, enabled, dueDay);
		entity.setEditable(true);
		return entity;
	}
	
	public Envelope createTopLevelEnvelope() {
		Envelope topLevel = cloneEnvelope(Envelope.TOP_LEVEL, null);
		/*
		if (envelopeService.getAvailableEnvelope() != null) {
			topLevel.addChild(envelopeService.getAvailableEnvelope());
		}
		if (envelopeService.getMonthlyEnvelope() != null) {
			topLevel.addChild(envelopeService.getMonthlyEnvelope());
		}
		if (envelopeService.getUnassignedEnvelope() != null) {
			topLevel.addChild(envelopeService.getUnassignedEnvelope());
		}
		if (envelopeService.getSavingsGoalsEnvelope() != null) {
			topLevel.addChild(envelopeService.getSavingsGoalsEnvelope());
		}
		*/
		if (envelopeService.getRootEnvelope() != null) {
			topLevel.addChild(envelopeService.getRootEnvelope());
		}
		return topLevel;
	}
	
	public Envelope cloneEnvelope(Envelope envelope, Envelope parent) {
		Envelope clone = new Envelope();
		clone.setId(createUuid());
		clone.setParent(parent);
		clone.setEditable(envelope.isEditable());
		clone.setExpanded(envelope.isExpanded());
		clone.setSelected(envelope.isSelected());
		clone.setMonthly(envelope.isMonthly());
		clone.setRoot(envelope.isRoot());
		clone.setUnassigned(envelope.isUnassigned());
		clone.setAvailable(envelope.isAvailable());
		clone.setName(envelope.getName() != null ? envelope.getName() : "");
		clone.setFrequency(envelope.getFrequency() != null ? envelope.getFrequency() : Frequency.NONE);
		clone.setBudget(envelope.getBudget() != null ? envelope.getBudget() : 0.0);
		clone.setEnabled(envelope.isEnabled());
		clone.setDueDay(envelope.getDueDay() != null ? envelope.getDueDay() : 0);
		return clone;
	}
}
