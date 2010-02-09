package net.deuce.moman.rcp.envelope;

import net.deuce.moman.model.Registry;
import net.deuce.moman.model.envelope.Bill;
import net.deuce.moman.model.envelope.Envelope;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TransferData;

public class EnvelopeDropListener extends ViewerDropAdapter {

	private Envelope target;

	public EnvelopeDropListener(TreeViewer viewer) {
		super(viewer);
	}
	
	@Override
	public void drop(DropTargetEvent event) {
		int location = this.determineLocation(event);
		target = (Envelope) determineTarget(event);
		switch (location) {
		case 1:
		case 2:
		case 4:
			target = null;
			break;
		}
		if (target instanceof Bill) {
			target = null;
		}
		super.drop(event);
	}

	@Override
	public boolean performDrop(Object data) {
		if (target != null) {
			String[] ids = (String[])data;
			for (int i=0; i<ids.length; i++) {
				Envelope env = Registry.instance().findEnvelope(ids[i]);
				if (env != null) {
					env.getParent().removeChild(env);
					env.setParent(target);
					target.addChild(env);
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean validateDrop(Object target, int operation,
			TransferData transferType) {
		return true;

	}

}
