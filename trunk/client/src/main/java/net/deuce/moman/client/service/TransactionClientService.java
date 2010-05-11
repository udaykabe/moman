package net.deuce.moman.client.service;

import net.deuce.moman.client.HttpRequest;
import net.deuce.moman.client.HttpRequestUtils;
import net.deuce.moman.client.model.AccountClient;
import net.deuce.moman.client.model.EnvelopeClient;
import net.deuce.moman.client.model.TransactionClient;
import net.deuce.moman.client.service.EntityClientService;
import net.deuce.moman.client.service.TransactionListResult;
import org.dom4j.Document;
import org.dom4j.Element;

import java.util.LinkedList;
import java.util.List;

public class TransactionClientService extends EntityClientService<TransactionClient> {

  private static TransactionClientService __instance = new TransactionClientService();

  public static TransactionClientService instance() { return __instance; }

  private TransactionClientService() {}

  @Override
  protected String getServiceName() {
    return "transaction";
  }

  @Override
  protected String getEntityName() {
    return "transaction";
  }

  @Override
  protected TransactionClient newEntity() {
    return new TransactionClient();
  }

  public TransactionListResult getTransactions(EnvelopeClient env, int listPosition, int pageSize) {

    try {
      HttpRequest req = HttpRequest.newGetRequest(buildServiceUrl(
          new String[]{"transaction", "executeCommand", "getSelectedAccountTransactionsCommand",
              env.getUuid(), "true", "false", "true", Integer.toString(listPosition),
              Integer.toString(pageSize)}));

      Document doc = HttpRequestUtils.executeRequest(req.buildMethod(), true, false);

      Element root = (Element) doc.selectSingleNode("//transactions");

      TransactionListResult result = new TransactionListResult(
          Integer.valueOf(root.attributeValue("pageSize")),
          Integer.valueOf(root.attributeValue("totalSize")),
          null);

      List<TransactionClient> list = new LinkedList<TransactionClient>();
      result.setTransactions(list);

      List<Element> entities = doc.selectNodes("//transaction");
      if (entities == null || entities.size() == 0) return result;

      TransactionClient client;
      for (Element entity : entities) {
        client = new TransactionClient();
        client.buildEntityClient(client, entity);
        list.add(client);
      }

      return result;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}