package com.java.sahil.minio.repo;

import com.java.sahil.minio.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User, Long> {
}
