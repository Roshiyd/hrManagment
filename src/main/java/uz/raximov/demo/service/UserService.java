package uz.raximov.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.raximov.demo.payload.UserDto;
import uz.raximov.demo.repository.RoleRepository;
import uz.raximov.demo.repository.UserRepository;
import uz.raximov.demo.response.ApiResponse;

import javax.servlet.http.HttpServletRequest;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    public ApiResponse add(UserDto userDto, HttpServletRequest httpServletRequest){
        boolean existsByEmail = userRepository.existsByEmail(userDto.getEmail());
        if (existsByEmail)
            return new ApiResponse("Email already exists!", false, null);
    }
}
