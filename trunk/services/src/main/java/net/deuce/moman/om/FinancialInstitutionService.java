package net.deuce.moman.om;

import net.sf.ofx4j.client.AccountStatement;
import net.sf.ofx4j.client.BankAccount;
import net.sf.ofx4j.client.context.DefaultApplicationContext;
import net.sf.ofx4j.client.context.OFXApplicationContextHolder;
import net.sf.ofx4j.client.impl.BaseFinancialInstitutionData;
import net.sf.ofx4j.domain.data.banking.AccountType;
import net.sf.ofx4j.domain.data.banking.BankAccountDetails;
import net.sf.ofx4j.domain.data.banking.BankAccountInfo;
import net.sf.ofx4j.domain.data.signup.AccountProfile;
import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URL;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;


@Service
public class FinancialInstitutionService extends EntityService<FinancialInstitution, FinancialInstitutionDao> {

  @Autowired
  private FinancialInstitutionDao financialInstitutionDao;

  protected FinancialInstitutionDao getDao() {
    return financialInstitutionDao;
  }

  /**
   * Clears all entities.
   */
  @Transactional
  public void clear() {
    getDao().clear();
  }

  public void toXml(User user, Document doc) {

    Element root = doc.getRootElement().addElement(getRootElementName());

    for (FinancialInstitution fi : list()) {
      toXml(fi, root);
    }
  }

  public TransactionFetchResult fetchTransactions(Account account, Date startDate, Date endDate) throws Exception {

		OFXApplicationContextHolder.setCurrentContext(new DefaultApplicationContext("QWIN", "1700"));

		BaseFinancialInstitutionData data = new BaseFinancialInstitutionData(account.getFinancialInstitution().getFinancialInstitutionId());
		data.setOFXURL(new URL(account.getFinancialInstitution().getUrl()));
		data.setOrganization(account.getFinancialInstitution().getOrganization());
		data.setFinancialInstitutionId(account.getFinancialInstitution().getFinancialInstitutionId());
		net.sf.ofx4j.client.FinancialInstitutionService service = new net.sf.ofx4j.client.impl.FinancialInstitutionServiceImpl();
		net.sf.ofx4j.client.FinancialInstitution fi = service.getFinancialInstitution(data);

		// read the fi profile (note: not all institutions
		// support this, and you normally don't need it.)
		// FinancialInstitutionProfile profile = fi.readProfile();

		// get a reference to a specific bank account at your FI
		BankAccountDetails bankAccountDetails = new BankAccountDetails();

		// routing number to the bank.
		bankAccountDetails.setRoutingNumber(account.getBankId());
		// bank account number.
		bankAccountDetails.setAccountNumber(account.getAccountId());
		// it's a checking account
		bankAccountDetails.setAccountType(AccountType.CHECKING);

		BankAccount bankAccount = fi.loadBankAccount(bankAccountDetails, account.getUsername(), account.getPassword());

		AccountStatement statement = null;

		statement = bankAccount.readStatement(startDate, endDate);

		/*
		FileWriter fileWriter = new FileWriter("/Users/nbolton/Downloads/importedTransactions.ofx");
		try {
			StatementRange range = new StatementRange();
		    range.setIncludeTransactions(true);
		    range.setStart(startDate);
		    range.setEnd(endDate);

		    FinancialInstitutionWrapper wiggy = new FinancialInstitutionWrapper(data, new OFXV1Connection());
		    RequestEnvelope request = wiggy.createRequest(account.getUsername(), account.getPassword());
		    TransactionWrappedRequestMessage requestTransaction = new BankStatementRequestTransaction();
		    BankStatementRequest bankRequest = new BankStatementRequest();
		    bankRequest.setAccount(bankAccountDetails);
		    bankRequest.setStatementRange(range);
		    requestTransaction.setWrappedMessage(bankRequest);
		    BankingRequestMessageSet bankingRequest = new BankingRequestMessageSet();
		    bankingRequest.setStatementRequest((BankStatementRequestTransaction) requestTransaction);
		    request.getMessageSets().add(bankingRequest);

		    ResponseEnvelope response = new OFXV1Connection().sendRequest(request, data.getOFXURL());
			OFXV2Writer writer = new OFXV2Writer(fileWriter);
			AggregateMarshaller marshaller = new AggregateMarshaller();
			marshaller.marshal(response, writer);
		} finally {
			if (fileWriter != null) {
				fileWriter.close();
			}
		}
		*/

		return new TransactionFetchResult(
				statement.getLedgerBalance().getAmount(),
				endDate,
				statement.getTransactionList().getTransactions());
	}

	public List<Account> getAvailableAccounts(FinancialInstitution financialInstitution,
			String username, String password) throws Exception {
		List<Account> list = new LinkedList<Account>();

		OFXApplicationContextHolder.setCurrentContext(new DefaultApplicationContext("QWIN", "1700"));

		BaseFinancialInstitutionData data = new BaseFinancialInstitutionData(financialInstitution.getFinancialInstitutionId());
		data.setOFXURL(new URL(financialInstitution.getUrl()));
		data.setOrganization(financialInstitution.getOrganization());
		data.setFinancialInstitutionId(financialInstitution.getFinancialInstitutionId());
		net.sf.ofx4j.client.FinancialInstitutionService service = new net.sf.ofx4j.client.impl.FinancialInstitutionServiceImpl();
		net.sf.ofx4j.client.FinancialInstitution fi = service.getFinancialInstitution(data);

		Collection<AccountProfile> profiles = fi.readAccountProfiles(username, password);
		Account account;

		for (AccountProfile profile : profiles) {
			BankAccountInfo bankInfo = profile.getBankSpecifics();

			account = new Account();
      account.setSelected(Boolean.TRUE);
      account.setNickname(profile.getDescription());
      account.setBankId(bankInfo.getBankAccount().getBankId());
      account.setAccountId(bankInfo.getBankAccount().getAccountNumber());
      account.setUsername(username);
      account.setPassword(password);
      account.setStatus(bankInfo.getStatus());
      account.setSupportsDownloading(bankInfo.getSupportsTransactionDetailOperations());
      account.setBalance(0.0);
      account.setOnlineBalance(0.0);
      account.setLastReconciledEndingBalance(0.0);
			list.add(account);
		}
		return list;
	}

  public void toXml(FinancialInstitution fi, Element parent) {
    Element el = parent.addElement("financialInstitution");
    addElement(el, "id", fi.getId());
    addElement(el, "name", fi.getName());
    addElement(el, "url", fi.getUrl());
    addElement(el, "fid", fi.getFinancialInstitutionId());
    addElement(el, "org", fi.getOrganization());
  }

  public Class<FinancialInstitution> getType() {
    return FinancialInstitution.class;
  }

  public String getRootElementName() {
    return "financialInstitutions";
  }
}
