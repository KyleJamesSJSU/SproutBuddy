package sproutbuddy.model;

import java.sql.Date;

public class CareTask {
    private final int taskId;
    private final String nickname;
    private final String commonName;
    private final String taskType;
    private final Date dueDate;
    private final int intervalDays;

    public CareTask(int taskId, String nickname, String commonName,
                    String taskType, Date dueDate, int intervalDays) {
        this.taskId = taskId;
        this.nickname = nickname;
        this.commonName = commonName;
        this.taskType = taskType;
        this.dueDate = dueDate;
        this.intervalDays = intervalDays;
    }

    public int getTaskId() { return taskId; }
    public String getNickname() { return nickname; }
    public String getCommonName() { return commonName; }
    public String getTaskType() { return taskType; }
    public Date getDueDate() { return dueDate; }
    public int getIntervalDays() { return intervalDays; }
}
