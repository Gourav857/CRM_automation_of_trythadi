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
    private void sendRealEmail(String toEmail, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("ydasratkumar@gmail.com"); // Application properties context configuration email
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);
            System.out.println("📬 [EMAIL SUCCESS] Email successfully dispatched to: " + toEmail);
        } catch (Exception e) {
            System.out.println("❌ [EMAIL ERROR] Failed to send email: " + e.getMessage());
            System.out.println("💡 [MOCK LOG] Body would be: \n" + body);
        }
    }
}