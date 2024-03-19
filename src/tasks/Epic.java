package tasks;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> epicSubtasks;

    public Epic(String title, String description) {
        super(title, description, null);
        epicSubtasks = new ArrayList<>();
    }
    public Epic(int id,String title, String description, StatusOfTasks status){
        super(id, title, description, null);
        epicSubtasks = new ArrayList<>();
        this.taskType = TaskType.EPIC;
        this.status = status;
    }

    public ArrayList<Integer> getIndSubtasks() {
        return epicSubtasks;
    }

    public void setIndSubtasks(ArrayList<Integer> epicSubtasks) {
        this.epicSubtasks = epicSubtasks;
    }

    @Override
    public String toString() {
        return "Эпик{" +
                "№=" + ind +
                ", Название='" + title + '\'' +
                ", Описание='" + description + '\'' +
                ", Статус='" + status + '\'' +
                '}';
    }


}
