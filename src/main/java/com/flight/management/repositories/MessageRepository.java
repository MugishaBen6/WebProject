package com.flight.management.repositories;

import com.flight.management.model.Message;
import com.flight.management.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findBySender(User sender);
    List<Message> findByReceiver(User receiver);

    // Add any custom query methods here if needed
}