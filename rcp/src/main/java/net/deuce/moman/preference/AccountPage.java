package net.deuce.moman.preference;

import net.deuce.moman.ui.Activator;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class AccountPage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {
	
	public static final String ACCOUNT_IMPORT_MATCHING_DAY_THRESHOLD = 
		"ACCOUNT_IMPORT_MATCHING_DAY_THRESHOLD";

	public AccountPage() {
		super(GRID);
	}

	public void createFieldEditors() {
		addField(new IntegerFieldEditor(ACCOUNT_IMPORT_MATCHING_DAY_THRESHOLD,
				"Import Matching &Threshold:", getFieldEditorParent()));
	}

	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Account Import Preferences");
	}

}
