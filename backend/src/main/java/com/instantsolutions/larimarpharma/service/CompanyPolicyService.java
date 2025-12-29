package com.instantsolutions.larimarpharma.service;

import com.instantsolutions.larimarpharma.entity.CompanyPolicy;
import com.instantsolutions.larimarpharma.exceptions.BadRequestException;
import com.instantsolutions.larimarpharma.exceptions.ResourceNotFoundException;
import com.instantsolutions.larimarpharma.repository.CompanyPolicyRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyPolicyService {

    @Autowired
    CompanyPolicyRepository repository;

    public CompanyPolicy createPolicy(CompanyPolicy policy) {
        validatePolicy(policy);

        if (repository.existsByPolicyCode(policy.getPolicyCode())) {
            throw new BadRequestException("Policy code already exists");
        }

        policy.setVersion(1);
        return repository.save(policy);
    }

    public List<CompanyPolicy> getAllPolicies() {
        return repository.findAll();
    }


    public CompanyPolicy getPolicyById(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Policy not found with id: " + id));
    }

    public CompanyPolicy getPolicyByCode(String policyCode) {
        return repository.findByPolicyCode(policyCode)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Policy not found with code: " + policyCode));
    }

    public CompanyPolicy updatePolicy(Long id, CompanyPolicy updated) {
        validatePolicy(updated);

        CompanyPolicy existing = getPolicyById(id);


        existing.setTitle(updated.getTitle());
        existing.setDescription(updated.getDescription());
        existing.setCategory(updated.getCategory());
        existing.setActive(updated.isActive());
        existing.setVersion(existing.getVersion() + 1);

        return repository.save(existing);
    }


        @Transactional
        public void deletePolicy(Long id) {
            CompanyPolicy policy = repository.findById(id)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Company policy not found with id: " + id));

            repository.delete(policy);
        }

    private void validatePolicy(CompanyPolicy policy) {
        if (policy == null) {
            throw new BadRequestException("Policy data is required");
        }
        if (!StringUtils.hasText(policy.getPolicyCode())) {
            throw new BadRequestException("Policy code is required");
        }
        if (!StringUtils.hasText(policy.getTitle())) {
            throw new BadRequestException("Title is required");
        }
        if (!StringUtils.hasText(policy.getDescription())) {
            throw new BadRequestException("Description is required");
        }
        if (policy.getCategory() == null) {
            throw new BadRequestException("Policy category is required");
        }
        try {
            CompanyPolicy.PolicyCategory.valueOf(policy.getCategory().name());
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("Invalid policy category");
        }
    }
}
