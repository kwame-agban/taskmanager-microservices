import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';

export interface Task {
  id: number;
  title: string;
  description: string;
  completed: boolean;
  ownerUsername: string;
}

@Injectable({
  providedIn: 'root'
})
export class TaskService {
  private tasks: Task[] = [];
  private idCounter = 1;

  getTasks(): Observable<Task[]> {
    return of(this.tasks);
  }

  createTask(task: { title: string; description: string }): Observable<Task> {
    const newTask: Task = {
      id: this.idCounter++,
      title: task.title,
      description: task.description,
      completed: false,
      ownerUsername: 'user.nom'
    };

    this.tasks.push(newTask);
    return of(newTask);
  }

  completeTask(id: number): Observable<Task> {
    const task = this.tasks.find(t => t.id === id);
    if (task) {
      task.completed = true;
    }
    return of(task!);
  }

  deleteTask(id: number): Observable<void> {
    this.tasks = this.tasks.filter(t => t.id !== id);
    return of(void 0);
  }
}
