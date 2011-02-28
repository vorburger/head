package org.mifos.test.acceptance.framework.client;

import org.mifos.test.acceptance.framework.MifosPage;
import org.testng.Assert;

import com.thoughtworks.selenium.Selenium;
import java.util.List;

public class ClientViewChangeLogPage extends MifosPage{

    public ClientViewChangeLogPage(Selenium selenium) {
        super(selenium);
        this.verifyPage("ClientViewChangeLog");
    }

    public void verifyLastEntryOnChangeLog(String field, String oldvalue, String newValue, String user){
        Assert.assertEquals(selenium.getTable("auditLogRecords.1.1"),field);
        Assert.assertEquals(selenium.getTable("auditLogRecords.1.2"),oldvalue);
        Assert.assertEquals(selenium.getTable("auditLogRecords.1.3"),newValue);
        Assert.assertEquals(selenium.getTable("auditLogRecords.1.4"),user);
    }

    public void verifyChangeLog(List<String> fields, List<String> oldvalues, List<String> newValues, List<String> users, int maxRows) {
        for(int i=0;i<fields.size();i++) {
            Assert.assertTrue(isTextPresentInPage(fields.get(i)));
            for(int j=0;j<maxRows;j++) {
                if(selenium.getTable("auditLogRecords."+j+".1").equals(fields.get(i))) {
                    Assert.assertEquals(selenium.getTable("auditLogRecords."+j+".2"),oldvalues.get(i));
                    Assert.assertEquals(selenium.getTable("auditLogRecords."+j+".3"),newValues.get(i));
                    Assert.assertEquals(selenium.getTable("auditLogRecords."+j+".4"),users.get(i));
                }
            }
        }
    }
}
