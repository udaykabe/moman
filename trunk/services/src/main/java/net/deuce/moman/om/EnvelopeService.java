package net.deuce.moman.om;

import net.deuce.moman.util.Constants;
import net.deuce.moman.util.Utils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EnvelopeService extends UserBasedService<Envelope, EnvelopeDao> {

  @Autowired
  private TransactionService transactionService;

  @Autowired
  private EnvelopeDao envelopeDao;

  protected EnvelopeDao getDao() {
    return envelopeDao;
  }

  public Envelope getSelectedEnvelope(User user) {
    return getDao().findSelected(user);
  }

  /*
  public void setSelectedEnvelope(Envelope env) {
    Envelope selectedEnvelope = getSelectedEnvelope(env.getUser());
    if (selectedEnvelope != null) {
      selectedEnvelope.setSelected(false);
      update(selectedEnvelope);
    }
    env.setSelected(true);
    transactionService.setCustomTransactionList(null);
  }

  public Envelope getMonthlyEnvelope() {
    return monthlyEnvelope;
  }

  public void setMonthlyEnvelope(Envelope env) {
    this.monthlyEnvelope = env;
  }

  public void setRootEnvelope(Envelope envelope) {
    this.rootEnvelope = envelope;
  }

  public Envelope getRootEnvelope() {
    return rootEnvelope;
  }

  public Envelope getSavingsGoalsEnvelope() {
    return savingsGoalsEnvelope;
  }

  public void setSavingsGoalsEnvelope(Envelope savingsGoalsEnvelope) {
    this.savingsGoalsEnvelope = savingsGoalsEnvelope;
  }

  public Envelope getUnassignedEnvelope() {
    return unassignedEnvelope;
  }

  public void setUnassignedEnvelope(Envelope unassignedEnvelope) {
    this.unassignedEnvelope = unassignedEnvelope;
  }

  public Envelope getAvailableEnvelope() {
    return availableEnvelope;
  }

  public void setAvailableEnvelope(Envelope availableEnvelope) {
    this.availableEnvelope = availableEnvelope;
  }

  public void moveTransaction(Account account, Envelope source,
                              Envelope target, InternalTransaction transaction) {

  }

  private void addEnvelopeToList(Envelope env, List<Envelope> list) {
    list.add(env);
    for (Envelope child : env.getChildren()) {
      addEnvelopeToList(child, list);
    }
  }

  public List<Envelope> getAllEnvelopes() {
    List<Envelope> envelopes = new LinkedList<Envelope>();
    addEnvelopeToList(rootEnvelope, envelopes);
    return envelopes;
  }

  public void transfer(Account sourceAccount, Account targetAccount,
                       Envelope source, Envelope target, double amount) {

    Date date = new Date();

    // source transaction
    InternalTransaction sTransaction = transactionFactory.newEntity(
        null, -amount, TransactionType.XFER, date,
        "Transfer to " + target.getName(), null, null, null,
        null, TransactionStatus.reconciled, sourceAccount);
    sTransaction.addSplit(source, -amount);
    transactionService.addEntity(sTransaction);

    // target transaction
    InternalTransaction tTransaction = transactionFactory.newEntity(
        null, amount, TransactionType.XFER, date,
        "Transfer from " + source.getName(), null, null, null,
        null, TransactionStatus.reconciled, targetAccount);
    tTransaction.addSplit(target, amount);
    transactionService.addEntity(tTransaction);

    sTransaction.setTransferTransaction(tTransaction);
    tTransaction.setTransferTransaction(sTransaction);

    source.markDirty();
    target.markDirty();
    fireEntityChanged(source);
    fireEntityChanged(target);
  }

  public void importDefaultEnvelopes() {
//              importDefaultEnvelope(defaultAvailableEnvelope, null);
//              importDefaultEnvelope(defaultUnassignedEnvelope, null);
//              importDefaultEnvelope(defaultMonthlyEnvelope, null);
    importDefaultEnvelope(defaultRootEnvelope, null);
  }

  private void importDefaultEnvelope(Envelope envelope, Envelope parent) {
    Envelope clone = envelopeFactory.cloneEnvelope(envelope, parent);
    addEnvelope(clone, parent);
    for (Envelope child : envelope.getChildren()) {
      importDefaultEnvelope(child, clone);
    }
  }

  public void addDefaultEnvelope(Envelope envelope, Envelope parent) {
    if (parent != null) {
      envelope.setParent(envelope);
      parent.addChild(envelope);
    }
    if (envelope.isRoot()) {
      defaultRootEnvelope = envelope;
    }
  }

  public void addEnvelope(Envelope envelope, Envelope parent) {

    if (parent != null) {

      envelope.setParent(parent);
      parent.addChild(envelope);
    }

    if (envelope.isRoot()) {
      rootEnvelope = envelope;
    }

    if (envelope.isAvailable()) {
      availableEnvelope = envelope;
    }

    if (envelope.isUnassigned()) {
      unassignedEnvelope = envelope;
    }

    if (envelope.isMonthly()) {
      monthlyEnvelope = envelope;
    }

    if (envelope.isSavingsGoals()) {
      savingsGoalsEnvelope = envelope;
    }

    if (envelope.isBill()) {
      bills.put(envelope.getId(), envelope);
    }

    if (envelope.isSavingsGoal()) {
      savingsGoals.put(envelope.getId(), envelope);
    }

    if (envelope.getIndex() > maxIndex) {
      maxIndex = envelope.getIndex();
    }

    super.addEntity(envelope);
  }

  public synchronized int getNextIndex() {
    return ++maxIndex;
  }

  public void removeEnvelope(Envelope envelope) {

    if (!envelope.isEditable()) return;

    Envelope parent = envelope.getParent();
    if (parent != null) {
      parent.removeChild(envelope);
    }
    List<Envelope> children = new LinkedList<Envelope>(envelope.getChildren());

    for (Envelope child : children) {
      removeEnvelope(child);
    }

    for (Rule rule : transactionRuleService.getEntities()) {
      if (rule.getEnvelope() == envelope) {
        rule.setEnvelope(unassignedEnvelope);
      }
    }

    if (envelope.isBill()) {
      bills.remove(envelope.getId());
    }

    if (envelope.isSavingsGoal()) {
      savingsGoals.remove(envelope.getId());
    }

    super.removeEntity(envelope);
  }

  public List<Envelope> getSavingsGoals() {
    return new LinkedList<Envelope>(savingsGoals.values());
  }

  public List<Envelope> getOrderedSavingsGoals(boolean reverse) {
    List<Envelope> list = getSavingsGoals();
    if (list.size() > 0) {
      Envelope entity = list.get(0);
      Comparator<Envelope> comparator = reverse ? entity.getReverseComparator() :
          entity.getForwardComparator();
      Collections.sort(list, comparator);
    }
    return list;
  }

  public List<Envelope> getOrderedBudgetedEnvelopes(boolean reverse) {
    List<Envelope> list = new LinkedList<Envelope>();
    for (Envelope env : getEntities()) {
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

  public List<Envelope> getBills() {
    return new LinkedList<Envelope>(bills.values());
  }

  public List<Envelope> getOrderedBills(boolean reverse) {
    List<Envelope> list = getBills();
    if (list.size() > 0) {
      Envelope entity = list.get(0);
      Comparator<Envelope> comparator = reverse ? entity.getReverseComparator() :
          entity.getForwardComparator();
      Collections.sort(list, comparator);
    }
    return list;
  }


  public Envelope findEntity(String id) {
    Envelope env = super.findEntity(id);
    if (env == null) {
      env = bills.get(id);
    }
    return env;
  }

  public Envelope getBill(String id) {
    Envelope bill = bills.get(id);
    if (bill == null) {
      throw new RuntimeException("No bill exists with ID " + id);
    }
    return bill;
  }

  public void bindEnvelopes() {
    Envelope parent;
    for (Envelope env : getEntities()) {

      if (env.getParentId() != null) {
        parent = getEntity(env.getParentId());
        env.setParent(parent);
        parent.addChild(env);

      }

      if (env.isBill()) {
        bills.put(env.getId(), env);
      }

      if (env.isSavingsGoal()) {
        savingsGoals.put(env.getId(), env);
      }

      env.clearDirty();
    }
  }

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
            transfer(account, account, getAvailableEnvelope(), env, transferAmount);
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
  */

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