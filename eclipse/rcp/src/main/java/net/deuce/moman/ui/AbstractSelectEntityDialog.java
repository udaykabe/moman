package net.deuce.moman.ui;

import java.lang.reflect.ParameterizedType;

import net.deuce.moman.entity.model.AbstractEntity;
import net.deuce.moman.entity.service.EntityService;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.EntityCombo;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

@SuppressWarnings("unchecked")
public abstract class AbstractSelectEntityDialog<E extends AbstractEntity>
		extends AbstractModelDialog<E> {

	private EntityCombo<E> entityCombo;

	public AbstractSelectEntityDialog(Shell shell) {
		super(shell);
	}

	public E getEntity() {
		return entityCombo.getEntity();
	}

	protected abstract EntityLabelProvider getEntityLabelProvider();

	protected abstract EntityService<E> getService();

	protected String getEntityTitle() {
		return "Select a "
				+ ((Class<E>) ((ParameterizedType) getClass()
						.getGenericSuperclass()).getActualTypeArguments()[0])
						.getSimpleName() + ":";
	}

	protected Control createDialogArea(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		container.setLayout(layout);

		// The text fields will grow with the size of the dialog
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;

		Label label = new Label(container, SWT.NONE);
		label.setText(getEntityTitle());
		entityCombo = new EntityCombo(container, getService(),
				getEntityLabelProvider());

		return container;
	}

	protected boolean isValidInput() {
		return true;
	}

	protected void saveInput() {
		/*
		 * try {
		 * PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPages()[
		 * 0].showView(AllocationView.ID,null,IWorkbenchPage.VIEW_ACTIVATE);
		 * AllocationSet allocationSet =
		 * ServiceNeeder.instance().getAllocationSetFactory
		 * ().newEntity("Set Name", income); new
		 * CreateEntityOperation<AllocationSet, AllocationSetService>(
		 * allocationSet,
		 * ServiceNeeder.instance().getAllocationSetService()).execute(); }
		 * catch (Exception e) { e.printStackTrace(); throw new
		 * RuntimeException(e.getMessage(), e); }
		 */
	}

}
