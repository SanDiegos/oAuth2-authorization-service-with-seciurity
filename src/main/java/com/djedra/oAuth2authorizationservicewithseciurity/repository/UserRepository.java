package com.djedra.oAuth2authorizationservicewithseciurity.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.djedra.oAuth2authorizationservicewithseciurity.entity.User;

//import ai.auth.jwt.domain.User;
public interface UserRepository extends JpaRepository<User, Long> {
	User findByUsername(String username);
}
