package org.mifos.server.tray;

import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;

public class MifosTray extends Tray {

	private String logFile;
	private String mifosURL;

	public MifosTray(String mifosURL, String logFile) {
		super();
		this.logFile = logFile;
		this.mifosURL = mifosURL;
	}

	public void init() {
		init("/mifos-tray.png");
		message("Mifos", "is starting...");
	}

	public void started(boolean openBrowser) {
		PopupMenu popup = new PopupMenu();
		addMenuItem(popup, "Open Mifos", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openURL(mifosURL);
			}
		});
		addMenuItem(popup, "Open Log", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openURL(logFile);
			}
		});
		addMenuItem(popup, "Quit", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				quit();
			}
		});
		trayIcon.setPopupMenu(popup);
		
		trayIcon.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openURL(mifosURL);
			}
		});
		
		message("Mifos", "is running");
		if (openBrowser)
			openURL(mifosURL);
	}
	
	protected void openURL(String uri) {
		try {
			java.awt.Desktop.getDesktop().browse(new URI(uri));
		} catch (Throwable t) {
		}
	}

}
