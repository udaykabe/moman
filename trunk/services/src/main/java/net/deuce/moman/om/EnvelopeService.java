package net.deuce.moman.om;

import net.deuce.moman.util.CalendarUtil;
import net.deuce.moman.util.Constants;
import net.deuce.moman.util.DataDateRange;
import net.deuce.moman.util.Utils;
import net.sf.ofx4j.domain.data.common.TransactionType;
import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class EnvelopeService extends UserBasedService<Envelope, EnvelopeDao> {

  @Autowired
  private EnvelopeDao envelopeDao;

  @Autowired
  private TransactionService transactionService;

  @Autowired
  private AccountService accountService;

  @Autowired
  private RuleService ruleService;

  @Autowired
  private UserService userService;

  @Autowired
  private SplitService splitService;

  protected EnvelopeDao getDao() {
    return envelopeDao;
  }

  @Transactional(readOnly = true)
  public Envelope getSelectedEnvelope(User user) {
    return envelopeDao.findSelected(user);
  }

  @Transactional
  public void setSelectedEnvelope(Envelope env) {
    Envelope selectedEnvelope = envelopeDao.findSelected(env.getUser());
    if (selectedEnvelope != null) {
      selectedEnvelope.setSelected(false);
      saveOrUpdate(selectedEnvelope);
    }
    env.setSelected(true);
    transactionService.clearCustomTransactions(env.getUser());
    saveOrUpdate(env);
  }

  @Transactional(readOnly = true)
  public Envelope getMonthlyEnvelope(User user) {
    return envelopeDao.getMonthlyEnvelope(user);
  }

  @Transactional(readOnly = true)
  public Envelope getRootEnvelope(User user) {
    return envelopeDao.getRootEnvelope(user);
  }

  @Transactional(readOnly = true)
  public Envelope getSavingsGoalsEnvelope(User user) {
    return envelopeDao.getSavingsGoalsEnvelope(user);
  }

  @Transactional(readOnly = true)
  public Envelope getUnassignedEnvelope(User user) {
    return envelopeDao.getUnassignedEnvelope(user);
  }

  @Transactional(readOnly = true)
  public Envelope getAvailableEnvelope(User user) {
    return envelopeDao.getAvailableEnvelope(user);
  }

  public double expensesDuringPeriod(Envelope env, Account account, Frequency frequency) {
    Calendar cal = new GregorianCalendar();
    CalendarUtil.convertCalendarToMidnight(cal);

    frequency.advanceCalendar(cal, true);

    return calculateTransactionsSince(env, account, cal);
  }

  public double expensesDuringLastNDays(Envelope env, Account account, int days) {
    Calendar cal = new GregorianCalendar();
    CalendarUtil.convertCalendarToMidnight(cal);

    cal.add(Calendar.DAY_OF_YEAR, -days);

    return calculateTransactionsSince(env, account, cal);
  }

  public double calculateTransactionsSince(Envelope env, Account account, Calendar cal) {
    List<InternalTransaction> transactions = null;
    if (account != null) {
      transactions = transactionService.getAccountTransactions(env, account);
    } else {
      transactions = transactionService.getAllTransactions(env);
    }

    double sum = 0.0;
    for (InternalTransaction it : transactions) {
      Calendar tcal = new GregorianCalendar();
      tcal.setTime(it.getDate());
      CalendarUtil.convertCalendarToMidnight(tcal);

      if (tcal.after(cal)) {
        sum += it.getAmount();
      }
    }
    return sum;
  }

  public String getChartLegendLabel(Envelope env) {
    return env.getName() + " " + Constants.CURRENCY_VALIDATOR.format(getBalance(env));
  }

  public Double getBalance(Envelope env) {

    Double value = env.getBalance();

    if (env.isBalanceDirty()) {

      value = 0.0;
      for (InternalTransaction t : transactionService.getSelectedAccountTransactions(env)) {
        double splitAmount = t.getSplitAmount(env);
        value += splitAmount;
      }
      for (Envelope e : env.getChildren()) {
        value += getBalance(e);
      }
      envelopeDao.saveOrUpdate(env);
    }

    return Math.round(value * 100) / 100.0;
  }

  @Transactional
  public void setSelected(Envelope env, Boolean selected) {
    if (env.propertyChanged(env.isSelected(), selected)) {
      env.setSelected(selected);
      Envelope oldEnvelope = getSelectedEnvelope(env.getUser());
      if (oldEnvelope != null) {
        oldEnvelope.setSelected(false);
        saveOrUpdate(oldEnvelope);
      }
      saveOrUpdate(env);
    }
  }

  @Transactional
  public void addChild(Envelope env, Envelope child) {
    env.addChild(child);
    saveOrUpdate(env);
  }

  @Transactional
  public void removeChild(Envelope env, Envelope child) {
    env.removeChild(child);
    saveOrUpdate(env);
  }

  @Transactional
  public void resetBalance(Envelope envelope) {
    if (envelope != null) {
      envelope.clearBalance();
      saveOrUpdate(envelope);
      resetBalance(envelope.getParent());
    }
  }

  /*
  private void addEnvelopeToList(Envelope env, List<Envelope> list) {
    list.add(env);
    for (Envelope child : env.getChildren()) {
      addEnvelopeToList(child, list);
    }
  }
  */

  public List<Envelope> getAllEnvelopes(User user) {
    /*
    List<Envelope> envelopes = new LinkedList<Envelope>();
    addEnvelopeToList(getRootEnvelope(user), envelopes);
    return envelopes;
    */
    return getEntities(user);
  }

  @Transactional
  public void transfer(Account sourceAccount, Account targetAccount,
                       Envelope source, Envelope target, double amount) {

    Date date = new Date();

    Split splitItem;

    // source transaction
    InternalTransaction sTransaction = new InternalTransaction();
    sTransaction.setAmount(-amount);
    sTransaction.setType(TransactionType.XFER);
    sTransaction.setDate(date);
    sTransaction.setDescription("Transfer to " + target.getName());
    sTransaction.setStatus(TransactionStatus.reconciled);
    sTransaction.setAccount(sourceAccount);
    transactionService.doAddEntity(sTransaction);

    splitItem = new Split();
    splitItem.setEnvelope(source);
    splitItem.setAmount(-amount);
    splitItem.setTransaction(sTransaction);
    splitService.saveOrUpdate(splitItem);

    sTransaction.addSplit(splitItem);


    // target transaction
    InternalTransaction tTransaction = new InternalTransaction();
    tTransaction.setAmount(amount);
    tTransaction.setType(TransactionType.XFER);
    tTransaction.setDate(date);
    tTransaction.setDescription("Transfer from " + source.getName());
    tTransaction.setStatus(TransactionStatus.reconciled);
    tTransaction.setAccount(targetAccount);
    transactionService.doAddEntity(tTransaction);

    splitItem = new Split();
    splitItem.setEnvelope(target);
    splitItem.setAmount(amount);
    splitItem.setTransaction(tTransaction);
    splitService.saveOrUpdate(splitItem);

    tTransaction.addSplit(splitItem);

    sTransaction.setTransferTransaction(tTransaction);
    tTransaction.setTransferTransaction(sTransaction);

    transactionService.saveOrUpdate(sTransaction);
    transactionService.saveOrUpdate(tTransaction);
  }

  @Transactional
  public void importDefaultEnvelopes(User user) {
    Envelope defaultRootEnvelope = getRootEnvelope(userService.getDefaultUser());
    importDefaultEnvelope(user, defaultRootEnvelope);
  }

  private void importDefaultEnvelope(User user, Envelope defaultEnvelope) {
    Envelope env = new Envelope();
    // TODO
  }

  @Transactional
  public void addEnvelope(Envelope envelope, Envelope parent) {

    if (parent != null) {
      envelope.setParent(parent);
      parent.addChild(envelope);
      saveOrUpdate(parent);
    }

    doAddEntity(envelope);

  }

  @Transactional
  public void removeEnvelope(Envelope envelope) {
    _removeEnvelope(envelope);
  }

  protected void _removeEnvelope(Envelope envelope) {

    if (!envelope.isEditable()) return;

    Envelope parent = envelope.getParent();
    if (parent != null) {
      parent.removeChild(envelope);
      saveOrUpdate(parent);
    }
    List<Envelope> children = new LinkedList<Envelope>(envelope.getChildren());

    for (Envelope child : children) {
      _removeEnvelope(child);
    }

    for (Rule rule : ruleService.getEntities(envelope.getUser())) {
      if (rule.getEnvelope().equals(envelope)) {
        rule.setEnvelope(getUnassignedEnvelope(envelope.getUser()));
        ruleService.saveOrUpdate(rule);
      }
    }

    super.removeEntity(envelope);
  }

  public List<Envelope> getSavingsGoals(User user) {
    List<Envelope> savingsGoals = new LinkedList<Envelope>(getAllEnvelopes(user));
    ListIterator<Envelope> itr = savingsGoals.listIterator();
    while (itr.hasNext()) {
      if (!itr.next().isSavingsGoal()) {
        itr.remove();
      }
    }
    return savingsGoals;
  }

  public List<Envelope> getOrderedSavingsGoals(User user, boolean reverse) {
    List<Envelope> list = getSavingsGoals(user);
    if (list.size() > 0) {
      Envelope entity = list.get(0);
      Comparator<Envelope> comparator = reverse ? entity.getReverseComparator() :
          entity.getForwardComparator();
      Collections.sort(list, comparator);
    }
    return list;
  }

  public List<Envelope> getOrderedBudgetedEnvelopes(User user, boolean reverse) {
    List<Envelope> list = new LinkedList<Envelope>();
    for (Envelope env : getAllEnvelopes(user)) {
      if (env.isEnabled() && env.getBudget() != null && env.getBudget() > 0.0) {
        list.add(env);
      }
    }

    if (list.size() > 0) {
      Envelope entity = list.get(0);
      Comparator<Envelope> comparator = reverse ? entity.getReverseComparator() :
          entity.getForwardComparator();
      Collections.sort(list, comparator);
    }
    return list;
  }

  public List<Envelope> getBills(User user) {
    List<Envelope> bills = new LinkedList<Envelope>(getAllEnvelopes(user));
    ListIterator<Envelope> itr = bills.listIterator();
    while (itr.hasNext()) {
      if (!itr.next().isSavingsGoal()) {
        itr.remove();
      }
    }
    return bills;
  }

  public List<Envelope> getOrderedBills(User user, boolean reverse) {
    List<Envelope> list = getBills(user);
    if (list.size() > 0) {
      Envelope entity = list.get(0);
      Comparator<Envelope> comparator = reverse ? entity.getReverseComparator() :
          entity.getForwardComparator();
      Collections.sort(list, comparator);
    }
    return list;
  }


  @Transactional
  public double distributeToNegativeEnvelopes(Account account, Envelope env, double balance) {
    if (!env.isAvailable()) {
      if (balance > 0) {
        if (!env.hasChildren()) {
          if (env.getBalance() < 0) {
            double transferAmount = 0;
            if (balance > -env.getBalance()) {
              transferAmount = -env.getBalance();
            } else {
              transferAmount = balance;
            }
            transfer(account, account, getAvailableEnvelope(account.getUser()), env, transferAmount);
            return balance - transferAmount;
          }
        } else {
          for (Envelope child : env.getChildren()) {
            balance = distributeToNegativeEnvelopes(account, child, balance);
          }
        }
      }
    }
    return balance;
  }

  public void buildEnvelope(Envelope env, Element root, String name) {
    Element el;

    el = root.addElement(name);
    el.addAttribute("id", env.getUuid());
    addElement(el, "editable", env.isEditable());
    addOptionalBooleanElement(el, "selected", env.isSelected());
    addOptionalBooleanElement(el, "root", env.isRoot());
    addOptionalBooleanElement(el, "unassigned", env.isUnassigned());
    addOptionalBooleanElement(el, "monthly", env.isMonthly());
    addOptionalBooleanElement(el, "savings-goals", env.isSavingsGoals());
    addOptionalBooleanElement(el, "available", env.isAvailable());
    addOptionalBooleanElement(el, "expanded", env.isEnabled());
    addOptionalBooleanElement(el, "enabled", env.isEnabled());
    addElement(el, "name", env.getName());
    addElement(el, "index", env.getIndex());
    addElement(el, "frequency", env.getFrequency().name());
    addElement(el, "budget", Utils.formatDouble(env.getBudget()));

    addElement(el, "dueDay", env.getDueDay().toString());
    if (env.getSavingsGoalDate() != null) {
      addElement(el, "savings-goal-date", Constants.SHORT_DATE_FORMAT.format(env.getSavingsGoalDate()));
    }
    if (env.getSavingsGoalOverrideAmount() != null) {
      addElement(el, "savings-goal-override", Utils.formatDouble(env.getSavingsGoalOverrideAmount()));
    }

    if (env.getParent() != null) {
      el.addElement("parent").addAttribute("id", env.getParent().getUuid());
    }

  }

  public void toXml(User user, Document doc) {

    Element root = doc.getRootElement().addElement(getRootElementName());

    for (Envelope env : getEntities(user)) {
      buildEnvelope(env, root, "envelope");
    }
  }


  public void toXml(Envelope entity, Element parent) {
    buildEnvelope(entity, parent, "envelope");
  }

  public Class<Envelope> getType() {
    return Envelope.class;
  }

  public String getRootElementName() {
    return "envelopes";
  }

}