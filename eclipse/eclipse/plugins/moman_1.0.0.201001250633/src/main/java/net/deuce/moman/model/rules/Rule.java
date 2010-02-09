package net.deuce.moman.model.rules;

import net.deuce.moman.model.EntityMonitor;
import net.deuce.moman.model.MomanEntity;
import net.deuce.moman.model.envelope.Envelope;

public class Rule extends MomanEntity implements Comparable<Rule> {

	private static final long serialVersionUID = 1L;
	
	private static final RuleExecutor CONTAINS_RULE_EXECUTOR = new ContainsRuleExecutor();
	private static final RuleExecutor MATCHES_RULE_EXECUTOR = new MatchesRuleExecutor();
	private static final RuleExecutor EQUALS_RULE_EXECUTOR = new EqualsRuleExecutor();
	private static final RuleExecutor STARTS_WITH_RULE_EXECUTOR = new StartsWithRuleExecutor();
	private static final RuleExecutor ENDS_WITH_RULE_EXECUTOR = new EndsWithRuleExecutor();

	private String expression;
	private String conversion;
	private Condition condition;
	private Envelope envelope;
	private boolean enabled;
	
	private transient RuleExecutor ruleExecutor;
	private transient EntityMonitor<Rule> monitor = new EntityMonitor<Rule>();
	
	public void setMonitor(EntityMonitor<Rule> monitor) {
		this.monitor = monitor;
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		if (propertyChanged(this.expression, expression)) {
			this.expression = expression;
			monitor.fireEntityChanged(this);
		}
	}
	
	public String getConversion() {
		return conversion;
	}

	public void setConversion(String conversion) {
		if (propertyChanged(this.conversion, conversion)) {
			this.conversion = conversion;
			monitor.fireEntityChanged(this);
		}
	}
	
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		if (propertyChanged(this.enabled, enabled)) {
			this.enabled = enabled;
			monitor.fireEntityChanged(this);
		}
	}
	
	public Condition getCondition() {
		return condition;
	}

	public void setCondition(Condition condition) {
		if (propertyChanged(this.condition, condition)) {
			this.condition = condition;
			
			if (condition == Condition.Contains) {
				ruleExecutor = CONTAINS_RULE_EXECUTOR;
			} else if (condition == Condition.Matches) {
				ruleExecutor = MATCHES_RULE_EXECUTOR;
			} else if (condition == Condition.Equals) {
				ruleExecutor = EQUALS_RULE_EXECUTOR;
			} else if (condition == Condition.StartsWith) {
				ruleExecutor = STARTS_WITH_RULE_EXECUTOR;
			} else if (condition == Condition.EndsWith) {
				ruleExecutor = ENDS_WITH_RULE_EXECUTOR;
			}
			monitor.fireEntityChanged(this);
		}
	}
	
	public Envelope getEnvelope() {
		return envelope;
	}

	public void setEnvelope(Envelope envelope) {
		if (propertyChanged(this.envelope, envelope)) {
			this.envelope = envelope;
			monitor.fireEntityChanged(this);
		}
	}
	
	public boolean evaluate(String s) {
		return ruleExecutor.evaluate(expression, s);
	}
	
	@Override
	public int compareTo(Rule o) {
		return expression.compareTo(o.getExpression());
	}

	private static interface RuleExecutor {
		public boolean evaluate(String expression, String s);
	}
	
	private static class ContainsRuleExecutor implements RuleExecutor {
		@Override
		public boolean evaluate(String expression, String s) {
			return s.toLowerCase().contains(expression.toLowerCase());
		}
	}
	
	private static class MatchesRuleExecutor implements RuleExecutor {
		@Override
		public boolean evaluate(String expression, String s) {
			return s.toLowerCase().matches(expression.toLowerCase());
		}
	}
	
	private static class EqualsRuleExecutor implements RuleExecutor {
		@Override
		public boolean evaluate(String expression, String s) {
			return s.toLowerCase().equals(expression.toLowerCase());
		}
	}
	
	private static class StartsWithRuleExecutor implements RuleExecutor {
		@Override
		public boolean evaluate(String expression, String s) {
			return s.toLowerCase().startsWith(expression.toLowerCase());
		}
	}
	
	private static class EndsWithRuleExecutor implements RuleExecutor {
		@Override
		public boolean evaluate(String expression, String s) {
			return s.toLowerCase().endsWith(expression.toLowerCase());
		}
	}
}
