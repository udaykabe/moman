package net.deuce.moman.entity.model.rule;


import net.deuce.moman.entity.model.EntityFactory;
import net.deuce.moman.entity.model.envelope.Envelope;

public interface RuleFactory extends EntityFactory<Rule> {

	public Rule buildEntity(String id, String expression, String conversion,
			Condition condition, Envelope envelope, Boolean enabled);
	
	public Rule newEntity(String expression, String conversion,
			Condition condition, Envelope envelope, Boolean enabled);
	
}
