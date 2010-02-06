package net.deuce.moman.account.ui;

import net.deuce.moman.fi.model.FinancialInstitution;

import org.eclipse.jface.fieldassist.IContentProposal;

public class FinancialInstitutionContentProposal implements IContentProposal {
	
	private FinancialInstitution financialInstitution;
	
	public FinancialInstitutionContentProposal(FinancialInstitution financialInstitution) {
		this.financialInstitution = financialInstitution;
	}

	@Override
	public String getContent() {
		return financialInstitution.getName();
	}

	@Override
	public int getCursorPosition() {
		return 0;
	}

	@Override
	public String getDescription() {
		return null;
	}

	@Override
	public String getLabel() {
		return financialInstitution.getName();
	}

}
