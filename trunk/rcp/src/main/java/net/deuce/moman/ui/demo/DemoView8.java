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

public class DemoView8 extends ViewPart {

	public static final String ID = DemoView8.class.getName();

	public DemoView8() {
	}

	public void createPartControl(Composite parent) {
		String[] images = { IWorkbenchGraphicConstants.IMG_LCL_BUTTON_MENU,
				IWorkbenchGraphicConstants.IMG_LCL_SELECTED_MODE,
				IWorkbenchGraphicConstants.IMG_LCL_SHOWCHILD_MODE,
				IWorkbenchGraphicConstants.IMG_LCL_SHOWSYNC_RN,
				IWorkbenchGraphicConstants.IMG_WIZBAN_NEW_WIZ,
				IWorkbenchGraphicConstants.IMG_WIZBAN_EXPORT_WIZ,
				IWorkbenchGraphicConstants.IMG_WIZBAN_IMPORT_WIZ,
				IWorkbenchGraphicConstants.IMG_WIZBAN_EXPORT_PREF_WIZ,
				IWorkbenchGraphicConstants.IMG_WIZBAN_IMPORT_PREF_WIZ,
				IWorkbenchGraphicConstants.IMG_WIZBAN_WORKINGSET_WIZ,
				IWorkbenchGraphicConstants.IMG_VIEW_DEFAULTVIEW_MISC, };

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
