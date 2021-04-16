package uz.raximov.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.raximov.demo.payload.TurniketHistoryDto;
import uz.raximov.demo.payload.response.ApiResponse;
import uz.raximov.demo.service.TurniketHistoryService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.sql.Timestamp;

@RestController
@RequestMapping("/api/turnikethistory")
public class TurniketHistoryController {

    @Autowired
    TurniketHistoryService turniketHistoryService;

    @PostMapping
    public HttpEntity<?> add(@Valid @RequestBody TurniketHistoryDto turniketHistoryDto, HttpServletRequest httpServletRequest){
        ApiResponse apiResponse = turniketHistoryService.add(turniketHistoryDto, httpServletRequest);
        return ResponseEntity.status(apiResponse.isStatus()? HttpStatus.OK:HttpStatus.BAD_REQUEST).body(apiResponse);
    }

    @GetMapping("/bydate")
    public HttpEntity<?> getAllByDate(@RequestParam String number, @RequestParam Timestamp startTime, @RequestParam Timestamp endTime, HttpServletRequest httpServletRequest){
        ApiResponse apiResponse = turniketHistoryService.getAllByDate(number, startTime, endTime, httpServletRequest);
        return ResponseEntity.status(apiResponse.isStatus()? HttpStatus.OK:HttpStatus.BAD_REQUEST).body(apiResponse);
    }

    @GetMapping("/all")
    public HttpEntity<?> getAll(@RequestParam String number, HttpServletRequest httpServletRequest){
        ApiResponse apiResponse = turniketHistoryService.getAll(number, httpServletRequest);
        return ResponseEntity.status(apiResponse.isStatus()? HttpStatus.OK:HttpStatus.BAD_REQUEST).body(apiResponse);
    }
}
