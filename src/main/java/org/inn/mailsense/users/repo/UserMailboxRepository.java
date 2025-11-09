package org.inn.mailsense.users.repo;

import org.inn.mailsense.users.entity.UserMailbox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserMailboxRepository extends JpaRepository<UserMailbox, UUID> {

    List<UserMailbox> findByUserId(UUID userId);

    Optional<UserMailbox> findByUserIdAndEmailAddress(UUID userId, String emailAddress);
}
