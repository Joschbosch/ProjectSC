/*
 * Project SC - 2015
 * 
 * 
 */

package de.projectsc.modes.server.game.utils;


/**
 * 
 * Event that will be triggered in the future.
 * 
 * @author Josch Bosch
 */
public class FutureEvent implements Comparable<FutureEvent> {

    private long executionTime;

    private Task task;

    public FutureEvent(long executionTime, Task task) {
        this.executionTime = executionTime;
        this.task = task;
    }

    @Override
    public int compareTo(FutureEvent o) {
        return (int) (executionTime - o.getExecutionTime());
    }

    public long getExecutionTime() {
        return executionTime;
    }

    public Task getTask() {
        return task;
    }
}
