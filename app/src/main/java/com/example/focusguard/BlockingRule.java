package com.example.focusguard;

public class BlockingRule {
    BlockingType blockingType;
    String packageName;
    long timeLimit;
    String datascheduling;

    public BlockingRule() {
    }

    public BlockingRule(BlockingType blockingType, String packageName, long timeLimit, String datascheduling) {
        this.blockingType = blockingType;
        this.packageName = packageName;
        this.timeLimit = timeLimit;
        this.datascheduling = datascheduling;
    }

    public BlockingType getBlockingType() {
        return blockingType;
    }

    public void setBlockingType(BlockingType blockingType) {
        this.blockingType = blockingType;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public long getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(long timeLimit) {
        this.timeLimit = timeLimit;
    }

    public String getDatascheduling() {
        return datascheduling;
    }

    public void setDatascheduling(String datascheduling) {
        this.datascheduling = datascheduling;
    }
}
