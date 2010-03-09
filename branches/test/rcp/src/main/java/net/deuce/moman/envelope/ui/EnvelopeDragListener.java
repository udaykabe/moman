package net.deuce.moman.envelope.ui;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.deuce.moman.envelope.model.Envelope;

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
		if (EnvelopeTransfer.getInstance().isSupportedType(event.dataType)) {
			IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
			
			List<String> envelopeIdList = new LinkedList<String>();
			
			Iterator<Envelope> itr = selection.iterator();
			for (int i=0; itr.hasNext(); i++) {
				Envelope env = itr.next();
				if (!env.hasChildren()) {
					envelopeIdList.add(env.getId());
				}
			}
			
			String[] envelopeIds = new String[envelopeIdList.size()];
			for (int i=0; i<envelopeIdList.size(); i++) {
				envelopeIds[i] = envelopeIdList.get(i);
			}
		
			event.data = envelopeIds;
		}
	}

	@Override
	public void dragStart(DragSourceEvent event) {
	}

}
