package ru.shmvsky.topic3task1;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import ru.shmvsky.topic3task1.entity.Todo;
import ru.shmvsky.topic3task1.repository.TodoRepository;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

public class TodoRepositoryTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private TodoRepository todoRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAll_ShouldReturnAllTodos() {
        Todo todo1 = new Todo();
        todo1.setId(1);
        todo1.setTitle("Test 1");
        todo1.setDescription("Description 1");
        todo1.setDone(false);
        todo1.setDueDate(LocalDate.now());

        Todo todo2 = new Todo();
        todo2.setId(2);
        todo2.setTitle("Test 2");
        todo2.setDescription("Description 2");
        todo2.setDone(true);
        todo2.setDueDate(LocalDate.now().plusDays(1));

        List<Todo> expectedTodos = Arrays.asList(todo1, todo2);

        when(jdbcTemplate.query(anyString(), any(RowMapper.class))).thenReturn(expectedTodos);

        List<Todo> actualTodos = todoRepository.findAll();

        assertEquals(expectedTodos, actualTodos);
    }

    @Test
    void findById_ShouldReturnTodo_WhenIdExists() {
        int id = 1;
        Todo expectedTodo = new Todo();
        expectedTodo.setId(id);
        expectedTodo.setTitle("Test");
        expectedTodo.setDescription("Description");
        expectedTodo.setDone(false);
        expectedTodo.setDueDate(LocalDate.now());

        when(jdbcTemplate.queryForObject(anyString(), any(Object[].class), any(RowMapper.class))).thenReturn(expectedTodo);

        Todo actualTodo = todoRepository.findById(id);

        assertEquals(expectedTodo, actualTodo);
    }

    @Test
    void findById_ShouldReturnNull_WhenIdDoesNotExist() {
        int id = 1;

        when(jdbcTemplate.queryForObject(anyString(), any(Object[].class), any(RowMapper.class))).thenThrow(new DataAccessException("No data found") {});

        Todo actualTodo = todoRepository.findById(id);

        assertNull(actualTodo);
    }

    @Test
    void save_ShouldInsertTodo() {
        Todo todo = new Todo();
        todo.setTitle("Test");
        todo.setDescription("Description");
        todo.setDone(false);
        todo.setDueDate(LocalDate.now());

        todoRepository.save(todo);

        verify(jdbcTemplate, times(1)).update(anyString(), eq(todo.getTitle()), eq(todo.getDescription()), eq(todo.getDone()), eq(todo.getDueDate()));
    }

    @Test
    void update_ShouldUpdateTodo() {
        Todo todo = new Todo();
        todo.setId(1);
        todo.setTitle("Test");
        todo.setDescription("Description");
        todo.setDone(false);
        todo.setDueDate(LocalDate.now());

        todoRepository.update(todo);

        verify(jdbcTemplate, times(1)).update(anyString(), eq(todo.getTitle()), eq(todo.getDescription()), eq(todo.getDone()), eq(todo.getDueDate()), eq(todo.getId()));
    }

    @Test
    void delete_ShouldDeleteTodoById() {
        int id = 1;

        todoRepository.delete(id);

        verify(jdbcTemplate, times(1)).update(anyString(), eq(id));
    }
}
