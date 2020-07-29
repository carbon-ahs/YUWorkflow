package com.redteam.repository;

import com.redteam.model.ChangeTS;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChangeTSRepository extends JpaRepository<ChangeTS, Long> {
	Optional<ChangeTS> findById(Long id);
	Page<ChangeTS> findAllByApprover1(Pageable pageable, String approver1);
	Page<ChangeTS> findAllByApprover2(Pageable pageable, String approver2);
	Page<ChangeTS> findAllByApprover3(Pageable pageable, String approver3);

	List<ChangeTS> findAllByStatus(String status);

	List<ChangeTS> findAllByApprover1(String approver1);
	List<ChangeTS> findAllByApprover2(String approver2);
	List<ChangeTS> findAllByApprover3(String approver3);
	Optional<ChangeTS> findByTrackingId(String trackingId);

}
