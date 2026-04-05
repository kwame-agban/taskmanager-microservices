package com.example.task_service.controller;

import com.example.task_service.dto.TaskRequest;
import com.example.task_service.entity.Task;
import com.example.task_service.repository.TaskRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

  private final TaskRepository repository;

  public TaskController(TaskRepository repository) {
    this.repository = repository;
  }

  @GetMapping
  public List<Task> getMyTasks(Authentication authentication) {
    String username = requireUsername(authentication);
    return repository.findByOwnerUsername(username);
  }

  @PostMapping
  public ResponseEntity<Task> createTask(
    @Valid @RequestBody TaskRequest request,
    Authentication authentication
  ) {
    String username = requireUsername(authentication);

    Task task = new Task();
    task.setTitle(request.getTitle());
    task.setDescription(request.getDescription());
    task.setCompleted(false);
    task.setOwnerUsername(username);

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
    String username = requireUsername(authentication);

    return repository.findByIdAndOwnerUsername(id, username)
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
    String username = requireUsername(authentication);

    return repository.findByIdAndOwnerUsername(id, username)
      .map(task -> {
        repository.delete(task);
        return ResponseEntity.noContent().<Void>build();
      })
      .orElse(ResponseEntity.notFound().build());
  }

  private String requireUsername(Authentication authentication) {
    if (authentication == null || !authentication.isAuthenticated()) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utilisateur non authentifié");
    }
    return authentication.getName();
  }
}
