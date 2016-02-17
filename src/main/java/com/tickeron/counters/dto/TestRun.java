package com.tickeron.counters.dto;

/**
 * Created by slaviann on 16.02.16.
 */
public final class TestRun {

    private String startTime;

    private String runId;

    private String endTime;

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getRunId() {
        return runId;
    }

    public void setRunId(final String runId) {
        this.runId = runId;
    }

    public String getStartTime() {
        return startTime;
    }
}
