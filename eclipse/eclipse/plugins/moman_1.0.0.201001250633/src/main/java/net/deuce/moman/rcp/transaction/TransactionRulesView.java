package net.deuce.moman.rcp.transaction;

import net.deuce.moman.command.importer.Delete;
import net.deuce.moman.model.EntityEvent;
import net.deuce.moman.model.EntityListener;
import net.deuce.moman.model.Registry;
import net.deuce.moman.model.envelope.Envelope;
import net.deuce.moman.model.rules.Rule;
import net.deuce.moman.rcp.envelope.EnvelopeSelectionDialog;

import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TableViewerEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.ViewPart;

public class TransactionRulesView extends ViewPart implements EntityListener<Rule> {
	
	public static final String ID = TransactionRulesView.class.getName();
	
	private TableViewer rulesViewer;
	private boolean editingEnvelope;

	public TransactionRulesView() {
		Registry.instance().addTransactionRulesListener(this);
	}

	@Override
	public void createPartControl(final Composite parent) {
		rulesViewer = new TableViewer(parent, SWT.SINGLE | SWT.V_SCROLL | SWT.FULL_SELECTION);
		
		rulesViewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				if (editingEnvelope) return;
				editingEnvelope = true;
				
				Point cursorLocation = Display.getCurrent().getCursorLocation();
				Rectangle tableBounds = rulesViewer.getTable().getParent().getParent().getBounds();
				Rectangle bounds = rulesViewer.getTable().getItem(0).getBounds(4);
				Rectangle shellBounds = Display.getCurrent().getActiveShell().getBounds();
				
				int x = cursorLocation.x;
				int minThreshold = tableBounds.x+shellBounds.x+bounds.x;
				int maxThreshold = tableBounds.x+shellBounds.x+bounds.x+bounds.width;
				
				if (x >= minThreshold && x <= maxThreshold) {
					StructuredSelection selection = (StructuredSelection)rulesViewer.getSelection();
					Rule rule = (Rule)selection.getFirstElement();
					Envelope envelope = rule.getEnvelope();
					EnvelopeSelectionDialog dialog = new EnvelopeSelectionDialog(parent.getShell(), envelope);
					
					dialog.create();
					dialog.open();
					if (envelope != dialog.getEnvelope()) {
						rule.setEnvelope(dialog.getEnvelope());
						rulesViewer.refresh(rule);
					}
				}
				editingEnvelope = false;
				
			}

		});
				
 		rulesViewer.getTable().addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.BS && e.stateMask == SWT.COMMAND) {
					IHandlerService handlerService = (IHandlerService) getSite().getService(IHandlerService.class);
					try {
						handlerService.executeCommand(Delete.ID, null);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}
 		});
 		
 		ColumnViewerEditorActivationStrategy actSupport = new ColumnViewerEditorActivationStrategy(rulesViewer) {
			protected boolean isEditorActivationEvent(ColumnViewerEditorActivationEvent event) {
				return event.eventType == ColumnViewerEditorActivationEvent.TRAVERSAL
						|| event.eventType == ColumnViewerEditorActivationEvent.MOUSE_DOUBLE_CLICK_SELECTION
						|| event.eventType == ColumnViewerEditorActivationEvent.PROGRAMMATIC;
			}
		};
		
		TableViewerEditor.create(rulesViewer, actSupport,
				ColumnViewerEditor.TABBING_HORIZONTAL
						| ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR
						| ColumnViewerEditor.TABBING_VERTICAL
						| ColumnViewerEditor.KEYBOARD_ACTIVATION);
   
        TableViewerColumn column = new TableViewerColumn(rulesViewer, SWT.CENTER);
 		column.getColumn().setText("Enabled");
 		column.getColumn().setAlignment(SWT.LEFT);
 	    column.getColumn().setWidth(50);
 	    column.setEditingSupport(new RuleEditingSupport(rulesViewer, 0));
		
        column = new TableViewerColumn(rulesViewer, SWT.LEFT);
 		column.getColumn().setText("Condition");
 	    column.getColumn().setWidth(100);
 	    column.setEditingSupport(new RuleEditingSupport(rulesViewer, 1));
		
        column = new TableViewerColumn(rulesViewer, SWT.CENTER);
 		column.getColumn().setText("Expression");
 		column.getColumn().setAlignment(SWT.LEFT);
 	    column.getColumn().setWidth(200);
 	    column.setEditingSupport(new RuleEditingSupport(rulesViewer, 2));
		
        column = new TableViewerColumn(rulesViewer, SWT.CENTER);
 		column.getColumn().setText("Converted Value");
 		column.getColumn().setAlignment(SWT.LEFT);
 	    column.getColumn().setWidth(200);
 	    column.setEditingSupport(new RuleEditingSupport(rulesViewer, 3));
		
 		column = new TableViewerColumn(rulesViewer, SWT.LEFT);
 		column.getColumn().setText("Use This Envelope");
 	    column.getColumn().setWidth(400);
		
 		rulesViewer.getTable().setFont(Registry.instance().getStandardFont());
 		rulesViewer.getTable().setHeaderVisible(true);
 		rulesViewer.getTable().setLinesVisible(true);
 		
	    rulesViewer.setContentProvider(new RuleContentProvider());
	    rulesViewer.setLabelProvider(new RuleLabelProvider());
	    rulesViewer.setInput(Registry.instance().getTransactionRules());
	}
	
	private void refresh() {
		rulesViewer.setInput(Registry.instance().getTransactionRules());
	}

	@Override
	public void setFocus() {
	}

	@Override
	public void entityAdded(EntityEvent<Rule> event) {
		refresh();
	}

	@Override
	public void entityChanged(EntityEvent<Rule> event) {
		refresh();
	}

	@Override
	public void entityRemoved(EntityEvent<Rule> event) {
		refresh();
	}

}
