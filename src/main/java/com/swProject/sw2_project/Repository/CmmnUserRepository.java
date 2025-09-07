package com.swProject.sw2_project.Repository;


import com.swProject.sw2_project.Entity.CmmnUser;
import com.swProject.sw2_project.Entity.CmmnUserLogin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface CmmnUserRepository extends JpaRepository<CmmnUser, String> {
    @Query("SELECT u.userId FROM CmmnUser u WHERE u.userEmail = :userEmail")
    String findUserIdByEmail(@Param("userEmail") String userEmail);

    @Query("SELECT u.userId FROM CmmnUserLogin u WHERE u.userId = :userId")
    String findByChkUserId(@Param("userId") String userId);

    @Query("SELECT u.userPassword FROM CmmnUserLogin u WHERE u.userId = :userId")
    String findUserPassword(@Param("userId") String userId);

    @Modifying
    @Transactional
    @Query("UPDATE CmmnUserLogin u " +
            "SET u.userPassword = :userPassword, " +
            "u.beforeUserPassword = :beforeUserPassword " +
            "WHERE u.userId = :userId")
    int updateUserPassword(@Param("userPassword") String userPassword,
                           @Param("beforeUserPassword") String beforeUserPassword,
                           @Param("userId") String userId);

}
