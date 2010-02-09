package net.deuce.moman.rcp.envelope;

import java.util.Iterator;

import net.deuce.moman.model.envelope.Envelope;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;

public class EnvelopeDragListener implements DragSourceListener {
	
	private TreeViewer viewer;
	
	public EnvelopeDragListener(TreeViewer viewer) {
		super();
		this.viewer = viewer;
	}

	@Override
	public void dragFinished(DragSourceEvent event) {
	}

	@SuppressWarnings("unchecked")
	@Override
	public void dragSetData(DragSourceEvent event) {
		IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
		
		String[] envelopeIds = new String[selection.size()];
		Iterator<Envelope> itr = selection.iterator();
		for (int i=0; itr.hasNext(); i++) {
			envelopeIds[i] = itr.next().getId();
		}
		
		if (EnvelopeTransfer.getInstance().isSupportedType(event.dataType)) {
			event.data = envelopeIds;
		}
	}

	@Override
	public void dragStart(DragSourceEvent event) {
	}

}
