//package com.groupware.security;
//
//import com.groupware.dto.EmployeeDto;
//
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.List;
//
//public class WebUserDetails implements UserDetails {
//
//    private EmployeeDto member;
//    private List<RoleDto> roles;
//
//    public DemoWebUserDetails() {}
//    public DemoWebUserDetails(EmployeeDto member) {
//        this.member = member;
//    }
//    public DemoWebUserDetails(EmployeeDto member, List<RoleDto> roles) {
//        this.member = member;
//        this.roles = roles;
//    }
//
//
//
//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() { // 권한 목록을 주는 오버라이딩 메서드
//
//        ArrayList<SimpleGrantedAuthority> grants = new ArrayList<>();
//        for (RoleDto role : roles) {
//            grants.add(new SimpleGrantedAuthority(role.getRoleName()));
//        }
//        return grants;
//    }
//
//    @Override
//    public String getPassword() {
//        return member.getEmpPasswd();
//    }
//
//    @Override
//    public String getUsername() {
//        return String.valueOf(member.getEmpId());
//    }
//
//    @Override
//    public boolean isAccountNonExpired() {
//        return true;
//    }
//    @Override
//    public boolean isAccountNonLocked() {
//        return true;
//    }
//    @Override
//    public boolean isCredentialsNonExpired() {
//        return true;
//    }
//    @Override
//    public boolean isEnabled() {
//        return member.isEmpActive();
//    }
//
//}