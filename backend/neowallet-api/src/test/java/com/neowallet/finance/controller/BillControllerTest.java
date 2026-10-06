package com.neowallet.finance.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neowallet.finance.dto.CreateBillRequest;
import com.neowallet.finance.dto.MarkPaidRequest;
import com.neowallet.finance.dto.UpdateBillRequest;
import com.neowallet.finance.entity.Bill;
import com.neowallet.finance.entity.FinancialOverview;
import com.neowallet.finance.repository.BillRepository;
import com.neowallet.finance.repository.BillStatusHistoryRepository;
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
public class BillControllerTest {

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
    private BillRepository billRepository;

    @Autowired
    private BillStatusHistoryRepository billStatusHistoryRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("TRUNCATE TABLE neowallet.bill_status_history, neowallet.bills, neowallet.bill_categories, " +
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
        overview.setCommittedAmount(BigDecimal.ZERO);
        overview.setPendingPayments(BigDecimal.ZERO);
        overview.setActualTransactions(BigDecimal.ZERO);
        overview.setAvailableFinancialCapacity(income);
        return financialOverviewRepository.save(overview);
    }

    private Bill createBill(User user, UUID familyId, String name, BigDecimal amount, LocalDate dueDate) {
        Bill bill = new Bill();
        bill.setUser(user);
        if (familyId != null) {
            bill.setFamily(familyRepository.getReferenceById(familyId));
        }
        bill.setName(name);
        bill.setAmount(amount);
        bill.setCurrency("USD");
        bill.setDueDate(dueDate);
        bill.setStatus("PENDING");
        return billRepository.save(bill);
    }

    @Test
    void createBill() throws Exception {
        User user = createUser("bill1@example.com", "Bill", "One");
        createOverview(user.getUserId(), null, "2026-08", new BigDecimal("10000"));

        CreateBillRequest request = new CreateBillRequest(
            "Rent", new BigDecimal("1200.00"), "USD", LocalDate.now().plusDays(1),
            "Housing", false, null, "Landlord", null);

        mockMvc.perform(post("/api/v1/bills")
                .header("Authorization", "Bearer " + tokenFor(user))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("Rent"))
            .andExpect(jsonPath("$.amount").value(1200.00))
            .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void createAndGetFamilyBill() throws Exception {
        User owner = createUser("owner-b1@example.com", "Owner", "B1");
        UUID familyId = createFamilyFor(owner);
        createOverview(owner.getUserId(), familyId, "2026-08", new BigDecimal("10000"));

        CreateBillRequest request = new CreateBillRequest(
            "Electricity", new BigDecimal("150.00"), "USD", LocalDate.of(2026, 8, 20),
            "Utilities", false, null, "Power Co", null);

        String response = mockMvc.perform(post("/api/v1/bills")
                .header("Authorization", "Bearer " + tokenFor(owner))
                .header("X-Family-ID", familyId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andReturn().getResponse().getContentAsString();

        String billId = objectMapper.readTree(response).get("billId").asText();

        mockMvc.perform(get("/api/v1/bills")
                .header("Authorization", "Bearer " + tokenFor(owner))
                .header("X-Family-ID", familyId.toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.bills").isArray())
            .andExpect(jsonPath("$.bills[0].name").value("Electricity"));

        mockMvc.perform(get("/api/v1/bills/" + billId)
                .header("Authorization", "Bearer " + tokenFor(owner))
                .header("X-Family-ID", familyId.toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.billId").value(billId));
    }

    @Test
    void markBillPaid() throws Exception {
        User user = createUser("bill2@example.com", "Bill", "Two");
        createOverview(user.getUserId(), null, "2026-08", new BigDecimal("10000"));
        Bill bill = createBill(user, null, "Internet", new BigDecimal("60.00"), LocalDate.of(2026, 8, 15));

        MarkPaidRequest request = new MarkPaidRequest(LocalDate.of(2026, 8, 10), "Credit Card", null);

        mockMvc.perform(post("/api/v1/bills/" + bill.getBillId() + "/mark-paid")
                .header("Authorization", "Bearer " + tokenFor(user))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("PAID"))
            .andExpect(jsonPath("$.paymentMethod").value("Credit Card"));
    }

    @Test
    void overdueBillShownAsOverdue() throws Exception {
        User user = createUser("bill3@example.com", "Bill", "Three");
        createOverview(user.getUserId(), null, "2026-08", new BigDecimal("10000"));
        Bill bill = createBill(user, null, "Old Bill", new BigDecimal("100.00"), LocalDate.of(2026, 8, 1));

        mockMvc.perform(get("/api/v1/bills/" + bill.getBillId())
                .header("Authorization", "Bearer " + tokenFor(user)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("OVERDUE"));
    }

    @Test
    void updateBill() throws Exception {
        User user = createUser("bill4@example.com", "Bill", "Four");
        createOverview(user.getUserId(), null, "2026-08", new BigDecimal("10000"));
        Bill bill = createBill(user, null, "Phone", new BigDecimal("50.00"), LocalDate.of(2026, 8, 30));

        UpdateBillRequest request = new UpdateBillRequest(
            "Phone Updated", new BigDecimal("55.00"), null, null, null, null, null, null, null);

        mockMvc.perform(put("/api/v1/bills/" + bill.getBillId())
                .header("Authorization", "Bearer " + tokenFor(user))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Phone Updated"))
            .andExpect(jsonPath("$.amount").value(55.00));
    }

    @Test
    void deleteBillSoftDeletes() throws Exception {
        User user = createUser("bill5@example.com", "Bill", "Five");
        createOverview(user.getUserId(), null, "2026-08", new BigDecimal("10000"));
        Bill bill = createBill(user, null, "Cancel Me", new BigDecimal("20.00"), LocalDate.of(2026, 8, 30));

        mockMvc.perform(delete("/api/v1/bills/" + bill.getBillId())
                .header("Authorization", "Bearer " + tokenFor(user)))
            .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/bills/" + bill.getBillId())
                .header("Authorization", "Bearer " + tokenFor(user)))
            .andExpect(status().isNotFound());
    }

    @Test
    void memberCannotMarkPaid() throws Exception {
        User owner = createUser("owner-b2@example.com", "Owner", "B2");
        UUID familyId = createFamilyFor(owner);
        User member = createUser("member-b2@example.com", "Member", "B2");
        addFamilyMember(familyId, member, "MEMBER");
        createOverview(owner.getUserId(), familyId, "2026-08", new BigDecimal("10000"));

        Bill bill = createBill(owner, familyId, "Family Bill", new BigDecimal("100.00"), LocalDate.of(2026, 8, 30));

        mockMvc.perform(post("/api/v1/bills/" + bill.getBillId() + "/mark-paid")
                .header("Authorization", "Bearer " + tokenFor(member))
                .header("X-Family-ID", familyId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new MarkPaidRequest(LocalDate.now(), "Cash", null))))
            .andExpect(status().isForbidden());
    }

    @Test
    void restrictedCannotCreateBill() throws Exception {
        User owner = createUser("owner-b3@example.com", "Owner", "B3");
        UUID familyId = createFamilyFor(owner);
        User restricted = createUser("restricted-b3@example.com", "Restricted", "B3");
        addFamilyMember(familyId, restricted, "RESTRICTED");
        createOverview(owner.getUserId(), familyId, "2026-08", new BigDecimal("10000"));

        CreateBillRequest request = new CreateBillRequest(
            "New Bill", new BigDecimal("50.00"), "USD", LocalDate.of(2026, 8, 30),
            "Other", false, null, null, null);

        mockMvc.perform(post("/api/v1/bills")
                .header("Authorization", "Bearer " + tokenFor(restricted))
                .header("X-Family-ID", familyId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isForbidden());
    }

    @Test
    void crossFamilyAccessDenied() throws Exception {
        User owner = createUser("owner-b4@example.com", "Owner", "B4");
        UUID familyId = createFamilyFor(owner);
        createOverview(owner.getUserId(), familyId, "2026-08", new BigDecimal("10000"));
        Bill bill = createBill(owner, familyId, "Family Bill", new BigDecimal("100.00"), LocalDate.of(2026, 8, 30));

        User other = createUser("other-b4@example.com", "Other", "B4");

        mockMvc.perform(get("/api/v1/bills/" + bill.getBillId())
                .header("Authorization", "Bearer " + tokenFor(other))
                .header("X-Family-ID", familyId.toString()))
            .andExpect(status().isForbidden());
    }

    @Test
    void invalidAmountRejected() throws Exception {
        User user = createUser("bill6@example.com", "Bill", "Six");
        createOverview(user.getUserId(), null, "2026-08", new BigDecimal("10000"));

        CreateBillRequest request = new CreateBillRequest(
            "Bad Bill", new BigDecimal("-10.00"), "USD", LocalDate.of(2026, 8, 30),
            "Other", false, null, null, null);

        mockMvc.perform(post("/api/v1/bills")
                .header("Authorization", "Bearer " + tokenFor(user))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void invalidDateRejected() throws Exception {
        User user = createUser("bill7@example.com", "Bill", "Seven");
        createOverview(user.getUserId(), null, "2026-08", new BigDecimal("10000"));

        CreateBillRequest request = new CreateBillRequest(
            "Bad Bill", new BigDecimal("10.00"), "USD", null,
            "Other", false, null, null, null);

        mockMvc.perform(post("/api/v1/bills")
                .header("Authorization", "Bearer " + tokenFor(user))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isUnprocessableEntity());
    }
}
