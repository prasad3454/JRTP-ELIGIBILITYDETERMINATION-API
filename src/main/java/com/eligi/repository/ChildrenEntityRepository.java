package com.eligi.repository;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eligi.entity.DCChildrenEntity;

public interface ChildrenEntityRepository extends JpaRepository<DCChildrenEntity, Serializable>{
	
	public List<DCChildrenEntity> findByCaseNum(Long caseNum);
}
