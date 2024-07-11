package ru.shmvsky.topic3task1.repository;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.shmvsky.topic3task1.entity.Todo;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Repository
public class TodoRepository {

    private final JdbcTemplate jdbcTemplate;

    public TodoRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final class TodoMapper implements RowMapper<Todo> {
        @Override
        public Todo mapRow(ResultSet rs, int rowNum) throws SQLException {
            Todo todo = new Todo();
            todo.setId(rs.getInt("id"));
            todo.setTitle(rs.getString("title"));
            todo.setDescription(rs.getString("description"));
            todo.setDone(rs.getBoolean("done"));

            Date dueDate = rs.getDate("due_date");
            if (dueDate != null) {
                todo.setDueDate(dueDate.toLocalDate());
            }
            return todo;
        }
    }

    public List<Todo> findAll() {
        return jdbcTemplate.query("SELECT * FROM todos", new TodoMapper());
    }

    public Todo findById(int id) {
        Todo todo;
        try {
            todo = jdbcTemplate.queryForObject("SELECT * FROM todos WHERE id = ?", new Object[]{id}, new TodoMapper());
        } catch (DataAccessException e) {
            return null;
        }
        return todo;
    }

    public void save(Todo todoList) {
        jdbcTemplate.update("INSERT INTO todos (title, description, done, due_date) VALUES (?, ?, ?, ?)", todoList.getTitle(), todoList.getDescription(), todoList.getDone(), todoList.getDueDate());
    }

    public void update(Todo todoList) {
        jdbcTemplate.update("UPDATE todos SET title = ?, description = ?, done = ?, due_date = ? WHERE id = ?", todoList.getTitle(), todoList.getDescription(), todoList.getDone(), todoList.getDueDate(), todoList.getId());
    }

    public void delete(int id) {
        jdbcTemplate.update("DELETE FROM todos WHERE id = ?", id);
    }

}
