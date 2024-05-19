package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.ApplicationType;
import org.fatmansoft.teach.models.EApplicationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApplicationTypeRepository extends JpaRepository<ApplicationType,Integer> {
    @Query("select at from ApplicationType at where at.typeName=?1")
    Optional<ApplicationType> findByType(EApplicationType typeName);
}
