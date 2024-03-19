package tasks;

public class Task {
    protected String title;
    protected String description;
    protected StatusOfTasks status;
    protected int ind;
    TaskType taskType;

    public Task(String title, String description, StatusOfTasks status) {
        this.title = title;
        this.description = description;
        this.status = status;
    }
    public Task(int ind, String title, String description, StatusOfTasks status) {
        this.ind = ind;
        this.title = title;
        this.description = description;
        this.status = status;
        this.taskType = TaskType.TASK;
    }
    public Task(){}

    public void setId(int ind) {
        this.ind = ind;
    }

    public int getInd() {
        return ind;
    }

    public StatusOfTasks getStatus() {
        return status;
    }

    public void setStatus(StatusOfTasks status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "ЗАДАЧА{" +
                "№=" + ind +
                ", Название='" + title + '\'' +
                ", Описание='" + description + '\'' +
                ", Статус='" + status + '\'' +
                '}';
    }
    public String toStringFromFile() {
        return String.format("%s,%s,%s,%s,%s,%s", ind, taskType, title, status, description, "");
    }

}


