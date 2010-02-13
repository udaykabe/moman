package net.deuce.moman.envelope.command;

import net.deuce.moman.envelope.model.Envelope;
import net.deuce.moman.envelope.ui.EnvelopeDialog;
import net.deuce.moman.envelope.ui.EnvelopeView;
import net.deuce.moman.service.ServiceNeeder;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

public class New extends AbstractEnvelopeHandler {

	public static final String ID = "net.deuce.moman.envelope.command.new";

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		Envelope envelope = getEnvelope(window);
		
		if (envelope == null) {
			envelope = ServiceNeeder.instance().getEnvelopeService().getRootEnvelope();
		}
		
		EnvelopeDialog dialog = new EnvelopeDialog(window.getShell());
		dialog.setParent(envelope);
		dialog.create();
		if (dialog.open() == Window.OK) {
			try {
				ServiceNeeder.instance().getEnvelopeService().addEnvelope(dialog.getEnvelope(), envelope);
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPages()[0].showView(EnvelopeView.ID,null,IWorkbenchPage.VIEW_ACTIVATE);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

}