package net.deuce.moman.entity.model.income.impl;

import java.util.Date;

import net.deuce.moman.entity.model.Frequency;
import net.deuce.moman.entity.model.impl.EntityFactoryImpl;
import net.deuce.moman.entity.model.income.Income;
import net.deuce.moman.entity.model.income.IncomeFactory;

import org.springframework.stereotype.Component;

@Component("incomeFactory")
public class IncomeFactoryImpl extends EntityFactoryImpl<Income> implements IncomeFactory {

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
