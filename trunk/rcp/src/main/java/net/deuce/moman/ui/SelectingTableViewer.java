package net.deuce.moman.ui;

import java.util.List;

import net.deuce.moman.entity.ServiceProvider;
import net.deuce.moman.entity.service.ServiceManager;

import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.springframework.beans.factory.annotation.Autowired;

public class SelectingTableViewer extends TableViewer {

	private ServiceManager serviceManager = ServiceProvider.instance().getServiceManager();

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
		ColumnViewerEditorActivationEvent event = new ColumnViewerEditorActivationEvent(
				cell);
		triggerEditorActivationEvent(event);
	}

	protected void triggerEditorActivationEvent(
			ColumnViewerEditorActivationEvent event) {
		List<String> ids = serviceManager.startQueuingNotifications();
		try {
			super.triggerEditorActivationEvent(event);
		} finally {
			serviceManager.stopQueuingNotifications(ids);
		}
	}
}
