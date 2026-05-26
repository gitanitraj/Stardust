package com.zipcode.stardust.repository;

import com.zipcode.stardust.model.Message;
import com.zipcode.stardust.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByReceiverOrderBySentAtDesc(User receiver);

    List<Message> findBySenderOrderBySentAtDesc(User sender);

    long countByReceiverAndReadStatusFalse(User receiver);

}