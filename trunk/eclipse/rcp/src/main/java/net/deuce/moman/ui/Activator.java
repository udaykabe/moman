package net.deuce.moman.ui;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.ILogger;
import org.eclipse.jface.util.Policy;
import org.eclipse.jface.util.StatusHandler;
import org.eclipse.swt.graphics.Image;
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
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);

		plugin = this;

		Policy.setStatusHandler(new StatusHandler() {

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
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
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
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static Image getImage(String path) {
		URL imageURL = getDefault().getBundle().getEntry(path);
		ImageDescriptor descriptor = ImageDescriptor.createFromURL(imageURL);
		return descriptor.createImage();
	}
}
