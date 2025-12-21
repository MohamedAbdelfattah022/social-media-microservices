package com.socialmedia.postservice.repository;

import com.socialmedia.postservice.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}