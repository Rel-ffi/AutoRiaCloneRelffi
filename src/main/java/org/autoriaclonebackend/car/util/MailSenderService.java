package org.autoriaclonebackend.car.util;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.autoriaclonebackend.car.model.CarAd;
import org.autoriaclonebackend.car.repository.CarAdRepository;
import org.autoriaclonebackend.user.model.Role;
import org.autoriaclonebackend.user.model.User;
import org.autoriaclonebackend.user.repository.RoleRepository;
import org.autoriaclonebackend.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
@RequiredArgsConstructor
@EnableScheduling
public class MailSenderService {
    private JavaMailSender javaMailSender;
    private RoleRepository roleRepository;
    private UserRepository userRepository;
    private CarAdRepository carAdRepository;

    @Value("${spring.mail.username}")
    private String mail;

    @Scheduled(cron = "0 0 * * * *")
    public void sendEmailScheduled() {

        Role role = roleRepository.findByName("MANAGER")
                .orElseThrow(() -> new RuntimeException("Role manager is undefined"));


        List<User> managers = userRepository.findAllByRoles(Set.of(role));
        List<CarAd> carAds = carAdRepository.findAllByStatusOrStatus("INACTIVE", "PENDING");

        if (carAds.isEmpty() || managers.isEmpty()) {
            log.info("No inactive or pending ads, or no managers found");
            return;
        }

        String adIds = carAds.stream().map(ad -> String.valueOf(ad.getId()))
                .collect(Collectors.joining(", "));
        String subject = "New INACTIVE or PENDING car ads detected";
        String message = "The following ads have status PENDING or INACTIVE: " + adIds;

        managers.forEach(manager -> sendEmail(manager.getEmail(), subject, message));
    }

    private void sendEmail(String email, String subject, String text) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);

            mimeMessage.setFrom(new InternetAddress(mail));
            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(text, true);

            javaMailSender.send(mimeMessage);
            log.info("Email sent to {}", email);
        } catch (MessagingException e) {
            log.error("Failed to send email to {}", email, e);
        }
    }
}