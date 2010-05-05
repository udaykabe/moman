package net.deuce.moman.om;

import net.deuce.moman.util.Utils;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RuleService extends UserBasedService<Rule, RuleDao> {

  @Autowired
  private RuleDao ruleDao;

  protected RuleDao getDao() {
    return ruleDao;
  }

  public void toXml(Rule rule, Element parent) {
    Element el = parent.addElement("rule");
    el.addAttribute("id", rule.getUuid());
    addOptionalBooleanElement(el, "enabled", rule.isEnabled());
    addOptionalElement(el, "amount", Utils.formatDouble(rule.getAmount()));
    addElement(el, "expression", rule.getExpression());
    addElement(el, "conversion", rule.getConversion());
    addElement(el, "condition", rule.getCondition().name());
    el.addElement("envelope").addAttribute("id", rule.getEnvelope().getUuid());
  }

  public Class<Rule> getType() {
    return Rule.class;
  }

  public String getRootElementName() {
    return "transactions";
  }
}