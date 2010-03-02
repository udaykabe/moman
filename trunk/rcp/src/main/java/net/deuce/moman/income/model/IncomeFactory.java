package net.deuce.moman.income.model;

import java.util.Date;

import net.deuce.moman.model.EntityFactory;
import net.deuce.moman.model.Frequency;

import org.springframework.stereotype.Component;

@Component
public class IncomeFactory extends EntityFactory<Income> {

	public Income buildEntity(String id,  String name, Double amount,
			Boolean enabled, Date nextPayday, Frequency frequency) {
		Income entity = super.buildEntity(Income.class, id);
		entity.setName(name);
		entity.setAmount(amount);
		entity.setEnabled(enabled);
		entity.setNextPayday(nextPayday);
		entity.setFrequency(frequency);
		return entity;
	}
	
	public Income newEntity(String name, Double amount,
			Boolean enabled, Date nextPayday, Frequency frequency) {
		return buildEntity(createUuid(), name, amount,
				enabled, nextPayday, frequency);
	}
}
