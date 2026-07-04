package com.uas.reservasi;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;

@Repository
public interface ReservasiRepository extends JpaRepository<Reservasi, Long> {
    
    // Perintah SQL khusus untuk menghitung kamar yang bentrok di tanggal tertentu
    @Query("SELECT COUNT(r) FROM Reservasi r WHERE r.tanggalCheckIn <= :checkOut AND r.tanggalCheckOut >= :checkIn")
    long hitungKamarTerpakai(@Param("checkIn") LocalDate checkIn, @Param("checkOut") LocalDate checkOut);
}