package net.deuce.moman.om;

import net.deuce.moman.job.AbstractCommand;
import net.deuce.moman.job.Command;
import net.deuce.moman.util.CalendarUtil;
import net.deuce.moman.util.Constants;
import net.deuce.moman.util.Utils;
import net.sf.ofx4j.domain.data.common.TransactionType;
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

  public Command setSelectedEnvelopeCommand(final Envelope env) {
    return new AbstractCommand(Envelope.class.getSimpleName() + " setSelectedEnvelope(" + env.getUuid() + ")", true) {

      public void doExecute() throws Exception {

        final Envelope oldSelectedEnvelope = envelopeDao.findSelected(env.getUser());

        setSelectedEnvelope(env);

        setUndo(new AbstractCommand("Undo " + getName(), true) {
          public void doExecute() throws Exception {
            setSelectedEnvelope(oldSelectedEnvelope);
          }
        });
      }
    };
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

  public Command getMonthlyEnvelopeCommand(final User user) {
    return new AbstractCommand(Envelope.class.getSimpleName() + " getMonthlyEnvelope()", true) {
      public void doExecute() throws Exception {
        List<Envelope> list = new LinkedList<Envelope>();
        list.add(getMonthlyEnvelope(user));
        setResult(Arrays.asList(new Element[]{toXml(list)}));
      }
    };
  }

  @Transactional(readOnly = true)
  public Envelope getMonthlyEnvelope(User user) {
    return envelopeDao.getMonthlyEnvelope(user);
  }

  public Command getRootEnvelopeCommand(final User user) {
    return new AbstractCommand(Envelope.class.getSimpleName() + " getRootEnvelope()", true) {
      public void doExecute() throws Exception {
        List<Envelope> list = new LinkedList<Envelope>();
        list.add(getRootEnvelope(user));
        setResult(Arrays.asList(new Element[]{toXml(list)}));
      }
    };
  }

  @Transactional(readOnly = true)
  public Envelope getRootEnvelope(User user) {
    return envelopeDao.getRootEnvelope(user);
  }

  public Command getSavingsGoalsEnvelopeCommand(final User user) {
    return new AbstractCommand(Envelope.class.getSimpleName() + " getSavingsGoalsEnvelope()", true) {
      public void doExecute() throws Exception {
        List<Envelope> list = new LinkedList<Envelope>();
        list.add(getSavingsGoalsEnvelope(user));
        setResult(Arrays.asList(new Element[]{toXml(list)}));
      }
    };
  }

  @Transactional(readOnly = true)
  public Envelope getSavingsGoalsEnvelope(User user) {
    return envelopeDao.getSavingsGoalsEnvelope(user);
  }

  public Command getUnassignedEnvelopeCommand(final User user) {
    return new AbstractCommand(Envelope.class.getSimpleName() + " getUnassignedEnvelope()", true) {
      public void doExecute() throws Exception {
        List<Envelope> list = new LinkedList<Envelope>();
        list.add(getUnassignedEnvelope(user));
        setResult(Arrays.asList(new Element[]{toXml(list)}));
      }
    };
  }

  @Transactional(readOnly = true)
  public Envelope getUnassignedEnvelope(User user) {
    return envelopeDao.getUnassignedEnvelope(user);
  }

  public Command getAvailableEnvelopeCommand(final User user) {
    return new AbstractCommand(Envelope.class.getSimpleName() + " getAvailableEnvelope()", true) {
      public void doExecute() throws Exception {
        List<Envelope> list = new LinkedList<Envelope>();
        list.add(getAvailableEnvelope(user));
        setResult(Arrays.asList(new Element[]{toXml(list)}));
      }
    };
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

  public Command saveAsTemplateCommand() {

    final User user = userService.getDefaultUser();
    return new AbstractCommand(Envelope.class.getSimpleName() + " saveAsTemplate(" + user.getUuid() + ")", true) {

      public void doExecute() throws Exception {

        final Envelope oldRoot = getRootEnvelope(userService.getTemplateUser());
        saveAsTemplate(user);

        setUndo(new AbstractCommand("Undo " + getName(), true) {
          public void doExecute() throws Exception {
            User templateUser = userService.getTemplateUser();
            envelopeDao.deleteByUser(templateUser);
            if (oldRoot != null) {
              saveAs(null, oldRoot, userService.getTemplateUser());
            }
          }
        });
      }
    };
  }

  @Transactional
  public void saveAsTemplate(User user) {
    envelopeDao.deleteByUser(userService.getTemplateUser());
    saveAs(null, getRootEnvelope(user), userService.getTemplateUser());
  }

  private void saveAs(Envelope parent, Envelope env, User user) {
    Envelope defaultEnvelope = new Envelope();
    defaultEnvelope.setName(env.getName());
    defaultEnvelope.setFrequency(env.getFrequency());
    defaultEnvelope.setBudget(0.0);
    defaultEnvelope.setParent(parent);
    defaultEnvelope.setEditable(env.isEditable());
    defaultEnvelope.setRoot(env.isRoot());
    defaultEnvelope.setMonthly(env.isMonthly());
    defaultEnvelope.setSavingsGoals(env.isSavingsGoals());
    defaultEnvelope.setUnassigned(env.isUnassigned());
    defaultEnvelope.setAvailable(env.isAvailable());
    defaultEnvelope.setDueDay(env.getDueDay());
    defaultEnvelope.setIndex(env.getIndex());
    defaultEnvelope.setSavingsGoalDate(env.getSavingsGoalDate());
    defaultEnvelope.setSavingsGoalOverrideAmount(env.getSavingsGoalOverrideAmount());
    defaultEnvelope.setUser(user);
    defaultEnvelope.setUuid(createUuid());
    saveOrUpdate(defaultEnvelope);
    if (parent != null) {
      addChild(parent, defaultEnvelope);
    }
    for (Envelope child : env.getChildren()) {
      saveAs(defaultEnvelope, child, user);
    }
  }

  public Command addChildCommand(final Envelope env, final Envelope child) {
    return new AbstractCommand(Envelope.class.getSimpleName() + " addChild(" + env.getUuid() + ", " + child.getUuid() + ")", true) {

      public void doExecute() throws Exception {

        addChild(env, child);

        setUndo(new AbstractCommand("Undo " + getName(), true) {
          public void doExecute() throws Exception {
            removeChild(env, child);
          }
        });
      }
    };
  }

  @Transactional
  public void addChild(Envelope env, Envelope child) {
    env.addChild(child);
    saveOrUpdate(env);
  }

  public Command removeChildCommand(final Envelope env, final Envelope child) {
    return new AbstractCommand(Envelope.class.getSimpleName() + " removeChild(" + env.getUuid() + ", " + child.getUuid() + ")", true) {

      public void doExecute() throws Exception {

        boolean removed = removeChild(env, child);

        if (removed) {
          setUndo(new AbstractCommand("Undo " + getName(), true) {
            public void doExecute() throws Exception {
              addChild(env, child);
            }
          });
        }
      }
    };
  }

  @Transactional
  public boolean removeChild(Envelope env, Envelope child) {
    boolean result = env.removeChild(child);
    saveOrUpdate(env);
    return result;
  }

  public Command resetBalanceCommand(final Envelope env) {
    return new AbstractCommand(Envelope.class.getSimpleName() + " resetBalance(" + env.getUuid() + ")", true) {

      public void doExecute() throws Exception {

        final List<Double> balances = new LinkedList<Double>();

        Envelope parent = env;
        while (parent != null) {
          balances.add(env.getBalance());
          parent = parent.getParent();
        }

        resetBalance(env);

        setUndo(new AbstractCommand("Undo " + getName(), true) {
          public void doExecute() throws Exception {
            restoreBalances(env, balances);
          }
        });
      }
    };
  }

  @Transactional
  public void restoreBalances(Envelope env, List<Double> balances) {

    int i=0;
    Envelope parent = env;
    while (parent != null) {
      parent.setBalance(balances.get(i++));
      saveOrUpdate(parent);
      parent = parent.getParent();
    }
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

  public Command transferCommand(final Account account,
                       final Envelope source, final Envelope target, final Double amount) {
    return new AbstractCommand(Envelope.class.getSimpleName() + " transfer(" + source.getUuid() + ", " + target.getUuid() + ")", true) {

      public void doExecute() throws Exception {

        final TransferResult result = transfer(account, source, target, amount);

        setUndo(new AbstractCommand("Undo " + getName(), true) {
          public void doExecute() throws Exception {
            undoTransfer(result);
          }
        });
      }
    };
  }

  @Override
  public boolean delete(Envelope entity) {
    return super.delete(entity);
  }

  @Override
  public boolean deleteByUuid(String uuid) {
    return super.deleteByUuid(uuid);
  }

  @Transactional
  public void undoTransfer(TransferResult result) {
    transactionService.delete(result.getSourceTransaction());
    transactionService.delete(result.getTargetTransaction());
    splitService.delete(result.getSourceSplit());
    splitService.delete(result.getTargetSplit());
    result.getSourceEnvelope().clearBalance();
    result.getTargetEnvelope().clearBalance();
  }

  @Transactional
  public TransferResult transfer(Account account, Envelope source, Envelope target, double amount) {

    Date date = new Date();

    // source transaction
    InternalTransaction sTransaction = new InternalTransaction();
    sTransaction.setUuid(createUuid());
    sTransaction.setAmount(-amount);
    sTransaction.setType(TransactionType.XFER);
    sTransaction.setDate(date);
    sTransaction.setDescription("Transfer to " + target.getName());
    sTransaction.setStatus(TransactionStatus.reconciled);
    sTransaction.setAccount(account);
    transactionService.doAddEntity(sTransaction);

    Split sourceSplit = new Split();
    sourceSplit.setEnvelope(source);
    sourceSplit.setAmount(-amount);
    sourceSplit.setTransaction(sTransaction);
    splitService.saveOrUpdate(sourceSplit);

    sTransaction.addSplit(sourceSplit);


    // target transaction
    InternalTransaction tTransaction = new InternalTransaction();
    tTransaction.setUuid(createUuid());
    tTransaction.setAmount(amount);
    tTransaction.setType(TransactionType.XFER);
    tTransaction.setDate(date);
    tTransaction.setDescription("Transfer from " + source.getName());
    tTransaction.setStatus(TransactionStatus.reconciled);
    tTransaction.setAccount(account);
    transactionService.doAddEntity(tTransaction);

    Split targetSplit = new Split();
    targetSplit.setEnvelope(target);
    targetSplit.setAmount(amount);
    targetSplit.setTransaction(tTransaction);
    splitService.saveOrUpdate(targetSplit);

    tTransaction.addSplit(targetSplit);

    sTransaction.setTransferTransaction(tTransaction);
    tTransaction.setTransferTransaction(sTransaction);

    transactionService.saveOrUpdate(sTransaction);
    transactionService.saveOrUpdate(tTransaction);

    source.clearBalance();
    target.clearBalance();

    return new TransferResult(source, target, sTransaction, tTransaction, sourceSplit, targetSplit);
  }

  public Command importTemplateEnvelopesCommand() {
    final User user = userService.getDefaultUser();
    return new AbstractCommand(Envelope.class.getSimpleName() + " importTemplateEnvelopes(" + user.getUuid() + ")", true) {
      public void doExecute() throws Exception {
        final Envelope oldRoot = getRootEnvelope(user);
        importTemplateEnvelopes(user);
        setUndo(new AbstractCommand("Undo " + getName(), true) {
          public void doExecute() throws Exception {
            envelopeDao.deleteByUser(user);
            if (oldRoot != null) {
              saveAs(null, oldRoot, user);
            }
          }
        });
      }
    };
  }

  @Transactional
  public void importTemplateEnvelopes(User user) {
    envelopeDao.deleteByUser(user);
    Envelope defaultRootEnvelope = getRootEnvelope(userService.getTemplateUser());
    saveAs(null, defaultRootEnvelope, user);
  }

  public Command addEnvelopeCommand(final Envelope envelope, final Envelope parent) {
    return new AbstractCommand(Envelope.class.getSimpleName() + " addEnvelope(" + envelope.getUuid() + ", " + parent.getUuid() + ")", true) {

      public void doExecute() throws Exception {

        addEnvelope(envelope, parent);

        setUndo(new AbstractCommand("Undo " + getName(), true) {
          public void doExecute() throws Exception {
            undoAddEnvelope(envelope, parent);
          }
        });
      }
    };
  }

  @Transactional
  public void undoAddEnvelope(Envelope envelope, Envelope parent) {
    removeChild(parent, envelope);
    delete(envelope);
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

  public Command removeEnvelopeCommand(final Envelope envelope) {
    return new AbstractCommand(Envelope.class.getSimpleName() + " removeEnvelope(" + envelope.getUuid() + ")", true) {

      public void doExecute() throws Exception {

        final List<Rule> affectedRules = new LinkedList<Rule>();
        final Envelope parent = envelope.getParent();

        for (Rule rule : ruleService.getEntities(envelope.getUser())) {
          if (rule.getEnvelope().equals(envelope)) {
            affectedRules.add(rule);
          }
        }

        removeEnvelope(envelope);

        setUndo(new AbstractCommand("Undo " + getName(), true) {
          public void doExecute() throws Exception {
            undoRemoveEnvelope(envelope, parent, affectedRules);
          }
        });
      }
    };
  }

  @Transactional
  public void undoRemoveEnvelope(Envelope envelope, Envelope parent, List<Rule> affectedRules) {
    addEnvelope(envelope, parent);
    for (Rule rule : affectedRules) {
      rule.setEnvelope(envelope);
      ruleService.saveOrUpdate(rule);
    }
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

  public Command distributeToNegativeEnvelopesCommand(final Account account, final Envelope env, final double balance) {
    return new AbstractCommand(Envelope.class.getSimpleName() + " distributeToNegativeEnvelopes(" + account.getUuid() + ", " + env.getUuid() + ")", true) {

      public void doExecute() throws Exception {

        final List<TransferResult> transferResults = new LinkedList<TransferResult>();
        distributeToNegativeEnvelopes(account, env, balance, transferResults);

        setUndo(new AbstractCommand("Undo " + getName(), true) {
          public void doExecute() throws Exception {
            undoDistributeToNegativeEnvelopes(transferResults);
          }
        });
      }
    };
  }

  @Transactional
  public void undoDistributeToNegativeEnvelopes(List<TransferResult> transferResults) {
    for (TransferResult result : transferResults) {
      undoTransfer(result);
    }
  }

  public double distributeToNegativeEnvelopes(Account account, Envelope env, double balance) {
    return distributeToNegativeEnvelopes(account, env, balance, null);
  }

  @Transactional
  public double distributeToNegativeEnvelopes(Account account, Envelope env, double balance, List<TransferResult> transferResults) {
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
            TransferResult result = transfer(account, getAvailableEnvelope(account.getUser()), env, transferAmount);
            if (transferResults != null) {
              transferResults.add(result);
            }
            return balance - transferAmount;
          }
        } else {
          for (Envelope child : env.getChildren()) {
            balance = distributeToNegativeEnvelopes(account, child, balance, transferResults);
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
    addOptionalBooleanElement(el, "savingsGoals", env.isSavingsGoals());
    addOptionalBooleanElement(el, "available", env.isAvailable());
    addOptionalBooleanElement(el, "expanded", env.isEnabled());
    addOptionalBooleanElement(el, "enabled", env.isEnabled());
    addElement(el, "name", env.getName());
    addElement(el, "index", env.getIndex());
    addElement(el, "frequency", env.getFrequency().name());
    addElement(el, "budget", Utils.formatDouble(env.getBudget()));
    addElement(el, "balance", Utils.formatDouble(getBalance(env)));

    addElement(el, "dueDay", env.getDueDay().toString());
    if (env.getSavingsGoalDate() != null) {
      addElement(el, "savingsGoalDate", Constants.SHORT_DATE_FORMAT.format(env.getSavingsGoalDate()));
    }
    if (env.getSavingsGoalOverrideAmount() != null) {
      addElement(el, "savingsGoalOverrideAmount", Utils.formatDouble(env.getSavingsGoalOverrideAmount()));
    }

    if (env.getParent() != null) {
      el.addElement("parent").addAttribute("id", env.getParent().getUuid());
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

  private static class TransferResult {
    private InternalTransaction sourceTransaction;
    private InternalTransaction targetTransaction;
    private Split sourceSplit;
    private Split targetSplit;
    private Envelope sourceEnvelope;
    private Envelope targetEnvelope;

    private TransferResult(Envelope sourceEnvelope, Envelope targetEnvelope,
                           InternalTransaction sourceTransaction, InternalTransaction targetTransaction,
                           Split sourceSplit, Split targetSplit) {
      this.sourceEnvelope = sourceEnvelope;
      this.targetEnvelope = targetEnvelope;
      this.sourceTransaction = sourceTransaction;
      this.targetTransaction = targetTransaction;
      this.sourceSplit = sourceSplit;
      this.targetSplit = targetSplit;
    }

    public Envelope getSourceEnvelope() {
      return sourceEnvelope;
    }

    public Envelope getTargetEnvelope() {
      return targetEnvelope;
    }

    public InternalTransaction getSourceTransaction() {
      return sourceTransaction;
    }

    public InternalTransaction getTargetTransaction() {
      return targetTransaction;
    }

    public Split getSourceSplit() {
      return sourceSplit;
    }

    public Split getTargetSplit() {
      return targetSplit;
    }
  }
}