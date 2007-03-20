package org.mifos.application.reports;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.mifos.application.reports.business.service.ReportsBusinessServiceTest;
import org.mifos.application.reports.persistence.ReportsPersistenceTest;
import org.mifos.application.reports.struts.action.TestReportsAction;

public class ReportsTestSuite extends TestSuite {
	
	public static Test suite() throws Exception {
		TestSuite testSuite = new ReportsTestSuite();		
		testSuite.addTestSuite(ReportsPersistenceTest.class);
		testSuite.addTestSuite(TestReportsAction.class);
		testSuite.addTestSuite(ReportsBusinessServiceTest.class);
		return testSuite;
	}

}
