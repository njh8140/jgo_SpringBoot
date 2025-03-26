package com.mysite.jgo.reply;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface ReplyRepository extends JpaRepository<Reply, Integer> {

}
