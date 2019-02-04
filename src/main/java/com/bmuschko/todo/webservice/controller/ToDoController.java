package com.bmuschko.todo.webservice.controller;

import com.bmuschko.todo.webservice.model.ToDoItem;
import com.bmuschko.todo.webservice.repository.ToDoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
public class ToDoController {

    private ToDoRepository toDoRepository;

    @Autowired
    public ToDoController(ToDoRepository toDoRepository) {
        this.toDoRepository = toDoRepository;
    }

    @GetMapping("/todos")
    public List<ToDoItem> retrieveAllItems() {
        return toDoRepository.findAll();
    }

    @GetMapping("/todos/{id}")
    public ToDoItem retrieveItem(@PathVariable Long id) {
        Optional<ToDoItem> toDoItem = toDoRepository.findById(id);

        if (!toDoItem.isPresent()) {
            throw new ToDoItemNotFoundException(id);
        }

        return toDoItem.get();
    }

    @DeleteMapping("/todos/{id}")
    public void deleteItem(@PathVariable Long id) {
        toDoRepository.deleteById(id);
    }

    @PostMapping("/todos")
    public ResponseEntity<Object> createItem(@RequestBody ToDoItem toDoItem) {
        ToDoItem savedToDoItem = toDoRepository.save(toDoItem);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(savedToDoItem.getId()).toUri();
        return ResponseEntity.created(location).body(savedToDoItem);
    }

    @PutMapping("/todos/{id}")
    public ResponseEntity<Object> updateItem(@RequestBody ToDoItem toDoItem, @PathVariable Long id) {
        Optional<ToDoItem> existingToDoItem = toDoRepository.findById(id);

        if (!existingToDoItem.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        toDoItem.setId(id);
        toDoRepository.save(toDoItem);
        return ResponseEntity.noContent().build();
    }
}