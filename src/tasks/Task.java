package tasks;

public class Task {
    protected String title;
    protected String description;
    protected StatusOfTasks status;
    protected int ind;

    public Task(String title, String description, StatusOfTasks status) {
        this.title = title;
        this.description = description;
        this.status = status;
    }

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


}


