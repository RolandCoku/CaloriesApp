package org.springboot.caloriesapp.repository;

import org.springboot.caloriesapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u WHERE u.username = :username")
    Optional<User> findByUsername(String username);

    @Query("SELECT u FROM User u WHERE u.email = :email")
    Optional<User> findByEmail(String email);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.username = :username")
    boolean existsByUsername(String username);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.email = :email")
    boolean existsByEmail(String email);

    @Query(value = "SELECT u.* " +
            "FROM users u " +
            "JOIN food_entries f ON u.id = f.user_id " +
            "WHERE f.created_at >= DATE_FORMAT(CURRENT_DATE - INTERVAL 1 MONTH, '%Y-%m-01') " +
            "AND f.created_at <= CURRENT_TIMESTAMP " +
            "GROUP BY u.id " +
            "HAVING SUM(f.price) > :priceLimit", nativeQuery = true)

    List<User> findUsersExceedingMonthlyLimit(@Param("priceLimit") double priceLimit);

}
