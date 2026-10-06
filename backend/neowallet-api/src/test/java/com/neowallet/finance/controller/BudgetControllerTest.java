package com.neowallet.finance.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neowallet.finance.dto.CreateBudgetRequest;
import com.neowallet.finance.dto.UpdateBudgetRequest;
import com.neowallet.finance.entity.*;
import com.neowallet.finance.repository.*;
import com.neowallet.identity.entity.*;
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
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class BudgetControllerTest {

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
    private TransactionRepository transactionRepository;

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("TRUNCATE TABLE neowallet.budgets, neowallet.budget_categories, neowallet.budget_periods, neowallet.budget_recommendations, " +
            "neowallet.transactions, neowallet.financial_overviews, neowallet.family_invitations, neowallet.family_members, neowallet.families, " +
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
        overview.setSavingsAllocation(income.multiply(new BigDecimal("0.05")));
        overview.setEmergencyAllocation(income.multiply(new BigDecimal("0.05")));
        overview.setDiscretionaryPlanning(income.multiply(new BigDecimal("0.40")));
        return financialOverviewRepository.save(overview);
    }

    private Transaction createExpense(User user, UUID familyId, String category, BigDecimal amount, LocalDate date) {
        Transaction t = new Transaction();
        t.setUser(user);
        if (familyId != null) {
            t.setFamily(familyRepository.getReferenceById(familyId));
        }
        t.setType("EXPENSE");
        t.setCategoryName(category);
        t.setAmount(amount);
        t.setCurrency("USD");
        t.setTransactionDate(date);
        t.setStatus("COMPLETED");
        return transactionRepository.save(t);
    }

    @Test
    void createBudget() throws Exception {
        User user = createUser("user1@example.com", "User", "One");
        createOverview(user.getUserId(), null, "2026-08", new BigDecimal("10000"));

        CreateBudgetRequest request = new CreateBudgetRequest(
            "August Budget", "2026-08", "USD",
            List.of(new CreateBudgetRequest.CreateBudgetCategoryRequest("Groceries", new BigDecimal("200.00"), "ESSENTIAL")));

        mockMvc.perform(post("/api/v1/budgets")
                .header("Authorization", "Bearer " + tokenFor(user))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("August Budget"))
            .andExpect(jsonPath("$.totalLimit").value(200.00))
            .andExpect(jsonPath("$.categories[0].category").value("Groceries"));
    }

    @Test
    void createBudgetWithFamily() throws Exception {
        User user = createUser("user2@example.com", "User", "Two");
        UUID familyId = createFamilyFor(user);
        createOverview(user.getUserId(), familyId, "2026-08", new BigDecimal("10000"));

        CreateBudgetRequest request = new CreateBudgetRequest(
            "Family Budget", "2026-08", "USD",
            List.of(new CreateBudgetRequest.CreateBudgetCategoryRequest("Groceries", new BigDecimal("500.00"), "ESSENTIAL")));

        mockMvc.perform(post("/api/v1/budgets")
                .header("Authorization", "Bearer " + tokenFor(user))
                .header("X-Family-ID", familyId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.familyId").value(familyId.toString()));
    }

    @Test
    void listAndGetBudget() throws Exception {
        User user = createUser("user3@example.com", "User", "Three");
        createOverview(user.getUserId(), null, "2026-08", new BigDecimal("10000"));

        CreateBudgetRequest request = new CreateBudgetRequest(
            "My Budget", "2026-08", "USD",
            List.of(new CreateBudgetRequest.CreateBudgetCategoryRequest("Groceries", new BigDecimal("100.00"), "ESSENTIAL")));

        String response = mockMvc.perform(post("/api/v1/budgets")
                .header("Authorization", "Bearer " + tokenFor(user))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andReturn().getResponse().getContentAsString();

        String budgetId = objectMapper.readTree(response).get("budgetId").asText();

        mockMvc.perform(get("/api/v1/budgets")
                .header("Authorization", "Bearer " + tokenFor(user)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.budgets").isArray())
            .andExpect(jsonPath("$.budgets[0].name").value("My Budget"));

        mockMvc.perform(get("/api/v1/budgets/" + budgetId)
                .header("Authorization", "Bearer " + tokenFor(user)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.budgetId").value(budgetId));
    }

    @Test
    void utilizationAndForecast() throws Exception {
        User user = createUser("user4@example.com", "User", "Four");
        createOverview(user.getUserId(), null, "2026-08", new BigDecimal("10000"));

        Budget budget = new Budget();
        budget.setUser(user);
        budget.setName("U Budget");
        budget.setPeriod("2026-08");
        budget.setCurrency("USD");
        budget.setTotalLimit(new BigDecimal("200.00"));
        BudgetCategory bc = new BudgetCategory();
        bc.setBudget(budget);
        bc.setCategoryName("Groceries");
        bc.setLimitAmount(new BigDecimal("200.00"));
        bc.setPriority("ESSENTIAL");
        budget.setCategories(List.of(bc));
        budget = budgetRepository.save(budget);

        createExpense(user, null, "Groceries", new BigDecimal("160.00"), LocalDate.of(2026, 8, 10));
        createExpense(user, null, "Groceries", new BigDecimal("20.00"), LocalDate.of(2026, 7, 10));

        mockMvc.perform(get("/api/v1/budgets/" + budget.getBudgetId() + "/utilization")
                .header("Authorization", "Bearer " + tokenFor(user)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalSpent").value(160.00))
            .andExpect(jsonPath("$.overallStatus").value("NEAR_LIMIT"));

        mockMvc.perform(get("/api/v1/budgets/" + budget.getBudgetId() + "/forecast")
                .header("Authorization", "Bearer " + tokenFor(user)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.forecast.confidence").value("LOW"));
    }

    @Test
    void recommendationWorks() throws Exception {
        User user = createUser("user5@example.com", "User", "Five");
        createOverview(user.getUserId(), null, "2026-08", new BigDecimal("80000"));

        createExpense(user, null, "Groceries", new BigDecimal("8140.00"), LocalDate.of(2026, 4, 5));
        createExpense(user, null, "Groceries", new BigDecimal("10000.00"), LocalDate.of(2026, 5, 5));
        createExpense(user, null, "Groceries", new BigDecimal("10500.00"), LocalDate.of(2026, 6, 5));
        createExpense(user, null, "Groceries", new BigDecimal("11340.00"), LocalDate.of(2026, 7, 5));

        mockMvc.perform(get("/api/v1/budgets/recommendation")
                .header("Authorization", "Bearer " + tokenFor(user))
                .param("period", "2026-08"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.recommendedAllocation.planningIncome").value(80000.00))
            .andExpect(jsonPath("$.categories[?(@.category == 'Groceries')].recommendedLimit").value(11340.00))
            .andExpect(jsonPath("$.confidence").value("HIGH"));
    }

    @Test
    void cannotDeleteBudgetWithTransactions() throws Exception {
        User user = createUser("user6@example.com", "User", "Six");
        createOverview(user.getUserId(), null, "2026-08", new BigDecimal("10000"));

        Budget budget = new Budget();
        budget.setUser(user);
        budget.setName("D Budget");
        budget.setPeriod("2026-08");
        budget.setCurrency("USD");
        budget.setTotalLimit(new BigDecimal("500.00"));
        BudgetCategory bc = new BudgetCategory();
        bc.setBudget(budget);
        bc.setCategoryName("Groceries");
        bc.setLimitAmount(new BigDecimal("500.00"));
        bc.setPriority("ESSENTIAL");
        budget.setCategories(List.of(bc));
        budget = budgetRepository.save(budget);

        createExpense(user, null, "Groceries", new BigDecimal("50.00"), LocalDate.of(2026, 8, 1));

        mockMvc.perform(delete("/api/v1/budgets/" + budget.getBudgetId())
                .header("Authorization", "Bearer " + tokenFor(user)))
            .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void crossFamilyAccessDenied() throws Exception {
        User owner = createUser("owner@example.com", "Owner", "One");
        User other = createUser("other@example.com", "Other", "One");
        UUID familyId = createFamilyFor(owner);
        createOverview(owner.getUserId(), familyId, "2026-08", new BigDecimal("10000"));

        Budget budget = new Budget();
        Family family = new Family();
        family.setFamilyId(familyId);
        budget.setFamily(family);
        budget.setName("Family Budget");
        budget.setPeriod("2026-08");
        budget.setCurrency("USD");
        budget.setTotalLimit(new BigDecimal("100.00"));
        budget = budgetRepository.save(budget);

        mockMvc.perform(get("/api/v1/budgets/" + budget.getBudgetId())
                .header("Authorization", "Bearer " + tokenFor(other)))
            .andExpect(status().isForbidden());
    }

    @Test
    void ownerCanUpdateFamilyBudget() throws Exception {
        User owner = createUser("owner-b1@example.com", "Owner", "B1");
        UUID familyId = createFamilyFor(owner);
        createOverview(owner.getUserId(), familyId, "2026-08", new BigDecimal("10000"));

        Budget budget = new Budget();
        budget.setFamily(familyRepository.getReferenceById(familyId));
        budget.setName("Family Budget");
        budget.setPeriod("2026-08");
        budget.setCurrency("USD");
        budget.setTotalLimit(new BigDecimal("100.00"));
        budget = budgetRepository.save(budget);

        UpdateBudgetRequest request = new UpdateBudgetRequest(
            "Updated Family Budget",
            List.of(new UpdateBudgetRequest.UpdateBudgetCategoryRequest("Groceries", new BigDecimal("300.00"), "ESSENTIAL")));

        mockMvc.perform(put("/api/v1/budgets/" + budget.getBudgetId())
                .header("Authorization", "Bearer " + tokenFor(owner))
                .header("X-Family-ID", familyId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Updated Family Budget"))
            .andExpect(jsonPath("$.totalLimit").value(300.00));
    }

    @Test
    void memberCannotUpdateFamilyBudget() throws Exception {
        User owner = createUser("owner-b2@example.com", "Owner", "B2");
        User member = createUser("member-b2@example.com", "Member", "B2");
        UUID familyId = createFamilyFor(owner);
        addFamilyMember(familyId, member, "MEMBER");
        createOverview(owner.getUserId(), familyId, "2026-08", new BigDecimal("10000"));

        Budget budget = new Budget();
        budget.setFamily(familyRepository.getReferenceById(familyId));
        budget.setName("Family Budget");
        budget.setPeriod("2026-08");
        budget.setCurrency("USD");
        budget.setTotalLimit(new BigDecimal("100.00"));
        budget = budgetRepository.save(budget);

        UpdateBudgetRequest request = new UpdateBudgetRequest(
            "Updated by Member",
            List.of(new UpdateBudgetRequest.UpdateBudgetCategoryRequest("Groceries", new BigDecimal("300.00"), "ESSENTIAL")));

        mockMvc.perform(put("/api/v1/budgets/" + budget.getBudgetId())
                .header("Authorization", "Bearer " + tokenFor(member))
                .header("X-Family-ID", familyId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isForbidden());
    }

    @Test
    void restrictedCannotDeleteFamilyBudget() throws Exception {
        User owner = createUser("owner-b3@example.com", "Owner", "B3");
        User restricted = createUser("restricted-b3@example.com", "Restricted", "B3");
        UUID familyId = createFamilyFor(owner);
        addFamilyMember(familyId, restricted, "RESTRICTED");
        createOverview(owner.getUserId(), familyId, "2026-08", new BigDecimal("10000"));

        Budget budget = new Budget();
        budget.setFamily(familyRepository.getReferenceById(familyId));
        budget.setName("Family Budget");
        budget.setPeriod("2026-08");
        budget.setCurrency("USD");
        budget.setTotalLimit(new BigDecimal("100.00"));
        budget = budgetRepository.save(budget);

        mockMvc.perform(delete("/api/v1/budgets/" + budget.getBudgetId())
                .header("Authorization", "Bearer " + tokenFor(restricted))
                .header("X-Family-ID", familyId.toString()))
            .andExpect(status().isForbidden());
    }
}
