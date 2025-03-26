package com.mysite.jgo.reply;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.mysite.jgo.DataNotFoundException;
import com.mysite.jgo.dashboard.Dashboard;
import com.mysite.jgo.member.Member;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReplyService {
	
	private final ReplyRepository rr;
	
	public void create(Dashboard dashboard, String content, Member member) {
		Reply reply = new Reply();
		reply.setContent(content);
		reply.setCreDate(LocalDateTime.now());
		reply.setDashboard(dashboard);
		reply.setMember(member);
		rr.save(reply);
	}
	
	public Reply getReply(Integer id) {
        Optional<Reply> reply = rr.findById(id);
        if (reply.isPresent()) {
            return reply.get();
        } else {
            throw new DataNotFoundException("reply not found");
        }
    }

    public void modify(Reply reply, String content) {
        reply.setContent(content);
        reply.setModDate(LocalDateTime.now());
        this.rr.save(reply);
    }
    
    public void delete(Reply reply) {
        rr.delete(reply);
    }
}