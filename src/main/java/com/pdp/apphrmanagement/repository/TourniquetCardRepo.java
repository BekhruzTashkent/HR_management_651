package com.pdp.apphrmanagement.repository;

import com.pdp.apphrmanagement.entity.TourniquetCard;
import io.swagger.models.auth.In;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface TourniquetCardRepo extends JpaRepository<TourniquetCard, UUID> {
    @Query("select t from TourniquetCard t where t.user.email = ?1")
    Optional<TourniquetCard> findByUserEmail(String email);

    @Query("select t from TourniquetCard t where t.user.email = ?1 and t.status = true")
    Optional<TourniquetCard> findByUser_EmailAndStatusTrue(String email);
}
