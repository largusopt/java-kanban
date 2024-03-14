package managers;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.Collection;
import java.util.List;

public interface TaskManager {
    Collection<Task> getAllTasks();

    Collection<Epic> getAllEpic();

    Collection<Subtask> getAllSubtask();

    Task addTasks(Task task);

    Epic addEpic(Epic epic);

    void addSubtask(Subtask subtask);

    void deleteTasks();

    void deleteEpic();

    void deleteSubtask();

    Task getIndexTasks(int index);

    Task getIndexEpic(int index);

    Subtask getIndexSubtask(int index);

    void updateTasks(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    void removeIndexTasks(int index);

    void removeIndexEpic(int index);

    void removeIndexSubtask(int index);

    List<Task> getHistory();
}
