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

public class DemoView4 extends ViewPart {

	public static final String ID = DemoView4.class.getName();

	public DemoView4() {
	}

	@Override
	public void createPartControl(Composite parent) {
		String[] images = { ISharedImages.IMG_TOOL_CUT_DISABLED,
				ISharedImages.IMG_TOOL_CUT_HOVER,
				ISharedImages.IMG_TOOL_DELETE,
				ISharedImages.IMG_TOOL_DELETE_DISABLED,
				ISharedImages.IMG_TOOL_DELETE_HOVER,
				ISharedImages.IMG_TOOL_FORWARD,
				ISharedImages.IMG_TOOL_FORWARD_DISABLED,
				ISharedImages.IMG_TOOL_FORWARD_HOVER,
				ISharedImages.IMG_TOOL_NEW_WIZARD,
				ISharedImages.IMG_TOOL_NEW_WIZARD_DISABLED,
				ISharedImages.IMG_TOOL_NEW_WIZARD_HOVER,
				ISharedImages.IMG_TOOL_PASTE,
				ISharedImages.IMG_TOOL_PASTE_DISABLED,
				ISharedImages.IMG_TOOL_PASTE_HOVER,
				ISharedImages.IMG_TOOL_REDO,
				ISharedImages.IMG_TOOL_REDO_DISABLED, };

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

	@Override
	public void setFocus() {
	}

}
