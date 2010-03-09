package net.deuce.moman.command;

import java.util.List;

import net.deuce.moman.model.AbstractEntity;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.ui.IWorkbenchWindow;

@SuppressWarnings("unchecked")
public abstract class AbstractEntityHandler<E extends AbstractEntity> extends AbstractHandler {
	
	private boolean multiSelection = false;
	private boolean promptOnEmptySelection = true;
	
	public AbstractEntityHandler(boolean multiSelection) {
		this.multiSelection = multiSelection;
	}
	
	public boolean isMultiSelection() {
		return multiSelection;
	}

	public void setMultiSelection(boolean multiSelection) {
		this.multiSelection = multiSelection;
	}
	
	protected String getEmptySelectionTitle() {
		return "Select an entity";
	}
	
	protected String getEmptySelectionMessage() {
		return "Please select one entity.";
	}
	
	protected String getMultiSelectionMessage() {
		return "Please select only one entity.";
	}

	public List<E> getEntities(IWorkbenchWindow window, TableViewer viewer) {
		ISelection selection = viewer.getSelection();
		if (!(selection instanceof StructuredSelection)) return null;
		
		StructuredSelection ss = (StructuredSelection)selection;
		if (ss.size() == 0) {
			if (promptOnEmptySelection) {
				MessageDialog.openInformation(window.getShell(), getEmptySelectionTitle(), getEmptySelectionMessage());
				return null;
			}
		}
		
		if (!multiSelection && ss.size() > 1) {
			MessageDialog.openError(window.getShell(), "Error", getMultiSelectionMessage());
			return null;
		}
	
		return ss.toList();
	}

}
