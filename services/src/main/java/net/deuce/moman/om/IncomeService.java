package net.deuce.moman.om;

import net.deuce.moman.util.Constants;
import net.deuce.moman.util.Utils;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class IncomeService extends UserBasedService<Income, IncomeDao> {

  @Autowired
  private IncomeDao incomeDao;

  protected IncomeDao getDao() {
    return incomeDao;
  }

  public void toXml(Income income, Element parent) {
    Element el = parent.addElement("income");
    el.addAttribute("id", income.getUuid());
    addOptionalBooleanElement(el, "enabled", income.isEnabled());
    addElement(el, "name", income.getName());
    addElement(el, "amount", Utils.formatDouble(income.getAmount()));
    addElement(el, "frequency", income.getFrequency().name());
    addElement(el, "next-payday", Constants.SHORT_DATE_FORMAT.format(income.getNextPayday()));
  }

  public Class<Income> getType() {
    return Income.class;
  }

  public String getRootElementName() {
    return "income-list";
  }

}