/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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
package org.mifos.platform.cashflow.service;

import org.joda.time.DateTime;

import java.io.Serializable;

public class MonthlyCashFlowDetail implements Serializable {
    private static final long serialVersionUID = 8951437837952219469L;

    private final DateTime dateTime;
    private Double expense;
    private String notes;
    private Double revenue;
    private CashFlowDetail cashFlowDetail;

    public MonthlyCashFlowDetail(DateTime dateTime, Double revenue, Double expense, String notes) {
        this.dateTime = dateTime;
        this.revenue = revenue;
        this.expense = expense;
        this.notes = notes;
    }

    public Double getRevenue() {
        return revenue;
    }

    public void setRevenue(Double revenue) {
        this.revenue = revenue;
    }

    public Double getExpense() {
        return expense;
    }

    public void setExpense(Double expense) {
        this.expense = expense;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public DateTime getDateTime() {
        return dateTime;
    }

    public Double getCumulativeCashFlow() {
        return cashFlowDetail.getCumulativeCashFlowForMonth(dateTime);
    }

    public void setCashFlowDetail(CashFlowDetail cashFlowDetail) {
        this.cashFlowDetail = cashFlowDetail;
    }
}
