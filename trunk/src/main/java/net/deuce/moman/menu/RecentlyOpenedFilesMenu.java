package net.deuce.moman.menu;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.deuce.moman.service.ServiceContainer;
import net.deuce.moman.service.ServiceNeeder;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.CompoundContributionItem;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;
import org.eclipse.ui.services.IServiceLocator;

public class RecentlyOpenedFilesMenu extends CompoundContributionItem {

	public RecentlyOpenedFilesMenu() {
		System.out.println();
	}

	public RecentlyOpenedFilesMenu(String id) {
		super(id);
	}
	
	

	@Override
	public void setId(String itemId) {
		super.setId(itemId);
	}

	@Override
	protected IContributionItem[] getContributionItems() {
		ServiceContainer serviceContainer =  ServiceNeeder.instance().getServiceContainer();
		File currentFile = serviceContainer.getActiveFile();
		List<String> list = serviceContainer.getRecentlyOpenedFiles();
		List<File> fileList = new LinkedList<File>();
		
		// prune list
		serviceContainer.clearRecentlyOpenedFiles();
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
		
		IServiceLocator serviceLocator = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        
		int pos = 1;
		for (int i=0; i<fileList.size(); i++) {
			file = fileList.get(i);
			
			if (!file.equals(currentFile)) {
				serviceContainer.addRecentlyOpenedFiles(file.getAbsolutePath());
				
				params.clear();
		        params.put("net.deuce.moman.command.file.openRecent.fileParam", file.getAbsolutePath());
		        
		        parameter = new CommandContributionItemParameter(
		        		serviceLocator, "net.deuce.moman.menu.file.recent"+(pos++),
						"net.deuce.moman.command.file.openRecent",
						params, null, null, null, (i+1) + " " + file.getName(),
						null, null, CommandContributionItem.STYLE_PUSH, null, false);
		        
				IContributionItem item = new CommandContributionItem(parameter);
				items[i] = item;
			}
			
		}
		return items;
	}

}
