package net.deuce.moman.account.ui;

import net.deuce.moman.entity.model.fi.FinancialInstitution;

import org.eclipse.jface.fieldassist.IContentProposal;

public class FinancialInstitutionContentProposal implements IContentProposal {

	private FinancialInstitution financialInstitution;

	public FinancialInstitutionContentProposal(
			FinancialInstitution financialInstitution) {
		this.financialInstitution = financialInstitution;
	}

	public String getContent() {
		return financialInstitution.getName();
	}

	public int getCursorPosition() {
		return 0;
	}

	public String getDescription() {
		return null;
	}

	public String getLabel() {
		return financialInstitution.getName();
	}

}
