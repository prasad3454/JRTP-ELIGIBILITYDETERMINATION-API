package com.eligi.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "DC_EDUCATION")
@Data
public class DCEducationEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long eduId;
	
	private Long caseNum;
	
	private String highestQualification;
	
	private Integer graduationYear;
	
	private String universityName;
}
