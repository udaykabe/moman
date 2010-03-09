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

public class DemoView5 extends ViewPart {

	public static final String ID = DemoView5.class.getName();

	public DemoView5() {
	}

	@Override
	public void createPartControl(Composite parent) {
		String[] images = { ISharedImages.IMG_TOOL_REDO_HOVER,
				ISharedImages.IMG_TOOL_UNDO,
				ISharedImages.IMG_TOOL_UNDO_DISABLED,
				ISharedImages.IMG_TOOL_UNDO_HOVER, ISharedImages.IMG_TOOL_UP,
				ISharedImages.IMG_TOOL_UP_DISABLED,
				ISharedImages.IMG_TOOL_UP_HOVER,
				ISharedImages.IMG_OBJS_DND_LEFT_SOURCE,
				ISharedImages.IMG_OBJS_DND_LEFT_MASK,
				ISharedImages.IMG_OBJS_DND_RIGHT_SOURCE,
				ISharedImages.IMG_OBJS_DND_RIGHT_MASK,
				ISharedImages.IMG_OBJS_DND_TOP_SOURCE,
				ISharedImages.IMG_OBJS_DND_TOP_MASK,
				ISharedImages.IMG_OBJS_DND_BOTTOM_SOURCE,
				ISharedImages.IMG_OBJS_DND_BOTTOM_MASK,
				ISharedImages.IMG_OBJS_DND_INVALID_SOURCE, };

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
