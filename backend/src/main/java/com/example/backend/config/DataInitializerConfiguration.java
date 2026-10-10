package com.example.backend.config;

import com.example.backend.domain.ComplaintStatus;
import com.example.backend.domain.Payment;
import com.example.backend.domain.PaymentStatus;
import com.example.backend.domain.Role;
import com.example.backend.domain.Room;
import com.example.backend.domain.Student;
import com.example.backend.domain.StudentComplaint;
import com.example.backend.domain.StudentStatus;
import com.example.backend.repository.PaymentRepository;
import com.example.backend.repository.RoomRepository;
import com.example.backend.repository.StudentComplaintRepository;
import com.example.backend.repository.StudentRepository;
import com.example.backend.repository.UserAccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Configuration
public class DataInitializerConfiguration {
    private static final Logger log = LoggerFactory.getLogger(DataInitializerConfiguration.class);

    @Bean
    @Order(10)
    ApplicationRunner seedHostelData(RoomRepository rooms,
                                     StudentRepository students,
                                     UserAccountRepository users,
                                     StudentComplaintRepository complaints,
                                     PaymentRepository payments) {
        return args -> {
            // 1. Seed Rooms if empty
            if (rooms.count() == 0) {
                log.info("Seeding initial hostel rooms with 1-5 sharing tariffs...");
                List<Room> initialRooms = List.of(
                        new Room("101", 1, new BigDecimal("7500.00")),
                        new Room("102", 2, new BigDecimal("7000.00")),
                        new Room("103", 3, new BigDecimal("6500.00")),
                        new Room("104", 4, new BigDecimal("6000.00")),
                        new Room("105", 5, new BigDecimal("5500.00")),
                        new Room("201", 1, new BigDecimal("7500.00")),
                        new Room("202", 2, new BigDecimal("7000.00")),
                        new Room("203", 3, new BigDecimal("6500.00")),
                        new Room("204", 4, new BigDecimal("6000.00")),
                        new Room("205", 5, new BigDecimal("5500.00")),
                        new Room("301", 2, new BigDecimal("7000.00")),
                        new Room("302", 3, new BigDecimal("6500.00")),
                        new Room("303", 4, new BigDecimal("6000.00")),
                        new Room("304", 5, new BigDecimal("5500.00"))
                );
                rooms.saveAll(initialRooms);
                log.info("Successfully seeded 14 hostel rooms across 1st, 2nd, and 3rd floors.");
            }

            // 2. Link existing students to their Room entity and UserAccount, ensure ACTIVE status and valid fees
            students.findAll().forEach(student -> {
                boolean changed = false;
                if (student.getStatus() != StudentStatus.INACTIVE && student.getStatus() != StudentStatus.ACTIVE) {
                    student.setStatus(StudentStatus.ACTIVE);
                    changed = true;
                }
                if (student.getAccount() == null && student.getEmail() != null) {
                    var acc = users.findByEmailIgnoreCase(student.getEmail().trim());
                    if (acc.isPresent()) {
                        student.setAccount(acc.get());
                        changed = true;
                    }
                }
                if (student.getAmountPerMonth() == null || student.getAmountPerMonth().signum() <= 0) {
                    student.setAmountPerMonth(new BigDecimal("6500.00"));
                    changed = true;
                }
                if (student.getSharing() == null || student.getSharing().isBlank()) {
                    student.setSharing("3 Sharing");
                    changed = true;
                }
                if (student.getRoom() == null && student.getRoomNumber() != null && !student.getRoomNumber().isBlank()) {
                    var r = rooms.findByRoomNumberIgnoreCase(student.getRoomNumber().trim());
                    if (r.isPresent()) {
                        student.setRoom(r.get());
                        changed = true;
                        log.info("Linked student {} to room {}", student.getStudentName(), r.get().getRoomNumber());
                    }
                }
                if (changed) {
                    students.save(student);
                }
            });

            // 3. Ensure every STUDENT user account has an active student profile
            users.findAll().stream()
                    .filter(u -> u.getRole() == Role.STUDENT)
                    .forEach(studentAccount -> {
                        if (students.findByAccountId(studentAccount.getId()).isEmpty()
                                && students.findByEmailIgnoreCase(studentAccount.getEmail()).isEmpty()) {
                            log.info("Creating default student profile for registered user {}", studentAccount.getEmail());
                            String name = studentAccount.getEmail().split("@")[0];
                            String studentName = Character.toUpperCase(name.charAt(0)) + name.substring(1);
                            Student student = new Student(studentName, studentAccount.getEmail());
                            student.setAccount(studentAccount);
                            student.setStatus(StudentStatus.ACTIVE);
                            student.updateProfile(
                                    studentName,
                                    studentAccount.getEmail(),
                                    "103",
                                    "3 Sharing",
                                    "Hostel Engineering College",
                                    "B.Tech 3rd Year",
                                    "+91 98765 12345",
                                    "+91 98765 67890",
                                    "Mana Boys Hostel, Main Campus",
                                    new BigDecimal("6500.00"),
                                    LocalDate.now().minusMonths(2)
                            );
                            rooms.findByRoomNumberIgnoreCase("103").ifPresent(student::setRoom);
                            students.save(student);
                        }
                    });

            // 4. Seed sample complaint if none exist
            if (complaints.count() == 0 && students.count() > 0) {
                Student sampleStudent = students.findAll().getFirst();
                StudentComplaint complaint = new StudentComplaint(sampleStudent,
                        "Maintenance: Study desk reading lamp & socket check",
                        "Hostel maintenance completed the reading light installation and verified room power outlets.");
                complaint.setStatus(ComplaintStatus.RESOLVED);
                complaints.save(complaint);
                log.info("Seeded sample resolved complaint.");
            }

            // 6. Seed past month payment if none exist
            if (payments.count() == 0 && students.count() > 0) {
                LocalDate today = LocalDate.now();
                LocalDate prevMonthDate = today.minusMonths(1);
                students.findAll().forEach(st -> {
                    BigDecimal amt = st.getAmountPerMonth() != null ? st.getAmountPerMonth() : new BigDecimal("6500.00");
                    String recNum = "REC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
                    String txnId = "TXN-INIT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
                    Payment pastPayment = new Payment(
                            st,
                            amt,
                            prevMonthDate.getMonthValue(),
                            prevMonthDate.getYear(),
                            prevMonthDate,
                            PaymentStatus.PAID,
                            recNum,
                            txnId
                    );
                    payments.save(pastPayment);
                    log.info("Seeded previous month paid record for student {}", st.getStudentName());
                });
            }
        };
    }
}
