package com.groupware.security;

import com.groupware.dto.EmployeeDto;
import lombok.Data;

import java.util.Set;

@Data
public class RoleDto {

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int roleNo;
    private String roleName;
    private String roleDesc;


    private Set<EmployeeDto> members; // Set? 권한 중복이 있으면 안되기 때문에
}
