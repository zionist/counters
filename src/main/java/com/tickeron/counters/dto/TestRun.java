package com.tickeron.counters.dto;

/**
 * Created by slaviann on 19.02.16.
 */
public final class TestRun {

    private String description;

    public String getRunId() {
        return runId;
    }

    public void setRunId(String runId) {
        this.runId = runId;
    }

    private String runId;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
