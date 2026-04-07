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
  deleteTask(id: number): void {
    this.handle(
      this.taskService.deleteTask(id),
      () => {
        this.tasks = this.tasks.filter(t => t.id !== id);
      }
    );
  }
  logout(): void {
    this.authService.logout();
  }

  private handle<T>(obs: Observable<T>, nextFn: (value: T) => void): void {
    obs.subscribe({
      next: (value) => {
        nextFn(value);
        this.cdr.detectChanges();
      },
      error: (err) => console.error(err)
    });
  }

}
