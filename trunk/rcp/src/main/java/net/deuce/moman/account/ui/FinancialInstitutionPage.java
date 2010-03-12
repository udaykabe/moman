package net.deuce.moman.account.ui;

import net.deuce.moman.entity.ServiceProvider;
import net.deuce.moman.entity.model.fi.FinancialInstitution;
import net.deuce.moman.entity.service.fi.FinancialInstitutionService;
import net.deuce.moman.fi.FinanacialInstitutionTextContentAdapter;

import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jface.fieldassist.TextControlCreator;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.fieldassist.ContentAssistField;

@SuppressWarnings("deprecation")
public class FinancialInstitutionPage extends WizardPage {

	private FinancialInstitutionService service = ServiceProvider.instance().getFinancialInstitutionService();
	private FinancialInstitution financialInstitution;

	protected FinancialInstitutionPage() {
		super("Financial Institution");
		setTitle("Financial Institution");
	}

	public FinancialInstitution getFinancialInstitution() {
		return financialInstitution;
	}

	public void createControl(Composite parent) {

		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		container.setLayout(layout);
		Label label1 = new Label(container, SWT.NULL);
		label1.setText("Search:");

		IContentProposalProvider contentProposalProvider = new FinancialInstitutionContentProposalProvider();

		char[] autoActivationCharacters = new char[] { 'A', 'B', 'C', 'D', 'E',
				'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q',
				'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c',
				'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o',
				'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };

		final ContentAssistField searchField = new ContentAssistField(
				container, SWT.BORDER | SWT.SINGLE, new TextControlCreator(),
				new FinanacialInstitutionTextContentAdapter(),
				contentProposalProvider, null, autoActivationCharacters);

		Text text = (Text) searchField.getControl();
		text.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				String name = ((Text) e.getSource()).getText();
				if (service.doesFinancialInstitutionExistByName(name)) {
					financialInstitution = service
							.getFinancialInstitutionByName(name);
					((NewAccountWizard) getWizard())
							.setFinancialInstitution(financialInstitution);
					setPageComplete(true);
				}
			}
		});
		FormData fd = new FormData();
		fd.width = 200;
		text.setLayoutData(fd);
		// Required to avoid an error in the system
		setControl(container);
		setPageComplete(false);
	}

}
