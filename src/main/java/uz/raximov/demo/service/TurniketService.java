package uz.raximov.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import uz.raximov.demo.component.Checker;
import uz.raximov.demo.component.MailSender;
import uz.raximov.demo.entity.Company;
import uz.raximov.demo.entity.Role;
import uz.raximov.demo.entity.Turniket;
import uz.raximov.demo.entity.User;
import uz.raximov.demo.enums.RoleName;
import uz.raximov.demo.payload.TurniketDto;
import uz.raximov.demo.repository.CompanyRepository;
import uz.raximov.demo.repository.TurniketRepository;
import uz.raximov.demo.payload.response.ApiResponse;
import uz.raximov.demo.security.JwtProvider;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.Set;

@Service
public class TurniketService {

    @Autowired
    TurniketRepository turniketRepository;

    @Autowired
    Checker checker;

    @Autowired
    UserService userService;

    @Autowired
    CompanyRepository companyRepository;

    @Autowired
    MailSender mailSender;

    @Autowired
    JwtProvider jwtProvider;

    public ApiResponse add(TurniketDto turniketDto) throws MessagingException {
        ApiResponse response = userService.getByEmail(turniketDto.getOwnerEmail());
        if (!response.isStatus())
            return response;

        User user = (User) response.getObject();
        Optional<Company> optionalCompany = companyRepository.findById(turniketDto.getCompanyId());
        if (!optionalCompany.isPresent())
            return new ApiResponse("Company not found!", false);

        Turniket turniket = new Turniket();
        turniket.setCompany(optionalCompany.get());
        turniket.setOwner(user);
        assert !turniketDto.isEnabled();
        turniket.setEnabled(turniketDto.isEnabled());
        Turniket saved = turniketRepository.save(turniket);
        mailSender.mailTextTurniketStatus(saved.getOwner().getEmail(), saved.isEnabled());
        return new ApiResponse("Turniket succesfully created!", true);
    }

    //FAQATGINA TURNIKETNING HUQUQINI O'ZGARTIRISH MUMKIN
    public ApiResponse edit(String number, TurniketDto turniketDto) throws MessagingException {
        Optional<Turniket> optionalTurniket = turniketRepository.findByNumber(number);
        if (!optionalTurniket.isPresent())
            return new ApiResponse("Turniket not found!", false);

        Turniket turniket = optionalTurniket.get();
        turniket.setEnabled(turniketDto.isEnabled());
        Turniket saved = turniketRepository.save(turniket);
        mailSender.mailTextTurniketStatus(saved.getOwner().getEmail(), saved.isEnabled());
        return new ApiResponse("Turniket succesfully edited!", true);
    }

    public ApiResponse delete(String number){
        Optional<Turniket> optionalTurniket = turniketRepository.findByNumber(number);
        if (!optionalTurniket.isPresent())
                return new ApiResponse("Turniket not found!", false);


        Set<Role> roles = optionalTurniket.get().getOwner().getRoles();
        String role = null;
        for (Role roleName : roles) {
            role = roleName.getName().name();
            break;
        }
        boolean check = checker.check(role);

        if (!check)
            return new ApiResponse("You have no such right!", false);

        turniketRepository.delete(optionalTurniket.get());
        return new ApiResponse("Turniket deleted!", true);
    }

    public ApiResponse getByNumber(String number){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ApiResponse byEmail = userService.getByEmailforCustom(user.getEmail());
        if (!byEmail.isStatus())
            return byEmail;

        Optional<Turniket> byNumber = turniketRepository.findByNumber(number);
        if (!byNumber.isPresent())
            return new ApiResponse("Turniket not found!", false);

        Set<Role> roles = byNumber.get().getOwner().getRoles();
        String role = null;
        for (Role roleName : roles) {
            role = roleName.getName().name();
            break;
        }
        boolean check = checker.check(role);

        if (byNumber.get().getOwner().getEmail().equals(user.getEmail()) || check){
            return new ApiResponse("Turniket", true, byNumber.get());
        }
        return new ApiResponse("You have no such right!", false);
    }

    //TOKEN BO'YICHA MA'LUMOTLARNI QAYTARADI
    public ApiResponse getAll(){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ApiResponse byEmail = userService.getByEmailforCustom(user.getEmail());
        if (!byEmail.isStatus())
            return byEmail;

        Set<Role> roles = user.getRoles();
        String role = RoleName.ROLE_STAFF.name();
        for (Role roleName : roles) {
            role = roleName.getName().name();
            break;
        }

        if (role.equals(RoleName.ROLE_DIRECTOR.name()))
            return new ApiResponse("Turniket List",true, turniketRepository.findAll());

        return new ApiResponse("Turniket List",true, turniketRepository.findAllByOwner(user));
    }

    public ApiResponse getByUser(User user){
        Set<Role> roles = user.getRoles();
        String role = RoleName.ROLE_STAFF.name();
        for (Role roleName : roles) {
            role = roleName.getName().name();
            break;
        }
        boolean check = checker.check(role);
        if (!check)
            return new ApiResponse("Sizga mumkin emas!", false);

        Optional<Turniket> optionalTurniket = turniketRepository.findAllByOwner(user);
        return new ApiResponse("TurniketListByUser", true, optionalTurniket);
    }
}
