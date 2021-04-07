package uz.raximov.demo.payload;

import lombok.Data;


@Data
public class UserDto {
    private String fullName;
    private String email;
    private String position;
    private Integer roleId;
}
