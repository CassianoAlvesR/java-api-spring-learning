package br.com.ccassoft.todolist.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.aspectj.weaver.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

        // validação data de inicio / fim da tarefa é maior ou igual a hoje
        var currentDate = LocalDateTime.now();
        if (currentDate.isAfter(taskModel.getStartAt()) || currentDate.isAfter(taskModel.getEndAt())) {
            TaskModel errorModel = new TaskModel();
            errorModel.setDescription("A data de início/término deve ser maior que a data atual!");
            return ResponseEntity.badRequest().body(errorModel);
        }

        // validacao se data de inicio é anterior a data de término
        if (taskModel.getStartAt().isAfter(taskModel.getEndAt())) {
            TaskModel errorModel = new TaskModel();
            errorModel.setDescription("A data de início deve ser menor que a data de término!");
            return ResponseEntity.badRequest().body(errorModel);
        }

        var task = this.taskRepository.save(taskModel);
        return ResponseEntity.ok(task);
    }

    @GetMapping("/")
    public List<TaskModel> list(HttpServletRequest request) {
        var idUser = request.getAttribute("idUser");
        var tasks = this.taskRepository.findByIdUser((UUID) idUser);
        return tasks;
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskModel> update(@RequestBody TaskModel taskModel, HttpServletRequest request, @PathVariable UUID id) {

        var task = this.taskRepository.findById(id).orElse(null);

        if(task == null){
            TaskModel errorModel = new TaskModel();
            errorModel.setDescription("Tarefa não encontrada.");
            return ResponseEntity.badRequest().body(errorModel);            
        }

        var idUser = request.getAttribute("idUser");

        if(!task.getIdUser().equals(idUser)){
            TaskModel errorModel = new TaskModel();
            errorModel.setDescription("Usuário não tem permissão para alterar essa tarefa.");
            return ResponseEntity.badRequest().body(errorModel);
        }

        br.com.ccassoft.todolist.utils.Utils.copyNonNullProperties(taskModel, task);

        
        return ResponseEntity
        .ok().body(this.taskRepository.save(task));
    }

}
