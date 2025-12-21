package com.socialmedia.postservice.repository;

import com.socialmedia.postservice.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, Long> {
}