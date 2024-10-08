package com.eligi.response;

import java.time.LocalDate;

import lombok.Data;

@Data
public class EligiResponse {
	
	private String planName;
	
	private String planStatus;
	
	private LocalDate planStartDate;
	
	private LocalDate planEndDate;
	
	private Double benefitAmt;
	
	private String denialReason;
}
