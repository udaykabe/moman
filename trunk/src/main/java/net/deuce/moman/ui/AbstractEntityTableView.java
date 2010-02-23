package net.deuce.moman.ui;

import java.util.List;

import net.deuce.moman.Constants;
import net.deuce.moman.model.AbstractEntity;
import net.deuce.moman.model.EntityEvent;
import net.deuce.moman.model.EntityListener;
import net.deuce.moman.service.EntityService;

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

@SuppressWarnings("unchecked")
public abstract class AbstractEntityTableView<E extends AbstractEntity> extends ViewPart
implements EntityListener<E> {
	
	private SelectingTableViewer tableViewer;
	private EntityService<E> service;

	public AbstractEntityTableView(EntityService<E> service) {
		this.service = service;
		service.addEntityListener(this);
	}
	
	protected abstract SelectingTableViewer createTableViewer(Composite parent);
	protected abstract String getDeleteCommandId();
	
	public EntityService<E> getService() {
		return service;
	}
	
	protected List<E> getEntities() {
		return service.getOrderedEntities(false);
	}
	
	protected TableViewer getViewer() {
		return tableViewer;
	}
	
	protected boolean isSettingServiceViewer() {
		return true;
	}

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

	@Override
	public void createPartControl(final Composite parent) {
		
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
			service.setViewer(tableViewer);
		}
		
 		tableViewer.getTable().addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.BS && e.stateMask == SWT.COMMAND) {
					IHandlerService handlerService = (IHandlerService) getSite().getService(IHandlerService.class);
					try {
						handlerService.executeCommand(getDeleteCommandId(), null);
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
		
 		tableViewer.getTable().setFont(Constants.STANDARD_FONT);
 		tableViewer.getTable().setHeaderVisible(getHeaderVisible());
 		tableViewer.getTable().setLinesVisible(getLinesVisible());
 		
	    refresh();
	}
	
	protected void setupTableViewerEditor(TableViewer tableViewer, ColumnViewerEditorActivationStrategy strategy) {
		TableViewerEditor.create(tableViewer, strategy,
				ColumnViewerEditor.TABBING_HORIZONTAL
						| ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR
						| ColumnViewerEditor.TABBING_VERTICAL
						| ColumnViewerEditor.KEYBOARD_ACTIVATION);
   
	}
	
	protected ColumnViewerEditorActivationStrategy createColumnViewerEditorActivationStrategy(TableViewer viewer) {
 		return new ColumnViewerEditorActivationStrategy(viewer) {
			protected boolean isEditorActivationEvent(ColumnViewerEditorActivationEvent event) {
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
		BusyIndicator.showWhile(getViewSite().getShell().getDisplay(), new Runnable() {
			@Override
			public void run() {
				tableViewer.setInput(getEntities());
			}
		});
	}

	@Override
	public void setFocus() {
		tableViewer.getTable().setFocus();
	}
	
	protected int getNewEntitySelectionColumn() {
		return -1;
	}
	
	@Override
	public void entityAdded(EntityEvent<E> event) {
		refresh();
		try {
			setFocus();
			if (event != null && event.getEntity() != null) {
				tableViewer.setSelection(new StructuredSelection(new Object[]{event.getEntity()}));
				tableViewer.reveal(event.getEntity());
				
				int selectionColumn = getNewEntitySelectionColumn();
				if (selectionColumn >= 0) {
					TableItem[] selection = tableViewer.getTable().getSelection();
					if (selection.length > 0) {
						Rectangle bounds = selection[0].getBounds(selectionColumn);
						ViewerCell viewerCell = tableViewer.getCell(new Point(bounds.x, bounds.y));
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

	@Override
	public void entityChanged(EntityEvent<E> event) {
		if (event == null || event.getEntity() == null) {
			refresh();
		} else {
			tableViewer.refresh(event.getEntity());
		}
	}

	@Override
	public void entityRemoved(EntityEvent<E> event) {
		refresh();
	}

}
