package com.example.demo.registration.token;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

public interface ConfirmationTokenRepository
        extends JpaRepository<ConfirmationToken, Long> {
    Optional<ConfirmationToken> findByToken(String token);

    @Transactional
    @Modifying
    @Query("UPDATE ConfirmationToken c "+
            "SET c.confirmedAt = ?2 "+
            "WHERE c.token = ?1"
        )
    int updateConfirmedAt(String token,
                          LocalDateTime confirmedAt);

    @Transactional
    @Modifying
    @Query("UPDATE ConfirmationToken c "+
            "SET c.expiresAt = ?2 "+
            "WHERE c.token = ?1"
    )
    int expireTokenAtByToken(String token, LocalDateTime now);
    @Transactional
    @Modifying
    @Query("UPDATE ConfirmationToken c "+
            "SET c.expiresAt = ?2 "+
            "WHERE c.appUser.id = ?1"
    )
    int expireTokenAtByUserId(Long uId, LocalDateTime now);
}
