package managers;

import tasks.Epic;
import tasks.StatusOfTasks;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    HashMap<Integer, Task> taskMap = new HashMap<>();
    HashMap<Integer, Epic> epicMap = new HashMap<>();
    HashMap<Integer, Subtask> subtaskMap = new HashMap<>();
    HistoryManager historyManager = Managers.getDefaultHistory();
    int ind = 0;

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public Collection<Task> getAllTasks() { // печать всех tasks.Task
        return new ArrayList<>(taskMap.values());
    }

    @Override
    public Collection<Epic> getAllEpic() { // печать всех tasks.Epic
        return new ArrayList<>(epicMap.values());
    }

    @Override
    public Collection<Subtask> getAllSubtask() { // печать всех tasks.Subtask
        return new ArrayList<>(subtaskMap.values());
    }

    @Override
    public Task addTasks(Task task) { // создание tasks.Task
        if ((!taskMap.containsKey(task.getInd()))) {
            task.setId(++ind);
            taskMap.put(ind, task);
            return task;
        } else {
            Task taskNew = new Task(task.getTitle(), task.getDescription(), task.getStatus());
            taskNew.setId(++ind);
            taskMap.put(ind, taskNew);
            return taskNew;
        }

    }

    @Override
    public Epic addEpic(Epic epic) {
        if ((!epicMap.containsKey(epic.getInd()))) {
            epic.setId(++ind);
            epicMap.put(ind, epic);
        } else {
            Epic epicNew = new Epic(epic.getTitle(), epic.getDescription());
            epicNew.setId(++ind);
            taskMap.put(ind, epicNew);
        }
        return epic;
    }

    @Override
    public void addSubtask(Subtask subtask) {
        subtask.setId(++ind);
        subtaskMap.put(ind, subtask);
        epicMap.get(subtask.getEpicId()).getIndSubtasks().add(ind);
        setNewStatus(epicMap.get(subtask.getEpicId()));
    }

    @Override
    public void deleteTasks() { // удалить весь список задач
        for (Task task :taskMap.values()){
            historyManager.remove(task.getInd());
        }
        taskMap.clear();
    }

    @Override
    public void deleteEpic()
    {
        for (Epic epic: epicMap.values()){

            for (Integer indSubtask : epic.getIndSubtasks()) {
                historyManager.remove(indSubtask);
            }
            historyManager.remove(epic.getInd());
        }
        epicMap.clear();
        subtaskMap.clear();
    }

    @Override
    public void deleteSubtask() {
        for (Subtask i : subtaskMap.values()) {
            epicMap.get(i.getEpicId()).setStatus(StatusOfTasks.NEW);
            historyManager.remove(i.getInd());
        }
        subtaskMap.clear();
    }

    @Override
    public Task getIndexTasks(int index) {
        Task task = taskMap.get(index);
        historyManager.addTask(task);
        return task;

    }

    @Override
    public Task getIndexEpic(int index) {
        Task task = epicMap.get(index);
        historyManager.addTask(task);
        return task;
    }

    @Override
    public Subtask getIndexSubtask(int index) {
        Subtask task = subtaskMap.get(index);
        historyManager.addTask(task);
        return task;
    }

    @Override
    public void updateTasks(Task task) {

        if (taskMap.containsKey(task.getInd())) {
            taskMap.put(task.getInd(), task);
        }

    }

    @Override
    public void updateEpic(Epic epic) {
        if (epicMap.containsKey(epic.getInd())) {
            epic.setIndSubtasks(epicMap.get(epic.getInd()).getIndSubtasks());
            epicMap.put(epic.getInd(), epic);
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if ((epicMap.containsKey(subtask.getEpicId())) && (!epicMap.containsKey(subtask.getInd())) && (!subtaskMap.containsKey(subtask.getEpicId()))) {
            subtaskMap.put(subtask.getInd(), subtask);
            setNewStatus(epicMap.get(subtask.getEpicId()));
        }
    }

    @Override
    public void removeIndexTasks(int index) { // удалить задачу под заданным индексом
        if (taskMap.containsKey(index)) {
            System.out.println("Элемент с индексом " + index + " удален");
            taskMap.remove(index);
            historyManager.remove(index);
        }
    }

    @Override
    public void removeIndexEpic(int index) { // удалить tasks.Epic под заданным индексом
        if (epicMap.containsKey(index)) {
            System.out.println("Эпик с индексом " + index + " удален");
            Epic epic = epicMap.get(index); // получили название эпика
            epicMap.remove(index);
            historyManager.remove(index);
            for (Integer indSubtask : epic.getIndSubtasks()) {
                subtaskMap.remove(indSubtask);
                historyManager.remove(indSubtask);
            }
        }
    }

    @Override
    public void removeIndexSubtask(int index) { // удалить подзадачу под заданным индексом
        if (subtaskMap.containsKey(index)) {
            Epic epic = epicMap.get(subtaskMap.get(index).getEpicId()); // определила задачу в которой есть индекс подазадачи
            epic.getIndSubtasks().remove((Integer) index);
            System.out.println("Сабтаск с индексом " + index + " удален");
            subtaskMap.remove(index);
            setNewStatus(epic);
            historyManager.remove(index);
        }
    }

    public Collection<Subtask> getSubtaskForEp(Epic epic) {
        ArrayList<Subtask> subTask = new ArrayList<>();
        for (int i : epic.getIndSubtasks()) {
            subTask.add(subtaskMap.get(i));
        }
        return subTask;
    }

    void setNewStatus(Epic epic) {
        if (epic.getIndSubtasks().size() == 0) {
            epic.setStatus(StatusOfTasks.NEW);
            return;
        }
        boolean allTaskIsNew = true;
        boolean allTaskIsDone = true;

        for (Integer i : epic.getIndSubtasks()) {
            StatusOfTasks status = subtaskMap.get(i).getStatus();
            if (status != StatusOfTasks.DONE) {
                allTaskIsDone = false;
            }
            if (status != StatusOfTasks.NEW) {
                allTaskIsNew = false;
            }

        }
        if (allTaskIsNew) {
            epic.setStatus(StatusOfTasks.NEW);
        } else if (allTaskIsDone) {
            epic.setStatus(StatusOfTasks.DONE);
        } else {
            epic.setStatus(StatusOfTasks.IN_PROGRESS);
        }

    }

}
