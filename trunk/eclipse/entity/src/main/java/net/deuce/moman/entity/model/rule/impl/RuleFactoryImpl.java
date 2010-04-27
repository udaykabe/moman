package net.deuce.moman.entity.model.rule.impl;


import net.deuce.moman.entity.model.envelope.Envelope;
import net.deuce.moman.entity.model.impl.EntityFactoryImpl;
import net.deuce.moman.entity.model.rule.Condition;
import net.deuce.moman.entity.model.rule.Rule;
import net.deuce.moman.entity.model.rule.RuleFactory;

import org.springframework.stereotype.Component;

@Component("ruleFactory")
public class RuleFactoryImpl extends EntityFactoryImpl<Rule> implements RuleFactory {

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
