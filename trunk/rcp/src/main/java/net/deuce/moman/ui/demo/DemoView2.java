package net.deuce.moman.ui.demo;

import net.deuce.moman.ui.Activator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.part.ViewPart;

public class DemoView2 extends ViewPart {

	public static final String ID = DemoView2.class.getName();

	public DemoView2() {
	}

	public void createPartControl(Composite parent) {
		String[] images = { ISharedImages.IMG_ETOOL_DELETE,
				ISharedImages.IMG_ETOOL_DELETE_DISABLED,
				ISharedImages.IMG_ETOOL_HOME_NAV,
				ISharedImages.IMG_ETOOL_HOME_NAV_DISABLED,
				ISharedImages.IMG_ETOOL_PRINT_EDIT,
				ISharedImages.IMG_ETOOL_PRINT_EDIT_DISABLED,
				ISharedImages.IMG_ETOOL_SAVE_EDIT,
				ISharedImages.IMG_ETOOL_SAVE_EDIT_DISABLED,
				ISharedImages.IMG_ETOOL_SAVEALL_EDIT,
				ISharedImages.IMG_ETOOL_SAVEALL_EDIT_DISABLED,
				ISharedImages.IMG_ETOOL_SAVEAS_EDIT,
				ISharedImages.IMG_ETOOL_SAVEAS_EDIT_DISABLED,
				ISharedImages.IMG_LCL_LINKTO_HELP, ISharedImages.IMG_OBJ_ADD,
				ISharedImages.IMG_OBJ_ELEMENT, ISharedImages.IMG_OBJ_FILE, };

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
