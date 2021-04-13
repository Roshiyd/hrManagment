package uz.raximov.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
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
import uz.raximov.demo.response.ApiResponse;
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

    public ApiResponse add(UserDto userDto, HttpServletRequest httpServletRequest) throws MessagingException {
        Optional<Role> roleOptional = roleRepository.findById(userDto.getRoleId());
        if (!roleOptional.isPresent())
            return new ApiResponse("Role id not found!", false);

        boolean check = checker.check(httpServletRequest, roleOptional.get().getName().name());//huquqni tekshirish
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

    public ApiResponse edit(UserDto userDto, HttpServletRequest httpServletRequest) {
        //faqat userni o'zi o'zgartiradi
        String token = httpServletRequest.getHeader("Autorization");
        if (token == null)
            return new ApiResponse("Invalid token!", false);
        token = token.substring(7);

        String email = jwtProvider.getUsernameFromToken(token);

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (!optionalUser.isPresent())
            return new ApiResponse("Email not found!", false);

        Optional<Role> roleOptional = roleRepository.findById(userDto.getRoleId());
        if (!roleOptional.isPresent())
            return new ApiResponse("Role id not found!", false);

        boolean existsByEmail = userRepository.existsByEmailAndIdNot(userDto.getEmail(), optionalUser.get().getId());
        if (existsByEmail)
            return new ApiResponse("Email already exists!", false);

        User user = optionalUser.get();
        user.setEmail(userDto.getEmail());
        user.setFullName(userDto.getFullName());
        Set<Role> roles = optionalUser.get().getRoles();
        roles.add(roleOptional.get());
        user.setRoles(roles);
        user.setPosition(userDto.getPosition());

        userRepository.save(user);
        boolean b = mailSender.mailTextEdit(userDto.getEmail());
        if (b)
            return new ApiResponse("Muvaffaqqoyatli o'zgartirildi!", true);
        return new ApiResponse("Xatolik yuz berdi!", false);
    }

    public ApiResponse getOne(HttpServletRequest httpServletRequest){
        String token = httpServletRequest.getHeader("Autorization");
        if (token == null)
            return new ApiResponse("Invalid token!", false);
        token = token.substring(7);

        String email = jwtProvider.getUsernameFromToken(token);
        Optional<User> optionalUser = userRepository.findByEmail(email);
        return optionalUser.map(user -> new ApiResponse("Get by token!", true, user)).orElseGet(() -> new ApiResponse("Invalid token!", false, null));
    }

    public ApiResponse getByEmail(String email, HttpServletRequest httpServletRequest){
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (!userOptional.isPresent())
            return new ApiResponse("Email not found!", false);

        Set<Role> roles = userOptional.get().getRoles();
        String role = RoleName.ROLE_STAFF.name();
        for (Role roleName : roles) {
            role = roleName.getName().name();
            break;
        }

        boolean check = checker.check(httpServletRequest, role);
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
