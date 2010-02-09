package net.deuce.moman.model.fi;

import java.util.LinkedList;
import java.util.List;

import net.deuce.moman.model.Registry;

import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;

public class FinancialInstitutionContentProposalProvider implements
		IContentProposalProvider {

	@Override
	public IContentProposal[] getProposals(String contents, int position) {
		List<IContentProposal> proposals = new LinkedList<IContentProposal>();
		for (FinancialInstitution fi : Registry.instance().getFinancialInstitutions()) {
			if (fi.getName().toLowerCase().contains(contents.toLowerCase())) {
				proposals.add(new FinancialInstitutionContentProposal(fi));
			}
		}
		return proposals.toArray(new IContentProposal[proposals.size()]);
	}

}
