package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBookerId(Long bookerId, Sort sort);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :ownerId")
    List<Booking> findByItemOwnerId(@Param("ownerId") Long ownerId, Sort sort);

    List<Booking> findByBookerIdAndStatus(Long bookerId,
                                          @Param("status") Booking.BookingStatus status,
                                          Sort sort);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :ownerId " +
            "AND b.status = :status")
    List<Booking> findByItemOwnerIdAndStatus(@Param("ownerId") Long ownerId,
                                             @Param("status") Booking.BookingStatus status,
                                             Sort sort);

    List<Booking> findByBookerIdAndStartBeforeAndEndAfter(
            Long bookerId, LocalDateTime start, LocalDateTime end, Sort sort);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :ownerId " +
            "AND b.start <= :now " +
            "AND b.end > :now")
    List<Booking> findCurrentByItemOwnerId(@Param("ownerId") Long ownerId,
                                           @Param("now") LocalDateTime now,
                                           Sort sort);

    List<Booking> findByBookerIdAndEndBefore(Long bookerId, LocalDateTime end, Sort sort);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :ownerId " +
            "AND b.end <= :now")
    List<Booking> findPastByItemOwnerId(@Param("ownerId") Long ownerId,
                                        @Param("now") LocalDateTime now,
                                        Sort sort);

    List<Booking> findByBookerIdAndStartAfter(Long bookerId, LocalDateTime start, Sort sort);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :ownerId " +
            "AND b.start > :now")
    List<Booking> findFutureByItemOwnerId(@Param("ownerId") Long ownerId,
                                          @Param("now") LocalDateTime now,
                                          Sort sort);

    @Query("SELECT COUNT(b) > 0 FROM Booking b " +
            "WHERE b.item.id = :itemId " +
            "AND b.booker.id = :userId " +
            "AND b.status = :status " +
            "AND b.end < :now")
    boolean hasUserBookedAndFinished(@Param("itemId") Long itemId,
                                     @Param("userId") Long userId,
                                     @Param("status") Booking.BookingStatus status,
                                     @Param("now") LocalDateTime now);

    List<Booking> findByItemId(Long itemId);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.id = :itemId " +
            "AND b.status = :status")
    List<Booking> findBookingsByItemIdAndStatus(@Param("itemId") Long itemId,
                                                @Param("status") Booking.BookingStatus status);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.id IN :itemIds " +
            "AND b.status = :status")
    List<Booking> findBookingsByItemIdsAndStatus(@Param("itemIds") List<Long> itemIds,
                                                 @Param("status") Booking.BookingStatus status);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.id = :bookingId " +
            "AND (b.booker.id = :userId OR b.item.owner.id = :userId)")
    Optional<Booking> findByIdAndUserId(@Param("bookingId") Long bookingId,
                                        @Param("userId") Long userId);
}