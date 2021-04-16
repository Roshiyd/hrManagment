package uz.raximov.demo.payload;

import lombok.Data;
import uz.raximov.demo.enums.Month;

import javax.persistence.Enumerated;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
public class SalaryTakenDto {
    @Email
    @NotNull
    private String email;

    @NotNull
    private double amount;

    @Enumerated
    private Month period;
}
