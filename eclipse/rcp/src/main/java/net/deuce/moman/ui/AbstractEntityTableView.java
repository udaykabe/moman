package net.deuce.moman.ui;

import java.util.List;

import net.deuce.moman.RcpConstants;
import net.deuce.moman.entity.model.AbstractEntity;
import net.deuce.moman.entity.model.EntityEvent;
import net.deuce.moman.entity.model.EntityListener;
import net.deuce.moman.entity.service.EntityService;

import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerEditor;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.ViewPart;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("unchecked")
public abstract class AbstractEntityTableView<E extends AbstractEntity> extends
		ViewPart implements EntityListener<E> {

	private SelectingTableViewer tableViewer;

	private ViewerRegistry viewerRegistry = ViewerRegistry.instance();

	public AbstractEntityTableView() {
	}

	protected abstract SelectingTableViewer createTableViewer(Composite parent);

	protected abstract String getDeleteCommandId();

	abstract protected EntityService<E> getService();

	protected List<E> getEntities() {
		return getService().getOrderedEntities(false);
	}

	protected TableViewer getViewer() {
		return tableViewer;
	}

	protected boolean isSettingServiceViewer() {
		return true;
	}

	abstract protected String getViewerName();

	protected SelectingTableViewer getTableViewer() {
		return tableViewer;
	}

	protected void setTableViewer(SelectingTableViewer tableViewer) {
		this.tableViewer = tableViewer;
	}

	protected Control createTopControl(Composite parent) {
		return null;
	}

	protected boolean hasTopControl() {
		return false;
	}

	public void createPartControl(final Composite parent) {

		getService().addEntityListener(this);

		if (hasTopControl()) {
			GridLayout gridLayout = new GridLayout();
			gridLayout.numColumns = 1;
			parent.setLayout(gridLayout);

			GridData gridData = new GridData();
			gridData.grabExcessHorizontalSpace = true;
			gridData.horizontalAlignment = GridData.FILL;

			Composite topContainer = new Composite(parent, SWT.NONE);
			topContainer.setLayout(new FillLayout(SWT.HORIZONTAL));
			topContainer.setLayoutData(gridData);

			createTopControl(topContainer);
		}

		tableViewer = createTableViewer(parent);

		if (hasTopControl()) {
			GridData gridData = new GridData();
			gridData.grabExcessHorizontalSpace = true;
			gridData.horizontalAlignment = GridData.FILL;
			gridData.grabExcessVerticalSpace = true;
			gridData.verticalAlignment = GridData.FILL;
			tableViewer.getTable().setLayoutData(gridData);
		}

		if (isSettingServiceViewer()) {
			viewerRegistry.registerViewer(getViewerName(), tableViewer);
		}

		tableViewer.getTable().addKeyListener(new KeyAdapter() {

			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.BS && e.stateMask == SWT.COMMAND) {
					IHandlerService handlerService = (IHandlerService) getSite()
							.getService(IHandlerService.class);
					try {
						handlerService.executeCommand(getDeleteCommandId(),
								null);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				} else if (e.keyCode == 'a' && e.stateMask == SWT.COMMAND) {
					tableViewer.getTable().selectAll();
				}
			}
		});

		ColumnViewerEditorActivationStrategy actSupport = createColumnViewerEditorActivationStrategy(tableViewer);
		setupTableViewerEditor(tableViewer, actSupport);

		tableViewer.getTable().setFont(RcpConstants.STANDARD_FONT);
		tableViewer.getTable().setHeaderVisible(getHeaderVisible());
		tableViewer.getTable().setLinesVisible(getLinesVisible());

		refresh();
	}

	protected void setupTableViewerEditor(TableViewer tableViewer,
			ColumnViewerEditorActivationStrategy strategy) {
		TableViewerEditor.create(tableViewer, strategy,
				ColumnViewerEditor.TABBING_HORIZONTAL
						| ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR
						| ColumnViewerEditor.TABBING_VERTICAL
						| ColumnViewerEditor.KEYBOARD_ACTIVATION);

	}

	protected ColumnViewerEditorActivationStrategy createColumnViewerEditorActivationStrategy(
			TableViewer viewer) {
		return new ColumnViewerEditorActivationStrategy(viewer) {
			protected boolean isEditorActivationEvent(
					ColumnViewerEditorActivationEvent event) {
				return event.eventType == ColumnViewerEditorActivationEvent.TRAVERSAL
						|| event.eventType == ColumnViewerEditorActivationEvent.MOUSE_DOUBLE_CLICK_SELECTION
						|| event.eventType == ColumnViewerEditorActivationEvent.PROGRAMMATIC;
			}
		};
	}

	protected boolean getHeaderVisible() {
		return true;
	}

	protected boolean getLinesVisible() {
		return true;
	}

	protected void refresh() {
		BusyIndicator.showWhile(getViewSite().getShell().getDisplay(),
				new Runnable() {

					public void run() {
						tableViewer.setInput(getEntities());
					}
				});
	}

	public void setFocus() {
		tableViewer.getTable().setFocus();
	}

	protected int getNewEntitySelectionColumn() {
		return -1;
	}

	public void entityAdded(EntityEvent<E> event) {
		refresh();
		try {
			setFocus();
			if (event != null && event.getEntity() != null) {
				tableViewer.setSelection(new StructuredSelection(
						new Object[] { event.getEntity() }));
				tableViewer.reveal(event.getEntity());

				int selectionColumn = getNewEntitySelectionColumn();
				if (selectionColumn >= 0) {
					TableItem[] selection = tableViewer.getTable()
							.getSelection();
					if (selection.length > 0) {
						Rectangle bounds = selection[0]
								.getBounds(selectionColumn);
						ViewerCell viewerCell = tableViewer.getCell(new Point(
								bounds.x, bounds.y));
						if (viewerCell != null) {
							tableViewer.activateInitialCellEditor(viewerCell);
						}
					}
				}
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	public void entityChanged(EntityEvent<E> event) {
		if (event == null || event.getEntity() == null) {
			refresh();
		} else {
			tableViewer.refresh(event.getEntity());
		}
	}

	public void entityRemoved(EntityEvent<E> event) {
		refresh();
	}

}
