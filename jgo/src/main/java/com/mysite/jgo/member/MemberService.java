package com.mysite.jgo.member;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mysite.jgo.DataNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository mr;
	private final PasswordEncoder passwordEncoder;
	
	public Member create(String id, String pw, String addr, String email, String phoneno, 
			LocalDateTime cre_date, LocalDateTime mod_date) {
		
		Member member = new Member();
		member.setId(id);
		member.setPw(passwordEncoder.encode(pw));
		member.setAddr(addr);
		member.setEmail(email);
		member.setPhoneno(phoneno);
		member.setCre_date(cre_date);
		member.setMod_date(mod_date);
		mr.save(member);
		
		return member;
	}
	
	public Member getMember(String id) {
        Optional<Member> member = mr.findById(id);
        if (member.isPresent()) {
            return member.get();
        } else {
            throw new DataNotFoundException("member not found");
        }
    }
	
	public Member findById(String id) {
		
        return mr.findById(id).orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
        
    }
	
	public void updateMember(String id, String pw, String addr, String email, String phoneno) {
	    Optional<Member> member = mr.findById(id);
	    if (member.isEmpty()) {
	        throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
	    }

	    Member existingMember = member.get();
	    existingMember.setId(id);
	    existingMember.setPw(passwordEncoder.encode(pw));
	    existingMember.setAddr(addr);
	    existingMember.setEmail(email);
	    existingMember.setPhoneno(phoneno);

	    mr.save(existingMember); 
	}
	
	public void deleteMember(String id) {
	    Optional<Member> member = mr.findById(id);
	    if (member.isEmpty()) {
	        throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
	    }

	    mr.delete(member.get()); 
	}

	
}