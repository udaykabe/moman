package net.deuce.moman.menu;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.deuce.moman.entity.ServiceProvider;
import net.deuce.moman.entity.service.ServiceManager;
import net.deuce.moman.entity.service.preference.PreferenceService;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.CompoundContributionItem;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;
import org.eclipse.ui.services.IServiceLocator;
import org.springframework.beans.factory.annotation.Autowired;

public class RecentlyOpenedFilesMenu extends CompoundContributionItem {

	private ServiceManager serviceManager = ServiceProvider.instance().getServiceManager();

	private PreferenceService preferenceService = ServiceProvider.instance().getPreferenceService();

	public RecentlyOpenedFilesMenu() {
	}

	public RecentlyOpenedFilesMenu(String id) {
		super(id);
	}

	public void setId(String itemId) {
		super.setId(itemId);
	}

	protected IContributionItem[] getContributionItems() {
		File currentFile = serviceManager.getActiveFile();
		List<String> list = preferenceService.getRecentlyOpenedFiles();
		List<File> fileList = new LinkedList<File>();

		// prune list
		preferenceService.clearRecentlyOpenedFiles();
		for (String s : list) {
			File f = new File(s);
			if (f.exists()) {
				fileList.add(f);
			}
		}

		CommandContributionItemParameter parameter;
		IContributionItem[] items = new IContributionItem[fileList.size()];

		Map<String, String> params = new HashMap<String, String>();
		File file;

		IServiceLocator serviceLocator = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();

		int pos = 1;
		for (int i = 0; i < fileList.size(); i++) {
			file = fileList.get(i);

			if (!file.equals(currentFile)) {
				preferenceService
						.addRecentlyOpenedFiles(file.getAbsolutePath());

				params.clear();
				params.put("net.deuce.moman.command.file.openRecent.fileParam",
						file.getAbsolutePath());

				parameter = new CommandContributionItemParameter(
						serviceLocator, "net.deuce.moman.menu.file.recent"
								+ (pos++),
						"net.deuce.moman.command.file.openRecent", params,
						null, null, null, (i + 1) + " " + file.getName(), null,
						null, CommandContributionItem.STYLE_PUSH, null, false);

				IContributionItem item = new CommandContributionItem(parameter);
				items[i] = item;
			}

		}
		return items;
	}

}
