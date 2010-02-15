package net.deuce.moman.rule.model;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import net.deuce.moman.envelope.model.Envelope;
import net.deuce.moman.model.AbstractEntity;
import net.deuce.moman.model.EntityProperty;
import net.deuce.moman.transaction.model.Split;

public class Rule extends AbstractEntity<Rule> {

	private static final long serialVersionUID = 1L;
	
	private static final RuleExecutor CONTAINS_RULE_EXECUTOR = new ContainsRuleExecutor();
	private static final RuleExecutor MATCHES_RULE_EXECUTOR = new MatchesRuleExecutor();
	private static final RuleExecutor EQUALS_RULE_EXECUTOR = new EqualsRuleExecutor();
	private static final RuleExecutor STARTS_WITH_RULE_EXECUTOR = new StartsWithRuleExecutor();
	private static final RuleExecutor ENDS_WITH_RULE_EXECUTOR = new EndsWithRuleExecutor();

    public enum Properties implements EntityProperty {
        expression(String.class), conversion(String.class), amount(Double.class),
        condition(Condition.class), split(List.class), enabled(Boolean.class);
        
		private Class<?> type;
		
		public Class<?> type() { return type; }
		
		private Properties(Class<?> type) { this.type = type; }
    }

	private String expression;
	private Double amount;
	private String conversion;
	private Condition condition;
	private List<Split> split = new LinkedList<Split>();
	private Boolean enabled;
	
	private transient RuleExecutor ruleExecutor;
	
	public Rule() {
		super();
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		if (propertyChanged(this.amount, amount)) {
			this.amount = amount;
			getMonitor().fireEntityChanged(this);
		}
	}
	
	public boolean amountEquals(Double value) {
		return Math.round(value*100) == Math.round(amount*100);
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		if (propertyChanged(this.expression, expression)) {
			this.expression = expression;
			getMonitor().fireEntityChanged(this);
		}
	}
	
	public String getConversion() {
		return conversion;
	}

	public void setConversion(String conversion) {
		if (propertyChanged(this.conversion, conversion)) {
			this.conversion = conversion;
			getMonitor().fireEntityChanged(this);
		}
	}
	
	public boolean isEnabled() {
		return evaluateBoolean(enabled);
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		if (propertyChanged(this.enabled, enabled)) {
			this.enabled = enabled;
			getMonitor().fireEntityChanged(this);
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
			getMonitor().fireEntityChanged(this);
		}
	}
	
	public void clearSplit() {
		split.clear();
		getMonitor().fireEntityChanged(this, Properties.split);
	}
	
	public void addSplit(Envelope envelope, Double amount) {
		addSplit(new Split(envelope, amount));
	}
	
	public void addSplit(Split item) {
		if (!split.contains(item)) {
			split.add(item);
			getMonitor().fireEntityChanged(this, Properties.split);
		}
	}

	public void removeSplit(Envelope envelope) {
		ListIterator<Split> itr = split.listIterator();
		while (itr.hasNext()) {
			if (itr.next().getEnvelope() == envelope) {
				itr.remove();
				getMonitor().fireEntityChanged(this, Properties.split);
				break;
			}
		}
	}
	
	public List<Split> getSplit() {
		return split;
	}
	
	public boolean evaluate(String s) {
		return ruleExecutor.evaluate(expression, s);
	}
	
	@Override
	public int compareTo(Rule o) {
		return compare(this, o);
	}
	
	@Override
	public int compare(Rule o1, Rule o2) {
		return o1.expression.compareTo(o2.expression);
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
