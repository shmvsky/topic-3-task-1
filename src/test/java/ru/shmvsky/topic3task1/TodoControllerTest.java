package ru.shmvsky.topic3task1;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.shmvsky.topic3task1.entity.Todo;
import ru.shmvsky.topic3task1.service.TodoService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TodoService todoService;

    @Test
    void getAllTodos_ShouldReturnAllTodos() throws Exception {
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

        when(todoService.getAllTodos()).thenReturn(expectedTodos);

        mockMvc.perform(get("/todos"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(todo1.getId()))
                .andExpect(jsonPath("$[0].title").value(todo1.getTitle()))
                .andExpect(jsonPath("$[0].description").value(todo1.getDescription()))
                .andExpect(jsonPath("$[0].done").value(todo1.getDone()))
                .andExpect(jsonPath("$[1].id").value(todo2.getId()))
                .andExpect(jsonPath("$[1].title").value(todo2.getTitle()))
                .andExpect(jsonPath("$[1].description").value(todo2.getDescription()))
                .andExpect(jsonPath("$[1].done").value(todo2.getDone()));
    }

    @Test
    void getTodoById_ShouldReturnTodo_WhenIdExists() throws Exception {
        int id = 1;
        Todo todo = new Todo();
        todo.setId(id);
        todo.setTitle("Test");
        todo.setDescription("Description");
        todo.setDone(false);

        when(todoService.getTodoById(id)).thenReturn(todo);

        mockMvc.perform(get("/todos/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(todo.getId()))
                .andExpect(jsonPath("$.title").value(todo.getTitle()))
                .andExpect(jsonPath("$.description").value(todo.getDescription()))
                .andExpect(jsonPath("$.done").value(todo.getDone()));
    }

    @Test
    void getTodoById_ShouldReturnNotFound_WhenIdDoesNotExist() throws Exception {
        int id = 1;

        when(todoService.getTodoById(id)).thenReturn(null);

        mockMvc.perform(get("/todos/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void addTodo_ShouldCreateTodo() throws Exception {
        Todo todo = new Todo();
        todo.setTitle("Test");
        todo.setDescription("Description");
        todo.setDone(false);

        mockMvc.perform(post("/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(todo)))
                .andExpect(status().isCreated());

        verify(todoService, times(1)).addTodo(any(Todo.class));
    }

    @Test
    void updateTodo_ShouldUpdateTodo_WhenTodoExists() throws Exception {
        int id = 1;
        Todo todo = new Todo();
        todo.setId(id);
        todo.setTitle("Test");
        todo.setDescription("Description");
        todo.setDone(false);

        doNothing().when(todoService).updateTodo(todo);

        mockMvc.perform(put("/todos/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(todo)))
                .andExpect(status().isOk());

        verify(todoService, times(1)).updateTodo(any(Todo.class));
    }

    @Test
    void deleteTodoById_ShouldDeleteTodo_WhenTodoExists() throws Exception {
        int id = 1;

        doNothing().when(todoService).deleteTodoById(id);

        mockMvc.perform(delete("/todos/{id}", id))
                .andExpect(status().isNoContent());

        verify(todoService, times(1)).deleteTodoById(id);
    }

    @Test
    void deleteTodoById_ShouldReturnNotFound_WhenTodoDoesNotExist() throws Exception {
        int id = 1;

        doThrow(new IllegalArgumentException("Todo not found")).when(todoService).deleteTodoById(id);

        mockMvc.perform(delete("/todos/{id}", id))
                .andExpect(status().isNotFound());
    }
}