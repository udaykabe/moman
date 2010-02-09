package net.deuce.moman.command.envelope;

import net.deuce.moman.model.Registry;
import net.deuce.moman.model.envelope.Envelope;
import net.deuce.moman.rcp.envelope.EnvelopeDialog;
import net.deuce.moman.rcp.envelope.EnvelopeView;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

public class New extends AbstractEnvelopeHandler {

	public static final String ID = "net.deuce.moman.command.envelope.new";

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		Envelope envelope = getEnvelope(window);
		
		if (envelope == null) {
			envelope = Registry.instance().getRootEnvelope();
		}
		
		EnvelopeDialog dialog = new EnvelopeDialog(window.getShell());
		dialog.setParent(envelope);
		dialog.create();
		if (dialog.open() == Window.OK) {
			try {
				Registry.instance().addEnvelope(dialog.getEnvelope(), envelope);
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPages()[0].showView(EnvelopeView.ID,null,IWorkbenchPage.VIEW_ACTIVATE);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

}