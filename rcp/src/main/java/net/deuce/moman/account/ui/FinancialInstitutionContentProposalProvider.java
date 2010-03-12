package net.deuce.moman.account.ui;

import java.util.LinkedList;
import java.util.List;

import net.deuce.moman.entity.ServiceProvider;
import net.deuce.moman.entity.model.fi.FinancialInstitution;
import net.deuce.moman.entity.service.fi.FinancialInstitutionService;

import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;

public class FinancialInstitutionContentProposalProvider implements
		IContentProposalProvider {

	private FinancialInstitutionService financialInstitutionService = ServiceProvider.instance().getFinancialInstitutionService();

	public IContentProposal[] getProposals(String contents, int position) {
		List<IContentProposal> proposals = new LinkedList<IContentProposal>();
		for (FinancialInstitution fi : financialInstitutionService
				.getEntities()) {
			if (fi.getName().toLowerCase().contains(contents.toLowerCase())) {
				proposals.add(new FinancialInstitutionContentProposal(fi));
			}
		}
		return proposals.toArray(new IContentProposal[proposals.size()]);
	}

}
