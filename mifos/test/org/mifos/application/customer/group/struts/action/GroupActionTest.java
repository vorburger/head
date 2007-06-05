/**

 * GroupActionTest.java version: 1.0



 * Copyright (c) 2005-2006 Grameen Foundation USA

 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005

 * All rights reserved.



 * Apache License
 * Copyright (c) 2005-2006 Grameen Foundation USA
 *

 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the

 * License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an explanation of the license

 * and how it is applied.

 *

 */

package org.mifos.application.customer.group.struts.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.persistence.AccountPersistence;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.util.helpers.SavingsTestHelper;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.customer.business.CustomFieldDefinitionEntity;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.CustomerPositionEntity;
import org.mifos.application.customer.business.PositionEntity;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.group.struts.actionforms.GroupCustActionForm;
import org.mifos.application.customer.group.util.helpers.GroupConstants;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.fees.business.AmountFeeBO;
import org.mifos.application.fees.business.FeeView;
import org.mifos.application.fees.persistence.FeePersistence;
import org.mifos.application.fees.util.helpers.FeeCategory;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.components.fieldConfiguration.util.helpers.FieldConfig;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.hibernate.helper.QueryResult;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.plugin.helper.EntityMasterData;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.ResourceLoader;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class GroupActionTest extends MifosMockStrutsTestCase {
	private CenterBO center;

	private GroupBO group;

	private ClientBO client;

	private MeetingBO meeting;

	private String flowKey;

	private SavingsTestHelper helper = new SavingsTestHelper();

	private SavingsOfferingBO savingsOffering;

	private LoanBO loanBO;

	private SavingsBO savingsBO;
	
	private UserContext userContext;
	private Short officeId  = 3;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		setServletConfigFile(ResourceLoader.getURI("WEB-INF/web.xml")
				.getPath());
		setConfigFile(ResourceLoader.getURI(
				"org/mifos/application/customer/group/struts-config.xml")
				.getPath());
		userContext = TestObjectFactory.getContext();
		request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
		addRequestParameter("recordLoanOfficerId", "1");
		addRequestParameter("recordOfficeId", "1");
		request.getSession(false).setAttribute("ActivityContext", TestObjectFactory.getActivityContext());
		flowKey = createFlow(request, GroupCustAction.class);
		EntityMasterData.getInstance().init();
		FieldConfig fieldConfig = FieldConfig.getInstance();
		fieldConfig.init();
		getActionServlet().getServletContext().setAttribute(
				Constants.FIELD_CONFIGURATION,
				fieldConfig.getEntityMandatoryFieldMap());
	}

	@Override
	protected void tearDown() throws Exception {
		TestObjectFactory.cleanUp(loanBO);
		TestObjectFactory.cleanUp(savingsBO);
		TestObjectFactory.cleanUp(client);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		HibernateUtil.closeSession();
		userContext = null;
		super.tearDown();
	}

	public void testChooseOffice()throws Exception{
		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "chooseOffice");
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.chooseOffice_success.toString());
	}

	public void testHierarchyCheck()throws Exception{
		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "hierarchyCheck");
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		
		boolean isCenterHierarchyExists = Configuration.getInstance().getCustomerConfig(userContext.getBranchId()).isCenterHierarchyExists();
		if(isCenterHierarchyExists)
			verifyForward(ActionForwards.loadCenterSearch.toString());
		else
			verifyForward(ActionForwards.loadCreateGroup.toString());
	}
	
	public void testLoad_FeeDifferentFrequecny()throws Exception{
		createCenterWithoutFee();
		List<FeeView> fees = getFees(RecurrenceType.MONTHLY);
		HibernateUtil.closeSession();
		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("centerSystemId", center.getGlobalCustNum());
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.load_success.toString());
		
		boolean isCenterHierarchyExists = Configuration.getInstance().getCustomerConfig(userContext.getBranchId()).isCenterHierarchyExists();
		if(!isCenterHierarchyExists){
			assertNotNull(SessionUtils.getAttribute(CustomerConstants.LOAN_OFFICER_LIST, request));	
		}
		List<FeeView> additionalFees = (List<FeeView>)SessionUtils.getAttribute(CustomerConstants.ADDITIONAL_FEES_LIST,request);
		assertNotNull(additionalFees);
		assertEquals(0, additionalFees.size());
		assertNotNull(SessionUtils.getAttribute(GroupConstants.CENTER_HIERARCHY_EXIST,request));
		assertNotNull(SessionUtils.getAttribute(CustomerConstants.FORMEDBY_LOAN_OFFICER_LIST,request));
		assertNotNull(SessionUtils.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST,request));
		center = TestObjectFactory.getObject(CenterBO.class,	center.getCustomerId());
		removeFees(fees);	
	}
	
	public void testLoad_FeeSameFrequecny()throws Exception{
		createCenterWithoutFee();
		HibernateUtil.closeSession();
		List<FeeView> fees = getFees(RecurrenceType.WEEKLY);
		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("centerSystemId", center.getGlobalCustNum());
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.load_success.toString());
		
		boolean isCenterHierarchyExists = Configuration.getInstance().getCustomerConfig(userContext.getBranchId()).isCenterHierarchyExists();
		if(!isCenterHierarchyExists){
			assertNotNull(SessionUtils.getAttribute(CustomerConstants.LOAN_OFFICER_LIST, request));	
		}
		List<FeeView> additionalFees = (List<FeeView>)SessionUtils.getAttribute(CustomerConstants.ADDITIONAL_FEES_LIST,request);
		assertNotNull(additionalFees);
		assertEquals(1, additionalFees.size());
		assertNotNull(SessionUtils.getAttribute(GroupConstants.CENTER_HIERARCHY_EXIST,request));
		assertNotNull(SessionUtils.getAttribute(CustomerConstants.FORMEDBY_LOAN_OFFICER_LIST,request));
		assertNotNull(SessionUtils.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST,request));
		center = TestObjectFactory.getObject(CenterBO.class,	center.getCustomerId());
		removeFees(fees);
	}
	
	public void testLoadMeeting()throws Exception{
		createParentCustomer();
		HibernateUtil.closeSession();
		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("centerSystemId", center.getGlobalCustNum());
		actionPerform();
		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "loadMeeting");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		getRequest().getSession().setAttribute("security_param", "GroupCreate");
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.loadMeeting_success.toString());
		center = TestObjectFactory.getObject(CenterBO.class,	center.getCustomerId());
	}
	
	public void testPreviewFailure_With_Name_Null()throws Exception{
		createParentCustomer();
		HibernateUtil.closeSession();
		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("centerSystemId", center.getGlobalCustNum());
		actionPerform();
		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		assertEquals("Group Name", 1, getErrorSize(CustomerConstants.NAME));
		verifyInputForward();
		center = TestObjectFactory.getObject(CenterBO.class,	center.getCustomerId());		
	}

	public void testPreviewFailure_TrainedWithoutTrainedDate()throws Exception{
		createParentCustomer();
		HibernateUtil.closeSession();
		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("centerSystemId", center.getGlobalCustNum());
		actionPerform();
		
		List<CustomFieldDefinitionEntity> customFieldDefs = (List<CustomFieldDefinitionEntity>)SessionUtils.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request);
		
		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("displayName", "group");		
		addRequestParameter("trained", "1");
		addRequestParameter("status", CustomerStatus.GROUP_PENDING.getValue().toString());
		addRequestParameter("formedByPersonnel", center.getPersonnel().getPersonnelId().toString());	
		
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		int i = 0;
		for(CustomFieldDefinitionEntity customFieldDef: customFieldDefs){
			addRequestParameter("customField["+ i +"].fieldId", customFieldDef.getFieldId().toString());
			addRequestParameter("customField["+ i +"].fieldValue", "11");
			i++;
		}
		actionPerform();
		assertEquals("Trained Date", 1, getErrorSize(CustomerConstants.TRAINED_DATE_MANDATORY));
		verifyInputForward();
		center = TestObjectFactory.getObject(CenterBO.class,	center.getCustomerId());		
	}
	
	public void testFailurePreview_WithoutMandatoryCustomField_IfAny() throws Exception{
		createParentCustomer();
		HibernateUtil.closeSession();
		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("centerSystemId", center.getGlobalCustNum());
		actionPerform();
		
		
		List<CustomFieldDefinitionEntity> customFieldDefs = (List<CustomFieldDefinitionEntity>)SessionUtils.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request);
		boolean isCustomFieldMandatory = false;
		for(CustomFieldDefinitionEntity customFieldDef: customFieldDefs){
			if(customFieldDef.isMandatory()){
				isCustomFieldMandatory = true;
				break;
			}
		}
		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		addRequestParameter("displayName", "group");		
		addRequestParameter("status", CustomerStatus.GROUP_PENDING.getValue().toString());
		addRequestParameter("formedByPersonnel", center.getPersonnel().getPersonnelId().toString());		
		int i = 0;
		for(CustomFieldDefinitionEntity customFieldDef: customFieldDefs){
			addRequestParameter("customField["+ i +"].fieldId", customFieldDef.getFieldId().toString());
			addRequestParameter("customField["+ i +"].fieldValue", "");
			i++;
		}
		actionPerform();
		
		if(isCustomFieldMandatory){
			assertEquals("CustomField", 1, getErrorSize(CustomerConstants.CUSTOM_FIELD));
			verifyInputForward();
		}
		else{
			assertEquals("CustomField", 0, getErrorSize(CustomerConstants.CUSTOM_FIELD));
			verifyForward(ActionForwards.preview_success.toString());
		}
		center = TestObjectFactory.getObject(CenterBO.class,
			center.getCustomerId());		
	}
	
	public void testFailurePreview_WithDuplicateFee() throws Exception{
		List<FeeView> feesToRemove = getFees(RecurrenceType.WEEKLY);
		createParentCustomer();
		HibernateUtil.closeSession();
		
		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("centerSystemId", center.getGlobalCustNum());
		actionPerform();
		List<FeeView> feeList = (List<FeeView>)SessionUtils.getAttribute(CustomerConstants.ADDITIONAL_FEES_LIST, request);
		FeeView fee = feeList.get(0);
		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "preview");		
		addRequestParameter("selectedFee[0].feeId", fee.getFeeId());
		addRequestParameter("selectedFee[0].amount", "100");
		addRequestParameter("selectedFee[1].feeId", fee.getFeeId());
		addRequestParameter("selectedFee[1].amount", "150");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		assertEquals("Fee", 1, getErrorSize(CustomerConstants.FEE));
		verifyInputForward();
		removeFees(feesToRemove);
		center = TestObjectFactory.getObject(CenterBO.class,	center.getCustomerId());		
	}
	
	public void testFailurePreview_WithFee_WithoutFeeAmount() throws Exception{
		List<FeeView> feesToRemove = getFees(RecurrenceType.WEEKLY);
		createParentCustomer();
		HibernateUtil.closeSession();
		
		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("centerSystemId", center.getGlobalCustNum());
		actionPerform();
		List<FeeView> feeList = (List<FeeView>)SessionUtils.getAttribute(CustomerConstants.ADDITIONAL_FEES_LIST, request);
		FeeView fee = feeList.get(0);
		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "preview");		
		addRequestParameter("selectedFee[0].feeId", fee.getFeeId());
		addRequestParameter("selectedFee[0].amount", "");		
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		assertEquals("Fee", 1, getErrorSize(CustomerConstants.FEE));
		verifyInputForward();
		removeFees(feesToRemove);
		center = TestObjectFactory.getObject(CenterBO.class,	center.getCustomerId());		
	}

	public void testSuccessfulPreview() throws Exception{
		List<FeeView> feesToRemove = getFees(RecurrenceType.WEEKLY);
		createParentCustomer();		
		HibernateUtil.closeSession();
		
		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("centerSystemId", center.getGlobalCustNum());
		actionPerform();
		
		List<FeeView> feeList = (List<FeeView>)SessionUtils.getAttribute(CustomerConstants.ADDITIONAL_FEES_LIST, request);
		FeeView fee = feeList.get(0);
		
		List<CustomFieldDefinitionEntity> customFieldDefs = (List<CustomFieldDefinitionEntity>)SessionUtils.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request);
		
		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		addRequestParameter("displayName", "group");		
		addRequestParameter("status", CustomerStatus.GROUP_PENDING.getValue().toString());
		addRequestParameter("formedByPersonnel", center.getPersonnel().getPersonnelId().toString());	
		int i = 0;
		for(CustomFieldDefinitionEntity customFieldDef: customFieldDefs){
			addRequestParameter("customField["+ i +"].fieldId", customFieldDef.getFieldId().toString());
			addRequestParameter("customField["+ i +"].fieldValue", "11");
			i++;
		}
		addRequestParameter("selectedFee[0].feeId", fee.getFeeId());
		addRequestParameter("selectedFee[0].amount", fee.getAmount());
		actionPerform();
		assertEquals(0, getErrorSize());
		verifyForward(ActionForwards.preview_success.toString());
		verifyNoActionErrors();
		verifyNoActionMessages();
		HibernateUtil.closeSession();
		removeFees(feesToRemove);
		assertNotNull(SessionUtils.getAttribute(CustomerConstants.PENDING_APPROVAL_DEFINED,request));
		center = TestObjectFactory.getObject(CenterBO.class,	center.getCustomerId());		
	}
	
	public void testSuccessfulPrevious() throws Exception {
		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "previous");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyForward(ActionForwards.previous_success.toString());
		verifyNoActionErrors();
		verifyNoActionMessages();
	}
	
	public void testSuccessfulCreate_UnderCenter() throws Exception {
		createParentCustomer();		
		HibernateUtil.closeSession();
		
		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("centerSystemId", center.getGlobalCustNum());
		actionPerform();
		
		List<CustomFieldDefinitionEntity> customFieldDefs = (List<CustomFieldDefinitionEntity>)SessionUtils.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request);
		
		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		addRequestParameter("displayName", "group");		
		addRequestParameter("status", CustomerStatus.GROUP_PENDING.getValue().toString());
		addRequestParameter("formedByPersonnel", center.getPersonnel().getPersonnelId().toString());
		int i = 0;
		for(CustomFieldDefinitionEntity customFieldDef: customFieldDefs){
			addRequestParameter("customField["+ i +"].fieldId", customFieldDef.getFieldId().toString());
			addRequestParameter("customField["+ i +"].fieldValue", "11");
			i++;
		}		
		actionPerform();
		
		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "create");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		
		verifyNoActionErrors();		
		verifyForward(ActionForwards.create_success.toString());
		
		GroupCustActionForm actionForm = (GroupCustActionForm)request.getSession().getAttribute("groupCustActionForm");
		
		group = TestObjectFactory.getObject(GroupBO.class, actionForm.getCustomerIdAsInt());
		center = TestObjectFactory.getObject(CenterBO.class, center.getCustomerId());
		actionForm.setParentCustomer(null);
	}
	
	public void testSuccessfulCreate_UnderBranch() throws Exception {
		createParentCustomer();		
		HibernateUtil.closeSession();
		
		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("branchId", officeId.toString());
		addRequestParameter("centerSystemId", center.getGlobalCustNum());
		actionPerform();
		
		List<CustomFieldDefinitionEntity> customFieldDefs = (List<CustomFieldDefinitionEntity>)SessionUtils.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request);
				
		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		addRequestParameter("displayName", "group");		
		addRequestParameter("status", CustomerStatus.GROUP_PENDING.getValue().toString());
		addRequestParameter("formedByPersonnel", center.getPersonnel().getPersonnelId().toString());
		int i = 0;
		for(CustomFieldDefinitionEntity customFieldDef: customFieldDefs){
			addRequestParameter("customField["+ i +"].fieldId", customFieldDef.getFieldId().toString());
			addRequestParameter("customField["+ i +"].fieldValue", "11");
			i++;
		}		
		actionPerform();
		
		SessionUtils.setAttribute(GroupConstants.CENTER_HIERARCHY_EXIST,Boolean.FALSE, request);
		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "create");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		
		verifyNoActionErrors();		
		verifyForward(ActionForwards.create_success.toString());
		
		GroupCustActionForm actionForm = (GroupCustActionForm)request.getSession().getAttribute("groupCustActionForm");
		
		group = TestObjectFactory.getObject(GroupBO.class, actionForm.getCustomerIdAsInt());
		center = TestObjectFactory.getObject(CenterBO.class, center.getCustomerId());
		actionForm.setParentCustomer(null);
	}
	
	public void testFailureCreate_DuplicateName() throws Exception {
		createGroupWithCenter();
		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("centerSystemId", center.getGlobalCustNum());
		actionPerform();
		
		List<CustomFieldDefinitionEntity> customFieldDefs = (List<CustomFieldDefinitionEntity>)SessionUtils.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request);
		
		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		addRequestParameter("displayName", "group");		
		addRequestParameter("status", CustomerStatus.GROUP_PENDING.getValue().toString());
		addRequestParameter("formedByPersonnel", center.getPersonnel().getPersonnelId().toString());		
		int i = 0;
		for(CustomFieldDefinitionEntity customFieldDef: customFieldDefs){
			addRequestParameter("customField["+ i +"].fieldId", customFieldDef.getFieldId().toString());
			addRequestParameter("customField["+ i +"].fieldValue", "11");
			i++;
		}
		actionPerform();
		
		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "create");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		
		actionPerform();
		verifyActionErrors(new String[]{CustomerConstants.ERRORS_DUPLICATE_CUSTOMER});
		verifyForward(ActionForwards.create_failure.toString());
		group = TestObjectFactory.getObject(GroupBO.class, group.getCustomerId());
		center = TestObjectFactory.getObject(CenterBO.class, center.getCustomerId());
		
		GroupCustActionForm actionForm = (GroupCustActionForm)request.getSession().getAttribute("groupCustActionForm");
		actionForm.setParentCustomer(null);
	}
	
	public void testGet() throws Exception {
		createCustomers();
		CustomerPositionEntity customerPositionEntity = new CustomerPositionEntity(
				new PositionEntity(Short.valueOf("1")), client, client
						.getParentCustomer());
		group.addCustomerPosition(customerPositionEntity);
		savingsBO = getSavingsAccount("fsaf6","ads6");
		loanBO = getLoanAccount();
		group.update();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();

		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "get");
		addRequestParameter("globalCustNum", group.getGlobalCustNum());
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.get_success.toString());

		center = TestObjectFactory.getObject(CenterBO.class,
				Integer.valueOf(center.getCustomerId()).intValue());
		group = TestObjectFactory.getObject(GroupBO.class,
				Integer.valueOf(group.getCustomerId()).intValue());
		client = TestObjectFactory.getObject(ClientBO.class,
				Integer.valueOf(client.getCustomerId()).intValue());
		loanBO = (LoanBO) new AccountPersistence().getAccount(loanBO
				.getAccountId());
		savingsBO = (SavingsBO) new AccountPersistence()
				.getAccount(savingsBO.getAccountId());

		assertEquals("Size of active loan accounts should be 1", 1,
				((List<LoanBO>) SessionUtils.getAttribute(
						GroupConstants.GROUPLOANACCOUNTSINUSE, request)).size());
		assertEquals("Size of active savings accounts should be 1", 1,
				((List<SavingsBO>) SessionUtils.getAttribute(
						GroupConstants.GROUPSAVINGSACCOUNTSINUSE, request))
						.size());
		assertEquals("No of active clients should be 1", 1,
				((List<CustomerBO>) SessionUtils.getAttribute(
						GroupConstants.CLIENT_LIST, request)).size());
		for (CustomerPositionEntity customerPosition : group
				.getCustomerPositions()) {
			assertEquals("Center Leader", customerPosition.getPosition()
					.getName());
			break;
		}
		TestObjectFactory.removeCustomerFromPosition(group);
		HibernateUtil.closeSession();
		center = TestObjectFactory.getObject(CenterBO.class,
				Integer.valueOf(center.getCustomerId()).intValue());
		group = TestObjectFactory.getObject(GroupBO.class,
				Integer.valueOf(group.getCustomerId()).intValue());
		client = TestObjectFactory.getObject(ClientBO.class,
				Integer.valueOf(client.getCustomerId()).intValue());
		loanBO = (LoanBO) new AccountPersistence().getAccount(loanBO
				.getAccountId());
		savingsBO = (SavingsBO) new AccountPersistence()
				.getAccount(savingsBO.getAccountId());
	}

	public void testManage() throws Exception {
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		createGroupWithCenterAndSetInSession();
		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "manage");
		addRequestParameter("officeId", "3");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.manage_success.toString());
		assertNotNull(SessionUtils.getAttribute(
				CustomerConstants.CUSTOM_FIELDS_LIST, request));
		assertNotNull(SessionUtils.getAttribute(CustomerConstants.CLIENT_LIST,
				request));
		assertNotNull(SessionUtils.getAttribute(CustomerConstants.POSITIONS,
				request));
	}

	public void testManageWithoutCenterHierarchy() throws Exception {		
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		createGroupWithCenterAndSetInSession();
		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "manage");
		addRequestParameter("officeId", "3");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.manage_success.toString());
		assertNotNull(SessionUtils.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST,request));
		assertNotNull(SessionUtils.getAttribute(CustomerConstants.CLIENT_LIST,request));
		assertNotNull(SessionUtils.getAttribute(CustomerConstants.POSITIONS,request));
	}
	
	public void testPreviewManageFailureForName() throws Exception {
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		createGroupWithCenterAndSetInSession();
		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "manage");
		addRequestParameter("officeId", "3");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		List<CustomFieldDefinitionEntity> customFieldDefs = (List<CustomFieldDefinitionEntity>) SessionUtils
				.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request);
		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "previewManage");
		addRequestParameter("officeId", "3");
		addRequestParameter("displayName", "");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request
				.getAttribute(Constants.CURRENTFLOWKEY));
		int i = 0;
		for (CustomFieldDefinitionEntity customFieldDef : customFieldDefs) {
			addRequestParameter("customField[" + i + "].fieldId",
					customFieldDef.getFieldId().toString());
			addRequestParameter("customField[" + i + "].fieldValue", "Req");
			i++;
		}
		addRequestParameter("trained", "1");
		addRequestParameter("trainedDate", "20/03/2006");
		actionPerform();
		assertEquals(1, getErrorSize());
		assertEquals("Group Name not present", 1,
				getErrorSize(CustomerConstants.NAME));

	}

	public void testPreviewManageFailureForTrainedDate() throws Exception {
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		createGroupWithCenterAndSetInSession();
		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "manage");
		addRequestParameter("officeId", "3");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		List<CustomFieldDefinitionEntity> customFieldDefs = (List<CustomFieldDefinitionEntity>) SessionUtils
				.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request);
		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "previewManage");
		addRequestParameter("officeId", "3");
		addRequestParameter("displayName", "group");
		int i = 0;
		for (CustomFieldDefinitionEntity customFieldDef : customFieldDefs) {
			addRequestParameter("customField[" + i + "].fieldId",
					customFieldDef.getFieldId().toString());
			addRequestParameter("customField[" + i + "].fieldValue", "Req");
			i++;
		}
		addRequestParameter("trained", "1");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request
				.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		assertEquals(1, getErrorSize());
		assertEquals("Group Trained date not present", 1,
				getErrorSize(CustomerConstants.TRAINED_DATE_MANDATORY));

	}

	public void testPreviewManageFailureForTrained() throws Exception {
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		createGroupWithCenterAndSetInSession();
		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "manage");
		addRequestParameter("officeId", "3");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		List<CustomFieldDefinitionEntity> customFieldDefs = (List<CustomFieldDefinitionEntity>) SessionUtils
				.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request);
		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "previewManage");
		addRequestParameter("officeId", "3");
		addRequestParameter("displayName", "group");
		int i = 0;
		for (CustomFieldDefinitionEntity customFieldDef : customFieldDefs) {
			addRequestParameter("customField[" + i + "].fieldId",
					customFieldDef.getFieldId().toString());
			addRequestParameter("customField[" + i + "].fieldValue", "Req");
			i++;
		}
		addRequestParameter("trainedDate", "03/20/2006");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request
				.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		assertEquals(1, getErrorSize());
		assertEquals("Group Trained checkbox not checked ", 1,
				getErrorSize(CustomerConstants.TRAINED_CHECKED));

	}
	
	public void testPreviewManageSuccess() throws Exception {
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		createGroupWithCenterAndSetInSession();
		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "manage");
		addRequestParameter("officeId", "3");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		List<CustomFieldDefinitionEntity> customFieldDefs = (List<CustomFieldDefinitionEntity>)SessionUtils.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request);
		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "previewManage");
		addRequestParameter("officeId", "3");
		addRequestParameter("displayName", "group");
		int i = 0;
		for(CustomFieldDefinitionEntity customFieldDef: customFieldDefs){
			addRequestParameter("customField["+ i +"].fieldId", customFieldDef.getFieldId().toString());
			addRequestParameter("customField["+ i +"].fieldValue", "Req");
			i++;
		}
		addRequestParameter("trained", "1");
		addRequestParameter("trainedDate", "20/03/2006");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String)request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.previewManage_success.toString());
		
		
	}
	
	public void testUpdateSuccess() throws Exception {
		String newDisplayName ="group_01";
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		createGroupWithCenterAndSetInSession();
		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "manage");
		addRequestParameter("officeId", "3");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		List<CustomFieldDefinitionEntity> customFieldDefs = (List<CustomFieldDefinitionEntity>)SessionUtils.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request);
		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "previewManage");
		addRequestParameter("officeId", "3");
		addRequestParameter("displayName", newDisplayName);
		int i = 0;
		for(CustomFieldDefinitionEntity customFieldDef: customFieldDefs){
			addRequestParameter("customField["+ i +"].fieldId", customFieldDef.getFieldId().toString());
			addRequestParameter("customField["+ i +"].fieldType", customFieldDef.getFieldType().toString());
			addRequestParameter("customField["+ i +"].fieldValue", "Req");
			i++;
		}
		addRequestParameter("trained", "1");
		addRequestParameter("trainedDate", "20/03/2006");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String)request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.previewManage_success.toString());
		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "update");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String)request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		verifyNoActionMessages();
		verifyForward(ActionForwards.update_success.toString());
		group = TestObjectFactory.getObject(GroupBO.class, Integer.valueOf(group.getCustomerId()).intValue());
		assertTrue(group.isTrained());
		assertEquals(newDisplayName ,group.getDisplayName());
	}
	
	public void testUpdateSuccessForLogging() throws Exception {
		String newDisplayName ="group_01";
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		createGroupWithCenterAndSetInSession();
		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "manage");
		addRequestParameter("officeId", "3");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		List<CustomFieldDefinitionEntity> customFieldDefs = (List<CustomFieldDefinitionEntity>)SessionUtils.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request);
		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "previewManage");
		addRequestParameter("officeId", "3");
		addRequestParameter("displayName", newDisplayName);
		int i = 0;
		for(CustomFieldDefinitionEntity customFieldDef: customFieldDefs){
			addRequestParameter("customField["+ i +"].fieldId", customFieldDef.getFieldId().toString());
			addRequestParameter("customField["+ i +"].fieldType", customFieldDef.getFieldType().toString());
			addRequestParameter("customField["+ i +"].fieldValue", "Req");
			i++;
		}
		addRequestParameter("trained", "1");
		addRequestParameter("trainedDate", "20/03/2006");
		addRequestParameter("externalId", "1");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String)request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.previewManage_success.toString());
		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "update");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String)request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.update_success.toString());
		group = TestObjectFactory.getObject(GroupBO.class, Integer.valueOf(group.getCustomerId()).intValue());
		assertTrue(group.isTrained());
		assertEquals(newDisplayName ,group.getDisplayName());
		TestObjectFactory.cleanUpChangeLog();
	}
	
	public void testUpdateSuccessWithoutTrained() throws Exception {
		String newDisplayName ="group_01";
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		createGroupWithCenterAndSetInSession();
		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "manage");
		addRequestParameter("officeId", "3");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		List<CustomFieldDefinitionEntity> customFieldDefs = (List<CustomFieldDefinitionEntity>)SessionUtils.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request);
		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "previewManage");
		addRequestParameter("officeId", "3");
		addRequestParameter("displayName", newDisplayName);
		int i = 0;
		for(CustomFieldDefinitionEntity customFieldDef: customFieldDefs){
			addRequestParameter("customField["+ i +"].fieldId", customFieldDef.getFieldId().toString());
			addRequestParameter("customField["+ i +"].fieldType", customFieldDef.getFieldType().toString());
			addRequestParameter("customField["+ i +"].fieldValue", "Req");
			i++;
		}
		addRequestParameter(Constants.CURRENTFLOWKEY, (String)request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.previewManage_success.toString());
		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "update");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String)request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.update_success.toString());
		assertTrue(!group.isTrained());
		assertEquals(newDisplayName ,group.getDisplayName());
		
	}
	
	public void testCancelSuccess() throws Exception {
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "cancel");
		addRequestParameter("input", GroupConstants.PREVIEW_MANAGE_GROUP);
		addRequestParameter(Constants.CURRENTFLOWKEY, (String)request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.cancelEdit_success.toString());		
	}	

	public void testCancelSuccessForCreateGroup() throws Exception {
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "cancel");
		addRequestParameter("input", GroupConstants.CREATE_GROUP);
		addRequestParameter(Constants.CURRENTFLOWKEY, (String)request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.cancelCreate_success.toString());		
	}
	
	public void testLoadSearch()throws Exception{
		addActionAndMethod(Methods.loadSearch.toString());
		addCurrentFlowKey();
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.loadSearch_success.toString());
	}
	public void testSearch()throws Exception{
		createGroupWithCenter();
		addRequestParameter("searchString", "gr");
		addActionAndMethod(Methods.search.toString());
		addCurrentFlowKey();
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.search_success.toString());
		QueryResult queryResult = (QueryResult)SessionUtils.getAttribute(Constants.SEARCH_RESULTS,request);
		assertNotNull(queryResult);
		assertEquals(1,queryResult.getSize());
		assertEquals(1,queryResult.get(0,10).size());
		
	}	
	private void addActionAndMethod(String method){
		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", method);

	}
	private void addCurrentFlowKey(){
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		addRequestParameter(Constants.CURRENTFLOWKEY,flowKey);
	}
	private void createGroupWithCenterAndSetInSession() throws Exception {
		createGroupWithCenter();
		center = TestObjectFactory.getObject(CenterBO.class,
				Integer.valueOf(center.getCustomerId()).intValue());
		group = TestObjectFactory.getObject(GroupBO.class,
				Integer.valueOf(group.getCustomerId()).intValue());
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, group, request);
	}

	private void createGroupWithCenter()throws Exception{
		createParentCustomer();
		group = TestObjectFactory.createGroupUnderCenter("group",CustomerStatus.GROUP_ACTIVE, center);
		HibernateUtil.closeSession();
	}
	
	private void createParentCustomer() {
		meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getTypicalMeeting());
		center = TestObjectFactory.createCenter("Center",
				meeting);
	}
	
	private void createCenterWithoutFee()throws Exception{
		meeting = new MeetingBO(WeekDay.MONDAY, Short.valueOf("1"), new Date(), MeetingType.CUSTOMER_MEETING, "Delhi");
		center  = new CenterBO(userContext, "MyCenter", null, null, null, "1234", null, officeId, meeting, Short.valueOf("3"));
		center.save();
		HibernateUtil.commitTransaction();
	}

	private void createCustomers() {
		createParentCustomer();
		group = TestObjectFactory.createGroupUnderCenter("group", CustomerStatus.GROUP_ACTIVE, center);
		client = TestObjectFactory.createClient("Client",
				CustomerStatus.CLIENT_ACTIVE, group);
		HibernateUtil.closeSession();
	}

	private LoanBO getLoanAccount() {
		Date startDate = new Date(System.currentTimeMillis());
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				startDate, meeting);
		return TestObjectFactory.createLoanAccount("42423142341", group, 
				AccountState.LOANACC_ACTIVEINGOODSTANDING, 
				startDate, loanOffering);

	}

	private SavingsBO getSavingsAccount(String offeringName,String shortName) throws Exception {
		savingsOffering = helper.createSavingsOffering(offeringName,shortName);
		return TestObjectFactory.createSavingsAccount("000100000000017", group,
				AccountStates.SAVINGS_ACC_APPROVED, new Date(System
						.currentTimeMillis()), savingsOffering);
	}
	
	private List<FeeView> getFees(RecurrenceType frequency) {
		List<FeeView> fees = new ArrayList<FeeView>();
		AmountFeeBO fee1 = (AmountFeeBO) TestObjectFactory
				.createPeriodicAmountFee("PeriodicAmountFee",
						FeeCategory.GROUP, "200", frequency, Short.valueOf("2"));
		fees.add(new FeeView(TestObjectFactory.getContext(),fee1));
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		return fees;
	}
	
	private void removeFees(List<FeeView> feesToRemove){
		for(FeeView fee :feesToRemove){
			TestObjectFactory.cleanUp(new FeePersistence().getFee(fee.getFeeIdValue()));
		}
	}
}
