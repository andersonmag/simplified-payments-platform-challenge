package com.github.andersonmag.simplifiedpaymentsplatformchallenge.repository;

import com.github.andersonmag.simplifiedpaymentsplatformchallenge.domain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
