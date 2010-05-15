package net.deuce.moman.om;

import javax.persistence.*;

@Entity
@Table(name = "Rule", uniqueConstraints = {@UniqueConstraint(columnNames = {"uuid"})})
public class Rule extends AbstractEntity<Rule> implements UserBasedEntity {

  private static final long serialVersionUID = 1L;

  private static final RuleExecutor CONTAINS_RULE_EXECUTOR = new ContainsRuleExecutor();
  private static final RuleExecutor MATCHES_RULE_EXECUTOR = new MatchesRuleExecutor();
  private static final RuleExecutor EQUALS_RULE_EXECUTOR = new EqualsRuleExecutor();
  private static final RuleExecutor STARTS_WITH_RULE_EXECUTOR = new StartsWithRuleExecutor();
  private static final RuleExecutor ENDS_WITH_RULE_EXECUTOR = new EndsWithRuleExecutor();

  private String expression;
  private Double amount;
  private String conversion;
  private Condition condition;
  private Envelope envelope;
  private Boolean enabled;
  private User user;

  private transient RuleExecutor ruleExecutor;

  public Rule() {
    super();
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  @Basic
  public Double getAmount() {
    return amount;
  }

  public void setAmount(Double amount) {
    this.amount = amount;
  }

  public boolean amountEquals(Double value) {
    return Math.round(value * 100) == Math.round(amount * 100);
  }

  @Basic
  public String getExpression() {
    return expression;
  }

  public void setExpression(String expression) {
    this.expression = expression;
  }

  @Basic
  public String getConversion() {
    return conversion;
  }

  public void setConversion(String conversion) {
    this.conversion = conversion;
  }

  @Basic
  public boolean isEnabled() {
    return evaluateBoolean(enabled);
  }

  @Transient
  public Boolean getEnabled() {
    return enabled;
  }

  public void setEnabled(Boolean enabled) {
    this.enabled = enabled;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "envelope_id")
  public Envelope getEnvelope() {
    return envelope;
  }

  public void setEnvelope(Envelope envelope) {
    this.envelope = envelope;
  }

  @Enumerated
  @Column(name = "cond")
  public Condition getCondition() {
    return condition;
  }

  @Id
  @GeneratedValue
  public Long getId() {
    return super.getId();
  }

  @Basic
  public String getUuid() {
    return super.getUuid();
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
    }
  }

  public boolean evaluate(String s) {
    return ruleExecutor.evaluate(expression, s);
  }


  public int compareTo(Rule o) {
    return compare(this, o);
  }


  public int compare(Rule o1, Rule o2) {
    return compareObjects(o1.expression, o2.expression);
  }

  private static interface RuleExecutor {
    public boolean evaluate(String expression, String s);
  }

  private static class ContainsRuleExecutor implements RuleExecutor {

    public boolean evaluate(String expression, String s) {
      return s.toLowerCase().contains(expression.toLowerCase());
    }
  }

  private static class MatchesRuleExecutor implements RuleExecutor {

    public boolean evaluate(String expression, String s) {
      if (!expression.startsWith("^")) {
        expression = "^.*" + expression;
      }
      if (!expression.endsWith("$")) {
        expression = expression + ".*$";
      }
      return s.toLowerCase().matches(expression.toLowerCase());
    }
  }

  private static class EqualsRuleExecutor implements RuleExecutor {

    public boolean evaluate(String expression, String s) {
      return s.toLowerCase().equals(expression.toLowerCase());
    }
  }

  private static class StartsWithRuleExecutor implements RuleExecutor {

    public boolean evaluate(String expression, String s) {
      return s.toLowerCase().startsWith(expression.toLowerCase());
    }
  }

  private static class EndsWithRuleExecutor implements RuleExecutor {

    public boolean evaluate(String expression, String s) {
      return s.toLowerCase().endsWith(expression.toLowerCase());
    }
  }
}
