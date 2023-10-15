package br.com.ccassoft.todolist.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private ITaskRepository taskRepository;

    @PostMapping("/")
    public ResponseEntity<TaskModel> create(@RequestBody TaskModel taskModel, HttpServletRequest request) {
        var idUser = request.getAttribute("idUser");
        taskModel.setIdUser((UUID) idUser);
    
        //validação data de inicio / fim da tarefa é maior ou igual a hoje
        var currentDate = LocalDateTime.now();
        if (currentDate.isAfter(taskModel.getStartAt()) || currentDate.isAfter(taskModel.getEndAt())) {
            TaskModel errorModel = new TaskModel();
            errorModel.setDescription("A data de início/término deve ser maior que a data atual!");
            return ResponseEntity.badRequest().body(errorModel);
        }

        //validacao se data de inicio é anterior a data de término
        if (taskModel.getStartAt().isAfter(taskModel.getEndAt())) {
            TaskModel errorModel = new TaskModel();
            errorModel.setDescription("A data de início deve ser menor que a data de término!");
            return ResponseEntity.badRequest().body(errorModel);
        }
    
        var task = this.taskRepository.save(taskModel);
        return ResponseEntity.ok(task);
    }

}
