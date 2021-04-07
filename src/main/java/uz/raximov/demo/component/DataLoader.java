package uz.raximov.demo.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import uz.raximov.demo.entity.Company;
import uz.raximov.demo.entity.Role;
import uz.raximov.demo.entity.User;
import uz.raximov.demo.repository.CompanyRepository;
import uz.raximov.demo.repository.RoleRepository;
import uz.raximov.demo.repository.UserRepository;

import java.util.HashSet;
import java.util.Set;

@Component
public class DataLoader implements CommandLineRunner {
    @Value("${spring.datasource.initialization-mode}")
    private String initialMode;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    CompanyRepository companyRepository;

    @Override
    public void run(String... args) throws Exception {
        if (initialMode.equals("always")) {

            Set<Role> roles = new HashSet<>(roleRepository.findAll());

            User user = new User("Direktor", passwordEncoder.encode("salom"),roles ,"direktor@hr.uz","Katta direktor",true);

            User direktor = userRepository.save(user);

            Company company = new Company(direktor, "PDP");
            Company save = companyRepository.save(company);
        }
    }
}
