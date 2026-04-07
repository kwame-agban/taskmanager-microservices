import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../core/auth.service';
import { Task, TaskService } from '../../services/task.service';

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

  tasks: Task[] = [];
  title = '';
  description = '';

  ngOnInit(): void {
    this.loadTasks();
  }

  loadTasks(): void {
    this.taskService.getTasks().subscribe({
      next: (tasks: Task[]) => {
        //this.tasks = tasks;
        this.tasks = [...tasks];
      },
      error: (err: unknown) => {
        console.error('Erreur chargement tâches', err);
      }
    });
  }

  createTask(): void {
    const title = this.title.trim();
    const description = this.description.trim();

    if (!title) {
      return;
    }

    const payload = { title, description };

    this.taskService.createTask(payload).subscribe({
      next: (newTask) => {
          this.tasks = [...this.tasks, newTask]; // affichage immédiat

          setTimeout(() => {
            this.loadTasks(); // sync backend
          }, 200);

          this.title = '';
          this.description = '';
      },
      error: (err: unknown) => {
        console.error('Erreur création tâche', err);
      }
    });
  }

  completeTask(id: number): void {
    this.taskService.completeTask(id).subscribe({
      next: () => {
        this.loadTasks();
      },
      error: (err: unknown) => {
        console.error('Erreur complétion tâche', err);
      }
    });
  }

  deleteTask(id: number): void {
    this.taskService.deleteTask(id).subscribe({
      next: () => {
        this.loadTasks();
      },
      error: (err: unknown) => {
        console.error('Erreur suppression tâche', err);
      }
    });
  }

  logout(): void {
    this.authService.logout();
  }
}
