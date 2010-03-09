package net.deuce.moman.account.ui;

import java.util.LinkedList;
import java.util.List;

import net.deuce.moman.fi.model.FinancialInstitution;
import net.deuce.moman.service.ServiceNeeder;

import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;

public class FinancialInstitutionContentProposalProvider implements
		IContentProposalProvider {

	@Override
	public IContentProposal[] getProposals(String contents, int position) {
		List<IContentProposal> proposals = new LinkedList<IContentProposal>();
		for (FinancialInstitution fi : ServiceNeeder.instance().getFinancialInstitutionService().getEntities()) {
			if (fi.getName().toLowerCase().contains(contents.toLowerCase())) {
				proposals.add(new FinancialInstitutionContentProposal(fi));
			}
		}
		return proposals.toArray(new IContentProposal[proposals.size()]);
	}

}
