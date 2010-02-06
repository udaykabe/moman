package net.deuce.moman.ui;

import java.io.PrintWriter;
import java.io.StringWriter;

import net.deuce.moman.service.ServiceNeeder;

import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IOperationHistoryListener;
import org.eclipse.core.commands.operations.OperationHistoryEvent;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.ILogger;
import org.eclipse.jface.util.Policy;
import org.eclipse.jface.util.StatusHandler;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "moman";

	// The shared instance
	private static Activator plugin;
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		ServiceNeeder.instance().getServiceContainer().initialize();

		plugin = this;
		
		Policy.setStatusHandler(new StatusHandler() {

			@Override
			public void show(IStatus status, String title) {
				if (status.getException() != null) {
					StringWriter sw = new StringWriter();
					status.getException().printStackTrace(new PrintWriter(sw));
					System.out.println(sw.toString());
				} else {
					System.out.println(status.getMessage());
				}
			}
			
		});
		
		Policy.setLog(new ILogger() {

			@Override
			public void log(IStatus status) {
				if (status.getException() != null) {
					StringWriter sw = new StringWriter();
					status.getException().printStackTrace(new PrintWriter(sw));
					System.out.println(sw.toString());
				} else {
					System.out.println(status.getMessage());
				}
			}
			
		});
		
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
}
