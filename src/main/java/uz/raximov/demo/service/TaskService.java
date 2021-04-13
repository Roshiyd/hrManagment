package uz.raximov.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.raximov.demo.component.Checker;
import uz.raximov.demo.component.MailSender;
import uz.raximov.demo.entity.Role;
import uz.raximov.demo.entity.Task;
import uz.raximov.demo.entity.User;
import uz.raximov.demo.enums.RoleName;
import uz.raximov.demo.enums.TaskStatus;
import uz.raximov.demo.payload.TaskDto;
import uz.raximov.demo.repository.TaskRepository;
import uz.raximov.demo.repository.UserRepository;
import uz.raximov.demo.response.ApiResponse;
import uz.raximov.demo.security.JwtProvider;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.swing.plaf.PanelUI;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class TaskService {
    @Autowired
    TaskRepository taskRepository;

    @Autowired
    Checker checker;

    @Autowired
    UserRepository userRepository;

    @Autowired
    MailSender mailSender;

    @Autowired
    JwtProvider jwtProvider;

    //YANGI TASK QO'SHISH
    public ApiResponse add(TaskDto taskDto, HttpServletRequest httpServletRequest) throws MessagingException {
        String email = taskDto.getUserEmail();
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (!optionalUser.isPresent())
            return new ApiResponse("User Not found!", false);

        User user = optionalUser.get();
        //HUQUQLAR TO'G'RI KELISHINI TEKSHIRAMIZ
        Set<Role> roles = user.getRoles();
        ApiResponse response = null;
        for (Role role : roles) {
            response = checker.checkForAny(httpServletRequest, role.getName().name());
            if (!response.isStatus())
                return new ApiResponse("You have no such right!", false);
        }

        //USERDA YAKUNLANMAGAN TASK BORLIGINI TEKSHIRAMIZ
        List<Task> takerTasks = taskRepository.findByTaskTaker(user);
        for (Task takerTask : takerTasks) {
            if (!takerTask.getStatus().name().equals(TaskStatus.STATUS_COMPLETED.name()))
                return new ApiResponse("An unfinished task in the employee!", true);
        }

        Task task = new Task();
        task.setTaskTaker(user);
        assert response != null;
        task.setTaskGiver((User) response.getObject());
        task.setDeadline(taskDto.getDeadline());
        task.setDescription(taskDto.getDescription());
        task.setName(taskDto.getName());
        assert  taskDto.getStatus() != null;
        task.setStatus(taskDto.getStatus());
        Task saved = taskRepository.save(task);

        boolean addTask = mailSender.mailTextAddTask(user.getEmail(), saved.getName(), saved.getId());
        if (!addTask)
            return new ApiResponse("Task added, but no email sent!", true);
        return new ApiResponse("Task added and email sent!", true);
    }//YANGI TASK QO'SHISH

    //TASKNI O'ZGARTISRISH
    public ApiResponse edit(UUID id, TaskDto taskDto, HttpServletRequest httpServletRequest) throws MessagingException {
        ApiResponse apiResponse = getById(id, httpServletRequest);
        if (!apiResponse.isStatus())
            return apiResponse;

        Task oldTask = (Task) apiResponse.getObject();

        String email = taskDto.getUserEmail();
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (!optionalUser.isPresent())
            return new ApiResponse("User Not found!", false);

        User user = optionalUser.get();

        //HUQUQLAR TO'G'RI KELISHINI TEKSHIRAMIZ
        Set<Role> roles = user.getRoles();
        ApiResponse response = null;
        for (Role role : roles) {
            response = checker.checkForAny(httpServletRequest, role.getName().name());
            if (!response.isStatus())
                return new ApiResponse("You have no such right!", false);
        }

        //USERDA YAKUNLANMAGAN TASK BORLIGINI TEKSHIRAMIZ
        List<Task> takerTasks = taskRepository.findByTaskTakerAndIdNot(user, id);
        for (Task takerTask : takerTasks) {
            if (!takerTask.getStatus().name().equals(TaskStatus.STATUS_COMPLETED.name()))
                return new ApiResponse("An unfinished task in the employee!", true);
        }

        oldTask.setTaskTaker(user);

        assert response != null;
        oldTask.setTaskGiver((User) response.getObject());

        assert taskDto.getDeadline() != null;
        oldTask.setDeadline(taskDto.getDeadline());

        assert taskDto.getName() != null;
        oldTask.setName(taskDto.getName());

        assert  taskDto.getStatus() != null;
        oldTask.setStatus(taskDto.getStatus());

        assert taskDto.getDescription() != null;
        oldTask.setDescription(taskDto.getDescription());

        Task saved = taskRepository.save(oldTask);

        boolean editTask = mailSender.mailTextAddTask(user.getEmail(), saved.getName(), saved.getId());
        if (!editTask)
            return new ApiResponse("Task edited, but no email sent!", true);
        return new ApiResponse("Task edited and email sent!", true);
    }

    //TASK STATUSINI O'ZGARTIRISH(YA'NI AGAR YAKUNLANSA TASKNI BERGAN FOYDALANUVCHIGA XABAR YUBORISH VA BOSHQ.)
    public ApiResponse editStatus(HttpServletRequest httpServletRequest, UUID id, TaskDto taskDto) throws MessagingException {
        String token = httpServletRequest.getHeader("Autorization");
        if (token == null)
            return new ApiResponse("Invalid token!", false);
        token = token.substring(7);

        String username = jwtProvider.getUsernameFromToken(token);
        Optional<User> optionalUser = userRepository.findByEmail(username);
        if (!optionalUser.isPresent())
            return new ApiResponse("User not found!", false);

        Optional<Task> optionalTask = taskRepository.findById(id);
        if (!optionalTask.isPresent())
            return new ApiResponse("Task not found!", false);

        Task task = optionalTask.get();
        if (!task.getTaskTaker().getEmail().equals(username))
            return new ApiResponse("The task does not belong to you.",false);

        task.setStatus(taskDto.getStatus());
        Task saved = taskRepository.save(task);

        if (saved.getStatus().name().equals(TaskStatus.STATUS_COMPLETED.name())) {
            boolean completed = mailSender.mailTextTaskCompleted(saved.getTaskGiver().getEmail(), saved.getTaskTaker().getEmail(), saved.getName());
            if (completed)
                return new ApiResponse("Task completed and email sent.", true);
            return new ApiResponse("Task completed and but email not sent.", true);
        }
        return new ApiResponse("Status edited", true);
    }

    //ID BO'YICHA TASKNI QAYTARISH
    public ApiResponse getById(UUID id, HttpServletRequest httpServletRequest){
        Optional<Task> byId = taskRepository.findById(id);
        if (!byId.isPresent())
            return new ApiResponse("Task Not found!", false);

        String token = httpServletRequest.getHeader("Autorization");
        if (token == null)
            return new ApiResponse("Invalid token!", false);
        token = token.substring(7);

        String email = jwtProvider.getUsernameFromToken(token);
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (!optionalUser.isPresent())
            return new ApiResponse("Error!", false);

        //TASKNI DIREKTOR ROLIDAGI FOYDALANUVCHI KO'RMOQCHI BO'LSA
        for (Role role : optionalUser.get().getRoles()) {
            if (role.getName().name().equals(RoleName.ROLE_DIRECTOR.name()))
                return new ApiResponse("Task by id!", true, byId.get());
        }

        //TASKNI FAQAT TASK BERILGAN FOYDALANIVCHI VA UNI BERGAN FOYDALANIVCHI KO'RISHINI TEKSHIRISH
        UUID idTaker = byId.get().getTaskTaker().getId();
        UUID idGiver = byId.get().getTaskGiver().getId();
        UUID idToken = optionalUser.get().getId();
        if (idToken != idTaker && idToken !=idGiver)
            return new ApiResponse("Your task does not belong to you!", false);
        return new ApiResponse("Task by id!", true, byId.get());
    }

    //BARCHA TASKLARNI QAYTARISH(HUQUQLAR BO'YICHA YA'NI FOYDALANUVCHI OLGAN TASKLARI)
    public ApiResponse getAllTo(HttpServletRequest httpServletRequest){
        String token = httpServletRequest.getHeader("Autorization");
        if (token == null)
            return new ApiResponse("Invalid token!", false);
        token = token.substring(7);

        String username = jwtProvider.getUsernameFromToken(token);
        Optional<User> optionalUser = userRepository.findByEmail(username);
        if (!optionalUser.isPresent())
            return new ApiResponse("User not found!", false);
        List<Task> taskList = taskRepository.findByTaskTaker(optionalUser.get());
        return new ApiResponse("TaskTaker task list!", true, taskList);
    }

    //BARCHA TASKLARNI QAYTARISH(HUQUQLAR BO'YICHA YA'NI FOYDALANUVCHI BERGAN TASKLARI DIREKTOR VA MANAGERLAR UCHUN)
    public ApiResponse getAllFrom(HttpServletRequest httpServletRequest){
        String token = httpServletRequest.getHeader("Autorization");
        if (token == null)
            return new ApiResponse("Invalid token!", false);
        token = token.substring(7);

        String username = jwtProvider.getUsernameFromToken(token);
        Optional<User> optionalUser = userRepository.findByEmail(username);
        if (!optionalUser.isPresent())
            return new ApiResponse("User not found!", false);
        List<Task> taskList = taskRepository.findByTaskGiver(optionalUser.get());
        return new ApiResponse("TaskGiver task list!", true, taskList);
    }

    //TASKNI O'CHIRISH
    public ApiResponse deleteById(UUID id, HttpServletRequest httpServletRequest){
        String token = httpServletRequest.getHeader("Autorization");
        if (token == null)
            return new ApiResponse("Invalid token!", false);
        token = token.substring(7);

        String email = jwtProvider.getUsernameFromToken(token);
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (!optionalUser.isPresent())
            return new ApiResponse("User not found!", false);

        Optional<Task> byId = taskRepository.findById(id);
        if (byId.isPresent() && byId.get().getTaskGiver().getEmail().equals(optionalUser.get().getEmail())) {
            taskRepository.deleteById(id);
            return new ApiResponse("Task deleted!", true);
        }
        return new ApiResponse("The task was not deleted!", false);
    }
}