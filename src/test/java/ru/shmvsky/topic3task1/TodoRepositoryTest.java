package ru.shmvsky.topic3task1;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.shmvsky.topic3task1.entity.Todo;
import ru.shmvsky.topic3task1.repository.TodoRepository;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@Testcontainers
@SpringBootTest
public class TodoRepositoryTest {

    @Container
    private final static PostgreSQLContainer<?> pg =
            new PostgreSQLContainer<>("postgres:16");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", pg::getJdbcUrl);
        registry.add("spring.datasource.username", pg::getUsername);
        registry.add("spring.datasource.password", pg::getPassword);
    }

    @Autowired
    TodoRepository todoRepository;

    @BeforeEach
    void setUp() {
        Todo todo1 = new Todo();
        todo1.setTitle("Todo 1");
        todo1.setDescription("Todo 1 desc");
        todo1.setDone(false);
        todo1.setDueDate(LocalDate.now());

        Todo todo2 = new Todo();
        todo2.setTitle("Todo 2");
        todo2.setDescription("Todo 2 desc");
        todo2.setDone(false);
        todo2.setDueDate(LocalDate.now());

        todoRepository.save(todo1);
        todoRepository.save(todo2);
    }

    @AfterEach
    void tearDown() {
        todoRepository.deleteAll();
    }

    @Test
    void findAll_ShouldReturnAllTodos() {
        List<Todo> actualTodos = todoRepository.findAll();

        assertEquals(actualTodos.size(), 2);
    }

    @Test
    void findById_ShouldReturnNull_WhenIdDoesNotExist() {
        assertNull(todoRepository.findById(666));
    }

    @Test
    void save_ShouldInsertTodo() {
        Todo todo = new Todo();
        todo.setTitle("Saved todo");
        todo.setDescription("wdkjawdjkawd");
        todo.setDone(false);
        todo.setDueDate(LocalDate.now());
        todoRepository.save(todo);

        assertEquals(todoRepository.findAll().size(), 3);
    }

    @Test
    void update_ShouldUpdateTodo() {
        Todo todo = new Todo();
        todo.setId(1);
        todo.setTitle("Updated Todo 1");
        todo.setDescription("Updated todo 1 desc");
        todo.setDone(false);
        todo.setDueDate(LocalDate.now());

        todoRepository.update(todo);

        assertEquals(todoRepository.findById(1).getTitle(), "Updated Todo 1");

    }

    @Test
    void delete_ShouldDeleteTodoById() {
        todoRepository.delete(1);
        assertNull(todoRepository.findById(1));
    }

}
