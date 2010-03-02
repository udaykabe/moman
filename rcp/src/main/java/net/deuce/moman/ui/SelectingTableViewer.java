package net.deuce.moman.ui;

import java.util.List;

import net.deuce.moman.service.ServiceNeeder;

import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;

public class SelectingTableViewer extends TableViewer {

	public SelectingTableViewer(Composite parent, int style) {
		super(parent, style);
	}

	public SelectingTableViewer(Table table) {
		super(table);
	}

	public SelectingTableViewer(Composite parent) {
		super(parent);
	}

	public void activateInitialCellEditor(ViewerCell cell) {
		ColumnViewerEditorActivationEvent event = new ColumnViewerEditorActivationEvent(cell);
		triggerEditorActivationEvent(event);
	}
	
	@Override
	protected void triggerEditorActivationEvent(
			ColumnViewerEditorActivationEvent event) {
		List<String> ids = ServiceNeeder.instance().getServiceContainer().startQueuingNotifications();
		try {
			super.triggerEditorActivationEvent(event);
		} finally {
			ServiceNeeder.instance().getServiceContainer().stopQueuingNotifications(ids);
		}
	}
}
