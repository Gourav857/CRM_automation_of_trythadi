package com.example.crm.service;

import com.example.crm.model.Lead;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class AutomationService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
    
    // 🚀 BREVO API KEY BINDING
    private final String BREVO_API_KEY = "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"; 

    // 🚀 Completely Non-Blocking Background Async Execution
    @Async("crmAsyncExecutor")
    public void runAutomationWorkflow(Lead lead) {
        System.out.println("\n[⚡ ASYNC ACTIVATED] Workflow triggered on background thread: "
                + Thread.currentThread().getName() + " for client: " + lead.getName());

        // 1️⃣ Welcome Email (Direct HTTP API Trigger)
        String welcomeSubject = "Welcome to Our Premium Service!";
        String welcomeContent = "Hi " + lead.getName() + ",\n\n" +
                "Thank you for reaching out to TryThadi. Our team has received your inquiry regarding " +
                (lead.getCompany() != null ? lead.getCompany() : "your business") + ".\n" +
                "An automation strategist will contact you shortly on " + lead.getPhone() + ".\n\n" +
                "Best Regards,\n" +
                "Gourav Yadav\n" +
                "Founder, TryThadi.AI";

        sendEmailViaBrevo(lead.getEmail(), lead.getName(), welcomeSubject, welcomeContent);

        // 2️⃣ Delayed Action: Automatic Follow-up after exactly 30 seconds
        scheduler.schedule(() -> {
            try {
                System.out.println("\n⏰ [DELAYED ACTION] Triggering automatic follow-up on thread: "
                        + Thread.currentThread().getName() + " for: " + lead.getName());
                
                String followUpSubject = "Exclusive 20% Discount Just For You!";
                String followUpContent = "Hi " + lead.getName() + ",\n\n" +
                        "We noticed you haven't responded yet. Here is an exclusive 20% discount coupon code: CRM20. Grab it now!\n\n" +
                        "Cheers!\n" +
                        "Team TryThadi.AI";

                sendEmailViaBrevo(lead.getEmail(), lead.getName(), followUpSubject, followUpContent);
                
            } catch (Exception e) {
                System.out.println("❌ [SCHEDULER ERROR] Background follow-up pipeline failed: " + e.getMessage());
            }
        }, 30, TimeUnit.SECONDS);
    }

    // 📧 Shared Private Helper: Sends Email using Brevo's Restricted Outbound HTTP API Router
    private void sendEmailViaBrevo(String toEmail, String toName, String subject, String content) {
        String url = "https://api.brevo.com/v3/smtp/email";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("api-key", BREVO_API_KEY);

        Map<String, Object> payload = new HashMap<>();
        
        // Sender Details
        Map<String, String> sender = new HashMap<>();
        sender.put("name", "TryThadi AI");
        sender.put("email", "ydasratkumar@gmail.com"); 
        payload.put("sender", sender);

        // Receiver Details
        Map<String, String> receiver = new HashMap<>();
        receiver.put("email", toEmail);
        receiver.put("name", toName);
        payload.put("to", Collections.singletonList(receiver));

        payload.put("subject", subject);
        payload.put("textContent", content);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            if (response.getStatusCode() == HttpStatus.CREATED || response.getStatusCode() == HttpStatus.OK) {
                System.out.println("📩 [HTTP API SUCCESS] Email successfully delivered to: " + toEmail + " | Subject: " + subject);
            } else {
                System.out.println("❌ [BREVO ROUTER REJECTION] HTTP Status: " + response.getStatusCode());
            }
        } catch (Exception e) {
            System.err.println("❌ [HTTP API CRASH]: Failed to transmit packet: " + e.getMessage());
        }
    }
}
