package org.mifos.server.tray;

import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;

/**
 * System Tray helper class.
 * 
 * @author Michael Vorburger
 */
public class Tray {

	protected SystemTray systemTray;
	protected TrayIcon trayIcon;

	/**
	 * Intentionally just silently fails if Tray could not be set up.
	 */
	public void init(String imageResourcePath) {
		try {
			initWhichThrowsAllProblems(imageResourcePath);
		} catch (Throwable t) {
			quit();
			systemTray = null;
		}
	}

	protected void initWhichThrowsAllProblems(String imageResourcePath) throws Throwable {
		if (!SystemTray.isSupported())
			return;

		systemTray = SystemTray.getSystemTray();

		URL imageURL = getClass().getResource(imageResourcePath);
		if (imageURL == null)
			throw new IOException("Could not find image resource on classpath: " + imageResourcePath);
		Image image = Toolkit.getDefaultToolkit().createImage(imageURL);
		trayIcon = new TrayIcon(image);
		trayIcon.setImageAutoSize(true);
		
//		PopupMenu popup = new PopupMenu();
//		configureMenu(popup);
//		if (popup.getItemCount() > 0)
//			trayIcon.setPopupMenu(popup);
		
		//configureIconClickListener();
		
		systemTray.add(trayIcon);

		// EventQueue.invokeAndWait(runnable);
	}

//	protected static void addMenuItem(PopupMenu popup, String label, ActionListener actionListener) {
//		MenuItem item = new MenuItem(label);
//		item.addActionListener(actionListener);
//		popup.add(item);
//	}
//	
//	protected void configureMenu(PopupMenu popup) {
//		// subclasses should call addMenuItem() here
//	}
//
//	protected void configureIconClickListener() {
//		// subclasses can do trayIcon.addActionListener(listener); here
//	}
	
	public void message(String caption, String text) {
		if (trayIcon != null) {
			trayIcon.setToolTip(caption + " " + text);
			trayIcon.displayMessage(caption, text, MessageType.INFO);
		}
	}

	public void quit() {
		if (systemTray != null && trayIcon != null)
			systemTray.remove(trayIcon);
	}
	
	protected static void addMenuItem(PopupMenu popup, String label, ActionListener actionListener) {
		MenuItem item = new MenuItem(label);
		item.addActionListener(actionListener);
		popup.add(item);
	}

}
