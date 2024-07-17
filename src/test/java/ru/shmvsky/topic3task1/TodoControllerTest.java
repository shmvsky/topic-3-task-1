package ru.shmvsky.topic3task1;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.shmvsky.topic3task1.entity.Todo;
import ru.shmvsky.topic3task1.service.TodoService;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
public class TodoControllerTest {

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
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TodoService todoService;


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

        todoService.addTodo(todo1);
        todoService.addTodo(todo2);
    }

    @AfterEach
    void tearDown() {
        for (Todo todo : todoService.getAllTodos()) {
            todoService.deleteTodoById(todo.getId());
        }
    }

    @Test
    void getAllTodos_ShouldReturnAllTodos() throws Exception {
        mockMvc.perform(get("/todos"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].title").value("Todo 1"))
                .andExpect(jsonPath("$[0].description").value("Todo 1 desc"))
                .andExpect(jsonPath("$[0].done").value(false))
                .andExpect(jsonPath("$[1].title").value("Todo 2"))
                .andExpect(jsonPath("$[1].description").value("Todo 2 desc"))
                .andExpect(jsonPath("$[1].done").value(false));
    }

    @Test
    void getTodoById_ShouldReturnNotFound_WhenIdDoesNotExist() throws Exception {
        mockMvc.perform(get("/todos/{id}", 666))
                .andExpect(status().isNotFound());
    }

    @Test
    void addTodo_ShouldCreateTodo() throws Exception {
        Todo todo = new Todo();
        todo.setTitle("Todo 3");
        todo.setDescription("Todo 3 desc");
        todo.setDone(true);

        mockMvc.perform(post("/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(todo)))
                .andExpect(status().isCreated());

    }

    @Test
    void updateTodo_ShouldUpdateTodo_WhenTodoExists() throws Exception {

        Integer id = todoService.getAllTodos().get(0).getId();

        Todo todo = new Todo();
        todo.setId(id);
        todo.setTitle("Updated todo");
        todo.setDescription("Updated desc");
        todo.setDone(false);

        mockMvc.perform(put("/todos/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(todo)))
                .andExpect(status().isOk());
    }

    @Test
    void deleteTodoById_ShouldDeleteTodo_WhenTodoExists() throws Exception {

        Integer id = todoService.getAllTodos().get(0).getId();

        mockMvc.perform(delete("/todos/{id}", id))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteTodoById_ShouldReturnNotFound_WhenTodoDoesNotExist() throws Exception {
        Integer id = 666;

        mockMvc.perform(delete("/todos/{id}", id))
                .andExpect(status().isNotFound());
    }
}