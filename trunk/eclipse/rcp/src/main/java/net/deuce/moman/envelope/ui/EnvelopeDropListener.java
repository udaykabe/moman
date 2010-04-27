package net.deuce.moman.envelope.ui;

import net.deuce.moman.entity.ServiceProvider;
import net.deuce.moman.entity.model.account.Account;
import net.deuce.moman.entity.model.envelope.Envelope;
import net.deuce.moman.entity.service.account.AccountService;
import net.deuce.moman.entity.service.envelope.EnvelopeService;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.widgets.Display;

public class EnvelopeDropListener extends EnvelopeDropAdapter {

	private static final int ENVELOPE_TRANSFER = DND.DROP_MOVE;
	private static final int ENVELOPE_MOVE = DND.DROP_COPY;

	private Envelope target;
	private int operation = DND.DROP_NONE;

	private AccountService accountService = ServiceProvider.instance().getAccountService();

	private EnvelopeService envelopeService = ServiceProvider.instance().getEnvelopeService();

	public EnvelopeDropListener(TreeViewer viewer) {
		super(viewer);
	}

	public void drop(DropTargetEvent event) {
		int location = determineLocation(event);
		target = (Envelope) determineTarget(event);
		operation = getCurrentOperation();

		switch (location) {
		case 1:
		case 2:
		case 4:
			target = null;
			break;
		}
		if (operation == DND.DROP_COPY && target.isBill()) {
			target = null;
		}
		super.drop(event);
	}

	public boolean performDrop(Object data) {
		if (target != null) {

			String[] ids;

			switch (operation) {
			case ENVELOPE_TRANSFER:
				ids = (String[]) data;
				if (ids.length != 1) {
					MessageDialog.openError(Display.getCurrent()
							.getActiveShell(), "Error",
							"Select only one envelope to transfer funds.");
				} else {
					Envelope env = envelopeService.findEntity(ids[0]);
					if (env != null && !env.hasChildren()) {

						Account account = null;
						if (accountService.getSelectedAccounts().size() > 0) {
							account = accountService.getSelectedAccounts().get(
									0);
						} else {
							account = accountService.getOrderedEntities(false)
									.get(0);
						}
						EnvelopeTransferDialog dialog = new EnvelopeTransferDialog(
								Display.getCurrent().getActiveShell(), account,
								account, env, target);
						try {
							dialog.create();
							dialog.open();
						} catch (Throwable t) {
							t.printStackTrace();
						}
					}
				}
				break;
			case ENVELOPE_MOVE:
				ids = (String[]) data;
				for (String id : ids) {
					Envelope env = envelopeService.findEntity(id);
					if (env != null && env.getParent() != target) {
						env.getParent().removeChild(env);
						env.setParent(target);
						target.addChild(env);
					}
				}
				break;
			}
			return true;
		}
		return false;
	}

	public boolean validateDrop(Object target, int operation,
			TransferData transferType) {
		return true;

	}

}
