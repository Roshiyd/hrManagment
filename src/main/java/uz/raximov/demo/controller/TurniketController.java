package uz.raximov.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.raximov.demo.payload.TurniketDto;
import uz.raximov.demo.payload.response.ApiResponse;
import uz.raximov.demo.service.TurniketService;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping(value = "/api/turniket")
public class TurniketController {

    @Autowired
    TurniketService turniketService;

    @GetMapping("/all")
    public HttpEntity<?> getAll(HttpServletRequest httpServletRequest){
        ApiResponse apiResponse = turniketService.getAll(httpServletRequest);
        return ResponseEntity.status(apiResponse.isStatus()? HttpStatus.OK:HttpStatus.BAD_REQUEST).body(apiResponse);
    }

    @GetMapping
    public HttpEntity<?> getByNumber(@RequestParam String number, HttpServletRequest httpServletRequest){
        ApiResponse apiResponse = turniketService.getByNumber(httpServletRequest, number);
        return ResponseEntity.status(apiResponse.isStatus()? HttpStatus.OK:HttpStatus.BAD_REQUEST).body(apiResponse);
    }

    @PostMapping
    public HttpEntity<?> add(@Valid @RequestBody TurniketDto turniketDto, HttpServletRequest httpServletRequest) throws MessagingException {
        ApiResponse apiResponse = turniketService.add(turniketDto, httpServletRequest);
        return ResponseEntity.status(apiResponse.isStatus()? HttpStatus.OK:HttpStatus.BAD_REQUEST).body(apiResponse);
    }

    @PutMapping
    public HttpEntity<?> edit(@RequestBody TurniketDto turniketDto, @RequestParam String number, HttpServletRequest httpServletRequest) throws MessagingException {
        ApiResponse apiResponse = turniketService.edit(number, turniketDto, httpServletRequest);
        return ResponseEntity.status(apiResponse.isStatus()? HttpStatus.OK:HttpStatus.BAD_REQUEST).body(apiResponse);
    }

    @DeleteMapping
    public HttpEntity<?> delete(@RequestParam String number, HttpServletRequest httpServletRequest){
        ApiResponse apiResponse = turniketService.delete(number, httpServletRequest);
        return ResponseEntity.status(apiResponse.isStatus()? HttpStatus.OK:HttpStatus.BAD_REQUEST).body(apiResponse);
    }
}
