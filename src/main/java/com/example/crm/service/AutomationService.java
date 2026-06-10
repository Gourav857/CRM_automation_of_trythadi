package com.example.crm.service;

import com.example.crm.model.Lead;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async; // 🚀 Added for asynchronous execution
import org.springframework.stereotype.Service;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class AutomationService {

    @Autowired
    private JavaMailSender mailSender; // Spring Boot ka built-in email sender

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    // 🚀 Industrial Grade Optimization: Whole workflow is now completely non-blocking
    @Async("crmAsyncExecutor")
    public void runAutomationWorkflow(Lead lead) {
        System.out.println("\n[⚡ ASYNC ACTIVATED] Workflow triggered on background thread: "
                + Thread.currentThread().getName() + " for client: " + lead.getName());

        // 1. Welcome Email (Executes immediately on background thread)
        sendRealEmail(
                lead.getEmail(),
                "Welcome to Our Premium Service!",
                "Hi " + lead.getName() + ",\n\nThank you for reaching out to us. Our team will contact you shortly on " + lead.getPhone() + ".\n\nBest Regards,\nCRM Automation System"
        );

        // 2. Thik 30 Seconds baad automatic Follow-up (Handled inside async execution stack)
        scheduler.schedule(() -> {
            try {
                if (lead.getStatus() == null || "NEW".equals(lead.getStatus())) {
                    System.out.println("\n⏰ [DELAYED ACTION] Triggering automatic follow-up on thread: "
                            + Thread.currentThread().getName() + " for: " + lead.getName());
                    sendRealEmail(
                            lead.getEmail(),
                            "Exclusive 20% Discount Just For You!",
                            "Hi " + lead.getName() + ",\n\nWe noticed you haven't responded yet. Here is an exclusive 20% discount coupon code: CRM20. Grab it now!\n\nCheers!"
                    );
                }
            } catch (Exception e) {
                System.out.println("❌ [SCHEDULER ERROR] Background pipeline execution failed: " + e.getMessage());
            }
        }, 30, TimeUnit.SECONDS);
    }

    // Email bhejne ka common method
    package com.example.crm.service;

import com.example.crm.model.Lead;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.util.*;

@Service
public class AutomationService {

    private final RestTemplate restTemplate = new RestTemplate();
    
    // ⚠️ APNI BREVO API KEY YAHAN PASTE KAREIN
    private final String BREVO_API_KEY = "xsmtpsib-d680bd98cf9535c1812263325191195bf5ae8528113b83c66e6db08d8d0d5425-Rg9F6AEAIKI3vEQC"; 

    public void runAutomationWorkflow(Lead lead) {
        System.out.println("[⚡ ASYNC ACTIVATED] Workflow triggered on background thread for client: " + lead.getName());

        String url = "https://api.brevo.com/v3/smtp/email";

        // Headers Configuration
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("api-key", BREVO_API_KEY);

        // Dynamic Professional Email Body
        String emailContent = "Hi " + lead.getName() + ",\n\n" +
                "Thank you for reaching out to TryThadi. Our team has received your inquiry regarding " +
                (lead.getCompany() != null ? lead.getCompany() : "your business") + ".\n" +
                "An automation strategist will contact you shortly on " + lead.getPhone() + ".\n\n" +
                "Best Regards,\n" +
                "Gourav Yadav\n" +
                "Founder, TryThadi.AI";

        // Brevo Payload Structure (JSON)
        Map<String, Object> payload = new HashMap<>();
        
        // Sender details
        Map<String, String> sender = new HashMap<>();
        sender.put("name", "TryThadi AI");
        sender.put("email", "ydasratkumar@gmail.com"); // ⚠️ Aapka verified sender email
        payload.put("sender", sender);

        // Receiver details
        Map<String, String> toUser = new HashMap<>();
        toUser.put("email", lead.getEmail());
        toUser.put("name", lead.getName());
        payload.put("to", Collections.singletonList(toUser));

        payload.put("subject", "We Received Your Inquiry - TryThadi.AI");
        payload.put("textContent", emailContent);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            if (response.getStatusCode() == HttpStatus.CREATED || response.getStatusCode() == HttpStatus.OK) {
                System.out.println("📩 [HTTP API SUCCESS] Email successfully delivered via Brevo Router to: " + lead.getEmail());
            } else {
                System.out.println("❌ [BREVO ERROR] Code: " + response.getStatusCode());
            }
        } catch (Exception e) {
            System.err.println("❌ [HTTP MAIL PIPELINE CRASH]: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
