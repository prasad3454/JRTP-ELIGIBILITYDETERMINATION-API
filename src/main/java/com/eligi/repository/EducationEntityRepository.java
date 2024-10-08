package com.eligi.repository;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eligi.entity.DCEducationEntity;

public interface EducationEntityRepository extends JpaRepository<DCEducationEntity, Serializable>{
	
	public DCEducationEntity findByCaseNum(Long caseNum);
}
