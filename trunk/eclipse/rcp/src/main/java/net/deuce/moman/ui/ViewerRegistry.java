package net.deuce.moman.ui;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.viewers.StructuredViewer;

public class ViewerRegistry {
	
	private static ViewerRegistry __instance = new ViewerRegistry();
	
	public static ViewerRegistry instance() { return __instance; }

	private Map<String, StructuredViewer> viewers = new HashMap<String, StructuredViewer>();

	private ViewerRegistry() {
	}

	public void registerViewer(String name, StructuredViewer viewer) {
		viewers.put(name, viewer);
	}

	public StructuredViewer getViewer(String name) {
		return viewers.get(name);
	}
}
