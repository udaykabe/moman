package net.deuce.moman.allocation.ui;

import java.util.Iterator;

import net.deuce.moman.allocation.model.Allocation;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;

public class AllocationDragListener implements DragSourceListener {
	
	private TableViewer viewer;
	
	public AllocationDragListener(TableViewer viewer) {
		super();
		this.viewer = viewer;
	}

	@Override
	public void dragFinished(DragSourceEvent event) {
	}

	@SuppressWarnings("unchecked")
	@Override
	public void dragSetData(DragSourceEvent event) {
		if (AllocationTransfer.getInstance().isSupportedType(event.dataType)) {
			IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
			
			int[] ids = new int[selection.size()];
			
			Iterator<Allocation> itr = selection.iterator();
			for (int i=0; itr.hasNext(); i++) {
				Allocation allocation = itr.next();
				ids[i] = allocation.getIndex().byteValue();
			}
			
			System.out.println("ZZZ drag index: " + ids[0]);
			event.data = ids;
		}
	}

	@Override
	public void dragStart(DragSourceEvent event) {
	}

}
