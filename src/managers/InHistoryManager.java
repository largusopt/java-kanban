package managers;

import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class InHistoryManager implements HistoryManager {
    public Map<Integer, Node<Task>> receivedTasksMap;
    public Node<Task> tail;
    public Node<Task> head;

    public InHistoryManager() {

        receivedTasksMap = new HashMap<>();
    }

    @Override
    public void addTask(Task task) {
        remove(task.getInd());
        linkLast(task);


    }

    public List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        Node<Task> currentNode = head;
        while (currentNode != null) {
            tasks.add(currentNode.data);
            currentNode = currentNode.next;

        }
        return tasks;

    }

    public void remove(int id) {
        removeNode(receivedTasksMap.get(id));
        receivedTasksMap.remove(id);
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    public Node<Task> linkLast(Task task) {
        final Node<Task> oldTail = tail;
        final Node<Task> newNode = new Node<>(oldTail, task, null);
        tail = newNode;
        receivedTasksMap.put(task.getInd(), newNode);
        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.next = newNode;
        }
        return newNode;


    }

    public void removeNode(Node<Task> node) {
        if (!(node == null)) {
            final Node<Task> next = node.next;
            final Node<Task> prev = node.prev;
            node.data = null;

            if (head == node && tail == node) {
                head = null;
                tail = null;
            } else if (head == node && !(tail == node)) {
                head = next;
                head.prev = null;
            } else if (!(head == node) && tail == node) {
                tail = prev;
                tail.next = null;
            } else {
                prev.next = next;
                next.prev = prev;
            }

        }
    }

}
