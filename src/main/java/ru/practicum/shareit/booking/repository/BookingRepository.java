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

    // Поиск по автору бронирования с сортировкой
    List<Booking> findByBookerId(Long bookerId, Sort sort);

    // Поиск по владельцу вещи
    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :ownerId")
    List<Booking> findByItemOwnerId(@Param("ownerId") Long ownerId, Sort sort);

    // Поиск по статусу для автора
    List<Booking> findByBookerIdAndStatus(Long bookerId, Booking.BookingStatus status, Sort sort);

    // Поиск по статусу для владельца
    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :ownerId " +
            "AND b.status = :status")
    List<Booking> findByItemOwnerIdAndStatus(@Param("ownerId") Long ownerId,
                                             @Param("status") Booking.BookingStatus status,
                                             Sort sort);

    // Текущие бронирования (start <= now < end)
    List<Booking> findByBookerIdAndStartBeforeAndEndAfter(
            Long bookerId, LocalDateTime start, LocalDateTime end, Sort sort);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :ownerId " +
            "AND b.start <= :now " +
            "AND b.end > :now")
    List<Booking> findCurrentByItemOwnerId(@Param("ownerId") Long ownerId,
                                           @Param("now") LocalDateTime now,
                                           Sort sort);

    // Прошедшие бронирования (end <= now)
    List<Booking> findByBookerIdAndEndBefore(Long bookerId, LocalDateTime end, Sort sort);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :ownerId " +
            "AND b.end <= :now")
    List<Booking> findPastByItemOwnerId(@Param("ownerId") Long ownerId,
                                        @Param("now") LocalDateTime now,
                                        Sort sort);

    // Будущие бронирования (start > now)
    List<Booking> findByBookerIdAndStartAfter(Long bookerId, LocalDateTime start, Sort sort);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :ownerId " +
            "AND b.start > :now")
    List<Booking> findFutureByItemOwnerId(@Param("ownerId") Long ownerId,
                                          @Param("now") LocalDateTime now,
                                          Sort sort);

    // Проверка, бронировал ли пользователь вещь (для комментариев)
    @Query("SELECT COUNT(b) > 0 FROM Booking b " +
            "WHERE b.item.id = :itemId " +
            "AND b.booker.id = :userId " +
            "AND b.status = 'APPROVED' " +
            "AND b.end < :now")
    boolean hasUserBookedItem(@Param("itemId") Long itemId,
                              @Param("userId") Long userId,
                              @Param("now") LocalDateTime now);

    // Поиск бронирований по вещи
    List<Booking> findByItemId(Long itemId);

    // Поиск подтвержденных бронирований по вещи
    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.id = :itemId " +
            "AND b.status = 'APPROVED'")
    List<Booking> findApprovedBookingsByItemId(@Param("itemId") Long itemId);

    // Поиск подтвержденных бронирований по списку вещей
    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.id IN :itemIds " +
            "AND b.status = 'APPROVED'")
    List<Booking> findApprovedBookingsByItemIds(@Param("itemIds") List<Long> itemIds);

    // Поиск бронирования для проверки прав (автор или владелец)
    @Query("SELECT b FROM Booking b " +
            "WHERE b.id = :bookingId " +
            "AND (b.booker.id = :userId OR b.item.owner.id = :userId)")
    Optional<Booking> findByIdAndUserId(@Param("bookingId") Long bookingId,
                                        @Param("userId") Long userId);
}