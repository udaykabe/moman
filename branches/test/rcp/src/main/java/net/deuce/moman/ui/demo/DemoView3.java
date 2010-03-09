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

public class DemoView3 extends ViewPart {
	
	public static final String ID = DemoView3.class.getName();
	
	public DemoView3() {
	}

	@Override
	public void createPartControl(Composite parent) {
		String[] images = {
				ISharedImages.IMG_OBJ_FOLDER, ISharedImages.IMG_OBJ_PROJECT, ISharedImages.IMG_OBJ_PROJECT_CLOSED, ISharedImages.IMG_OBJS_BKMRK_TSK, ISharedImages.IMG_OBJS_ERROR_TSK, ISharedImages.IMG_OBJS_INFO_TSK, ISharedImages.IMG_OBJS_TASK_TSK, ISharedImages.IMG_OBJS_WARN_TSK, ISharedImages.IMG_OPEN_MARKER, ISharedImages.IMG_TOOL_BACK,ISharedImages.IMG_TOOL_BACK_DISABLED,ISharedImages.IMG_TOOL_BACK_HOVER,ISharedImages.IMG_TOOL_COPY,ISharedImages.IMG_TOOL_COPY_DISABLED,ISharedImages.IMG_TOOL_COPY_HOVER,ISharedImages.IMG_TOOL_CUT,
		};
		
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		parent.setLayout(layout);
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		
		for (int i=0; i<images.length; i++) {
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
