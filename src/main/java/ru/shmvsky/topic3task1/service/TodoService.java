package ru.shmvsky.topic3task1.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.shmvsky.topic3task1.entity.Todo;
import ru.shmvsky.topic3task1.repository.TodoRepository;

import java.util.List;

@Service
public class TodoService {

    private final TodoRepository todoRepository;

    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public List<Todo> getAllTodos() {
        return todoRepository.findAll();
    }

    @Cacheable("todos")
    public Todo getTodoById(int id) {
        return todoRepository.findById(id);
    }

    @Transactional
    public void addTodo(Todo todo) {
        todoRepository.save(todo);
    }

    @Transactional
    @CachePut(value = "todos", key = "#todo.title")
    public void updateTodo(Todo todo) {
        Todo existingTodo = todoRepository.findById(todo.getId());
        if (existingTodo != null) {
            todoRepository.update(todo);
        } else {
            throw new IllegalArgumentException("Todo not found");
        }
    }

    @Transactional
    @CacheEvict("users")
    public void deleteTodoById(int id) {
        Todo existingTodo = todoRepository.findById(id);
        if (existingTodo != null) {
            todoRepository.delete(id);
        } else {
            throw new IllegalArgumentException("Todo not found");
        }
    }

}
