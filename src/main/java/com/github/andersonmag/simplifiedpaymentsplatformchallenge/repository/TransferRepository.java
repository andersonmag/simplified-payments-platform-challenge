package com.github.andersonmag.simplifiedpaymentsplatformchallenge.repository;

import com.github.andersonmag.simplifiedpaymentsplatformchallenge.domain.entities.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransferRepository extends JpaRepository<Transfer, Long> {}
