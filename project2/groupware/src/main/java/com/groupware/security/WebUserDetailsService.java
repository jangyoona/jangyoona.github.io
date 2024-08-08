//package com.groupware.security;
//
//import com.groupware.dto.EmployeeDto;
//import com.groupware.mapper.AccountMapper;
//import lombok.Setter;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import java.util.Optional;
//import java.util.Set;
//
//public class WebUserDetails implements UserDetailsService {
//
//    @Setter(onMethod_ = { @Autowired})
//    private AccountMapper AccountMapper;
//
//    @Override
//    @Transactional
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        //데이터베이스에서 데이터 조회
//        DemoWebUserDetails userDetails = null;
//        Optional<MemberEntity> result= AccountMapper.findById(username); // 이 방식에선 유저Id만 가져오고, Id를 가져온 후 Pw를 체크한다.
//        if (result.isEmpty() ) {
//            throw new UsernameNotFoundException(username);
//        } else {
//            // List<RoleDto> roles = memberRepository.selectRolesById(username);
//            EmployeeDto employee = result.get();
//            Set<RoleEntity> roles = employee.getRoles();
//
//            userDetails = new DemoWebUserDetails(MemberDto.of(memberEntity), roles.stream().map(RoleDto::of).toList()); // 여기 어케 고쳐야되냐
//        }
//
//        return userDetails;
//    }
//}
