package com.pdp.apphrmanagement.repository;

import com.pdp.apphrmanagement.entity.TourniquetCard;
import com.pdp.apphrmanagement.entity.TourniquetHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface TourniquetHistoryRepo extends JpaRepository<TourniquetHistory,Integer> {
    List<TourniquetHistory> findAllByExitedAtBetween(Timestamp fromDate, Timestamp toDate);

    Collection<? extends TourniquetHistory> findAllByEnteredAtBetween(Timestamp fromDate, Timestamp toDate);

    @Query("select t from TourniquetHistory t where t.tourniquetCard = ?1 and t.exitedAt is null")
    Optional<TourniquetHistory> findByTourniquetCardAndExitedAtNull(TourniquetCard tourniquetCard);
}
