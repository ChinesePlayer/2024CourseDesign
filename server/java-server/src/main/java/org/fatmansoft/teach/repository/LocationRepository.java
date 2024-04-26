package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.CourseLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationRepository extends JpaRepository<CourseLocation, Integer> {

}
