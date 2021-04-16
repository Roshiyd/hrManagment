package uz.raximov.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.raximov.demo.component.Checker;
import uz.raximov.demo.entity.Role;
import uz.raximov.demo.entity.Turniket;
import uz.raximov.demo.entity.User;
import uz.raximov.demo.enums.RoleName;
import uz.raximov.demo.payload.response.ApiResponse;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class LeadershipService {

    @Autowired
    TurniketHistoryService turniketHistoryService;

    @Autowired
    TurniketService turniketService;

    @Autowired
    Checker checker;

    @Autowired
    UserService userService;

    @Autowired
    TaskService taskService;

    //Har bir xodim haqidagi ma’lumotlarni ko’rmochi
    // bo’lsa ushbu xodimning belgilangan oraliq vaqt
    // bo’yicha ishga kelib-ketishi va bajargan tasklari haqida ma’lumot chiqishi kerak.
    public ApiResponse getHistoryAndTasks(Timestamp startTime, Timestamp endTime, String email, HttpServletRequest httpServletRequest){
        ApiResponse apiResponse = userService.getByEmail(email, httpServletRequest);
        if (!apiResponse.isStatus())
            return apiResponse;

        User user = (User) apiResponse.getObject();

        ApiResponse responseTurniket = turniketService.getByUser(user, httpServletRequest);
        if (!responseTurniket.isStatus())
            return responseTurniket;

        Turniket turniket = (Turniket) responseTurniket.getObject();
        ApiResponse historyList = turniketHistoryService.getAllByDate(turniket.getNumber(), startTime, endTime, httpServletRequest);

        ApiResponse taskList = taskService.getAllByUserAndDate(startTime, endTime, user, httpServletRequest);

        List<ApiResponse> responseList = new ArrayList<>();
        responseList.add(historyList);
        responseList.add(taskList);

        return new ApiResponse("So'ralgan narsalar", true, responseList);
    }

}
