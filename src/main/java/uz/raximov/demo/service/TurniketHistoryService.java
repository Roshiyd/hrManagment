package uz.raximov.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.raximov.demo.component.Checker;
import uz.raximov.demo.entity.Role;
import uz.raximov.demo.entity.Turniket;
import uz.raximov.demo.entity.TurniketHistory;
import uz.raximov.demo.enums.RoleName;
import uz.raximov.demo.payload.TurniketHistoryDto;
import uz.raximov.demo.repository.TurniketHistoryRepository;
import uz.raximov.demo.repository.TurniketRepository;
import uz.raximov.demo.payload.response.ApiResponse;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class TurniketHistoryService {

    @Autowired
    TurniketHistoryRepository turniketHistoryRepository;

    @Autowired
    TurniketRepository turniketRepository;

    @Autowired
    Checker checker;

    public ApiResponse add(TurniketHistoryDto turniketHistoryDto, HttpServletRequest httpServletRequest){
        Optional<Turniket> optionalTurniket = turniketRepository.findByNumber(turniketHistoryDto.getNumber());
        if (!optionalTurniket.isPresent()) {
            return new ApiResponse("Karochchi brat ruxsati yo'q joyga kirmoqchi bo'lyapti! Turniket topilmadi!", false);
        }

        Set<Role> roles = optionalTurniket.get().getOwner().getRoles();
        String role = RoleName.ROLE_STAFF.name();
        for (Role role1 : roles) {
            role = role1.getName().name();
            break;
        }

        boolean check = checker.check(httpServletRequest, role);
        if (!check)
            return new ApiResponse("Karochchi brat ruxsatiz yo'q joyga kirmoqchi bo'lyapsiz! Sizda huquq yo'q!", false);

        TurniketHistory turniketHistory = new TurniketHistory();
        turniketHistory.setTurniket(optionalTurniket.get());
        turniketHistory.setType(turniketHistoryDto.getType());
        turniketHistoryRepository.save(turniketHistory);
        return new ApiResponse("Yo'liz ochiq, bemalol kirishiz mumkin!", true);
    }

    public ApiResponse getAllByDate(String number, Timestamp startTime, Timestamp endTime, HttpServletRequest httpServletRequest){
        Optional<Turniket> optionalTurniket = turniketRepository.findByNumber(number);
        if (!optionalTurniket.isPresent())
            return new ApiResponse("Karochchi brat ruxsati yo'q joyga kirmoqchi bo'lyapti! Bunaqa turniket yoq.", false);

        Set<Role> roles = optionalTurniket.get().getOwner().getRoles();
        String role = RoleName.ROLE_STAFF.name();
        for (Role role1 : roles) {
            role = role1.getName().name();
            break;
        }

        boolean check = checker.check(httpServletRequest, role);
        if (!check)
            return new ApiResponse("Karochchi brat ruxsatiz yo'q joyga kirmoqchi bo'lyapsiz! Sizda huquq yo'q!", false);

        List<TurniketHistory> historyList = turniketHistoryRepository.findAllByTurniketAndTimeIsBetween(optionalTurniket.get(), startTime, endTime);
        return new ApiResponse("History list by date",true, historyList);
    }

    public ApiResponse getAll(String number, HttpServletRequest httpServletRequest){
        Optional<Turniket> optionalTurniket = turniketRepository.findByNumber(number);
        if (!optionalTurniket.isPresent())
            return new ApiResponse("Karochchi brat ruxsati yo'q joyga kirmoqchi bo'lyapti! Bunaqa turniket yoq.", false);

        Set<Role> roles = optionalTurniket.get().getOwner().getRoles();
        String role = RoleName.ROLE_STAFF.name();
        for (Role role1 : roles) {
            role = role1.getName().name();
            break;
        }

        boolean check = checker.check(httpServletRequest, role);
        if (!check)
            return new ApiResponse("Karochchi brat ruxsatiz yo'q joyga kirmoqchi bo'lyapsiz! Sizda huquq yo'q!", false);

        List<TurniketHistory> allByTurniket = turniketHistoryRepository.findAllByTurniket(optionalTurniket.get());
        return new ApiResponse("All history by turniket!", true, allByTurniket);
    }
}
