package com.zipcode.stardust.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zipcode.stardust.model.Post;
import com.zipcode.stardust.model.Reaction;
import com.zipcode.stardust.model.User;

public interface ReactionRepository extends JpaRepository<Reaction, Long>{
    List<Reaction> findByPost(Post post);
    Optional<Reaction> findByUserAndPost(User user, Post post);
}
