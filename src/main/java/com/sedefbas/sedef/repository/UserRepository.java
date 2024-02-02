package com.sedefbas.sedef.repository;

import com.sedefbas.sedef.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional(readOnly = true) ///sadece okuma işlemleri yapılcagını belirttim. performans artışı. hem hata alsamda roldback yapılmaz.
public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByEmail(String email);
    @Transactional
    @Modifying
    @Query("UPDATE users u SET u.enabled = TRUE WHERE u.email = ?1")
    int enableAppUser(String email);


}
