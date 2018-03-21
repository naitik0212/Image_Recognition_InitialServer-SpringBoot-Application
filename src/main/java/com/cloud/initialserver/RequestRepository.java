package com.cloud.initialserver;


import com.cloud.initialserver.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
}