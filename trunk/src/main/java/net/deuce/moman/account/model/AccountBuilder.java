package net.deuce.moman.account.model;

import java.text.ParseException;
import java.util.List;

import net.deuce.moman.Constants;
import net.deuce.moman.account.service.AccountService;
import net.deuce.moman.fi.service.FinancialInstitutionService;
import net.deuce.moman.model.AbstractBuilder;
import net.deuce.moman.util.Utils;
import net.sf.ofx4j.domain.data.common.AccountStatus;

import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class AccountBuilder extends AbstractBuilder<Account> {
	
	@Autowired
	private AccountService accountService;
	
	@Autowired
	private AccountFactory accountFactory;
	
	@Autowired 
	private FinancialInstitutionService financialInsitutionService;

	@SuppressWarnings("unchecked")
	public void parseXml(Element e) {
		try {
			Account account;
			String fiid;
			Element el;

			List<Element> nodes = e.selectNodes("accounts/account");
			for (Element n : nodes) {
				account = accountFactory.buildEntity(
						n.attributeValue("id"),
						Boolean.valueOf(n.elementText("selected")),
						n.elementText("nickname"),
						n.elementText("bankId"),
						n.elementText("accountId"),
						n.elementText("username"),
						n.elementText("password"),
						n.element("status") != null ? AccountStatus.valueOf(n.elementText("status")) : null,
						Boolean.valueOf(n.elementText("supports-downloading")),
						Double.valueOf(n.elementText("balance")),
						Double.valueOf(n.elementText("online-balance")),
						Double.valueOf(n.elementText("last-reconciled-ending-balance"))
						);
				
				String val = n.elementText("initial-balance");
				if (val != null) {
					account.setInitialBalance(Double.valueOf(val));
				}

				val = n.elementText("last-download-date");
				if (val != null) {
					account.setLastDownloadDate(Constants.SHORT_DATE_FORMAT.parse(val));
				}

				val = n.elementText("last-reconciled-date");
				if (val != null) {
					account.setLastReconciledDate(Constants.SHORT_DATE_FORMAT.parse(val));
				}

				el = n.element("financialInstitution");
				if (el != null) {
					fiid = el.attributeValue("id");
					account.setFinancialInstitution(financialInsitutionService.getEntity(fiid));
				}
				accountService.addEntity(account);
			}
		} catch (ParseException pe) {
			pe.printStackTrace();
			throw new RuntimeException(pe);
		}
	}
	
	public void buildXml(Document doc) {
		
		Element root = doc.getRootElement().addElement("accounts");
		
		for (Account account : accountService.getEntities()) {
			buildEntity(account, root);
		}
	}

	@Override
	protected Element buildEntity(Account account, Element parent) {
		Element el = parent.addElement("account");
		el.addAttribute("id", account.getId());
		addOptionalBooleanElement(el, "selected", account.isSelected());
		addElement(el, "bankId", account.getBankId());
		addElement(el, "accountId", account.getAccountId());
		addElement(el, "username", account.getUsername());
		addElement(el, "password", account.getPassword());
		addElement(el, "nickname", account.getNickname());
		addElement(el, "balance", Utils.formatDouble(account.getBalance()));
		addElement(el, "online-balance", Utils.formatDouble(account.getOnlineBalance()));
		addElement(el, "last-reconciled-ending-balance", Utils.formatDouble(account.getLastReconciledEndingBalance()));
		
		if (account.getStatus() != null) {
			addElement(el, "status", account.getStatus().name());
		}
		addElement(el, "supports-downloading", account.isSupportsDownloading());
		addOptionalElement(el, "initial-balance", account.getInitialBalance());
		if (account.getLastDownloadDate() != null) {
			addElement(el, "last-download-date", Constants.SHORT_DATE_FORMAT.format(account.getLastDownloadDate()));
		}
		if (account.getLastReconciledDate() != null) {
			addElement(el, "last-reconciled-date", Constants.SHORT_DATE_FORMAT.format(account.getLastReconciledDate()));
		}
		if (account.getFinancialInstitution() != null) {
			el.addElement("financialInstitution").addAttribute("id", account.getFinancialInstitution().getId());
		}
		return el;
	}

	@Override
	protected String getRootElementName() {
		return "accounts";
	}
}
