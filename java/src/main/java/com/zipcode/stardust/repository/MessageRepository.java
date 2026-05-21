package com.zipcode.stardust.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zipcode.stardust.model.Message;
import com.zipcode.stardust.model.User;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByReceiverOrderBySentAtDesc(User receiver);

    List<Message> findBySenderOrderBySentAtDesc(User sender);
}