package com.pdp.apphrmanagement.repository;

import com.pdp.apphrmanagement.entity.Role;
import com.pdp.apphrmanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;


@Repository
public interface UserRepo extends JpaRepository<User, UUID> {

    boolean existsByEmail(String  email);

    boolean existsByPassword(String password);

    Optional<User> findByEmailAndEmailCode(String email, String emailCode);

    Optional<User> findByEmail(String username);

    Optional<User> findByEmailCode(String emailCode);


    void deleteByEmail(String s);

    @Query("select u from users u where u.company.id = ?1 and u.enabled = true and u.roles = ?2")
    List<User> findAllByCompanyIdAndEnabledTrueAndRoles(Integer id, String role);

    @Query("select u from users u where u.id = ?1")
    Optional<User> findCompanyDirectorById(Integer directorId);


    @Query(nativeQuery = true, value = "select count(*)>0 " +
            "from users_roles ur join users u on ur.users_id=u.id" +
            "where ur.roles_id=1 and u.email=:email")
    boolean isDirector(String email);



    @Query(nativeQuery = true,value = "select * from users u join users_roles ur on ur.users_id=u.id where ur.roles_id in :rolesId ")
    Optional<List<User>> findAllByRolesId(List<Integer> rolesId);

    @Query(nativeQuery = true,value = "select count(*)>0 from users u join users_roles ur on u.id=ur.users_id where u.email=?1 and ur.roles_id=2")
    boolean isManager(String email);

    @Query(nativeQuery = true,value = "select count(*)>0 from users u join users_roles ur on u.id=ur.users_id where u.email=?1 and ur.roles_id=4")
    boolean isWorker(String email);

    @Query("select u from users u where u.email = ?1 and u.taskCode = ?2")
    Optional<User> findByEmailAndTaskCode(String email, String taskCode);
}
