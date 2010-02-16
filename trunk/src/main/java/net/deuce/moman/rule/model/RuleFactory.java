package net.deuce.moman.rule.model;


import net.deuce.moman.envelope.model.Envelope;
import net.deuce.moman.model.EntityFactory;

import org.springframework.stereotype.Component;

@Component
public class RuleFactory extends EntityFactory<Rule> {

	public Rule buildEntity(String id, String expression, String conversion,
			Condition condition, Envelope envelope, Boolean enabled) {
		Rule entity = super.buildEntity(Rule.class, id);
		entity.setExpression(expression);
		entity.setConversion(conversion);
		entity.setCondition(condition);
		entity.setEnvelope(envelope);
		entity.setEnabled(enabled);
		return entity;
	}
	
	public Rule newEntity(String expression, String conversion,
			Condition condition, Envelope envelope, Boolean enabled) {
		return buildEntity(createUuid(), expression, conversion, condition,
				envelope, enabled);
	}
	
}
