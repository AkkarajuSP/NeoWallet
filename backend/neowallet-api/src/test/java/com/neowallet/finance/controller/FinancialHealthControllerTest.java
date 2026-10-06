package com.neowallet.finance.controller;

import com.neowallet.identity.entity.Family;
import com.neowallet.identity.entity.FamilyMember;
import com.neowallet.identity.entity.User;
import com.neowallet.identity.entity.UserProfile;
import com.neowallet.identity.repository.*;
import com.neowallet.identity.security.JwtService;
import com.neowallet.finance.entity.*;
import com.neowallet.finance.repository.*;
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
import java.time.YearMonth;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class FinancialHealthControllerTest {

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
    private FinancialOverviewRepository overviewRepository;

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private SavingsGoalRepository savingsGoalRepository;

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("TRUNCATE TABLE neowallet.financial_health_calculations, neowallet.financial_health_factors, neowallet.financial_health_scores, " +
            "neowallet.transactions, neowallet.budget_categories, neowallet.budgets, neowallet.savings_goals, " +
            "neowallet.bills, neowallet.bill_status_history, neowallet.bill_categories, " +
            "neowallet.financial_overviews, neowallet.family_invitations, neowallet.family_members, neowallet.families, " +
            "neowallet.user_preferences, neowallet.user_profiles, neowallet.users CASCADE");
    }

    private User createUser(String email) {
        User user = new User();
        user.setEmail(email);
        user.setPasswordHash("$2a$10$hashed");
        user.setFirstName("Test");
        user.setLastName("User");
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

    private FinancialOverview createOverview(User user, BigDecimal income, BigDecimal essential, BigDecimal savings, BigDecimal emergency) {
        FinancialOverview overview = new FinancialOverview();
        overview.setUser(user);
        overview.setPlanningIncome(income);
        overview.setEssentialAllocation(essential);
        overview.setSavingsAllocation(savings);
        overview.setEmergencyAllocation(emergency);
        overview.setCurrency("USD");
        overview.setPeriod(YearMonth.now().toString());
        overview.setAvailableFinancialCapacity(income);
        return overviewRepository.save(overview);
    }

    private Budget createBudget(User user, String period, String category, BigDecimal limit, BigDecimal spent) {
        Budget budget = new Budget();
        budget.setUser(user);
        budget.setName("Monthly");
        budget.setPeriod(period);
        budget.setTotalLimit(limit);
        budget.setTotalSpent(spent);
        budget.setCurrency("USD");
        budget = budgetRepository.save(budget);

        BudgetCategory bc = new BudgetCategory();
        bc.setBudget(budget);
        bc.setCategoryName(category);
        bc.setLimitAmount(limit);
        bc.setSpentAmount(spent);
        budget.getCategories().add(bc);
        budgetRepository.save(budget);
        return budget;
    }

    private Transaction createTransaction(User user, LocalDate date, BigDecimal amount, String category, String type) {
        Transaction t = new Transaction();
        t.setUser(user);
        t.setType(type);
        t.setCategoryName(category);
        t.setAmount(amount);
        t.setCurrency("USD");
        t.setTransactionDate(date);
        t.setStatus("COMPLETED");
        return transactionRepository.save(t);
    }

    private SavingsGoal createSavingsGoal(User user, BigDecimal current, BigDecimal target, LocalDate targetDate) {
        SavingsGoal g = new SavingsGoal();
        g.setUser(user);
        g.setName("Goal");
        g.setCurrentAmount(current);
        g.setTargetAmount(target);
        g.setTargetDate(targetDate);
        g.setCurrency("USD");
        g.setPriority("MEDIUM");
        g.setStatus("ACTIVE");
        return savingsGoalRepository.save(g);
    }

    private Bill createBill(User user, LocalDate dueDate, LocalDate paidDate, String status) {
        Bill b = new Bill();
        b.setUser(user);
        b.setName("Bill");
        b.setAmount(new BigDecimal("100"));
        b.setCurrency("USD");
        b.setDueDate(dueDate);
        b.setPaidDate(paidDate);
        b.setStatus(status);
        return billRepository.save(b);
    }

    @Test
    void getFinancialHealth_newUser() throws Exception {
        User user = createUser("fh1@example.com");
        createOverview(user, new BigDecimal("80000"), new BigDecimal("50000"), new BigDecimal("5000"), new BigDecimal("2000"));

        mockMvc.perform(get("/api/v1/financial-health")
                .header("Authorization", "Bearer " + tokenFor(user))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.overallScore").isNumber())
            .andExpect(jsonPath("$.scoreLabel").exists())
            .andExpect(jsonPath("$.confidence").exists())
            .andExpect(jsonPath("$.disclaimer").exists());
    }

    @Test
    void getFinancialHealth_establishedUser() throws Exception {
        User user = createUser("fh2@example.com");
        createOverview(user, new BigDecimal("80000"), new BigDecimal("50000"), new BigDecimal("10000"), new BigDecimal("5000"));
        YearMonth current = YearMonth.now();
        createBudget(user, current.toString(), "Food", new BigDecimal("100"), new BigDecimal("80"));
        createTransaction(user, current.minusMonths(2).atDay(1), new BigDecimal("5000"), "Food", "EXPENSE");
        createTransaction(user, current.minusMonths(1).atDay(1), new BigDecimal("5000"), "Food", "EXPENSE");
        createTransaction(user, current.atDay(1), new BigDecimal("5250"), "Food", "EXPENSE");
        createSavingsGoal(user, new BigDecimal("10000"), new BigDecimal("20000"), LocalDate.now().plusMonths(3));
        createBill(user, current.minusMonths(1).atDay(15), current.minusMonths(1).atDay(15), "PAID");

        mockMvc.perform(get("/api/v1/financial-health")
                .header("Authorization", "Bearer " + tokenFor(user))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.overallScore").isNumber())
            .andExpect(jsonPath("$.scoreLabel").exists());
    }

    @Test
    void getFactors() throws Exception {
        User user = createUser("fh3@example.com");
        createOverview(user, new BigDecimal("80000"), new BigDecimal("50000"), new BigDecimal("5000"), new BigDecimal("2000"));

        mockMvc.perform(get("/api/v1/financial-health/factors")
                .header("Authorization", "Bearer " + tokenFor(user))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.factorScores").isMap());
    }

    @Test
    void getHistory() throws Exception {
        User user = createUser("fh4@example.com");
        createOverview(user, new BigDecimal("80000"), new BigDecimal("50000"), new BigDecimal("5000"), new BigDecimal("2000"));

        mockMvc.perform(get("/api/v1/financial-health/history")
                .header("Authorization", "Bearer " + tokenFor(user))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.history").isArray());
    }

    @Test
    void getExplanation() throws Exception {
        User user = createUser("fh5@example.com");
        createOverview(user, new BigDecimal("80000"), new BigDecimal("50000"), new BigDecimal("5000"), new BigDecimal("2000"));

        mockMvc.perform(get("/api/v1/financial-health/explanation")
                .header("Authorization", "Bearer " + tokenFor(user))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.explanation").exists())
            .andExpect(jsonPath("$.recommendedActions").isArray());
    }

    @Test
    void crossFamilyAccessDenied() throws Exception {
        User owner = createUser("fh6-owner@example.com");
        Family family = new Family();
        family.setName("Test");
        family.setCurrency("USD");
        family.setOwner(owner);
        family.setMemberCount(1);
        family = familyRepository.save(family);

        FamilyMember member = new FamilyMember();
        member.setFamily(family);
        member.setUser(owner);
        member.setRole("OWNER");
        familyMemberRepository.save(member);

        User other = createUser("fh6-other@example.com");

        mockMvc.perform(get("/api/v1/financial-health")
                .header("Authorization", "Bearer " + tokenFor(other))
                .header("X-Family-ID", family.getFamilyId().toString())
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());
    }

    @Test
    void noIncome_returns422() throws Exception {
        User user = createUser("fh7@example.com");
        createOverview(user, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);

        mockMvc.perform(get("/api/v1/financial-health")
                .header("Authorization", "Bearer " + tokenFor(user))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnprocessableEntity());
    }
}
