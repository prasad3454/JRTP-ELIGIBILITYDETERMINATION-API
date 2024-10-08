package com.eligi.repository;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eligi.entity.Plan;

public interface PlanRepo extends JpaRepository<Plan, Serializable>{
	
//	public List<String> findPlanByNames();
}
