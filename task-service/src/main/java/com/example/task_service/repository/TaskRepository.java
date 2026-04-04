package com.example.task_service.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import com.example.task_service.entity.Task;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {
  List<Task> findByOwnerUsername(String ownerUsername);
  Optional<Task> findByIdAndOwnerUsername(Long id, String ownerUsername);
}
