package com.eligi.service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eligi.entity.CitizenAppEntity;
import com.eligi.entity.CoTriggerEntity;
import com.eligi.entity.DCCaseEntity;
import com.eligi.entity.DCChildrenEntity;
import com.eligi.entity.DCEducationEntity;
import com.eligi.entity.DCIncomeEntity;
import com.eligi.entity.EligibilityDetailEntity;
import com.eligi.entity.Plan;
import com.eligi.repository.CaseEntityRepository;
import com.eligi.repository.ChildrenEntityRepository;
import com.eligi.repository.CitizenAppRepository;
import com.eligi.repository.CoTriggerEntityRepository;
import com.eligi.repository.EducationEntityRepository;
import com.eligi.repository.EligibilityDetailEntityRepository;
import com.eligi.repository.IncomeEntityRepository;
import com.eligi.repository.PlanRepo;
import com.eligi.response.EligiResponse;

@Service
public class EligiServiceImpl implements EligiService {

	@Autowired
	private CaseEntityRepository caseEntityRepository;

	@Autowired
	private PlanRepo planRepo;

	@Autowired
	private IncomeEntityRepository incomeEntityRepository;

	@Autowired
	private ChildrenEntityRepository childrenEntityRepository;

	@Autowired
	private CitizenAppRepository appRepository;

	@Autowired
	private EducationEntityRepository educationEntityRepository;

	@Autowired
	private EligibilityDetailEntityRepository detailEntityRepository;
	
	@Autowired
	private CoTriggerEntityRepository coTriggerEntityRepository;

	@Override
	public EligiResponse determineEligibility(Long caseNum) {

		Optional<DCCaseEntity> dcCaseEntity = caseEntityRepository.findById(caseNum);
		Integer planId = null;
		String planName = null;
		Integer appId = null;

		if (dcCaseEntity.isPresent()) {
			DCCaseEntity caseEntity = dcCaseEntity.get();
			planId = caseEntity.getPlanId();
			appId = caseEntity.getAppId();
		}

		Optional<Plan> plan = planRepo.findById(planId);

		if (plan.isPresent()) {
			Plan planEntity = plan.get();
			planName = planEntity.getPlanName();
		}

		Optional<CitizenAppEntity> appEntity = appRepository.findById(appId);
		Integer age = 0;
		CitizenAppEntity citizenAppEntity = appEntity.get();
		
		if (appEntity.isPresent()) {
			LocalDate dob = citizenAppEntity.getDob();
			LocalDate now = LocalDate.now();

			age = Period.between(dob, now).getYears();
		}

		EligiResponse eligResponse = executePlanCondition(caseNum, planName, age);

		EligibilityDetailEntity detailEntity = new EligibilityDetailEntity();
		BeanUtils.copyProperties(eligResponse, detailEntity);
		
		detailEntity.setCaseNum(caseNum);
		detailEntity.setHolderName(citizenAppEntity.getFullName());
		detailEntity.setHolderSsn(citizenAppEntity.getSsn());
		
		detailEntityRepository.save(detailEntity);
		
		CoTriggerEntity triggerEntity = new CoTriggerEntity();
		triggerEntity.setCaseNum(caseNum);
		triggerEntity.setTrgStatus("Pending");
		
		coTriggerEntityRepository.save(triggerEntity);

		return eligResponse;
	}

	private EligiResponse executePlanCondition(Long caseNum, String planName, Integer age) {

		EligiResponse response = new EligiResponse();
		response.setPlanName(planName);

		DCIncomeEntity incomeEntity = incomeEntityRepository.findByCaseNum(caseNum);

		if ("SNAP".equals(planName)) {

			Double empIncome = incomeEntity.getEmpIncome();

			if (empIncome <= 300) {
				response.setPlanStatus("AP");
			} else {
				response.setPlanStatus("DN");
				response.setDenialReason("High Income");
			}

		} else if ("CCAP".equals(planName)) {

			boolean ageCondition = true;
			boolean kidsCountCondition = false;

			List<DCChildrenEntity> childrenEntity = childrenEntityRepository.findByCaseNum(caseNum);

			if (!childrenEntity.isEmpty()) {
				kidsCountCondition = true;
				for (DCChildrenEntity childs : childrenEntity) {
					Integer childAge = childs.getChildrenAge();
					if (childAge > 16) {
						ageCondition = false;
						break;
					}
				}
			}

			if (incomeEntity.getEmpIncome() <= 300 && kidsCountCondition && ageCondition) {
				response.setPlanStatus("AP");
			} else {
				response.setPlanStatus("DN");
				response.setDenialReason("Not Satisfied Business Rule");
			}

		} else if ("Medicaid".equals(planName)) {

			Double empIncome = incomeEntity.getEmpIncome();
			Double empProperty = incomeEntity.getPropertyIncome();

			if (empIncome <= 300 && empProperty == 0) {
				response.setPlanStatus("AP");
			} else {
				response.setPlanStatus("DN");
				response.setDenialReason("High Income");
			}

		} else if ("Medicare".equals(planName)) {

			if (age >= 65) {
				response.setPlanStatus("AP");
			} else {
				response.setPlanStatus("DN");
				response.setDenialReason("Age Not matched");
			}

		} else if ("NJW".equals(planName)) {

			DCEducationEntity educationEntity = educationEntityRepository.findByCaseNum(caseNum);
			Integer graduationYear = educationEntity.getGraduationYear();
			int currYear = LocalDate.now().getYear();

			if (incomeEntity.getEmpIncome() <= 0 && graduationYear < currYear) {
				response.setPlanStatus("AP");
			} else {
				response.setPlanStatus("DN");
				response.setDenialReason("Rules Not Satisfied");
			}
		}

		if (response.getPlanStatus().equals("AP")) {

			response.setPlanStartDate(LocalDate.now());
			response.setPlanEndDate(LocalDate.now().plusMonths(6));
			response.setBenefitAmt(350.00D);

		}

		return response;
	}

}
