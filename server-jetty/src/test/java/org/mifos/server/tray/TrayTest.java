package org.mifos.server.tray;

import org.junit.Test;


public class TrayTest {

	@Test
	public void testTray() throws InterruptedException {
		// UnComment // when manually testing menu
		final MifosTray testTray = new MifosTray("http://www.mifos.org", "pom.xml");
		testTray.init();
		//Thread.sleep(10000);
		testTray.started(false);
		//Thread.sleep(30000);
		testTray.quit();
	}

}
