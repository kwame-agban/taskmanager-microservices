package com.example.task_service.controller;

import com.example.task_service.dto.TaskRequest;
import com.example.task_service.repository.TaskRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import com.example.task_service.entity.Task;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
  private final TaskRepository repository;

  public TaskController(TaskRepository repository) {
    this.repository = repository;
  }

  @GetMapping
  public List<Task> getMyTasks(Authentication authentication) {
    return repository.findByOwnerUsername(authentication.getName());
  }

  @PostMapping
  public ResponseEntity<Task> createTask(
    @Valid @RequestBody TaskRequest request,
    Authentication authentication
  ) {
    Task task = new Task();
    task.setTitle(request.getTitle());
    task.setDescription(request.getDescription());
    task.setCompleted(false);
    task.setOwnerUsername(authentication.getName());

    Task saved = repository.save(task);

    return ResponseEntity
      .created(URI.create("/api/tasks/" + saved.getId()))
      .body(saved);
  }

  @PutMapping("/{id}/complete")
  public ResponseEntity<Task> complete(
    @PathVariable Long id,
    Authentication authentication
  ) {
    return repository.findByIdAndOwnerUsername(id, authentication.getName())
      .map(task -> {
        task.setCompleted(true);
        return ResponseEntity.ok(repository.save(task));
      })
      .orElse(ResponseEntity.notFound().build());
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(
    @PathVariable Long id,
    Authentication authentication
  ) {
    return repository.findByIdAndOwnerUsername(id, authentication.getName())
      .map(task -> {
        repository.delete(task);
        return ResponseEntity.noContent().<Void>build();
      })
      .orElse(ResponseEntity.notFound().build());
  }

}
