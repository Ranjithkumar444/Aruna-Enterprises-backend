package com.arunaenterprisesbackend.ArunaEnterprises.Repository;

import com.arunaenterprisesbackend.ArunaEnterprises.Entity.ContactMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface ContactRepository extends JpaRepository<ContactMessage,Long> {
    List<ContactMessage> findAll();

    @Query("SELECT m FROM ContactMessage m WHERE m.createdAt >= :cutoff")
    List<ContactMessage> findMessagesFromLast48Hours(@Param("cutoff") LocalDateTime cutoff);

    @Query("SELECT c FROM ContactMessage c WHERE c.createdAt >= :cutoff")
    List<ContactMessage> findMessagesFromLast48Hours(@Param("cutoff") ZonedDateTime cutoff);
}
