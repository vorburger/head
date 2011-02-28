/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */

package org.mifos.test.acceptance.savingsproduct;

import org.joda.time.DateTime;
import org.mifos.framework.util.DbUnitUtilities;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.account.AccountStatus;
import org.mifos.test.acceptance.framework.account.EditAccountStatusParameters;
import org.mifos.test.acceptance.framework.admin.AdminPage;
import org.mifos.test.acceptance.framework.savings.CreateSavingsAccountSearchParameters;
import org.mifos.test.acceptance.framework.savings.CreateSavingsAccountSubmitParameters;
import org.mifos.test.acceptance.framework.savings.DepositWithdrawalSavingsParameters;
import org.mifos.test.acceptance.framework.savings.SavingsAccountDetailPage;
import org.mifos.test.acceptance.framework.savings.SavingsApplyAdjustmentPage;
import org.mifos.test.acceptance.framework.savingsproduct.DefineNewSavingsProductConfirmationPage;
import org.mifos.test.acceptance.framework.savingsproduct.EditSavingsProductPage;
import org.mifos.test.acceptance.framework.savingsproduct.EditSavingsProductPreviewPage;
import org.mifos.test.acceptance.framework.savingsproduct.SavingsProductDetailsPage;
import org.mifos.test.acceptance.framework.savingsproduct.SavingsProductParameters;
import org.mifos.test.acceptance.framework.savingsproduct.ViewSavingsProductsPage;
import org.mifos.test.acceptance.framework.testhelpers.BatchJobHelper;
import org.mifos.test.acceptance.framework.testhelpers.NavigationHelper;
import org.mifos.test.acceptance.framework.testhelpers.QuestionGroupTestHelper;
import org.mifos.test.acceptance.framework.testhelpers.SavingsAccountHelper;
import org.mifos.test.acceptance.framework.testhelpers.SavingsProductHelper;
import org.mifos.test.acceptance.remote.DateTimeUpdaterRemoteTestingService;
import org.mifos.test.acceptance.remote.InitializeApplicationRemoteTestingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

@ContextConfiguration(locations = {"classpath:ui-test-context.xml"})
@Test(sequential = true, groups = {"savingsproduct", "acceptance", "ui", "no_db_unit"})
@SuppressWarnings("PMD.SignatureDeclareThrowsException")
public class DefineNewSavingsProductTest extends UiTestCaseBase {

    private SavingsProductHelper savingsProductHelper;

    @Autowired
    private DbUnitUtilities dbUnitUtilities;
    @Autowired
    private DriverManagerDataSource dataSource;
    @Autowired
    private InitializeApplicationRemoteTestingService initRemote;

    private SavingsAccountHelper savingsAccountHelper;
    private NavigationHelper navigationHelper;
    private QuestionGroupTestHelper questionGroupTestHelper;

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    @BeforeMethod(alwaysRun = true)
    public void setUp() throws Exception {
        super.setUp();

        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        dateTimeUpdaterRemoteTestingService.resetDateTime();

        savingsProductHelper = new SavingsProductHelper(selenium);
        savingsAccountHelper = new SavingsAccountHelper(selenium);
        navigationHelper = new NavigationHelper(selenium);
        questionGroupTestHelper = new QuestionGroupTestHelper(selenium);
        questionGroupTestHelper.markQuestionGroupAsActive("QGForCreateSavingsAccount");
    }

    @AfterMethod
    public void logOut() {
        questionGroupTestHelper.markQuestionGroupAsInactive("QGForCreateSavingsAccount");
        (new MifosPage(selenium)).logout();
    }

    // http://mifosforge.jira.com/browse/MIFOSTEST-139
    @Test(enabled = true)
    public void createVoluntarySavingsProductForCenters() throws Exception {
        SavingsProductParameters params = savingsProductHelper.getGenericSavingsProductParameters(SavingsProductParameters.VOLUNTARY, SavingsProductParameters.CENTERS);
        DefineNewSavingsProductConfirmationPage confirmationPage = savingsProductHelper.createSavingsProduct(params);

        confirmationPage.navigateToSavingsProductDetails();
        createSavingAccountWithCreatedProduct("Default Center", params.getProductInstanceName(), "7777.8");
    }

    // http://mifosforge.jira.com/browse/MIFOSTEST-137
    @Test(enabled = true)
    public void createVoluntarySavingsProductForGroups() throws Exception {

        SavingsProductParameters params = savingsProductHelper.getGenericSavingsProductParameters(SavingsProductParameters.VOLUNTARY, SavingsProductParameters.GROUPS);
        DefineNewSavingsProductConfirmationPage confirmationPage = savingsProductHelper.createSavingsProduct(params);

        confirmationPage.navigateToSavingsProductDetails();
        createSavingAccountWithCreatedProduct("Default Group", params.getProductInstanceName(), "234.0");
    }

    // http://mifosforge.jira.com/browse/MIFOSTEST-1093
    @Test(enabled = true)
    public void createVoluntarySavingsProductForClients() throws Exception {

        SavingsProductParameters params = savingsProductHelper.getGenericSavingsProductParameters(SavingsProductParameters.VOLUNTARY, SavingsProductParameters.CLIENTS);
        DefineNewSavingsProductConfirmationPage confirmationPage = savingsProductHelper.createSavingsProduct(params);

        confirmationPage.navigateToSavingsProductDetails();
        createSavingAccountWithCreatedProduct("Client - Mary Monthly", params.getProductInstanceName(), "200.0");
    }

    // http://mifosforge.jira.com/browse/MIFOSTEST-1094
    @Test(enabled = true)
    public void createMandatorySavingsProductForGroups() throws Exception {

        SavingsProductParameters params = savingsProductHelper.getGenericSavingsProductParameters(SavingsProductParameters.MANDATORY, SavingsProductParameters.GROUPS);
        DefineNewSavingsProductConfirmationPage confirmationPage = savingsProductHelper.createSavingsProduct(params);

        confirmationPage.navigateToSavingsProductDetails();
        createSavingAccountWithCreatedProduct("Default Group", params.getProductInstanceName(), "534.0");
    }

    // http://mifosforge.jira.com/browse/MIFOSTEST-138
    @Test(enabled = true)
    public void createMandatorySavingsProductForClients() throws Exception {

        SavingsProductParameters params = savingsProductHelper.getGenericSavingsProductParameters(SavingsProductParameters.MANDATORY, SavingsProductParameters.CLIENTS);
        DefineNewSavingsProductConfirmationPage confirmationPage = savingsProductHelper.createSavingsProduct(params);

        confirmationPage.navigateToSavingsProductDetails();
        createSavingAccountWithCreatedProduct("Client - Mary Monthly", params.getProductInstanceName(), "248.0");
    }

    // http://mifosforge.jira.com/browse/MIFOSTEST-1095
    @Test(enabled = true)
    public void createMandatorySavingsProductForCenters() throws Exception {

        SavingsProductParameters params = savingsProductHelper.getGenericSavingsProductParameters(SavingsProductParameters.MANDATORY, SavingsProductParameters.CENTERS);
        DefineNewSavingsProductConfirmationPage confirmationPage = savingsProductHelper.createSavingsProduct(params);

        confirmationPage.navigateToSavingsProductDetails();
        createSavingAccountWithCreatedProduct("Default Center", params.getProductInstanceName(), "7777.8");
    }

    //http://mifosforge.jira.com/browse/MIFOSTEST-712
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")// one of the dependent methods throws Exception
    @Test(enabled = false)
    public void savingsAccountWithDailyInterestMandatoryDeposits() throws Exception {
        //Given
        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        DateTime targetTime = new DateTime(2011, 2, 10, 13, 0, 0, 0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_008_dbunit.xml", dataSource, selenium);

        //When
        SavingsProductParameters params = savingsProductHelper.getMandatoryClientsMinimumBalanceSavingsProductParameters();
        DefineNewSavingsProductConfirmationPage confirmationPage = savingsProductHelper.createSavingsProduct(params);
        confirmationPage.navigateToSavingsProductDetails();     //"Stu1233266079799 Client1233266079799"
        SavingsAccountDetailPage savingsAccountDetailPage = createSavingAccountWithCreatedProduct("Stu1233266079799 Client1233266079799", params.getProductInstanceName(), "100000.0");
        String savingsId = savingsAccountDetailPage.getAccountId();

        EditAccountStatusParameters editAccountStatusParameters = new EditAccountStatusParameters();
        editAccountStatusParameters.setAccountStatus(AccountStatus.SAVINGS_ACTIVE);
        editAccountStatusParameters.setNote("change status to active");
        savingsAccountHelper.changeStatus(savingsId, editAccountStatusParameters);

        DepositWithdrawalSavingsParameters depositParams = new DepositWithdrawalSavingsParameters();

        make3StraightDeposit(savingsId);

        depositParams = setDepositParams(depositParams, "01", "03", "2011");
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);

        //Then
        targetTime = new DateTime(2011, 3, 1, 13, 0, 0, 0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);

        navigationHelper.navigateToSavingsAccountDetailPage(savingsId);

        navigationHelper.navigateToAdminPage();
        runBatchJobsForSavingsIntPosting();

        navigationHelper.navigateToSavingsAccountDetailPage(savingsId);
        Assert.assertEquals(selenium.getTable("recentActivityForDetailPage.1.2"), "57.4");

        //When
        targetTime = new DateTime(2011, 3, 7, 13, 0, 0, 0);
        depositParams = makeDefaultDepositWithdrawal(targetTime, depositParams, savingsId);

        targetTime = new DateTime(2011, 3, 14, 13, 0, 0, 0);
        depositParams = makeDefaultDepositWithdrawal(targetTime, depositParams, savingsId);

        targetTime = new DateTime(2011, 3, 21, 13, 0, 0, 0);
        depositParams = makeDefaultDepositWithdrawal(targetTime, depositParams, savingsId);

        targetTime = new DateTime(2011, 3, 28, 13, 0, 0, 0);
        depositParams = makeDefaultDepositWithdrawal(targetTime, depositParams, savingsId);

        //Then
        targetTime = new DateTime(2011, 4, 1, 13, 0, 0, 0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);

        navigationHelper.navigateToSavingsAccountDetailPage(savingsId);

        navigationHelper.navigateToAdminPage();
        runBatchJobsForSavingsIntPosting();

        navigationHelper.navigateToSavingsAccountDetailPage(savingsId);
        Assert.assertEquals(selenium.getTable("recentActivityForDetailPage.1.2"), "402.7");
    }

    //http://mifosforge.jira.com/browse/MIFOSTEST-141
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")// one of the dependent methods throws Exception
    @Test(enabled = false)
    public void savingsAccountWith3monthInterestVoluntaryDeposits() throws Exception {
        //Given
        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        DateTime targetTime = new DateTime(2011, 2, 15, 13, 0, 0, 0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_008_dbunit.xml", dataSource, selenium);

        //When
        SavingsProductParameters params = savingsProductHelper.getVoluntaryClients3MonthCalculactionPostingProductParameters();

        DefineNewSavingsProductConfirmationPage confirmationPage = savingsProductHelper.createSavingsProduct(params);
        confirmationPage.navigateToSavingsProductDetails();
        SavingsAccountDetailPage savingsAccountDetailPage = createSavingAccountWithCreatedProduct("Stu1233266079799 Client1233266079799", params.getProductInstanceName(), "100000.0");
        String savingsId = savingsAccountDetailPage.getAccountId();

        EditAccountStatusParameters editAccountStatusParameters = new EditAccountStatusParameters();
        editAccountStatusParameters.setAccountStatus(AccountStatus.SAVINGS_ACTIVE);
        editAccountStatusParameters.setNote("change status to active");
        savingsAccountHelper.changeStatus(savingsId, editAccountStatusParameters);

        DepositWithdrawalSavingsParameters depositParams = new DepositWithdrawalSavingsParameters();

        depositParams = makeDefaultDepositWithdrawal(targetTime, depositParams, savingsId);

        //Then
        targetTime = new DateTime(2011, 5, 15, 22, 0, 0, 0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);

        navigationHelper.navigateToSavingsAccountDetailPage(savingsId);
        navigationHelper.navigateToAdminPage();
        runBatchJobsForSavingsIntPosting();

        navigationHelper.navigateToSavingsAccountDetailPage(savingsId);
        Assert.assertEquals(selenium.getTable("recentActivityForDetailPage.1.2"), "602.7");

        targetTime = new DateTime(2011, 9, 1, 22, 0, 0, 0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);

        navigationHelper.navigateToSavingsAccountDetailPage(savingsId);
        navigationHelper.navigateToAdminPage();
        runBatchJobsForSavingsIntPosting();

        navigationHelper.navigateToSavingsAccountDetailPage(savingsId);
        Assert.assertEquals(selenium.getTable("recentActivityForDetailPage.1.2"), "1254.1");
    }

    //http://mifosforge.jira.com/browse/MIFOSTEST-721
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")// one of the dependent methods throws Exception
    @Test(enabled = false)
    public void savingsAccountsWithDifferentTransactionsOrdering() throws Exception {
        //Given
        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        DateTime targetTime = new DateTime(2011, 2, 10, 13, 0, 0, 0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_008_dbunit.xml", dataSource, selenium);

        //When
        SavingsProductParameters params = savingsProductHelper.getMandatoryClientsMinimumBalanceSavingsProductParameters();
        params.setTypeOfDeposits(SavingsProductParameters.VOLUNTARY);
        DefineNewSavingsProductConfirmationPage confirmationPage = savingsProductHelper.createSavingsProduct(params);
        confirmationPage.navigateToSavingsProductDetails();

        //account1
        SavingsAccountDetailPage savingsAccountDetailPage = createSavingAccountWithCreatedProduct("Stu1233266079799 Client1233266079799", params.getProductInstanceName(), "100000.0");
        String savingsId = savingsAccountDetailPage.getAccountId();

        EditAccountStatusParameters editAccountStatusParameters = new EditAccountStatusParameters();
        editAccountStatusParameters.setAccountStatus(AccountStatus.SAVINGS_ACTIVE);
        editAccountStatusParameters.setNote("change status to active");
        savingsAccountHelper.changeStatus(savingsId, editAccountStatusParameters);

        DepositWithdrawalSavingsParameters depositParams = new DepositWithdrawalSavingsParameters();

        depositParams = makeDefaultDepositWithdrawal(targetTime, depositParams, savingsId);

        targetTime = new DateTime(2011, 2, 15, 13, 0, 0, 0);
        depositParams = makeDefaultDepositWithdrawal(targetTime, depositParams, savingsId);
        depositParams = makeDefaultDepositWithdrawal(targetTime, depositParams, savingsId);

        //account2
        targetTime = new DateTime(2011, 2, 10, 13, 0, 0, 0);
        savingsAccountDetailPage = createSavingAccountWithCreatedProduct("Stu1233266079799 Client1233266079799", params.getProductInstanceName(), "100000.0");
        String savingsId2 = savingsAccountDetailPage.getAccountId();

        editAccountStatusParameters = new EditAccountStatusParameters();
        editAccountStatusParameters.setAccountStatus(AccountStatus.SAVINGS_ACTIVE);
        editAccountStatusParameters.setNote("change status to active");
        savingsAccountHelper.changeStatus(savingsId2, editAccountStatusParameters);

        depositParams = new DepositWithdrawalSavingsParameters();

        depositParams = makeDefaultDepositWithdrawal(targetTime, depositParams, savingsId2);

        targetTime = new DateTime(2011, 2, 15, 13, 0, 0, 0);
        depositParams = makeDefaultDepositWithdrawal(targetTime, depositParams, savingsId2);

        depositParams = makeDefaultDepositWithdrawal(targetTime, depositParams, savingsId2);

        //Then
        targetTime = new DateTime(2011, 4, 1, 13, 0, 0, 0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);

        navigationHelper.navigateToAdminPage();
        runBatchJobsForSavingsIntPosting();

        navigationHelper.navigateToSavingsAccountDetailPage(savingsId);
        Assert.assertEquals(selenium.getTable("recentActivityForDetailPage.1.2"), "48.6");

        navigationHelper.navigateToSavingsAccountDetailPage(savingsId2);
        Assert.assertEquals(selenium.getTable("recentActivityForDetailPage.1.2"), "48.6");
    }

    //http://mifosforge.jira.com/browse/MIFOSTEST-624
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")// one of the dependent methods throws Exception
    @Test(enabled = false)
    public void savingsMonthlyAccountsAverageBalance() throws Exception {
        //Given
        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        DateTime targetTime = new DateTime(2011, 2, 10, 13, 0, 0, 0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_008_dbunit.xml", dataSource, selenium);

        //When
        SavingsProductParameters params = savingsProductHelper.getMandatoryClientsMinimumBalanceSavingsProductParameters();
        params.setBalanceUsedForInterestCalculation(SavingsProductParameters.AVERAGE_BALANCE);

        String savingsId = createSavingsAccount(params);
        make3StraightDeposit(savingsId);

        //Then
        targetTime = new DateTime(2011, 3, 1, 13, 0, 0, 0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);

        navigationHelper.navigateToSavingsAccountDetailPage(savingsId);

        navigationHelper.navigateToAdminPage();
        runBatchJobsForSavingsIntPosting();

        navigationHelper.navigateToSavingsAccountDetailPage(savingsId);
        Assert.assertEquals(selenium.getTable("recentActivityForDetailPage.1.2"), "57.4");

    }

    //http://mifosforge.jira.com/browse/MIFOSTEST-1070
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")// one of the dependent methods throws Exception
    @Test(enabled = false)
    public void restrictionsSavingsTransactions() throws Exception {
        //Given
        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        DateTime targetTime = new DateTime(2011, 2, 25, 13, 0, 0, 0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_003_dbunit.xml", dataSource, selenium);

        //When
        SavingsAccountDetailPage savingsAccountDetailPage = createSavingAccountWithCreatedProduct("Stu1232993852651 Client1232993852651", "MySavingsProduct1233265923516", "100000.0");
        String savingsId = savingsAccountDetailPage.getAccountId();

        EditAccountStatusParameters editAccountStatusParameters = new EditAccountStatusParameters();
        editAccountStatusParameters.setAccountStatus(AccountStatus.SAVINGS_ACTIVE);
        editAccountStatusParameters.setNote("change status to active");
        savingsAccountHelper.changeStatus(savingsId, editAccountStatusParameters);

        make3StraightDeposit(savingsId);

        targetTime = new DateTime(2012, 1, 1, 13, 0, 0, 0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);

        navigationHelper.navigateToAdminPage();
        runBatchJobsForSavingsIntPosting();

        navigationHelper.navigateToSavingsAccountDetailPage(savingsId);
        Assert.assertEquals(selenium.getTable("recentActivityForDetailPage.1.2"), "20594.9");

        //Then
        DepositWithdrawalSavingsParameters depositParams = new DepositWithdrawalSavingsParameters();
        DateTime badDate = new DateTime(2011, 5, 5, 13, 0, 0, 0);

        makeDefaultDepositWithdrawal(badDate, depositParams, savingsId);

        Assert.assertTrue(isTextPresentInPage("Date of transaction is invalid. It can not be prior to the last meeting date of the customer or prior to activation date of the savings account."));
    }

    //http://mifosforge.jira.com/browse/MIFOSTEST-722
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")// one of the dependent methods throws Exception
    @Test(enabled = false)
    public void savingsAccountsWithAdjustments() throws Exception {
        //Given
        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        DateTime targetTime = new DateTime(2011, 1, 1, 13, 0, 0, 0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_003_dbunit.xml", dataSource, selenium);

        //When
        SavingsProductParameters params = getVoluntaryGroupsMonthCalculactionProductParameters();
        DefineNewSavingsProductConfirmationPage confirmationPage = savingsProductHelper.createSavingsProduct(params);
        confirmationPage.navigateToSavingsProductDetails();

        //account1
        SavingsAccountDetailPage savingsAccountDetailPage = createSavingAccountWithCreatedProduct("MyGroup1232993846342", params.getProductInstanceName(), "2000.0");
        String savingsId = savingsAccountDetailPage.getAccountId();

        EditAccountStatusParameters editAccountStatusParameters = new EditAccountStatusParameters();
        editAccountStatusParameters.setAccountStatus(AccountStatus.SAVINGS_ACTIVE);
        editAccountStatusParameters.setNote("change status to active");
        savingsAccountHelper.changeStatus(savingsId, editAccountStatusParameters);

        DepositWithdrawalSavingsParameters depositParams = new DepositWithdrawalSavingsParameters();

        depositParams = makeDefaultDepositWithdrawal(targetTime, depositParams, savingsId);

        depositParams = makeDefaultDepositWithdrawal(targetTime, depositParams, savingsId);

        targetTime = new DateTime(2011, 2, 1, 13, 0, 0, 0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);

        navigationHelper.navigateToAdminPage();
        runBatchJobsForSavingsIntPosting();

        //Then
        navigationHelper.navigateToSavingsAccountDetailPage(savingsId);

        Assert.assertEquals(selenium.getTable("recentActivityForDetailPage.1.2"), "7.2");

        //account2
        //When
        targetTime = new DateTime(2011, 3, 1, 13, 0, 0, 0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);
        savingsAccountDetailPage = createSavingAccountWithCreatedProduct("MyGroup1232993846342", params.getProductInstanceName(), "2000.0");
        String savingsId2 = savingsAccountDetailPage.getAccountId();

        editAccountStatusParameters = new EditAccountStatusParameters();
        editAccountStatusParameters.setAccountStatus(AccountStatus.SAVINGS_ACTIVE);
        editAccountStatusParameters.setNote("change status to active");
        savingsAccountHelper.changeStatus(savingsId2, editAccountStatusParameters);

        depositParams = new DepositWithdrawalSavingsParameters();

        depositParams = makeDefaultDepositWithdrawal(targetTime, depositParams, savingsId2);
        depositParams = makeDefaultDepositWithdrawal(targetTime, depositParams, savingsId2);

        targetTime = new DateTime(2011, 4, 1, 13, 0, 0, 0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);

        navigationHelper.navigateToAdminPage();
        runBatchJobsForSavingsIntPosting();

        //Then
        navigationHelper.navigateToSavingsAccountDetailPage(savingsId2);
        Assert.assertEquals(selenium.getTable("recentActivityForDetailPage.1.2"), "7.2");

    }

    //http://mifosforge.jira.com/browse/MIFOSTEST-725
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")// one of the dependent methods throws Exception
    @Test(enabled = false)
    public void savingsProductUpdateableFields() throws Exception {
        //Given
        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        DateTime targetTime = new DateTime(2011, 2, 1, 13, 0, 0, 0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_008_dbunit.xml", dataSource, selenium);

        //When
        SavingsProductParameters params = savingsProductHelper.getMandatoryClientsMinimumBalanceSavingsProductParameters();
        params.setBalanceUsedForInterestCalculation(SavingsProductParameters.AVERAGE_BALANCE);
        params.setTypeOfDeposits(SavingsProductParameters.VOLUNTARY);

        String productName = params.getProductInstanceName();
        String savingsId = createSavingsAccount(params);

        DepositWithdrawalSavingsParameters depositParams = new DepositWithdrawalSavingsParameters();

        depositParams = makeDefaultDepositWithdrawal(targetTime, depositParams, savingsId);

        //Then
        targetTime = new DateTime(2011, 3, 1, 13, 0, 0, 0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);

        navigationHelper.navigateToSavingsAccountDetailPage(savingsId);

        navigationHelper.navigateToAdminPage();
        runBatchJobsForSavingsIntPosting();

        navigationHelper.navigateToSavingsAccountDetailPage(savingsId);
        Assert.assertEquals(selenium.getTable("recentActivityForDetailPage.1.2"), "2.7");

        AdminPage adminPage = navigationHelper.navigateToAdminPage();
        ViewSavingsProductsPage viewSavingsProducts = adminPage.navigateToViewSavingsProducts();
        viewSavingsProducts.verifyPage();

        SavingsProductDetailsPage savingsProductDetailsPage = viewSavingsProducts.viewSavingsProductDetails(productName);
        EditSavingsProductPage editSavingsProductPage = savingsProductDetailsPage.editSavingsProduct();

        selenium.type("interestRate", "10.0");
        selenium.type("minBalanceRequiredForInterestCalculation", "100.0");

        EditSavingsProductPreviewPage editSavingsProductPreviewPage = editSavingsProductPage.editSubmit();
        savingsProductDetailsPage = editSavingsProductPreviewPage.submit();

        makeDefaultDepositWithdrawal(targetTime, depositParams, savingsId);

        targetTime = new DateTime(2011, 4, 1, 13, 0, 0, 0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);

        navigationHelper.navigateToSavingsAccountDetailPage(savingsId);

        navigationHelper.navigateToAdminPage();
        runBatchJobsForSavingsIntPosting();

        navigationHelper.navigateToSavingsAccountDetailPage(savingsId);
        Assert.assertEquals(selenium.getTable("recentActivityForDetailPage.1.2"), "33.1");
    }

    //http://mifosforge.jira.com/browse/MIFOSTEST-144
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")// one of the dependent methods throws Exception
    @Test(enabled = false)
    public void savingsAdjustmentsForDepositsWithdrawals() throws Exception {
        //Given
        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        DateTime targetTime = new DateTime(2011, 1, 1, 13, 0, 0, 0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_008_dbunit.xml", dataSource, selenium);
        //When
        SavingsProductParameters params = getVoluntaryGroupsMonthCalculactionProductParameters();
        params.setApplicableFor(SavingsProductParameters.CLIENTS);
        params.setStartDateDD("01");
        params.setStartDateMM("01");
        params.setStartDateYYYY("2011");
        params.setInterestRate("10");
        String savingsId = createSavingsAccount(params);
        DepositWithdrawalSavingsParameters depositParams = new DepositWithdrawalSavingsParameters();
        depositParams = makeDefaultDepositWithdrawal(targetTime, depositParams, savingsId);

        targetTime = new DateTime(2011, 2, 1, 13, 0, 0, 0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);
        depositParams = makeDefaultDepositWithdrawal(targetTime, depositParams, savingsId);

        targetTime = new DateTime(2011, 3, 1, 13, 0, 0, 0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);

        navigationHelper.navigateToAdminPage();
        runBatchJobsForSavingsIntPosting();

        navigationHelper.navigateToSavingsAccountDetailPage(savingsId);
        Assert.assertEquals(selenium.getTable("recentActivityForDetailPage.1.2"), "4.1");

        targetTime = new DateTime(2011, 4, 1, 13, 0, 0, 0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);
        navigationHelper.navigateToAdminPage();
        runBatchJobsForSavingsIntPosting();
        navigationHelper.navigateToSavingsAccountDetailPage(savingsId);
        Assert.assertEquals(selenium.getTable("recentActivityForDetailPage.1.2"), "2.6");

        SavingsAccountDetailPage savingsAccountDetailPage = new SavingsAccountDetailPage(selenium);
        SavingsApplyAdjustmentPage savingsApplyAdjustmentPage = new SavingsApplyAdjustmentPage(selenium);

        depositParams = makeDefaultDepositWithdrawal(targetTime, depositParams, savingsId);
        savingsAccountDetailPage = navigationHelper.navigateToSavingsAccountDetailPage(savingsId);
        savingsApplyAdjustmentPage = savingsAccountDetailPage.navigateToApplyAdjustmentPage();
        savingsApplyAdjustmentPage.applyAdjustment("234", "adjustment");

        targetTime = new DateTime(2011, 5, 1, 13, 0, 0, 0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);
        navigationHelper.navigateToAdminPage();
        runBatchJobsForSavingsIntPosting();

        navigationHelper.navigateToSavingsAccountDetailPage(savingsId);

        Assert.assertEquals(selenium.getTable("recentActivityForDetailPage.1.2"), "4.4");
        depositParams = makeDefaultDepositWithdrawal(targetTime, depositParams, savingsId);
        savingsAccountDetailPage = navigationHelper.navigateToSavingsAccountDetailPage(savingsId);
        savingsApplyAdjustmentPage = savingsAccountDetailPage.navigateToApplyAdjustmentPage();
        savingsApplyAdjustmentPage.applyAdjustment("55", "adjustment");

        targetTime = new DateTime(2011, 6, 1, 13, 0, 0, 0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);
        navigationHelper.navigateToAdminPage();
        runBatchJobsForSavingsIntPosting();

        navigationHelper.navigateToSavingsAccountDetailPage(savingsId);
        Assert.assertEquals(selenium.getTable("recentActivityForDetailPage.1.2"), "4.2");
        depositParams = makeDefaultDepositWithdrawal(targetTime, depositParams, savingsId);
        savingsAccountDetailPage = navigationHelper.navigateToSavingsAccountDetailPage(savingsId);
        savingsApplyAdjustmentPage = savingsAccountDetailPage.navigateToApplyAdjustmentPage();
        savingsApplyAdjustmentPage.applyAdjustment("444", "adjustment");

        targetTime = new DateTime(2011, 7, 1, 13, 0, 0, 0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);
        navigationHelper.navigateToAdminPage();
        runBatchJobsForSavingsIntPosting();

        navigationHelper.navigateToSavingsAccountDetailPage(savingsId);
        Assert.assertEquals(selenium.getTable("recentActivityForDetailPage.1.2"), "7.6");
        //step 12-15
        params = getVoluntaryGroupsMonthCalculactionProductParameters();
        params.setApplicableFor(SavingsProductParameters.CLIENTS);
        params.setStartDateDD("01");
        params.setStartDateMM("07");
        params.setStartDateYYYY("2011");

        String savingsId2 = createSavingsAccount(params);
        depositParams = makeDefaultDepositWithdrawal(targetTime, depositParams, savingsId2);
        depositParams = makeDefaultDepositWithdrawal(targetTime, depositParams, savingsId2);
        // month with last day is a non-working day
        targetTime = new DateTime(2011, 8, 1, 13, 0, 0, 0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);
        navigationHelper.navigateToAdminPage();
        runBatchJobsForSavingsIntPosting();

        navigationHelper.navigateToSavingsAccountDetailPage(savingsId2);
        Assert.assertEquals(selenium.getTable("recentActivityForDetailPage.1.2"), "1.4");
    }

    private String createSavingsAccount(SavingsProductParameters params) {
        DefineNewSavingsProductConfirmationPage confirmationPage = savingsProductHelper.createSavingsProduct(params);
        confirmationPage.navigateToSavingsProductDetails();
        SavingsAccountDetailPage savingsAccountDetailPage = createSavingAccountWithCreatedProduct("Stu1233266079799 Client1233266079799", params.getProductInstanceName(), "100000.0");
        String savingsId = savingsAccountDetailPage.getAccountId();

        EditAccountStatusParameters editAccountStatusParameters = new EditAccountStatusParameters();
        editAccountStatusParameters.setAccountStatus(AccountStatus.SAVINGS_ACTIVE);
        editAccountStatusParameters.setNote("change status to active");
        savingsAccountHelper.changeStatus(savingsId, editAccountStatusParameters);
        return savingsId;
    }

    private void make3StraightDeposit(String savingsId) throws Exception {
        DateTime targetTime = new DateTime(2011, 2, 10, 13, 0, 0, 0);
        DepositWithdrawalSavingsParameters depositParams = new DepositWithdrawalSavingsParameters();

        targetTime = new DateTime(2011, 2, 14, 13, 0, 0, 0);
        depositParams = makeDefaultDepositWithdrawal(targetTime, depositParams, savingsId);

        targetTime = new DateTime(2011, 2, 21, 13, 0, 0, 0);
        depositParams = makeDefaultDepositWithdrawal(targetTime, depositParams, savingsId);

        targetTime = new DateTime(2011, 2, 28, 13, 0, 0, 0);
        depositParams = makeDefaultDepositWithdrawal(targetTime, depositParams, savingsId);
    }

    private DepositWithdrawalSavingsParameters makeDefaultDepositWithdrawal(DateTime date, DepositWithdrawalSavingsParameters depositParams, String savingsId) throws Exception {
        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        dateTimeUpdaterRemoteTestingService.setDateTime(date);
        DepositWithdrawalSavingsParameters depositParamsReturn = setDepositParams(depositParams, String.valueOf(date.getDayOfMonth()), String.valueOf(date.getMonthOfYear()), String.valueOf(date.getYear()));
        savingsAccountHelper.makeDepositOrWithdrawalOnSavingsAccount(savingsId, depositParams);
        return depositParamsReturn;
    }

    private DepositWithdrawalSavingsParameters setDepositParams(DepositWithdrawalSavingsParameters depositParams, String dd, String mm, String yy) {
        depositParams.setTrxnDateMM(mm);
        depositParams.setTrxnDateDD(dd);
        depositParams.setTrxnDateYYYY(yy);
        depositParams.setPaymentType(DepositWithdrawalSavingsParameters.CASH);
        depositParams.setTrxnType(DepositWithdrawalSavingsParameters.DEPOSIT);
        return depositParams;
    }

    private void runBatchJobsForSavingsIntPosting() {
        List<String> jobsToRun = new ArrayList<String>();
        jobsToRun.add("SavingsIntPostingTaskJob");
        new BatchJobHelper(selenium).runSomeBatchJobs(jobsToRun);
    }

    private SavingsAccountDetailPage createSavingAccountWithCreatedProduct(String client, String productName, String amount) {
        CreateSavingsAccountSearchParameters searchParameters = new CreateSavingsAccountSearchParameters();
        searchParameters.setSearchString(client);
        searchParameters.setSavingsProduct(productName);

        CreateSavingsAccountSubmitParameters submitAccountParameters = new CreateSavingsAccountSubmitParameters();
        submitAccountParameters.setAmount(amount);
        SavingsAccountDetailPage savingsAccountPage = savingsAccountHelper.createSavingsAccountWithQG(searchParameters, submitAccountParameters);
        savingsAccountPage.verifyPage();
        savingsAccountPage.verifySavingsAmount(submitAccountParameters.getAmount());
        savingsAccountPage.verifySavingsProduct(searchParameters.getSavingsProduct());
        return savingsAccountPage;
    }

    private SavingsProductParameters getVoluntaryGroupsMonthCalculactionProductParameters() {
        SavingsProductParameters params = savingsProductHelper.getGenericSavingsProductParameters(SavingsProductParameters.VOLUNTARY, SavingsProductParameters.GROUPS);
        params.setMandatoryAmount("2000");
        params.setInterestRate("5");
        params.setDaysOrMonthsForInterestCalculation(params.MONTHS);
        params.setFrequencyOfInterestPostings("1");
        params.setNumberOfDaysOrMonthsForInterestCalculation("1");
        params.setAmountAppliesTo(SavingsProductParameters.WHOLE_GROUP);
        return params;
    }
}