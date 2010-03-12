package net.deuce.moman.ui.demo;

import net.deuce.moman.ui.Activator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.internal.IWorkbenchGraphicConstants;
import org.eclipse.ui.part.ViewPart;

public class DemoView7 extends ViewPart {

	public static final String ID = DemoView7.class.getName();

	public DemoView7() {
	}

	public void createPartControl(Composite parent) {
		String[] images = { IWorkbenchGraphicConstants.IMG_ETOOL_IMPORT_WIZ,
				IWorkbenchGraphicConstants.IMG_ETOOL_EXPORT_WIZ,
				IWorkbenchGraphicConstants.IMG_ETOOL_NEW_PAGE,
				IWorkbenchGraphicConstants.IMG_ETOOL_PIN_EDITOR,
				IWorkbenchGraphicConstants.IMG_ETOOL_PIN_EDITOR_DISABLED,
				IWorkbenchGraphicConstants.IMG_ETOOL_HELP_CONTENTS,
				IWorkbenchGraphicConstants.IMG_ETOOL_HELP_SEARCH,
				IWorkbenchGraphicConstants.IMG_ETOOL_NEW_FASTVIEW,
				IWorkbenchGraphicConstants.IMG_DTOOL_NEW_FASTVIEW,
				IWorkbenchGraphicConstants.IMG_ETOOL_RESTORE_TRIMPART,
				IWorkbenchGraphicConstants.IMG_ETOOL_EDITOR_TRIMPART,
				IWorkbenchGraphicConstants.IMG_LCL_CLOSE_VIEW,
				IWorkbenchGraphicConstants.IMG_LCL_PIN_VIEW,
				IWorkbenchGraphicConstants.IMG_LCL_MIN_VIEW,
				IWorkbenchGraphicConstants.IMG_LCL_RENDERED_VIEW_MENU,
				IWorkbenchGraphicConstants.IMG_LCL_VIEW_MENU, };

		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		parent.setLayout(layout);
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;

		for (int i = 0; i < images.length; i++) {
			Image image = Activator.getDefault().getWorkbench()
					.getSharedImages().getImage(images[i]);
			if (image != null) {
				Label name = new Label(parent, SWT.NONE);
				name.setText(images[i]);
				name.setLayoutData(gridData);
				Button button = new Button(parent, SWT.NONE);
				button.setImage(image);
				button.setLayoutData(gridData);
			}
		}
	}

	public void setFocus() {
	}

}
