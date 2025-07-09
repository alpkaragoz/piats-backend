package com.piats.backend.config;

import com.piats.backend.enums.EmploymentType;
import com.piats.backend.enums.ExperienceLevel;
import com.piats.backend.enums.Role;
import com.piats.backend.models.*;
import com.piats.backend.repos.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final ApplicationStatusRepository applicationStatusRepository;
    private final JobPostingStatusRepository jobPostingStatusRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final JobPostingRepository jobPostingRepository;
    private final ApplicantRepository applicantRepository;
    private final ApplicationRepository applicationRepository;
    private final PasswordEncoder passwordEncoder;
    private final ConfessionRepository confessionRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        initializeLookupTables();
        seedCoreData();
    }

    private void initializeLookupTables() {
        if (applicationStatusRepository.count() == 0) {
            log.info("Initializing Application Statuses...");
            List<String> appStatuses = Arrays.asList("Draft", "New", "Longlisted", "Shortlisted", "Interview", "Rejected");
            appStatuses.forEach(name -> {
                ApplicationStatus status = new ApplicationStatus();
                status.setName(name);
                applicationStatusRepository.save(status);
            });
        }

        if (jobPostingStatusRepository.count() == 0) {
            log.info("Initializing Job Posting Statuses...");
            List<String> jobStatuses = Arrays.asList("Open", "Closed", "Draft");
            jobStatuses.forEach(name -> {
                JobPostingStatus status = new JobPostingStatus();
                status.setName(name);
                jobPostingStatusRepository.save(status);
            });
        }
    }

    private void seedCoreData() {
        if (userRepository.count() > 0) {
            log.info("Core data already exists. Skipping seed.");
            return;
        }
        log.info("Seeding core database...");

        Map<String, Skill> skills = createSkills();
        List<User> users = createUsers();
        List<JobPosting> jobPostings = createJobPostings(users, jobPostingStatusRepository.findAll());
        createApplications(jobPostings, skills, applicationStatusRepository.findAll());
        createConfessions();

        log.info("Core data seeding finished.");
    }

    private Map<String, Skill> createSkills() {
        return Arrays.asList("Java", "Spring Boot", "PostgreSQL", "Docker", "AWS", "React", "TypeScript", "SQL")
                .stream()
                .map(name -> {
                    Skill skill = new Skill();
                    skill.setName(name);
                    return skillRepository.save(skill);
                })
                .collect(Collectors.toMap(Skill::getName, skill -> skill));
    }

    private List<User> createUsers() {
        User user1 = new User();
        user1.setFirstName("Jane");
        user1.setLastName("Doe");
        user1.setEmail("jane.doe@example.com");
        user1.setPassword(passwordEncoder.encode("123456"));
        user1.setRole(Role.RECRUITER);

        User user2 = new User();
        user2.setFirstName("John");
        user2.setLastName("Smith");
        user2.setEmail("john.smith@example.com");
        user2.setPassword(passwordEncoder.encode("123456"));
        user2.setRole(Role.TECHNICAL_LEAD);

        return userRepository.saveAll(Arrays.asList(user1, user2));
    }

    private List<JobPosting> createJobPostings(List<User> users, List<JobPostingStatus> statuses) {
        JobPostingStatus openStatus = statuses.stream().filter(s -> "Open".equals(s.getName())).findFirst().orElseThrow();

        JobPosting jp1 = new JobPosting();
        jp1.setTitle("Senior Backend Engineer");
        jp1.setDescription("""
                We are seeking an experienced Senior Backend Engineer to join our dynamic team. You will be at the heart of our mission to build a robust and scalable platform, tackling challenges in architecture, performance, and security.

                **Responsibilities:**
                - Design, develop, and maintain high-performance, reliable, and scalable backend services using Java and Spring Boot.
                - Collaborate with cross-functional teams to define, design, and ship new features.
                - Write clean, maintainable, and well-tested code.
                - Mentor junior engineers and contribute to a culture of technical excellence.
                - Participate in code reviews and architectural discussions.
                - Optimize applications for maximum speed and scalability.

                **Qualifications:**
                - 5+ years of professional experience in backend development.
                - Strong proficiency in Java and the Spring Framework (Spring Boot, Spring MVC).
                - Experience with relational databases like PostgreSQL.
                - Familiarity with cloud services (AWS, GCP, or Azure).
                - Solid understanding of RESTful API design and microservices architecture.
                - Experience with containerization technologies like Docker and Kubernetes is a plus.
                """);
        jp1.setLocation("Remote");
        jp1.setEmploymentType(EmploymentType.FULL_TIME);
        jp1.setExperienceLevel(ExperienceLevel.SENIOR_LEVEL);
        jp1.setCreatedBy(users.get(0));
        jp1.setAssignee(users.get(0));
        jp1.setStatus(openStatus);

        JobPosting jp2 = new JobPosting();
        jp2.setTitle("Frontend Developer (React)");
        jp2.setDescription("""
                We are looking for a passionate Frontend Developer to create beautiful and intuitive user interfaces for our web applications. You will work with a talented team of designers and engineers to bring our products to life.

                **Responsibilities:**
                - Develop new user-facing features using React.js and TypeScript.
                - Build reusable components and front-end libraries for future use.
                - Translate designs and wireframes into high-quality code.
                - Optimize components for maximum performance across a vast array of web-capable devices and browsers.
                - Collaborate with backend engineers to integrate with our APIs.

                **Qualifications:**
                - 2+ years of experience in frontend development.
                - Strong proficiency in JavaScript, TypeScript, and React.js.
                - Experience with popular React.js workflows (such as Redux or Context API).
                - Familiarity with modern frontend build pipelines and tools (e.g., Webpack, Babel, NPM).
                - A keen eye for detail and a passion for UI/UX.
                - Experience with RESTful APIs.
                """);
        jp2.setLocation("New York, NY");
        jp2.setEmploymentType(EmploymentType.FULL_TIME);
        jp2.setExperienceLevel(ExperienceLevel.MID_LEVEL);
        jp2.setCreatedBy(users.get(1));
        jp2.setAssignee(users.get(0));
        jp2.setStatus(openStatus);

        return jobPostingRepository.saveAll(Arrays.asList(jp1, jp2));
    }

    private void createApplications(List<JobPosting> jobPostings, Map<String, Skill> skills, List<ApplicationStatus> statuses) {
        ApplicationStatus newStatus = statuses.stream().filter(s -> "New".equals(s.getName())).findFirst().orElseThrow();
        ApplicationStatus longlistedStatus = statuses.stream().filter(s -> "Longlisted".equals(s.getName())).findFirst().orElseThrow();

        // --- Application 1: Alice for Backend Role ---
        Applicant applicant1 = new Applicant();
        applicant1.setFirstName("Alice");
        applicant1.setLastName("Johnson");
        applicant1.setEmail("alice.j@example.com");
        applicant1.setProfessionalSummary("Experienced backend developer with 5+ years in Java and cloud systems.");
        applicant1.setPhone("123-456-7890");
        applicantRepository.save(applicant1);

        Application app1 = new Application();
        app1.setApplicant(applicant1);
        app1.setJobPosting(jobPostings.get(0));
        app1.setStatus(longlistedStatus);
        app1.setRanking(1);

        Experience exp1 = new Experience();
        exp1.setApplication(app1);
        exp1.setJobTitle("Software Engineer");
        exp1.setCompanyName("Tech Solutions Inc.");
        exp1.setStartDate(LocalDate.of(2020, 1, 15));
        app1.getExperiences().add(exp1);
        
        Education edu1 = new Education();
        edu1.setApplication(app1);
        edu1.setDegree("B.Sc. in Computer Science");
        edu1.setInstitution("State University");
        edu1.setEndDate(LocalDate.of(2019, 12, 20));
        app1.getEducations().add(edu1);
        
        app1.getSkills().add(createAppSkill(app1, skills.get("Java"), 5));
        app1.getSkills().add(createAppSkill(app1, skills.get("Spring Boot"), 4));
        app1.getSkills().add(createAppSkill(app1, skills.get("AWS"), 3));
        applicationRepository.save(app1);


        // --- Application 2: Bob for Frontend Role ---
        Applicant applicant2 = new Applicant();
        applicant2.setFirstName("Bob");
        applicant2.setLastName("Williams");
        applicant2.setEmail("bob.w@example.com");
        applicant2.setProfessionalSummary("Creative frontend developer passionate about user experience.");
        applicant2.setPhone("098-765-4321");
        applicantRepository.save(applicant2);

        Application app2 = new Application();
        app2.setApplicant(applicant2);
        app2.setJobPosting(jobPostings.get(1));
        app2.setStatus(newStatus);
        app2.setRanking(1);

        Experience exp2 = new Experience();
        exp2.setApplication(app2);
        exp2.setJobTitle("UI Developer");
        exp2.setCompanyName("Web Creations LLC");
        exp2.setStartDate(LocalDate.of(2021, 6, 1));
        app2.getExperiences().add(exp2);

        Education edu2 = new Education();
        edu2.setApplication(app2);
        edu2.setDegree("B.A. in Digital Media");
        edu2.setInstitution("Arts College");
        edu2.setEndDate(LocalDate.of(2021, 5, 15));
        app2.getEducations().add(edu2);

        app2.getSkills().add(createAppSkill(app2, skills.get("React"), 3));
        app2.getSkills().add(createAppSkill(app2, skills.get("TypeScript"), 2));
        applicationRepository.save(app2);
    }
    
    private ApplicationSkill createAppSkill(Application app, Skill skill, int years) {
        ApplicationSkill appSkill = new ApplicationSkill();
        appSkill.setApplication(app);
        appSkill.setSkill(skill);
        appSkill.setYearsOfExperience(years);
        return appSkill;
    }

    private void createConfessions() {

        if (confessionRepository.count() > 0) {
            log.info("Confession data already exists. Skipping seed.");
            return;
        }
        log.info("Seeding confession data...");

        Confession c1 = new Confession();
        c1.setNickname("Anonymous A");
        c1.setConfessionText("I secretly take extra biscuits from the office kitchen.");
        c1.setDepartment("Marketing");
        c1.setCreatedAt(ZonedDateTime.now().minusHours(1));

        Confession c2 = new Confession();
        c2.setNickname("Code Ninja");
        c2.setConfessionText("Sometimes I copy-paste code from Stack Overflow without understanding it fully.");
        c2.setDepartment("Engineering");
        c2.setCreatedAt(ZonedDateTime.now().minusHours(3));

        Confession c3 = new Confession();
        c3.setNickname("Office Prankster");
        c3.setConfessionText("I swapped everyone's mouse settings to left-handed for a day, and nobody noticed.");
        c3.setDepartment("IT Support");

        Confession c4 = new Confession();
        c4.setNickname("Coffee Lover");
        c4.setConfessionText("I've blamed the empty coffee pot on others more times than I can count.");
        c4.setDepartment("HR");

        Confession c5 = new Confession();
        c5.setNickname("The Daydreamer");
        c5.setConfessionText("My best ideas usually come to me during long, unproductive meetings.");
        c5.setDepartment("Product Development");

        Confession c6 = new Confession();
        c6.setNickname("Weekend Warrior");
        c6.setConfessionText("I pretend to be busy on Fridays, but I'm actually planning my weekend adventures.");
        c6.setDepartment("Sales");

        Confession c7 = new Confession();
        c7.setNickname("Email Evader");
        c7.setConfessionText("I often mark emails as 'read' without actually opening them, hoping they go away.");
        c7.setDepartment("Customer Service");
        c7.setCreatedAt(ZonedDateTime.now().minusHours(5));

        Confession c8 = new Confession();
        c8.setNickname("Meeting Muter");
        c8.setConfessionText("I join video calls and immediately mute myself, then wander off to do other things.");
        c8.setDepartment("Operations");
        c8.setCreatedAt(ZonedDateTime.now().minusHours(2));

        Confession c9 = new Confession();
        c9.setNickname("Stationery Hoarder");
        c9.setConfessionText("My desk drawer is a secret stash of company pens, notebooks, and sticky notes.");
        c9.setDepartment("Administration");
        c9.setCreatedAt(ZonedDateTime.now().minusDays(1));

        Confession c10 = new Confession();
        c10.setNickname("Snack Thief");
        c10.setConfessionText("I occasionally 'borrow' snacks from unattended desks, pretending I didn't see them.");
        c10.setDepartment("Finance");
        c10.setCreatedAt(ZonedDateTime.now().minusDays(2));

        Confession c11 = new Confession();
        c11.setNickname("The Impostor");
        c11.setConfessionText("I sometimes nod knowingly in meetings even when I have no idea what's being discussed.");
        c11.setDepartment("Research & Development");
        c11.setCreatedAt(ZonedDateTime.now().minusHours(6));

        Confession c12 = new Confession();
        c12.setNickname("Desk Chef");
        c12.setConfessionText("I've attempted to cook instant noodles with the office hot water dispenser more than once.");
        c12.setDepartment("Logistics");
        c12.setCreatedAt(ZonedDateTime.now().minusHours(4));

        confessionRepository.saveAll(Arrays.asList(c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12));
        log.info("Confession data seeding finished.");
    }
} 