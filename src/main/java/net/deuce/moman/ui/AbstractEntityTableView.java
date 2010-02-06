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
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.ViewPart;

@SuppressWarnings("unchecked")
public abstract class AbstractEntityTableView<E extends AbstractEntity> extends ViewPart
implements EntityListener<E> {
	
	private TableViewer tableViewer;
	private boolean editingEntity;
	private EntityService<E> service;

	public AbstractEntityTableView(EntityService<E> service) {
		this.service = service;
		service.addEntityListener(this);
	}
	
	protected abstract TableViewer createTableViewer(Composite parent);
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

	protected int[] getDoubleClickableColumns() {
		return new int[0];
	}
	
	protected void doubleClickHandler(int column, StructuredSelection selection, Shell shell) {
	}
	
	protected IDoubleClickListener getDoubleClickListener(final Shell shell) {
		return new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				int[] columns = getDoubleClickableColumns();
				if (columns.length == 0) return;
				if (editingEntity) return;
				editingEntity = true;
				
				try {
					Point cursorLocation = Display.getCurrent().getCursorLocation();
					Rectangle tableBounds = tableViewer.getTable().getParent().getParent().getBounds();
					Rectangle shellBounds = Display.getCurrent().getActiveShell().getBounds();
					
					int x = cursorLocation.x;
					
					for (int i=0; i<columns.length; i++) {
						Rectangle bounds = tableViewer.getTable().getItem(0).getBounds(columns[i]);
						int minThreshold = tableBounds.x+shellBounds.x+bounds.x;
						int maxThreshold = tableBounds.x+shellBounds.x+bounds.x+bounds.width;
				
						if (x >= minThreshold && x <= maxThreshold) {
							StructuredSelection selection = (StructuredSelection)tableViewer.getSelection();
							doubleClickHandler(columns[i], selection, shell);
						}
					}
				} finally {
					editingEntity = false;
				}
				
			}

		};
	}
	
	@Override
	public void createPartControl(final Composite parent) {
		
		tableViewer = createTableViewer(parent);
		
		if (isSettingServiceViewer()) {
			service.setViewer(tableViewer);
		}
		
		tableViewer.addDoubleClickListener(getDoubleClickListener(parent.getShell()));
				
 		tableViewer.getTable().addKeyListener(new KeyListener() {
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

			@Override
			public void keyReleased(KeyEvent e) {
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

	@Override
	public void entityAdded(EntityEvent<E> event) {
		refresh();
		try {
			setFocus();
			if (event != null && event.getEntity() != null) {
				tableViewer.setSelection(new StructuredSelection(new Object[]{event.getEntity()}));
				tableViewer.reveal(event.getEntity());
			} else {
				refresh();
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
