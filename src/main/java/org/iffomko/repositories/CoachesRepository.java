package org.iffomko.repositories;

import org.iffomko.domain.Coach;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoachesRepository extends JpaRepository<Coach, Integer> {
}
