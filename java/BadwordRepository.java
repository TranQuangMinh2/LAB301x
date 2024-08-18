package com.trnqngmnh.library;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BadwordRepository extends JpaRepository<Badword, Long> {
	@Query("SELECT b.name FROM Badword b")
	List<String> findAllNames();
}