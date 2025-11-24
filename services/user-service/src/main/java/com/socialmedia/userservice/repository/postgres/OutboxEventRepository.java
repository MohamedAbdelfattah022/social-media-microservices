package com.socialmedia.userservice.repository.postgres;

import com.socialmedia.userservice.entity.postgres.OutboxEvent;
import com.socialmedia.userservice.enums.OutboxEventStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {
    List<OutboxEvent> findByStatus(OutboxEventStatus status);

    @Query("select e from OutboxEvent e where e.status = 'PENDING' order by e.createdAt")
    List<OutboxEvent> findPendingEvents();
}