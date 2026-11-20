import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../core/auth.service';
import { Task, TaskService } from '../../services/task.service';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-tasks',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './tasks.html',
  styleUrl: './tasks.css'
})
export class Tasks implements OnInit {
  private readonly taskService = inject(TaskService);
  private readonly authService = inject(AuthService);
  private readonly cdr = inject(ChangeDetectorRef);

  tasks: Task[] = [];
  title = '';
  description = '';

  ngOnInit(): void {
    this.loadTasks();
  }

  loadTasks(): void {
    this.handle(this.taskService.getTasks(), (tasks: Task[]) => {
      this.tasks = tasks;
    });
  }

  createTask(): void {
    const payload = { title: this.title, description: this.description };
    this.handle(
      this.taskService.createTask(payload),
      (newTask) => {
        this.tasks = [...this.tasks, newTask];
        this.title = '';
        this.description = '';
      }
    );
  }

  completeTask(id: number): void {
    this.handle(
      this.taskService.completeTask(id),
      (updatedTask) => {
        this.tasks = this.tasks.map(t =>
          t.id === id ? updatedTask : t
        );
      }
    );
  }
  /** Task currently going through the delete confirmation, and its step (1 or 2). */
  pendingDelete: { id: number; step: 1 | 2 } | null = null;

  /** Id of the task whose last delete attempt failed (drives the inline error). */
  deleteErrorId: number | null = null;

  requestDelete(id: number): void {
    this.deleteErrorId = null;
    this.pendingDelete = { id, step: 1 };
  }

  confirmDelete(id: number): void {
    if (this.pendingDelete?.id !== id) {
      return;
    }
    if (this.pendingDelete.step === 1) {
      this.pendingDelete = { id, step: 2 };
      return;
    }
    this.pendingDelete = null;
    this.deleteTask(id);
  }

  cancelDelete(): void {
    this.pendingDelete = null;
  }

  private deleteTask(id: number): void {
    this.handle(
      this.taskService.deleteTask(id),
      () => {
        this.tasks = this.tasks.filter(t => t.id !== id);
      },
      () => {
        this.deleteErrorId = id;
      }
    );
  }
  logout(): void {
    this.authService.logout();
  }

  private handle<T>(
    obs: Observable<T>,
    nextFn: (value: T) => void,
    errorFn?: (err: unknown) => void
  ): void {
    obs.subscribe({
      next: (value) => {
        nextFn(value);
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error(err);
        if (errorFn) {
          errorFn(err);
          this.cdr.detectChanges();
        }
      }
    });
  }

}
