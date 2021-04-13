package uz.raximov.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.raximov.demo.payload.TaskDto;
import uz.raximov.demo.response.ApiResponse;
import uz.raximov.demo.service.TaskService;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api/task")
public class TaskController {
    @Autowired
    TaskService taskService;

    @PostMapping
    public HttpEntity<?> add(@Valid @RequestBody TaskDto taskDto, HttpServletRequest httpServletRequest) throws MessagingException {
        ApiResponse apiResponse = taskService.add(taskDto, httpServletRequest);
        return ResponseEntity.status(apiResponse.isStatus()? HttpStatus.OK:HttpStatus.BAD_REQUEST).body(apiResponse);
    }

    @PutMapping("/{id}")
    public HttpEntity<?> edit(@RequestBody TaskDto taskDto, HttpServletRequest httpServletRequest, @PathVariable UUID id) throws MessagingException {
        ApiResponse apiResponse = taskService.edit(id, taskDto, httpServletRequest);
        return ResponseEntity.status(apiResponse.isStatus()? HttpStatus.OK:HttpStatus.BAD_REQUEST).body(apiResponse);
    }

    @PutMapping("/s/{id}")
    public HttpEntity<?> editStatus(@RequestBody TaskDto taskDto, @PathVariable UUID id, HttpServletRequest httpServletRequest) throws MessagingException {
        ApiResponse apiResponse = taskService.editStatus(httpServletRequest, id, taskDto);
        return ResponseEntity.status(apiResponse.isStatus()?HttpStatus.OK:HttpStatus.BAD_REQUEST).body(apiResponse);
    }

    @GetMapping("/{id}")
    public HttpEntity<?> getById(@PathVariable UUID id, HttpServletRequest httpServletRequest){
        ApiResponse apiResponse = taskService.getById(id, httpServletRequest);
        return ResponseEntity.status(apiResponse.isStatus()? HttpStatus.OK:HttpStatus.BAD_REQUEST).body(apiResponse);
    }

    @GetMapping()
    public HttpEntity<?> getAllToFrom(@RequestParam String stat, HttpServletRequest httpServletRequest){
        ApiResponse response = null;
        if (stat.equals("to")){
            response = taskService.getAllTo(httpServletRequest);
        } else if (stat.equals("from"))
            response = taskService.getAllFrom(httpServletRequest);

        assert response != null;
        return ResponseEntity.status(response.isStatus()?HttpStatus.OK:HttpStatus.BAD_REQUEST).body(response);
    }


    @DeleteMapping("{id}")
    public HttpEntity<?> delete(@PathVariable UUID id, HttpServletRequest httpServletRequest){
        ApiResponse response = taskService.deleteById(id, httpServletRequest);
        return ResponseEntity.status(response.isStatus()?HttpStatus.OK:HttpStatus.BAD_REQUEST).body(response);
    }

}
