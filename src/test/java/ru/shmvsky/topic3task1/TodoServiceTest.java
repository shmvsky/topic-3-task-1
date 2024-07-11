package ru.shmvsky.topic3task1;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.shmvsky.topic3task1.entity.Todo;
import ru.shmvsky.topic3task1.repository.TodoRepository;
import ru.shmvsky.topic3task1.service.TodoService;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TodoServiceTest {

    @Mock
    private TodoRepository todoRepository;

    @InjectMocks
    private TodoService todoService;

    @Test
    void getAllTodos_ShouldReturnAllTodos() {
        Todo todo1 = new Todo();
        todo1.setId(1);
        todo1.setTitle("Test 1");
        todo1.setDescription("Description 1");
        todo1.setDone(false);

        Todo todo2 = new Todo();
        todo2.setId(2);
        todo2.setTitle("Test 2");
        todo2.setDescription("Description 2");
        todo2.setDone(true);

        List<Todo> expectedTodos = Arrays.asList(todo1, todo2);

        when(todoRepository.findAll()).thenReturn(expectedTodos);

        List<Todo> actualTodos = todoService.getAllTodos();

        assertEquals(expectedTodos, actualTodos);
    }

    @Test
    void getTodoById_ShouldReturnTodo_WhenIdExists() {
        int id = 1;
        Todo expectedTodo = new Todo();
        expectedTodo.setId(id);
        expectedTodo.setTitle("Test");
        expectedTodo.setDescription("Description");
        expectedTodo.setDone(false);

        when(todoRepository.findById(id)).thenReturn(expectedTodo);

        Todo actualTodo = todoService.getTodoById(id);

        assertEquals(expectedTodo, actualTodo);
    }

    @Test
    void getTodoById_ShouldReturnNull_WhenIdDoesNotExist() {
        int id = 1;

        when(todoRepository.findById(id)).thenReturn(null);

        Todo actualTodo = todoService.getTodoById(id);

        assertNull(actualTodo);
    }

    @Test
    void addTodo_ShouldSaveTodo() {
        Todo todo = new Todo();
        todo.setTitle("Test");
        todo.setDescription("Description");
        todo.setDone(false);

        todoService.addTodo(todo);

        verify(todoRepository, times(1)).save(todo);
    }

    @Test
    void updateTodo_ShouldUpdateTodo_WhenTodoExists() {
        Todo todo = new Todo();
        todo.setId(1);
        todo.setTitle("Test");
        todo.setDescription("Description");
        todo.setDone(false);

        when(todoRepository.findById(todo.getId())).thenReturn(todo);

        todoService.updateTodo(todo);

        verify(todoRepository, times(1)).update(todo);
    }

    @Test
    void updateTodo_ShouldThrowException_WhenTodoDoesNotExist() {
        Todo todo = new Todo();
        todo.setId(1);
        todo.setTitle("Test");
        todo.setDescription("Description");
        todo.setDone(false);

        when(todoRepository.findById(todo.getId())).thenReturn(null);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            todoService.updateTodo(todo);
        });

        assertEquals("Todo not found", exception.getMessage());
    }

    @Test
    void deleteTodoById_ShouldDeleteTodo_WhenTodoExists() {
        int id = 1;
        Todo todo = new Todo();
        todo.setId(id);

        when(todoRepository.findById(id)).thenReturn(todo);

        todoService.deleteTodoById(id);

        verify(todoRepository, times(1)).delete(id);
    }

    @Test
    void deleteTodoById_ShouldThrowException_WhenTodoDoesNotExist() {
        int id = 1;

        when(todoRepository.findById(id)).thenReturn(null);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            todoService.deleteTodoById(id);
        });

        assertEquals("Todo not found", exception.getMessage());
    }
}