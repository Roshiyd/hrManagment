package uz.raximov.demo.payload;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class TurniketDto {
    @NotNull
    private String ownerEmail;

    @NotNull
    private Integer companyId;

    private boolean enabled = true;
}
