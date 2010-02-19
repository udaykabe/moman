package net.deuce.moman.envelope.ui;

import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;

public class EnvelopeSelectionTreeViewer extends TreeViewer {

	public EnvelopeSelectionTreeViewer(Composite parent, int style) {
		super(parent, style);
	}

	public EnvelopeSelectionTreeViewer(Composite parent) {
		super(parent);
	}

	public EnvelopeSelectionTreeViewer(Tree tree) {
		super(tree);
	}

	@Override
	protected void triggerEditorActivationEvent(
			ColumnViewerEditorActivationEvent event) {
//		super.triggerEditorActivationEvent(event);
	}
}
