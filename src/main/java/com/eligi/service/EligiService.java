package com.eligi.service;

import com.eligi.response.EligiResponse;

public interface EligiService {
	
	public EligiResponse determineEligibility(Long caseNum);
}
