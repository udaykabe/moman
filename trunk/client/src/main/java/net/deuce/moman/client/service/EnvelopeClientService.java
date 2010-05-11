package net.deuce.moman.client.service;

import net.deuce.moman.client.HttpRequest;
import net.deuce.moman.client.HttpRequestUtils;
import net.deuce.moman.client.model.AccountClient;
import net.deuce.moman.client.model.EntityClient;
import net.deuce.moman.client.model.EnvelopeClient;
import net.deuce.moman.client.service.EntityClientService;
import net.deuce.moman.client.service.NoAvailableServerException;
import net.deuce.moman.util.Utils;
import org.dom4j.Document;
import org.dom4j.Element;

import java.net.URLEncoder;
import java.util.*;

public class EnvelopeClientService extends EntityClientService {

  private static EnvelopeClientService __instance = new EnvelopeClientService();

  public static EnvelopeClientService instance() { return __instance; }

  private List<EnvelopeClient> envelopes = null;
  private Map<String, EnvelopeClient> envelopeMap = new HashMap<String, EnvelopeClient>();

  private Stack<EnvelopeClient> currentEnvelope = new Stack<EnvelopeClient>();

  private EnvelopeClient targetEnvelope = null;
  private EnvelopeClient selectedEnvelope = null;

  private EnvelopeClient rootEnvelope = null;

  private EnvelopeClientService() {}

  @Override
  protected String getEntityName() {
    return "envelope";
  }

  @Override
  protected String getServiceName() {
    return "envelope";
  }

  @Override
  protected EntityClient newEntity() {
    return new EnvelopeClient();
  }

  @Override
  public List list(Comparator sort) throws NoAvailableServerException {
    if (envelopes == null) {
      envelopes = super.list(null);

      envelopeMap.clear();
      for (EnvelopeClient client : envelopes) {
        envelopeMap.put(client.getUuid(), client);

        if (client.isRoot()) {
          rootEnvelope = client;
        }
      }

      Stack<EnvelopeClient> oldStack = currentEnvelope;
      currentEnvelope = new Stack<EnvelopeClient>();

      for (EnvelopeClient env : oldStack) {
        currentEnvelope.push(envelopeMap.get(env.getUuid()));
      }
    }

    if (sort != null) {
      List<EnvelopeClient> sortedList = new ArrayList<EnvelopeClient>();
      sortedList.addAll(envelopes);
      Collections.sort(sortedList, sort);
      return sortedList;
    }
    return envelopes;
  }

  public int currentStackSize() {
    return currentEnvelope.size();
  }

  public void pushCurrent(EnvelopeClient env) throws NoAvailableServerException {
    checkCache();
    currentEnvelope.push(env);
  }

  public EnvelopeClient popCurrent() throws NoAvailableServerException {
    checkCache();
    return currentEnvelope.pop();
  }

  public EnvelopeClient peekCurrent() throws NoAvailableServerException {
    checkCache();
    return currentEnvelope.peek();
  }

  public EnvelopeClient getSelectedEnvelope() {
    return selectedEnvelope;
  }

  public void setSelectedEnvelope(EnvelopeClient selectedEnvelope) {
    this.selectedEnvelope = selectedEnvelope;
  }

  public EnvelopeClient getTargetEnvelope() {
    return targetEnvelope;
  }

  public void setTargetEnvelope(EnvelopeClient targetEnvelope) {
    this.targetEnvelope = targetEnvelope;
  }

  private void checkCache() throws NoAvailableServerException {
    list(null);
  }

  public List<EnvelopeClient> getChildren(EnvelopeClient parent) throws NoAvailableServerException {

    checkCache();

    List<EnvelopeClient> children = new LinkedList<EnvelopeClient>();

    for (EnvelopeClient client : envelopes) {
      if (parent.getUuid().equals(client.getParentId())) {
        children.add(client);
      }
    }

    return children;
  }

  public void moveEnvelope(EnvelopeClient env, EnvelopeClient child) throws NoAvailableServerException {

    try {
      HttpRequest req = HttpRequest.newGetRequest(buildServiceUrl(new String[]{"envelope", "executeCommand", "addChildCommand",
        env.getUuid(), child.getUuid()}));
      HttpRequestUtils.executeRequest(req.buildMethod(), true, true);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public void deleteEnvelope(EnvelopeClient env) throws NoAvailableServerException {
    try {
      HttpRequest req = HttpRequest.newGetRequest(buildServiceUrl(
          new String[]{"envelope", "executeCommand", "removeEnvelopeCommand", env.getUuid()}));
      HttpRequestUtils.executeRequest(req.buildMethod(), true, true);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public void transferEnvelope(AccountClient targetAccount, EnvelopeClient sourceEnvelope, EnvelopeClient targetEnvelope, Double amount) {
    try {
      HttpRequest req = HttpRequest.newGetRequest(buildServiceUrl(
          new String[]{"envelope", "executeCommand", "transferCommand",
              targetAccount.getUuid(), sourceEnvelope.getUuid(), targetEnvelope.getUuid(), Utils.formatDouble(amount)}));
      HttpRequestUtils.executeRequest(req.buildMethod(), true, true);
      envelopes = null;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public void persist(EnvelopeClient env, EnvelopeClient original) throws NoAvailableServerException {
    try {

      if (!env.getName().equals(original.getName()) || env.isEnabled() != original.isEnabled() || env.getBudget() != original.getBudget()) {
        HttpRequest req = HttpRequest.newGetRequest(buildServiceUrl(new String[]{
            "envelope", "edit", env.getUuid(),
            "name", URLEncoder.encode(env.getName(), "UTF-8"),
            "enabled", Boolean.toString(env.isEnabled()),
            "budget", Double.toString(env.getBudget())
        }));

        HttpRequestUtils.executeRequest(req.buildMethod(), true, false);
      }

    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public EnvelopeClient getRootEnvelope() throws NoAvailableServerException {
    checkCache();
    return rootEnvelope;
  }
}
