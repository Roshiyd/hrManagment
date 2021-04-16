package uz.raximov.demo.service;

import org.hibernate.annotations.Check;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.raximov.demo.component.Checker;
import uz.raximov.demo.entity.Role;
import uz.raximov.demo.entity.SalaryTaken;
import uz.raximov.demo.entity.User;
import uz.raximov.demo.enums.Month;
import uz.raximov.demo.enums.RoleName;
import uz.raximov.demo.payload.SalaryTakenDto;
import uz.raximov.demo.payload.response.ApiResponse;
import uz.raximov.demo.repository.SalaryTakenRepository;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.Set;

@Service
public class SalaryTakenService {
    @Autowired
    SalaryTakenRepository salaryTakenRepository;

    @Autowired
    Checker checker;

    @Autowired
    UserService userService;

    public ApiResponse add(SalaryTakenDto salaryTakenDto, HttpServletRequest httpServletRequest){
        ApiResponse response = userService.getByEmail(salaryTakenDto.getEmail(), httpServletRequest);
        if (!response.isStatus())
            return response;
        User user = (User) response.getObject();

        Set<Role> roles = user.getRoles();
        String role = RoleName.ROLE_STAFF.name();
        for (Role rolex : roles) {
            role = rolex.getName().name();
        }

        boolean check = checker.check(httpServletRequest, role);
        if(!check)
            return new ApiResponse("Sizda huquq yo'q!", false);

        SalaryTaken salaryTaken = new SalaryTaken();
        salaryTaken.setAmount(salaryTakenDto.getAmount());
        salaryTaken.setOwner(user);
        salaryTaken.setPeriod(salaryTakenDto.getPeriod());
        SalaryTaken save = salaryTakenRepository.save(salaryTaken);
        return new ApiResponse("Xodimga oylik kiritildi!", true);
    }

    public ApiResponse edit(SalaryTakenDto salaryTakenDto, HttpServletRequest httpServletRequest){

        ApiResponse response = userService.getByEmail(salaryTakenDto.getEmail(), httpServletRequest);
        if (!response.isStatus())
            return response;
        User user = (User) response.getObject();

        Set<Role> roles = user.getRoles();
        String role = RoleName.ROLE_STAFF.name();
        for (Role rolex : roles) {
            role = rolex.getName().name();
        }

        boolean check = checker.check(httpServletRequest, role);
        if(!check)
            return new ApiResponse("Sizda huquq yo'q!", false);

        Optional<SalaryTaken> optional = salaryTakenRepository.findByOwnerAndPeriod(user, salaryTakenDto.getPeriod());
        if (!optional.isPresent())
            return new ApiResponse("Oylik mavjud emas!", false);

        if (optional.get().isPaid())
            return new ApiResponse("Bu oylik allaqachon to'langan, uni o'zgartira olmaysiz!", false);


        SalaryTaken salaryTaken = optional.get();
        salaryTaken.setAmount(salaryTakenDto.getAmount());
        salaryTaken.setOwner(user);
        salaryTaken.setPeriod(salaryTakenDto.getPeriod());
        SalaryTaken save = salaryTakenRepository.save(salaryTaken);
        return new ApiResponse("Xodimning oyligi o'zgartirildi!", true);
    }

    public ApiResponse delete(String email, String month, HttpServletRequest httpServletRequest){
        ApiResponse response = userService.getByEmail(email, httpServletRequest);
        if (!response.isStatus())
            return response;
        User user = (User) response.getObject();

        Set<Role> roles = user.getRoles();
        String role = RoleName.ROLE_STAFF.name();
        for (Role rolex : roles) {
            role = rolex.getName().name();
        }

        boolean check = checker.check(httpServletRequest, role);
        if(!check)
            return new ApiResponse("Sizda huquq yo'q!", false);

        Month period = null;

        for (Month value : Month.values()) {
            if (value.name().equals(month)){
                period = value;
                break;
            }
        }
        if (period == null)
            return new ApiResponse("Month xato!", false);

        Optional<SalaryTaken> optional = salaryTakenRepository.findByOwnerAndPeriod(user, period);
        if (!optional.isPresent())
            return new ApiResponse("Oylik topilmadi!", false);

        if (optional.get().isPaid())
            return new ApiResponse("Bu oylik allaqachon to'langan, uni o'zgartira olmaysiz!", false);

        salaryTakenRepository.delete(optional.get());
        return new ApiResponse("Oylik o'chirildi!", true);
    }

    //OYLIKNI BERILGAN HOLATGA O'TKAZISH
    public ApiResponse customize(String email, String month, boolean stat, HttpServletRequest httpServletRequest){
        ApiResponse response = userService.getByEmail(email, httpServletRequest);
        if (!response.isStatus())
            return response;
        User user = (User) response.getObject();

        Set<Role> roles = user.getRoles();
        String role = RoleName.ROLE_STAFF.name();
        for (Role rolex : roles) {
            role = rolex.getName().name();
        }

        boolean check = checker.check(httpServletRequest, role);
        if(!check)
            return new ApiResponse("Sizda huquq yo'q!", false);

        Month period = null;

        for (Month value : Month.values()) {
            if (value.name().equals(month)){
                period = value;
                break;
            }
        }
        if (period == null)
            return new ApiResponse("Month xato!", false);

        Optional<SalaryTaken> optional = salaryTakenRepository.findByOwnerAndPeriod(user, period);
        if (!optional.isPresent())
            return new ApiResponse("Oylik topilmadi!", false);

        SalaryTaken salaryTaken = optional.get();
        if (salaryTaken.isPaid())
            return new ApiResponse("Bu oylik allaqachon to'langan, uni o'zgartira olmaysiz!", false);

        salaryTaken.setPaid(stat);
        return new ApiResponse("Oylik to'langanlik holati o'zgartirildi!", true);
    }

    public ApiResponse getByUser(String email, HttpServletRequest httpServletRequest){
        ApiResponse response = userService.getByEmail(email, httpServletRequest);
        if (!response.isStatus())
            return response;
        User user = (User) response.getObject();

        Set<Role> roles = user.getRoles();
        String role = RoleName.ROLE_STAFF.name();
        for (Role rolex : roles) {
            role = rolex.getName().name();
        }

        boolean check = checker.check(httpServletRequest, role);
        if(!check)
            return new ApiResponse("Sizda huquq yo'q!", false);

        return new ApiResponse("List by Owner", true, salaryTakenRepository.findAllByOwner(user));
    }

    public ApiResponse getByMonth(String month, HttpServletRequest httpServletRequest){
        boolean check = checker.check(httpServletRequest);
        if (!check)
            return new ApiResponse("Sizda huquq yo'q", false);

        Month period = null;

        for (Month value : Month.values()) {
            if (value.name().equals(month)){
                period = value;
                break;
            }
        }
        if (period == null)
            return new ApiResponse("Month xato!", false);

        return new ApiResponse("List by period", true, salaryTakenRepository.findAllByPeriod(period));
    }
}
