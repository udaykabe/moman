package net.deuce.moman.client.service;

import net.deuce.moman.client.HttpRequest;
import net.deuce.moman.client.HttpRequestUtils;
import net.deuce.moman.client.model.AccountClient;
import net.deuce.moman.client.model.FinancialInstitutionClient;
import net.deuce.moman.client.model.TransactionClient;
import net.deuce.moman.client.service.EntityClientService;
import net.deuce.moman.client.service.NoAvailableServerException;
import org.dom4j.Document;
import org.dom4j.Element;

import java.net.URLEncoder;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class FinancialInstitutionClientService extends EntityClientService<FinancialInstitutionClient> {

  private static FinancialInstitutionClientService __instance = new FinancialInstitutionClientService();

  public static FinancialInstitutionClientService instance() {
    return __instance;
  }

  private FinancialInstitutionClientService() {
  }

  @Override
  protected String getServiceName() {
    return "fi";
  }

  @Override
  protected String getEntityName() {
    return "financialInstitution";
  }

  protected FinancialInstitutionClient newEntity() {
    return null;
  }

  public List<TransactionClient> downloadBankTransactions(AccountClient account) {
   try {
      HttpRequest req = HttpRequest.newGetRequest(buildServiceUrl(new String[]{getServiceName(), "executeCommand",
        "importTransactionsCommand", account.getUuid(), "false"}));

      Document doc = HttpRequestUtils.executeRequest(req.buildMethod(), true, false);


      List<TransactionClient> list = new LinkedList<TransactionClient>();

      List<Element> entities = doc.selectNodes("//" + getEntityName());
      if (entities == null || entities.size() == 0) return list;

      TransactionClient client;
      for (Element entity : entities) {
        client = new TransactionClient();
        client.buildEntityClient(client, entity);
        list.add(client);
      }

      return list;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

}