package tasks;

public class Subtask extends Task {

    int epicId;

    public Subtask(String title, String description, StatusOfTasks status, Epic epic) {
        super(title, description, status);
        epicId = epic.getInd();
    }

    public int getEpicId() {
        return epicId;
    }


    @Override
    public String toString() {
        return "Подзадача{" +
                " Название='" + title + '\'' +
                ", Описание='" + description + '\'' +
                ", Статус='" + status + '\'' +
                ", эпик='" + getEpicId() + '\'' +
                ", id='" + getInd() + '\'' +
                '}';
    }
}
