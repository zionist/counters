package com.tickeron.counters.dto;

/**
 * Created by slaviann on 16.02.16.
 */
public final class LoadTestRun {

    private Integer loadTestRunId;

    private String loadTestName;

    private String startTime;

    private String endTime;

    private Integer runDuration;

    private Integer warmupTime;

    private String outcome;

    public Integer getLoadTestRunId() {
        return loadTestRunId;
    }

    public void setLoadTestRunId(Integer loadTestRunId) {
        this.loadTestRunId = loadTestRunId;
    }

    public String getLoadTestName() {
        return loadTestName;
    }

    public void setLoadTestName(String loadTestName) {
        this.loadTestName = loadTestName;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Integer getRunDuration() {
        return runDuration;
    }

    public void setRunDuration(Integer runDuration) {
        this.runDuration = runDuration;
    }

    public Integer getWarmupTime() {
        return warmupTime;
    }

    public void setWarmupTime(Integer warmupTime) {
        this.warmupTime = warmupTime;
    }

    public String getOutcome() {
        return outcome;
    }

    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }
}
