package com.neowallet.finance.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neowallet.finance.dto.CreateSavingsContributionRequest;
import com.neowallet.finance.dto.CreateSavingsGoalRequest;
import com.neowallet.finance.dto.UpdateSavingsGoalRequest;
import com.neowallet.finance.entity.FinancialOverview;
import com.neowallet.finance.entity.SavingsGoal;
import com.neowallet.finance.repository.FinancialOverviewRepository;
import com.neowallet.finance.repository.SavingsGoalRepository;
import com.neowallet.identity.entity.Family;
import com.neowallet.identity.entity.FamilyMember;
import com.neowallet.identity.entity.User;
import com.neowallet.identity.entity.UserProfile;
import com.neowallet.identity.repository.*;
import com.neowallet.identity.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class SavingsGoalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private FamilyRepository familyRepository;

    @Autowired
    private FamilyMemberRepository familyMemberRepository;

    @Autowired
    private FinancialOverviewRepository financialOverviewRepository;

    @Autowired
    private SavingsGoalRepository savingsGoalRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("TRUNCATE TABLE neowallet.savings_progress, neowallet.savings_contributions, neowallet.savings_goals, " +
            "neowallet.financial_overviews, neowallet.family_invitations, neowallet.family_members, neowallet.families, " +
            "neowallet.user_preferences, neowallet.user_profiles, neowallet.users CASCADE");
    }

    private User createUser(String email, String first, String last) {
        User user = new User();
        user.setEmail(email);
        user.setPasswordHash("$2a$10$hashed");
        user.setFirstName(first);
        user.setLastName(last);
        user.setAccountStatus("ACTIVE");
        user.setEmailVerified(true);
        user = userRepository.save(user);

        UserProfile profile = new UserProfile();
        profile.setUser(user);
        userProfileRepository.save(profile);

        return user;
    }

    private String tokenFor(User user) {
        return jwtService.generateAccessToken(user.getUserId(), null);
    }

    private UUID createFamilyFor(User owner) {
        Family family = new Family();
        family.setName("Test Family");
        family.setCurrency("USD");
        family.setOwner(owner);
        family.setMemberCount(1);
        family = familyRepository.save(family);

        FamilyMember ownerMember = new FamilyMember();
        ownerMember.setFamily(family);
        ownerMember.setUser(owner);
        ownerMember.setRole("OWNER");
        familyMemberRepository.save(ownerMember);

        return family.getFamilyId();
    }

    private void addFamilyMember(UUID familyId, User user, String role) {
        FamilyMember member = new FamilyMember();
        member.setFamily(familyRepository.getReferenceById(familyId));
        member.setUser(user);
        member.setRole(role);
        familyMemberRepository.save(member);
    }

    private FinancialOverview createOverview(UUID userId, UUID familyId, String period, BigDecimal income) {
        FinancialOverview overview = new FinancialOverview();
        overview.setUser(userRepository.getReferenceById(userId));
        if (familyId != null) {
            overview.setFamily(familyRepository.getReferenceById(familyId));
        }
        overview.setPeriod(period);
        overview.setCurrency("USD");
        overview.setPlanningIncome(income);
        overview.setEssentialAllocation(income.multiply(new BigDecimal("0.50")));
        overview.setVariableAllocation(BigDecimal.ZERO);
        overview.setSavingsAllocation(income.multiply(new BigDecimal("0.10")));
        overview.setEmergencyAllocation(income.multiply(new BigDecimal("0.05")));
        overview.setDiscretionaryPlanning(income.multiply(new BigDecimal("0.35")));
        return financialOverviewRepository.save(overview);
    }

    private SavingsGoal createGoal(User user, UUID familyId, String name, BigDecimal target, BigDecimal current, LocalDate date) {
        SavingsGoal goal = new SavingsGoal();
        goal.setUser(user);
        if (familyId != null) {
            goal.setFamily(familyRepository.getReferenceById(familyId));
        }
        goal.setName(name);
        goal.setTargetAmount(target);
        goal.setCurrentAmount(current);
        goal.setTargetDate(date);
        goal.setCurrency("USD");
        goal.setPriority("MEDIUM");
        goal.setStatus("ACTIVE");
        goal.setProgressPercentage(current.multiply(BigDecimal.valueOf(100)).divide(target, 2, BigDecimal.ROUND_HALF_UP));
        return savingsGoalRepository.save(goal);
    }

    @Test
    void createSavingsGoal() throws Exception {
        User user = createUser("save1@example.com", "Save", "One");
        createOverview(user.getUserId(), null, "2026-08", new BigDecimal("10000"));

        CreateSavingsGoalRequest request = new CreateSavingsGoalRequest(
            "Vacation", new BigDecimal("120000"), null,
            LocalDate.now().plusMonths(12), "USD", "HIGH", "Vacation");

        mockMvc.perform(post("/api/v1/savings-goals")
                .header("Authorization", "Bearer " + tokenFor(user))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("Vacation"))
            .andExpect(jsonPath("$.targetAmount").value(120000.00))
            .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void createAndGetSavingsGoal() throws Exception {
        User user = createUser("save2@example.com", "Save", "Two");
        createOverview(user.getUserId(), null, "2026-08", new BigDecimal("10000"));

        CreateSavingsGoalRequest request = new CreateSavingsGoalRequest(
            "Emergency", new BigDecimal("120000"), new BigDecimal("60000"),
            LocalDate.now().plusMonths(12), "USD", "HIGH", "Emergency");

        String response = mockMvc.perform(post("/api/v1/savings-goals")
                .header("Authorization", "Bearer " + tokenFor(user))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andReturn().getResponse().getContentAsString();

        String goalId = objectMapper.readTree(response).get("goalId").asText();

        mockMvc.perform(get("/api/v1/savings-goals/" + goalId)
                .header("Authorization", "Bearer " + tokenFor(user)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.currentAmount").value(60000.00))
            .andExpect(jsonPath("$.progressPercentage").value(50.00));
    }

    @Test
    void updateSavingsGoalMarksCompleted() throws Exception {
        User user = createUser("save3@example.com", "Save", "Three");
        createOverview(user.getUserId(), null, "2026-08", new BigDecimal("10000"));

        SavingsGoal goal = createGoal(user, null, "Laptop", new BigDecimal("120000"), new BigDecimal("100000"),
            LocalDate.now().plusMonths(6));

        UpdateSavingsGoalRequest request = new UpdateSavingsGoalRequest(
            null, null, new BigDecimal("120000"), null, null, null, null, null);

        mockMvc.perform(put("/api/v1/savings-goals/" + goal.getGoalId())
                .header("Authorization", "Bearer " + tokenFor(user))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("COMPLETED"))
            .andExpect(jsonPath("$.progressPercentage").value(100.00));
    }

    @Test
    void deleteSavingsGoalSoftDeletes() throws Exception {
        User user = createUser("save4@example.com", "Save", "Four");
        createOverview(user.getUserId(), null, "2026-08", new BigDecimal("10000"));

        SavingsGoal goal = createGoal(user, null, "Car", new BigDecimal("120000"), new BigDecimal("0"),
            LocalDate.now().plusMonths(12));

        mockMvc.perform(delete("/api/v1/savings-goals/" + goal.getGoalId())
                .header("Authorization", "Bearer " + tokenFor(user)))
            .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/savings-goals/" + goal.getGoalId())
                .header("Authorization", "Bearer " + tokenFor(user)))
            .andExpect(status().isNotFound());
    }

    @Test
    void progressCalculation() throws Exception {
        User user = createUser("save5@example.com", "Save", "Five");
        createOverview(user.getUserId(), null, "2026-08", new BigDecimal("10000"));

        SavingsGoal goal = createGoal(user, null, "House", new BigDecimal("120000"), new BigDecimal("60000"),
            LocalDate.now().plusMonths(12));

        mockMvc.perform(get("/api/v1/savings-goals/" + goal.getGoalId() + "/progress")
                .header("Authorization", "Bearer " + tokenFor(user)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.remainingAmount").value(60000.00))
            .andExpect(jsonPath("$.requiredMonthlyContribution").value(5000.00))
            .andExpect(jsonPath("$.status").exists());
    }

    @Test
    void forecastForLargeTarget() throws Exception {
        User user = createUser("save6@example.com", "Save", "Six");
        createOverview(user.getUserId(), null, "2026-08", new BigDecimal("10000"));

        SavingsGoal goal = createGoal(user, null, "Education", new BigDecimal("120000"), new BigDecimal("120000"),
            LocalDate.now().plusMonths(12));

        mockMvc.perform(get("/api/v1/savings-goals/" + goal.getGoalId() + "/forecast")
                .header("Authorization", "Bearer " + tokenFor(user)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.forecast.monthsToCompletion").value(0))
            .andExpect(jsonPath("$.forecast.projectedCompletionAmount").value(120000.00));
    }

    @Test
    void crossFamilyAccessDenied() throws Exception {
        User owner = createUser("owner1@example.com", "Owner", "One");
        UUID familyId = createFamilyFor(owner);
        createOverview(owner.getUserId(), familyId, "2026-08", new BigDecimal("10000"));

        User other = createUser("other1@example.com", "Other", "One");

        SavingsGoal goal = createGoal(owner, familyId, "Family Goal", new BigDecimal("120000"), new BigDecimal("0"),
            LocalDate.now().plusMonths(12));

        mockMvc.perform(get("/api/v1/savings-goals/" + goal.getGoalId())
                .header("Authorization", "Bearer " + tokenFor(other))
                .header("X-Family-ID", familyId.toString()))
            .andExpect(status().isForbidden());
    }

    @Test
    void memberCannotDeleteGoal() throws Exception {
        User owner = createUser("owner2@example.com", "Owner", "Two");
        UUID familyId = createFamilyFor(owner);

        User member = createUser("member2@example.com", "Member", "Two");
        addFamilyMember(familyId, member, "MEMBER");

        SavingsGoal goal = createGoal(owner, familyId, "Family Goal", new BigDecimal("120000"), new BigDecimal("0"),
            LocalDate.now().plusMonths(12));

        mockMvc.perform(delete("/api/v1/savings-goals/" + goal.getGoalId())
                .header("Authorization", "Bearer " + tokenFor(member))
                .header("X-Family-ID", familyId.toString()))
            .andExpect(status().isForbidden());
    }

    @Test
    void restrictedCannotCreateGoal() throws Exception {
        User owner = createUser("owner3@example.com", "Owner", "Three");
        UUID familyId = createFamilyFor(owner);

        User restricted = createUser("restricted3@example.com", "Restricted", "Three");
        addFamilyMember(familyId, restricted, "RESTRICTED");

        CreateSavingsGoalRequest request = new CreateSavingsGoalRequest(
            "Goal", new BigDecimal("120000"), null,
            LocalDate.now().plusMonths(12), "USD", "MEDIUM", null);

        mockMvc.perform(post("/api/v1/savings-goals")
                .header("Authorization", "Bearer " + tokenFor(restricted))
                .header("X-Family-ID", familyId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isForbidden());
    }

    @Test
    void invalidTargetDateRejected() throws Exception {
        User user = createUser("save7@example.com", "Save", "Seven");
        createOverview(user.getUserId(), null, "2026-08", new BigDecimal("10000"));

        CreateSavingsGoalRequest request = new CreateSavingsGoalRequest(
            "Goal", new BigDecimal("120000"), null,
            LocalDate.now().minusDays(1), "USD", "MEDIUM", null);

        mockMvc.perform(post("/api/v1/savings-goals")
                .header("Authorization", "Bearer " + tokenFor(user))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isUnprocessableEntity());
    }

    private String createGoalFor(User user, String name) {
        try {
            String response = mockMvc.perform(post("/api/v1/savings-goals")
                    .header("Authorization", "Bearer " + tokenFor(user))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(new CreateSavingsGoalRequest(
                        name, new BigDecimal("120000"), null,
                        LocalDate.now().plusMonths(12), "USD", "MEDIUM", null))))
                .andReturn().getResponse().getContentAsString();
            return objectMapper.readTree(response).get("goalId").asText();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void addContributionUpdatesProgress() throws Exception {
        User user = createUser("contrib1@example.com", "Contrib", "One");
        createOverview(user.getUserId(), null, "2026-08", new BigDecimal("10000"));
        String goalId = createGoalFor(user, "Vacation");

        CreateSavingsContributionRequest request = new CreateSavingsContributionRequest(
            new BigDecimal("5000.00"), LocalDate.now(), "Monthly");

        mockMvc.perform(post("/api/v1/savings-goals/" + goalId + "/contributions")
                .header("Authorization", "Bearer " + tokenFor(user))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.contribution.amount").value(5000.00))
            .andExpect(jsonPath("$.goal.currentAmount").value(5000.00))
            .andExpect(jsonPath("$.goal.progressPercentage").value(4.17))
            .andExpect(jsonPath("$.goal.goalId").value(goalId));
    }

    @Test
    void multipleContributionsAccumulate() throws Exception {
        User user = createUser("contrib2@example.com", "Contrib", "Two");
        createOverview(user.getUserId(), null, "2026-08", new BigDecimal("10000"));
        String goalId = createGoalFor(user, "Car");

        for (int i = 1; i <= 3; i++) {
            CreateSavingsContributionRequest request = new CreateSavingsContributionRequest(
                new BigDecimal("1000.00"), LocalDate.now(), "Week " + i);
            mockMvc.perform(post("/api/v1/savings-goals/" + goalId + "/contributions")
                    .header("Authorization", "Bearer " + tokenFor(user))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
        }

        mockMvc.perform(get("/api/v1/savings-goals/" + goalId)
                .header("Authorization", "Bearer " + tokenFor(user)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.currentAmount").value(3000.00))
            .andExpect(jsonPath("$.progressPercentage").value(2.50));
    }

    @Test
    void zeroContributionRejected() throws Exception {
        User user = createUser("contrib3@example.com", "Contrib", "Three");
        createOverview(user.getUserId(), null, "2026-08", new BigDecimal("10000"));
        String goalId = createGoalFor(user, "Zero");

        CreateSavingsContributionRequest request = new CreateSavingsContributionRequest(
            BigDecimal.ZERO, LocalDate.now(), null);

        mockMvc.perform(post("/api/v1/savings-goals/" + goalId + "/contributions")
                .header("Authorization", "Bearer " + tokenFor(user))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void negativeContributionRejected() throws Exception {
        User user = createUser("contrib4@example.com", "Contrib", "Four");
        createOverview(user.getUserId(), null, "2026-08", new BigDecimal("10000"));
        String goalId = createGoalFor(user, "Negative");

        CreateSavingsContributionRequest request = new CreateSavingsContributionRequest(
            new BigDecimal("-10.00"), LocalDate.now(), null);

        mockMvc.perform(post("/api/v1/savings-goals/" + goalId + "/contributions")
                .header("Authorization", "Bearer " + tokenFor(user))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void missingGoalRejected() throws Exception {
        User user = createUser("contrib5@example.com", "Contrib", "Five");

        CreateSavingsContributionRequest request = new CreateSavingsContributionRequest(
            new BigDecimal("100.00"), LocalDate.now(), null);

        mockMvc.perform(post("/api/v1/savings-goals/" + UUID.randomUUID() + "/contributions")
                .header("Authorization", "Bearer " + tokenFor(user))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }

    @Test
    void memberCannotContribute() throws Exception {
        User owner = createUser("owner-c1@example.com", "Owner", "C1");
        UUID familyId = createFamilyFor(owner);
        createOverview(owner.getUserId(), familyId, "2026-08", new BigDecimal("10000"));

        User member = createUser("member-c1@example.com", "Member", "C1");
        addFamilyMember(familyId, member, "MEMBER");

        SavingsGoal goal = createGoal(owner, familyId, "Family Goal", new BigDecimal("120000"), new BigDecimal("0"),
            LocalDate.now().plusMonths(12));

        CreateSavingsContributionRequest request = new CreateSavingsContributionRequest(
            new BigDecimal("1000.00"), LocalDate.now(), null);

        mockMvc.perform(post("/api/v1/savings-goals/" + goal.getGoalId() + "/contributions")
                .header("Authorization", "Bearer " + tokenFor(member))
                .header("X-Family-ID", familyId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isForbidden());
    }

    @Test
    void restrictedCannotContribute() throws Exception {
        User owner = createUser("owner-c2@example.com", "Owner", "C2");
        UUID familyId = createFamilyFor(owner);
        createOverview(owner.getUserId(), familyId, "2026-08", new BigDecimal("10000"));

        User restricted = createUser("restricted-c2@example.com", "Restricted", "C2");
        addFamilyMember(familyId, restricted, "RESTRICTED");

        SavingsGoal goal = createGoal(owner, familyId, "Restricted Goal", new BigDecimal("120000"), new BigDecimal("0"),
            LocalDate.now().plusMonths(12));

        CreateSavingsContributionRequest request = new CreateSavingsContributionRequest(
            new BigDecimal("1000.00"), LocalDate.now(), null);

        mockMvc.perform(post("/api/v1/savings-goals/" + goal.getGoalId() + "/contributions")
                .header("Authorization", "Bearer " + tokenFor(restricted))
                .header("X-Family-ID", familyId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isForbidden());
    }

    @Test
    void crossFamilyContributionDenied() throws Exception {
        User owner = createUser("owner-c3@example.com", "Owner", "C3");
        UUID familyId = createFamilyFor(owner);
        createOverview(owner.getUserId(), familyId, "2026-08", new BigDecimal("10000"));

        User other = createUser("other-c3@example.com", "Other", "C3");

        SavingsGoal goal = createGoal(owner, familyId, "Cross Goal", new BigDecimal("120000"), new BigDecimal("0"),
            LocalDate.now().plusMonths(12));

        CreateSavingsContributionRequest request = new CreateSavingsContributionRequest(
            new BigDecimal("1000.00"), LocalDate.now(), null);

        mockMvc.perform(post("/api/v1/savings-goals/" + goal.getGoalId() + "/contributions")
                .header("Authorization", "Bearer " + tokenFor(other))
                .header("X-Family-ID", familyId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isForbidden());
    }

    @Test
    void futureContributionDateRejected() throws Exception {
        User user = createUser("contrib6@example.com", "Contrib", "Six");
        createOverview(user.getUserId(), null, "2026-08", new BigDecimal("10000"));
        String goalId = createGoalFor(user, "Future");

        CreateSavingsContributionRequest request = new CreateSavingsContributionRequest(
            new BigDecimal("1000.00"), LocalDate.now().plusDays(1), null);

        mockMvc.perform(post("/api/v1/savings-goals/" + goalId + "/contributions")
                .header("Authorization", "Bearer " + tokenFor(user))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void numericPrecisionPreserved() throws Exception {
        User user = createUser("contrib7@example.com", "Contrib", "Seven");
        createOverview(user.getUserId(), null, "2026-08", new BigDecimal("10000"));

        SavingsGoal goal = createGoal(user, null, "Precision",
            new BigDecimal("123456789012.34"), BigDecimal.ZERO,
            LocalDate.now().plusMonths(12));

        CreateSavingsContributionRequest request = new CreateSavingsContributionRequest(
            new BigDecimal("123456789012.34"), LocalDate.now(), null);

        mockMvc.perform(post("/api/v1/savings-goals/" + goal.getGoalId() + "/contributions")
                .header("Authorization", "Bearer " + tokenFor(user))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.contribution.amount").value(123456789012.34))
            .andExpect(jsonPath("$.goal.currentAmount").value(123456789012.34))
            .andExpect(jsonPath("$.goal.progressPercentage").value(100.00));
    }

    @Test
    void ownerCanContributeToFamilyGoal() throws Exception {
        User owner = createUser("owner-c4@example.com", "Owner", "C4");
        UUID familyId = createFamilyFor(owner);
        createOverview(owner.getUserId(), familyId, "2026-08", new BigDecimal("10000"));

        SavingsGoal goal = createGoal(owner, familyId, "Family Car", new BigDecimal("120000"), new BigDecimal("0"),
            LocalDate.now().plusMonths(12));

        CreateSavingsContributionRequest request = new CreateSavingsContributionRequest(
            new BigDecimal("120000.00"), LocalDate.now(), "Full");

        mockMvc.perform(post("/api/v1/savings-goals/" + goal.getGoalId() + "/contributions")
                .header("Authorization", "Bearer " + tokenFor(owner))
                .header("X-Family-ID", familyId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.goal.currentAmount").value(120000.00))
            .andExpect(jsonPath("$.goal.status").value("COMPLETED"))
            .andExpect(jsonPath("$.goal.progressPercentage").value(100.00));
    }
}
