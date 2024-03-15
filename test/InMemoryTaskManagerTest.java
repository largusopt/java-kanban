
import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.StatusOfTasks;
import tasks.Subtask;
import tasks.Task;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class InMemoryTaskManagerTest {
    private static TaskManager manager;
    Task task1 = new Task("Проверка", "связи", StatusOfTasks.DONE);
    Epic epic1 = new Epic("Проверка", "№1");
    Epic epic2 = new Epic("Проверка", "№2");

    @BeforeEach
    public void beforeEach() {
        manager = Managers.getDefault();
    }

    @Test
    void managersShouldBeNotNull() {
        assertNotEquals(null, manager);
    }

    @Test
    void shouldEgualsTask() {
        manager.addTasks(task1);
        assertEquals(manager.getIndexTasks(1), manager.getIndexTasks(1));
    }

    @Test
    void shouldEgualsEpic() {
        manager.addEpic(epic1);
        assertEquals(manager.getIndexEpic(1), manager.getIndexEpic(1));
    }

    @Test
    void shouldUpdateStatus() {

        manager.addEpic(epic1);
        Subtask subtask1 = new Subtask("Проверка сабтаск", "№1", StatusOfTasks.DONE, epic1);
        Subtask subtask2 = new Subtask("Проверка сабтаск", "№2", StatusOfTasks.DONE, epic1);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        assertEquals(StatusOfTasks.DONE, epic1.getStatus());
        subtask1.setStatus(StatusOfTasks.NEW);
        manager.updateSubtask(subtask1);
        assertEquals(StatusOfTasks.IN_PROGRESS, epic1.getStatus());

    }

    @Test
    void shouldChangeIdSameTasks() {

        assertEquals(1, manager.addTasks(task1).getInd());
        assertEquals(2, manager.addTasks(task1).getInd());
    }

    @Test
    void shouldAddTaskandFindbyId() {

        Task taskN1 = manager.addTasks(task1);
        Task taskN2 = manager.addTasks(task1);
        assertNotEquals(null, manager.getAllTasks());
        assertEquals(2, manager.getAllTasks().size());
        assertEquals(2, taskN2.getInd());
    }

    @Test
    void shoulEpicandFindbyId() {

        manager.addEpic(epic1);
        manager.addEpic(epic2);
        assertNotEquals(null, manager.getAllEpic());
        assertEquals(2, manager.getAllEpic().size());
        assertEquals(1, epic1.getInd());
    }

    @Test
    void shouldEpicTaskandFindbyId() {

        manager.addEpic(epic1);
        manager.addEpic(epic2);
        Subtask subtask1 = new Subtask("Проверка сабтаск", "№1", StatusOfTasks.DONE, epic1);
        Subtask subtask2 = new Subtask("Проверка сабтаск", "№1", StatusOfTasks.DONE, epic1);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        assertNotEquals(null, manager.getAllSubtask());
        assertEquals(2, manager.getAllSubtask().size());
        assertEquals(3, subtask1.getInd());

    }

}

