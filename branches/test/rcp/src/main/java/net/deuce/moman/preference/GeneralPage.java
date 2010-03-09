package net.deuce.moman.preference;

import net.deuce.moman.ui.Activator;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class GeneralPage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {

	public GeneralPage() {
		super(GRID);
	}

	public void createFieldEditors() {
	}

	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("General Preferences");
	}

}
