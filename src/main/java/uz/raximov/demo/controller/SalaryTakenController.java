package uz.raximov.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.raximov.demo.payload.SalaryTakenDto;
import uz.raximov.demo.payload.response.ApiResponse;
import uz.raximov.demo.service.SalaryTakenService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/salarytaken")
public class SalaryTakenController {
    @Autowired
    SalaryTakenService salaryTakenService;

    @PostMapping
    public HttpEntity<?> add(@Valid @RequestBody SalaryTakenDto salaryTakenDto, HttpServletRequest httpServletRequest){
        ApiResponse apiResponse = salaryTakenService.add(salaryTakenDto, httpServletRequest);
        return ResponseEntity.status(apiResponse.isStatus()? HttpStatus.OK:HttpStatus.BAD_REQUEST).body(apiResponse);
    }

    @PutMapping
    public HttpEntity<?> edit(@RequestBody SalaryTakenDto salaryTakenDto, HttpServletRequest httpServletRequest){
        ApiResponse apiResponse = salaryTakenService.add(salaryTakenDto, httpServletRequest);
        return ResponseEntity.status(apiResponse.isStatus()? HttpStatus.OK:HttpStatus.BAD_REQUEST).body(apiResponse);
    }

    @DeleteMapping
    public HttpEntity<?> delete(@RequestParam String email, @RequestParam String month, HttpServletRequest httpServletRequest){
        ApiResponse apiResponse = salaryTakenService.delete(email, month, httpServletRequest);
        return ResponseEntity.status(apiResponse.isStatus()? HttpStatus.OK:HttpStatus.BAD_REQUEST).body(apiResponse);
    }

    @PutMapping("/stat")
    public HttpEntity<?> customize(@RequestParam String email, @RequestParam String month, @RequestParam boolean stat, HttpServletRequest httpServletRequest){
        ApiResponse apiResponse = salaryTakenService.customize(email, month,stat, httpServletRequest);
        return ResponseEntity.status(apiResponse.isStatus()? HttpStatus.OK:HttpStatus.BAD_REQUEST).body(apiResponse);
    }
}
