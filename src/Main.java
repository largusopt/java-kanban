import managers.InMemoryTaskManager;
import tasks.Epic;
import tasks.StatusOfTasks;
import tasks.Subtask;
import tasks.Task;

public class Main {

    public static void main(String[] args) {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        manager.addTasks(new Task("купить молоко", "-", StatusOfTasks.DONE));
        Epic epic1 = new Epic("переезд", "-");
        manager.addEpic(epic1);
        manager.addSubtask(new Subtask("Проверка сабтаск", "№1", StatusOfTasks.DONE, epic1));
        manager.addSubtask(new Subtask("Отлично работает", "№2", StatusOfTasks.DONE, epic1));
        manager.getIndexSubtask(3).setStatus(StatusOfTasks.NEW);
        manager.updateSubtask(manager.getIndexSubtask(3));
        manager.getIndexSubtask(3);

        System.out.println("Задачи:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Task epic : manager.getAllEpic()) {
            System.out.println(epic);

            for (Task task : manager.getSubtaskForEp(epic1)) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : manager.getAllSubtask()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }
}

