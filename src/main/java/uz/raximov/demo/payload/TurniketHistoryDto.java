package uz.raximov.demo.payload;

import lombok.Data;
import uz.raximov.demo.enums.TurniketType;

import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;

@Data
public class TurniketHistoryDto {
    @NotNull
    private String number;

    @NotNull
    @Enumerated
    private TurniketType type;
}
