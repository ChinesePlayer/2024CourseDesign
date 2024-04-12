package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.EHonorType;
import org.fatmansoft.teach.models.UserType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HonorTypeRepository extends JpaRepository<UserType, Integer> {
    UserType findByName(EHonorType name);
}
