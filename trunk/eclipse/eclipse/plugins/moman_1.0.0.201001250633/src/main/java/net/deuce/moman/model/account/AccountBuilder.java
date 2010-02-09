package net.deuce.moman.model.account;

import java.text.ParseException;
import java.util.List;

import net.deuce.moman.model.EntityBuilder;
import net.deuce.moman.model.Registry;

import org.dom4j.Document;
import org.dom4j.Element;


public class AccountBuilder implements EntityBuilder {

	@SuppressWarnings("unchecked")
	public void parseXml(Registry registry, Element e) {
		try {
			Account account;
			String fiid;
			Element el;

			List<Element> nodes = e.selectNodes("accounts/account");
			for (Element n : nodes) {
				account = new Account();
				account.setId(n.attributeValue("id"));
				account.setSelected(new Boolean(n.attributeValue("selected")));
				account.setBankId(n.elementText("bankId"));
				account.setAccountId(n.elementText("accountId"));
				account.setUsername(n.elementText("username"));
				account.setPassword(n.elementText("password"));
				account.setNickname(n.elementText("nickname"));

				el = n.element("lastDownloadDate");
				if (el != null) {
					String val = el.getTextTrim();
					if (val != null && val.length() > 0) {
						account.setLastDownloadDate(Registry.DATE_FORMAT.parse(val));
					}
				}

				el = n.element("financialInstitution");
				fiid = el.attributeValue("id");
				account.setFinancialInstitution(registry.getFinancialInstitution(fiid));
				registry.addAccount(account);
			}
		} catch (ParseException pe) {
			throw new RuntimeException(pe);
		}
	}
	
	public void buildXml(Registry registry, Document doc) {
		
		Element root = doc.getRootElement().addElement("accounts");
		Element el;
		
		for (Account account : registry.getAccounts()) {
			el = root.addElement("account");
			el.addAttribute("id", account.getId());
			el.addAttribute("selected", Boolean.toString(account.isSelected()));
			el.addElement("bankId").setText(account.getBankId());
			el.addElement("accountId").setText(account.getAccountId());
			el.addElement("username").setText(account.getUsername());
			el.addElement("password").setText(account.getPassword());
			el.addElement("nickname").setText(account.getNickname());
			el.addElement("financialInstitution").addAttribute("id", account.getFinancialInstitution().getId());
		}
	}
}
