package com.selzlein.djeison.fileuploader.repository;

import com.selzlein.djeison.fileuploader.domain.Authority;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the Authority entity.
 */
public interface AuthorityRepository extends JpaRepository<Authority, String> {
}
