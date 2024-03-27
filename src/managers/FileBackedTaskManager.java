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
                String errorMessage = "Ошибка при создании файла 'back up.csv'";
                throw new ManagerSaveException(errorMessage, e);
            }
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fileBackedTasksManager = new FileBackedTaskManager(file);
        String data = "";
        try {
            data = Files.readString(Path.of(file.getAbsolutePath()));
        } catch (IOException e) {
            String errorMessage = "Ошибка при чтении файла 'back up.csv'";
            throw new ManagerSaveException(errorMessage, e);
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

                        if (subtask.getInd() > maxId) {
                            maxId = subtask.getInd();
                        }
                        int epicId = Integer.parseInt(line.split(",")[5].trim());
                        Epic parentEpic = fileBackedTasksManager.epicMap.get(epicId);
                        if (parentEpic != null) {
                            parentEpic.getIndSubtasks().add(subtask.getInd());
                        } else {
                            System.out.println("Эпик с id " + epicId + " не найден");
                        }
                        fileBackedTasksManager.subtaskMap.put(subtask.getInd(), subtask);
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
                Epic parentEpic = fileBackedTaskManager.epicMap.get(Integer.valueOf(epicId));
                if (parentEpic != null) {
                    return new Subtask(id, name, description, status, parentEpic);
                } else {
                    throw new RuntimeException("Эпик с id " + epicId + " не найден");
                }

            default:
                return null;
        }
    }

    private static Task getTasksOfDifferentKinds(int id, InMemoryTaskManager inMemoryTaskManager) {
        Task task = inMemoryTaskManager.getIndexTasks(id);
        if (task != null) {
            return task;
        }
        Task epic = inMemoryTaskManager.getIndexEpic(id);
        if (epic != null) {
            return epic;
        }
        Task subtask = inMemoryTaskManager.getIndexSubtask(id);
        if (subtask != null) {
            return subtask;
        }
        return null;
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


            for (Task ts : super.getAllTasks()) {
                writer.write(ts.toStringFromFile() + "\n");
            }

            for (Epic ep : super.getAllEpic()) {
                writer.write(ep.toStringFromFile() + "\n");
            }

            for (Subtask sb : super.getAllSubtask()) {
                writer.write(sb.toStringFromFile() + "\n");
            }

            writer.write("\n");
            writer.write(toString(this.historyManager));

        } catch (IOException e) {
            String errorMessage = "Ошибка при сохранении файла 'back up.csv'";
            throw new ManagerSaveException(errorMessage, e);
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

