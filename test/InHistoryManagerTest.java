

import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.StatusOfTasks;
import tasks.Task;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InHistoryManagerTest {

    private static TaskManager managerTest;
    private Task task1;
    private Task subTask1;


    @BeforeEach
    void beforeEach() {
        managerTest = Managers.getDefault();
        //parentTask = new Task("Parent Task", "Description", StatusOfTasks.DONE);
        task1 = new Task("Проверка", "связи", StatusOfTasks.DONE);
        subTask1 = new Task("Subtask 1", "Description", StatusOfTasks.IN_PROGRESS);
    }


    @Test
    public void shouldSaveOldVersion() {
        List<Task> viewedTask = new ArrayList<>();

        managerTest.addTasks(task1);


        viewedTask.add(managerTest.getIndexTasks(1));

        task1.setStatus(StatusOfTasks.NEW);
        managerTest.updateTasks(task1);

        viewedTask.add(managerTest.getIndexTasks(1));

        // Убеждаемся, что история не равна списку просмотренных задач
        assertNotEquals(viewedTask, managerTest.getHistory());
    }

    @Test
    void shouldRemoveTaskFromHistory() {

        managerTest.addTasks(task1);

        // Удаляем подзадачу
        managerTest.removeIndexSubtask(subTask1.getInd());

        // Проверяем, что подзадача удалена и не содержит старого id
        List<Task> history = managerTest.getHistory();

        // Проверяем, что задача удалена из истории
        assertTrue(history.isEmpty());
    }

    @Test
    void shouldRemoveOldFromHistory() {
        managerTest.addTasks(task1);
        managerTest.getIndexTasks(task1.getInd());
        assertEquals(1, managerTest.getHistory().size());
        managerTest.getIndexTasks(task1.getInd());
        assertEquals(1, managerTest.getHistory().size());
    }

}