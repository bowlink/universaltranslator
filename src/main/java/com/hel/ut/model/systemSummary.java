/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hel.ut.model;

/**
 *
 * @author chadmccue
 */
public class systemSummary {

    Integer batchesPastHour = 0, batchesToday = 0, batchesThisWeek = 0, batchesThisMonth = 0, batchesInError = 0, batchesToProcess = 0;

    public Integer getBatchesPastHour() {
        return batchesPastHour;
    }

    public void setBatchesPastHour(Integer batchesPastHour) {
        this.batchesPastHour = batchesPastHour;
    }

    public Integer getBatchesToday() {
        return batchesToday;
    }

    public void setBatchesToday(Integer batchesToday) {
        this.batchesToday = batchesToday;
    }

    public Integer getBatchesThisWeek() {
        return batchesThisWeek;
    }

    public void setBatchesThisWeek(Integer batchesThisWeek) {
        this.batchesThisWeek = batchesThisWeek;
    }

    public Integer getBatchesThisMonth() {
        return batchesThisMonth;
    }

    public void setBatchesThisMonth(Integer batchesThisMonth) {
        this.batchesThisMonth = batchesThisMonth;
    }

    public Integer getBatchesInError() {
        return batchesInError;
    }

    public void setBatchesInError(Integer batchesInError) {
        this.batchesInError = batchesInError;
    }

    public Integer getBatchesToProcess() {
        return batchesToProcess;
    }

    public void setBatchesToProcess(Integer batchesToProcess) {
        this.batchesToProcess = batchesToProcess;
    }
}