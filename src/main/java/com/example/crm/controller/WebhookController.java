package com.example.crm.controller;

import com.example.crm.model.Lead;
import com.example.crm.repository.LeadRepository;
import com.example.crm.service.AutomationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;  // 🚀 REDIS IMPORT
import org.springframework.cache.annotation.Cacheable;  // 🚀 REDIS IMPORT
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/webhook")
// ✅ Sahi Frontend URL explicitly allow kar diya hai bina aakhiri slash ke
@CrossOrigin(origins = "https://trythadi-85fr.onrender.com", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class WebhookController {

    @Autowired
    private LeadRepository leadRepository;

    @Autowired
    private AutomationService automationService;

    // 🚀 REDIS FIX: Nayi lead aane par purana cache pool clear ho jaye
    @PostMapping("/new-lead")
    @CacheEvict(value = "leadsCache", allEntries = true)
    public ResponseEntity<String> receiveNewLead(@RequestBody Lead incomingLead) {
        try {
            Lead lead = new Lead(
                    incomingLead.getName(),
                    incomingLead.getEmail(),
                    incomingLead.getPhone(),
                    incomingLead.getCompany(),
                    incomingLead.getMessage()
            );

            // ✅ AI SCREENING COMPLETELY REMOVED & DEFAULT STATUS SET TO PENDING
            lead.setStatus("PENDING");

            Lead savedLead = leadRepository.save(lead);

            System.out.println("\n========== NEW LEAD RECEIVED ==========");
            System.out.println("ID      : " + savedLead.getId());
            System.out.println("Name    : " + savedLead.getName());
            System.out.println("Status  : " + savedLead.getStatus());
            System.out.println("=======================================\n");

            automationService.runAutomationWorkflow(savedLead);

            return new ResponseEntity<>(
                    "{\"status\": \"Success\", \"message\": \"Lead processing in background with stable data ingestion\"}",
                    HttpStatus.CREATED
            );

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(
                    "{\"status\": \"Error\", \"message\": \"" + e.getMessage() + "\"}",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    // 🚀 FIXED STEP: Yeh internal helper method ab direct list ko cache karega bina ResponseEntity ke interference ke
    @Cacheable(value = "leadsCache", key = "'all'")
    public List<Lead> fetchLeadsFromDbOrCache() {
        System.out.println("🐢 [DATABASE HIT] Fetching directly from MySQL because cache is empty...");
        return leadRepository.findAll();
    }

    // 🚀 FIXED STEP: Yeh aapka real endpoint hai jo dashboard use karta hai
    @GetMapping("/all-leads")
    public ResponseEntity<List<Lead>> getAllLeads() {
        List<Lead> leads = fetchLeadsFromDbOrCache();
        return ResponseEntity.ok(leads);
    }

    // 🚀 REDIS FIX: Status badalne par purana cache pool delete ho jaye
    @PutMapping("/update-status/{id}")
    @CacheEvict(value = "leadsCache", allEntries = true)
    public ResponseEntity<Lead> updateLeadStatus(@PathVariable Long id, @RequestParam String status) {
        return leadRepository.findById(id).map(lead -> {
            lead.setStatus(status);
            Lead updatedLead = leadRepository.save(lead);
            System.out.println("🔄 [STATUS UPDATED] Lead ID #" + id + " changed status to: " + status);
            return ResponseEntity.ok(updatedLead);
        }).orElse(ResponseEntity.notFound().build());
    }

    // 🚀 REDIS FIX: Remarks/Notes save hone par bhi fresh cache reload ho
    @PutMapping("/update-notes/{id}")
    @CacheEvict(value = "leadsCache", allEntries = true)
    public ResponseEntity<Lead> updateLeadNotes(@PathVariable Long id, @RequestParam String notes) {
        return leadRepository.findById(id).map(lead -> {
            lead.setNotes(notes);
            Lead updatedLead = leadRepository.save(lead);
            System.out.println("📝 [NOTE SAVED] Added remark for Lead ID #" + id);
            return ResponseEntity.ok(updatedLead);
        }).orElse(ResponseEntity.notFound().build());
    }
}
