package uz.raximov.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uz.raximov.demo.component.Checker;
import uz.raximov.demo.component.MailSender;
import uz.raximov.demo.component.PasswordGenerator;
import uz.raximov.demo.entity.Role;
import uz.raximov.demo.entity.User;
import uz.raximov.demo.enums.RoleName;
import uz.raximov.demo.payload.UserDto;
import uz.raximov.demo.repository.RoleRepository;
import uz.raximov.demo.repository.UserRepository;
import uz.raximov.demo.payload.response.ApiResponse;
import uz.raximov.demo.security.JwtProvider;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    PasswordGenerator passwordGenerator;

    @Autowired
    MailSender mailSender;

    @Autowired
    Checker checker;

    @Autowired
    JwtProvider jwtProvider;

    public ApiResponse add(UserDto userDto) throws MessagingException {
        Optional<Role> roleOptional = roleRepository.findById(userDto.getRoleId());
        if (!roleOptional.isPresent())
            return new ApiResponse("Role id not found!", false);

        boolean check = checker.check(roleOptional.get().getName().name());//huquqni tekshirish
        if (!check)
            return new ApiResponse("You have no such right!", false);

        boolean existsByEmail = userRepository.existsByEmail(userDto.getEmail());
        if (existsByEmail)
            return new ApiResponse("Email already exists!", false);

        User user = new User();
        user.setFullName(userDto.getFullName());
        user.setEmail(userDto.getEmail());
        Set<Role> roles = new HashSet<>();
        roles.add(roleOptional.get());
        user.setRoles(roles);
        user.setPosition(userDto.getPosition());
        String pass = passwordGenerator.generateRandomPassword(8);
        user.setPassword(passwordEncoder.encode(pass));
        String code = UUID.randomUUID().toString();
        user.setVerifyCode(code);
        userRepository.save(user);
        boolean b = mailSender.mailTextAdd(userDto.getEmail(), code, pass);
        if (b)
            return new ApiResponse("User qo'shildi va emailiga xabar yuborildi", true);
        return new ApiResponse("Xatolik yuz berdi!", false);
    }

    public ApiResponse edit(UserDto userDto) {
        //faqat userni o'zi o'zgartiradi
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> userOptional = userRepository.findById(user.getId());
        if (!userOptional.isPresent())
            return new ApiResponse("Email not found!", false);

        Optional<Role> roleOptional = roleRepository.findById(userDto.getRoleId());
        if (!roleOptional.isPresent())
            return new ApiResponse("Role id not found!", false);

        boolean existsByEmail = userRepository.existsByEmailAndIdNot(userDto.getEmail(), userOptional.get().getId());
        if (existsByEmail)
            return new ApiResponse("Email already exists!", false);

        user.setEmail(userDto.getEmail());
        user.setFullName(userDto.getFullName());
        Set<Role> roles = userOptional.get().getRoles();
        roles.add(roleOptional.get());
        user.setRoles(roles);
        user.setPosition(userDto.getPosition());

        userRepository.save(user);
        boolean b = mailSender.mailTextEdit(userDto.getEmail());
        if (b)
            return new ApiResponse("Muvaffaqqoyatli o'zgartirildi!", true);
        return new ApiResponse("Xatolik yuz berdi!", false);
    }

    public ApiResponse getOne(){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> userOptional = userRepository.findById(user.getId());
        return userOptional.map(newuser -> new ApiResponse("Get by token!", true, user)).orElseGet(() -> new ApiResponse("Invalid token!", false, null));
    }

    public ApiResponse getByEmail(String email){
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (!userOptional.isPresent())
            return new ApiResponse("Email not found!", false);

        Set<Role> roles = userOptional.get().getRoles();
        String role = RoleName.ROLE_STAFF.name();
        for (Role roleName : roles) {
            role = roleName.getName().name();
            break;
        }

        boolean check = checker.check(role);
        if (!check)
            return new ApiResponse("You have no such right!", false);

        return new ApiResponse("Get by email!",true,userOptional.get());
    }

    public ApiResponse getByEmailforCustom(String email){
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (!userOptional.isPresent())
            return new ApiResponse("Email not found!", false);

        Set<Role> roles = userOptional.get().getRoles();
        String role = RoleName.ROLE_STAFF.name();
        for (Role roleName : roles) {
            role = roleName.getName().name();
            break;
        }

        boolean check = checker.check();
        if (!check)
            return new ApiResponse("You have no such right!", false);

        return new ApiResponse("Get by email!",true,userOptional.get());
    }

    public ApiResponse verifyEmail(String email, String code){
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (!optionalUser.isPresent())
            return new ApiResponse("Invalid request", false);

        User user = optionalUser.get();
        if (user.getEmail().equals(email) && user.getVerifyCode().equals(code)){
            user.setEnabled(true);
            user.setVerifyCode(null);
            userRepository.save(user);
            return new ApiResponse("Account verifyed!", true);
        }
        return new ApiResponse("Invalid request", false);
    }
}
