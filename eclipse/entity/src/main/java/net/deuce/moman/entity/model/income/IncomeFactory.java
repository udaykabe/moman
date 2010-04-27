package net.deuce.moman.entity.model.income;

import java.util.Date;

import net.deuce.moman.entity.model.EntityFactory;
import net.deuce.moman.entity.model.Frequency;

public interface IncomeFactory extends EntityFactory<Income> {

	public Income buildEntity(String id,  String name, Double amount,
			Boolean enabled, Date nextPayday, Frequency frequency);
	
	public Income newEntity(String name, Double amount,
			Boolean enabled, Date nextPayday, Frequency frequency);
}
