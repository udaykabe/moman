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

public class DemoView extends ViewPart {

	public static final String ID = DemoView.class.getName();

	public DemoView() {
	}

	public void createPartControl(Composite parent) {
		String[] images = { ISharedImages.IMG_DEC_FIELD_ERROR,
				ISharedImages.IMG_DEC_FIELD_WARNING,
				ISharedImages.IMG_DEF_VIEW, ISharedImages.IMG_ELCL_COLLAPSEALL,
				ISharedImages.IMG_ELCL_COLLAPSEALL_DISABLED,
				ISharedImages.IMG_ELCL_REMOVE,
				ISharedImages.IMG_ELCL_REMOVE_DISABLED,
				ISharedImages.IMG_ELCL_REMOVEALL,
				ISharedImages.IMG_ELCL_REMOVEALL_DISABLED,
				ISharedImages.IMG_ELCL_STOP,
				ISharedImages.IMG_ELCL_STOP_DISABLED,
				ISharedImages.IMG_ELCL_SYNCED,
				ISharedImages.IMG_ELCL_SYNCED_DISABLED,
				ISharedImages.IMG_ETOOL_CLEAR,
				ISharedImages.IMG_ETOOL_CLEAR_DISABLED,
				ISharedImages.IMG_ETOOL_DEF_PERSPECTIVE };

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
