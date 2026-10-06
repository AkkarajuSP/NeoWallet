package com.neowallet.finance.controller;

import com.neowallet.finance.entity.FinancialAllocation;
import com.neowallet.finance.entity.FinancialOverview;
import com.neowallet.finance.repository.FinancialAllocationRepository;
import com.neowallet.finance.repository.FinancialOverviewRepository;
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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class FinancialOverviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

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
    private FinancialAllocationRepository financialAllocationRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("TRUNCATE TABLE neowallet.financial_allocations, neowallet.financial_periods, neowallet.financial_overviews, neowallet.family_invitations, neowallet.family_members, neowallet.families, neowallet.user_preferences, neowallet.user_profiles, neowallet.users CASCADE");
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

    private FinancialOverview createOverviewForUser(User user) {
        FinancialOverview overview = new FinancialOverview();
        overview.setUser(user);
        overview.setPlanningIncome(new BigDecimal("5000.00"));
        overview.setMandatoryCommitments(new BigDecimal("1200.00"));
        overview.setEssentialAllocation(new BigDecimal("1500.00"));
        overview.setVariableAllocation(new BigDecimal("800.00"));
        overview.setSavingsAllocation(new BigDecimal("500.00"));
        overview.setEmergencyAllocation(new BigDecimal("300.00"));
        overview.setDiscretionaryPlanning(new BigDecimal("400.00"));
        overview.setCommittedAmount(new BigDecimal("900.00"));
        overview.setPendingPayments(new BigDecimal("200.00"));
        overview.setActualTransactions(new BigDecimal("600.00"));
        overview.setAvailableFinancialCapacity(new BigDecimal("3500.00"));
        overview.setCurrency("USD");
        overview.setPeriod(YearMonth.now().toString());
        return financialOverviewRepository.save(overview);
    }

    private FinancialOverview createOverviewForFamily(Family family) {
        FinancialOverview overview = new FinancialOverview();
        overview.setFamily(family);
        overview.setPlanningIncome(new BigDecimal("8000.00"));
        overview.setCommittedAmount(new BigDecimal("1500.00"));
        overview.setActualTransactions(new BigDecimal("1000.00"));
        overview.setAvailableFinancialCapacity(new BigDecimal("5500.00"));
        overview.setCurrency("USD");
        overview.setPeriod(YearMonth.now().toString());
        return financialOverviewRepository.save(overview);
    }

    @Test
    void getIndividualOverview() throws Exception {
        User user = createUser("user1@example.com", "User", "One");
        createOverviewForUser(user);

        mockMvc.perform(get("/api/v1/financial-overview")
                .header("Authorization", "Bearer " + tokenFor(user)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.planningIncome").value(5000.00))
            .andExpect(jsonPath("$.availableFinancialCapacity").value(3500.00))
            .andExpect(jsonPath("$.disclaimer").exists());
    }

    @Test
    void getFamilyOverview() throws Exception {
        User owner = createUser("owner@example.com", "Owner", "One");
        UUID familyId = createFamilyFor(owner);
        Family family = familyRepository.findById(familyId).orElseThrow();
        createOverviewForFamily(family);

        mockMvc.perform(get("/api/v1/financial-overview")
                .header("Authorization", "Bearer " + tokenFor(owner))
                .header("X-Family-ID", familyId.toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.familyId").value(familyId.toString()))
            .andExpect(jsonPath("$.planningIncome").value(8000.00))
            .andExpect(jsonPath("$.availableFinancialCapacity").value(5500.00));
    }

    @Test
    void nonMemberCannotAccessFamilyOverview() throws Exception {
        User owner = createUser("owner2@example.com", "Owner", "Two");
        User stranger = createUser("stranger@example.com", "Stranger", "One");
        UUID familyId = createFamilyFor(owner);

        mockMvc.perform(get("/api/v1/financial-overview")
                .header("Authorization", "Bearer " + tokenFor(stranger))
                .header("X-Family-ID", familyId.toString()))
            .andExpect(status().isForbidden());
    }

    @Test
    void overviewNotFoundWhenMissing() throws Exception {
        User user = createUser("user2@example.com", "User", "Two");

        mockMvc.perform(get("/api/v1/financial-overview")
                .header("Authorization", "Bearer " + tokenFor(user)))
            .andExpect(status().isNotFound());
    }

    @Test
    void summaryCalculationsAreCorrect() throws Exception {
        User user = createUser("user3@example.com", "User", "Three");
        createOverviewForUser(user);

        mockMvc.perform(get("/api/v1/financial-overview/summary")
                .header("Authorization", "Bearer " + tokenFor(user)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalIncome").value(5000.00))
            .andExpect(jsonPath("$.totalExpenses").value(1500.00))
            .andExpect(jsonPath("$.totalSavings").value(800.00))
            .andExpect(jsonPath("$.availableCapacity").value(3500.00));
    }

    @Test
    void allocationsAreReturned() throws Exception {
        User user = createUser("user4@example.com", "User", "Four");
        FinancialOverview overview = createOverviewForUser(user);

        FinancialAllocation allocation = new FinancialAllocation();
        allocation.setOverview(overview);
        allocation.setCategoryName("Housing");
        allocation.setAllocatedAmount(new BigDecimal("1200.00"));
        allocation.setActualAmount(new BigDecimal("1150.00"));
        allocation.setUtilizationPercentage(new BigDecimal("95.83"));
        allocation.setCurrency("USD");
        financialAllocationRepository.save(allocation);

        mockMvc.perform(get("/api/v1/financial-overview/allocations")
                .header("Authorization", "Bearer " + tokenFor(user)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.allocations[0].category").value("Housing"))
            .andExpect(jsonPath("$.allocations[0].allocatedAmount").value(1200.00));
    }

    @Test
    void capacityEndpointIsPlanningMetric() throws Exception {
        User user = createUser("user5@example.com", "User", "Five");
        createOverviewForUser(user);

        mockMvc.perform(get("/api/v1/financial-overview/capacity")
                .header("Authorization", "Bearer " + tokenFor(user)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.availableFinancialCapacity").value(3500.00))
            .andExpect(jsonPath("$.disclaimer").exists());
    }

    @Test
    void bigDecimalPrecisionIsPreserved() throws Exception {
        User user = createUser("user6@example.com", "User", "Six");
        FinancialOverview overview = new FinancialOverview();
        overview.setUser(user);
        overview.setPlanningIncome(new BigDecimal("123456789012.34"));
        overview.setCommittedAmount(new BigDecimal("0.01"));
        overview.setActualTransactions(new BigDecimal("0.02"));
        overview.setAvailableFinancialCapacity(new BigDecimal("123456789012.31"));
        overview.setCurrency("USD");
        overview.setPeriod(YearMonth.now().toString());
        financialOverviewRepository.save(overview);

        mockMvc.perform(get("/api/v1/financial-overview")
                .header("Authorization", "Bearer " + tokenFor(user)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.planningIncome").value(123456789012.34))
            .andExpect(jsonPath("$.availableFinancialCapacity").value(123456789012.31));
    }
}
