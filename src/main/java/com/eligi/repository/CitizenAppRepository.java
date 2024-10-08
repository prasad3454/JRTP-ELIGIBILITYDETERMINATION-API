package com.eligi.repository;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eligi.entity.CitizenAppEntity;

public interface CitizenAppRepository extends JpaRepository<CitizenAppEntity, Serializable> {

}
