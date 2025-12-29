package com.instantsolutions.larimarpharma.controller;

import com.instantsolutions.larimarpharma.entity.CompanyPolicy;
import com.instantsolutions.larimarpharma.service.CompanyPolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/company-policies")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class CompanyPolicyController {

    @Autowired
    private CompanyPolicyService companyPolicyService;

    /* CREATE */
    @PostMapping
    public ResponseEntity<CompanyPolicy> createPolicy(@RequestBody CompanyPolicy policy) {
        return ResponseEntity.ok(companyPolicyService.createPolicy(policy));
    }

    /* READ – ALL */
    @GetMapping
    public ResponseEntity<List<CompanyPolicy>> getAllPolicies() {
        return ResponseEntity.ok(companyPolicyService.getAllPolicies());
    }

    /* READ – BY ID */
    @GetMapping("/{id}")
    public ResponseEntity<CompanyPolicy> getPolicyById(@PathVariable Long id) {
        return ResponseEntity.ok(companyPolicyService.getPolicyById(id));
    }

    /* READ – BY CODE */
    @GetMapping("/code/{policyCode}")
    public ResponseEntity<CompanyPolicy> getPolicyByCode(@PathVariable String policyCode) {
        return ResponseEntity.ok(companyPolicyService.getPolicyByCode(policyCode));
    }

    /* UPDATE */
    @PutMapping("/{id}")
    public ResponseEntity<CompanyPolicy> updatePolicy(
            @PathVariable Long id,
            @RequestBody CompanyPolicy policy) {
        return ResponseEntity.ok(companyPolicyService.updatePolicy(id, policy));
    }

    /* DELETE */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePolicy(@PathVariable Long id) {
        companyPolicyService.deletePolicy(id);
        return ResponseEntity.ok("Policy deleted successfully");
    }
}
