package managers;

import exception.ManagerSaveException;
import tasks.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;


public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;
    private String fileName;

    public FileBackedTaskManager(File file) {
        this.file = file;

        fileName = "./data/data.csv";
        file = new File(fileName);
        if (!file.isFile()) {
            try {
                Path path = Files.createFile(Paths.get(fileName));
            } catch (IOException e) {
                throw new ManagerSaveException("Ошибка создания файла.");
            }
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fileBackedTasksManager = new FileBackedTaskManager(file);
        String data = "";
        try {
            data = Files.readString(Path.of(file.getAbsolutePath()));
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка чтения файла.");
        }
        String[] lines = data.split("\n");
        String history = "";
        boolean isTitle = true;
        boolean isTask = true;
        int maxId = 0;
        int id;

        for (String line : lines) {
            if (isTitle) {
                isTitle = false;
                continue;
            }
            if (line.isEmpty() || line.equals("\r")) {
                isTask = false;
                continue;
            }
            if (isTask) {
                TaskType type = TaskType.valueOf(line.split(",")[1]);
                switch (type) {
                    case EPIC:
                        Epic epic = (Epic) fromString(line, TaskType.EPIC, fileBackedTasksManager);
                        id = epic.getInd();
                        if (id > maxId) {
                            maxId = id;
                        }
                        fileBackedTasksManager.epicMap.put(id, epic);
                        break;

                    case SUBTASK:
                        Subtask subtask = (Subtask) fromString(line, TaskType.SUBTASK, fileBackedTasksManager);
                        id = subtask.getInd();
                        if (id > maxId) {
                            maxId = id;
                        }
                        fileBackedTasksManager.subtaskMap.put(id, subtask);
                        break;

                    case TASK:
                        Task task = fromString(line, TaskType.TASK, fileBackedTasksManager);

                        id = task.getInd();
                        if (id > maxId) {
                            maxId = id;
                        }
                        fileBackedTasksManager.taskMap.put(id, task);
                        break;

                }
            } else {
                history = line;
            }
        }
        fileBackedTasksManager.ind = maxId;
        List<Integer> ids = historyFromString(history);
        for (Integer taskId : ids) {
            fileBackedTasksManager.historyManager.addTask(getTasksOfDifferentKinds(taskId, fileBackedTasksManager));
        }
        return fileBackedTasksManager;
    }

    private static List<Integer> historyFromString(String value) {
        String[] idsString = value.split(",");
        List<Integer> tasksId = new ArrayList<>();
        for (String idString : idsString) {
            tasksId.add(Integer.valueOf(idString));
        }
        return tasksId;
    }

    public static Task fromString(String lines, TaskType taskType, FileBackedTaskManager fileBackedTaskManager) {
        String[] line = lines.split(",");
        String epicId = null;
        int id = Integer.parseInt(line[0]);
        String name = String.valueOf(line[2]);
        StatusOfTasks status = StatusOfTasks.valueOf(line[3]);
        String description = line[4];
        if (line.length == 6) {
            epicId = line[5].trim();
        }

        switch (taskType) {
            case TASK:
                return new Task(id, name, description, status);
            case EPIC:
                return new Epic(id, name, description, status);
            case SUBTASK:
                return new Subtask(id, name, description, status, fileBackedTaskManager.epicMap.get(Integer.valueOf(epicId)));
            default:
                return null;
        }
    }

    private static Task getTasksOfDifferentKinds(int id, InMemoryTaskManager inMemoryTaskManager) {
        Task task = null;
        for (Task t : inMemoryTaskManager.getAllTasks()) {
            if (t.getInd() == id) {
                task = t;
                break;
            }
        }
        if (task != null) {
            return task;
        }
        Epic epic = null;
        for (Epic e : inMemoryTaskManager.getAllEpic()) {
            if (e.getInd() == id) {
                epic = e;
                break;
            }
        }
        Subtask subtask = null;
        for (Subtask s : inMemoryTaskManager.getAllSubtask()) {
            if (s.getInd() == id) {
                subtask = s;
                break;
            }
        }
        return subtask;
    }

    private static String toString(HistoryManager manager) {
        List<String> s = new ArrayList<>();
        for (Task task : manager.getHistory()) {
            s.add(String.valueOf(task.getInd()));
        }
        String hist = String.join(",", s);
        return hist;
    }

    public void save() {
        try (Writer writer = new FileWriter(file)) {
            writer.write("id,type,title,status,description,epic\n");
            HashMap<Integer, String> allTasks = new HashMap<>();

            Collection<Task> tasks = super.getAllTasks();
            for (Task ts : tasks) {
                allTasks.put(ts.getInd(), ts.toStringFromFile());
            }
            Collection<Epic> epics = super.getAllEpic();
            for (Epic ep : epics) {
                allTasks.put(ep.getInd(), ep.toStringFromFile());
            }

            Collection<Subtask> subtasks = super.getAllSubtask();
            for (Subtask sb : subtasks) {
                allTasks.put(sb.getInd(), sb.toStringFromFile());
            }
            for (String name : allTasks.values()) {
                writer.write(String.format("%s\n", name));
            }
            writer.write("\n");
            writer.write(toString(this.historyManager));

        } catch (IOException e) {
            throw new ManagerSaveException("Невозможно прочитать файл.");
        }
    }

    @Override
    public Task addTasks(Task task) {
        super.addTasks(task);
        save();
        return task;
    }

    @Override
    public void updateTasks(Task task) {
        super.updateTasks(task);
        save();
    }

    @Override
    public Task getIndexTasks(int id) {
        Task task = super.getIndexTasks(id);
        save();
        return task;
    }

    @Override
    public void removeIndexTasks(int id) {
        super.removeIndexTasks(id);
        save();
    }

    @Override
    public void deleteTasks() {
        super.deleteTasks();
        save();
    }

    @Override
    public Epic addEpic(Epic epic) {
        super.addEpic(epic);
        save();
        return epic;
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public Epic getIndexEpic(int id) {
        Epic epic = (Epic) super.getIndexEpic(id);
        save();
        return epic;
    }

    @Override
    public void removeIndexEpic(int id) {
        super.removeIndexEpic(id);
        save();
    }

    @Override
    public void deleteEpic() {
        super.deleteEpic();
        save();
    }

    @Override
    public void addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public Subtask getIndexSubtask(int id) {
        Subtask subtask = super.getIndexSubtask(id);
        save();
        return subtask;
    }

    @Override
    public void removeIndexSubtask(int id) {
        super.removeIndexSubtask(id);
        save();
    }

    @Override
    public void deleteSubtask() {
        super.deleteSubtask();
        save();
    }

    @Override
    public List<Task> getHistory() {
        return super.getHistory();
    }
}

