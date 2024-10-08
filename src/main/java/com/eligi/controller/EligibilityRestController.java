package com.eligi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.eligi.response.EligiResponse;
import com.eligi.service.EligiServiceImpl;

@RestController
public class EligibilityRestController {
	
	@Autowired
	private EligiServiceImpl serviceImpl;
	
	@GetMapping("/eligibility/{caseNum}")
	public EligiResponse determineEligibility(@PathVariable Long caseNum) {
		
		EligiResponse eligiResponse = serviceImpl.determineEligibility(caseNum);
		
		return eligiResponse;
	}
}
