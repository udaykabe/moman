package net.deuce.moman.transaction.ui;

import java.util.List;

import net.deuce.moman.entity.ServiceProvider;
import net.deuce.moman.entity.model.transaction.InternalTransaction;
import net.deuce.moman.entity.model.transaction.Split;
import net.deuce.moman.entity.model.transaction.SplitSelectionHandler;
import net.deuce.moman.entity.service.ServiceManager;
import net.deuce.moman.envelope.ui.SplitSelectionDialog;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;
import org.springframework.beans.factory.annotation.Autowired;

public class SwtSplitSelectionHandler implements SplitSelectionHandler {

	private ServiceManager serviceManager = ServiceProvider.instance().getServiceManager();

	public boolean handleSplitSelection(final InternalTransaction transaction,
			double newAmount, List<Split> split) {
		final SplitSelectionDialog dialog = new SplitSelectionDialog(Display
				.getCurrent().getActiveShell(), newAmount, split);

		dialog.setAllowBills(true);
		dialog.create();
		int status = dialog.open();
		final List<Split> result = dialog.getSplit();
		if (status == Window.OK) {
			if (!split.equals(result)) {
				BusyIndicator.showWhile(Display.getCurrent(), new Runnable() {
					public void run() {
						List<String> ids = serviceManager
								.startQueuingNotifications();
						try {
							transaction.clearSplit();

							for (Split item : result) {
								if (transaction.getAmount() < 0.0) {
									item.setAmount(-item.getAmount());
								}
								transaction.addSplit(item, true);
							}
						} finally {
							serviceManager.stopQueuingNotifications(ids);
						}
					}
				});
			}
			return true;
		} else {
			return false;
		}
	}

}
