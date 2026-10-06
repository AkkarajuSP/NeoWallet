package com.neowallet.finance.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neowallet.finance.dto.CreateTransactionRequest;
import com.neowallet.finance.dto.UpdateTransactionRequest;
import com.neowallet.finance.entity.Transaction;
import com.neowallet.finance.repository.TransactionRepository;
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
public class TransactionControllerTest {

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
    private TransactionRepository transactionRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("TRUNCATE TABLE neowallet.transactions, neowallet.family_invitations, neowallet.family_members, neowallet.families, neowallet.user_preferences, neowallet.user_profiles, neowallet.users CASCADE");
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

    private CreateTransactionRequest request(String type, String category, BigDecimal amount, String date) {
        return new CreateTransactionRequest(type, category, amount, "USD", "Test", date, null, null, null);
    }

    @Test
    void createTransaction() throws Exception {
        User user = createUser("user1@example.com", "User", "One");

        mockMvc.perform(post("/api/v1/transactions")
                .header("Authorization", "Bearer " + tokenFor(user))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request("EXPENSE", "Groceries", new BigDecimal("50.00"), "2026-08-18"))))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.type").value("EXPENSE"))
            .andExpect(jsonPath("$.category").value("Groceries"))
            .andExpect(jsonPath("$.amount").value(50.00))
            .andExpect(jsonPath("$.status").value("PLANNED"));
    }

    @Test
    void createTransactionWithFamily() throws Exception {
        User owner = createUser("owner@example.com", "Owner", "One");
        UUID familyId = createFamilyFor(owner);

        mockMvc.perform(post("/api/v1/transactions")
                .header("Authorization", "Bearer " + tokenFor(owner))
                .header("X-Family-ID", familyId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request("INCOME", "Salary", new BigDecimal("2500.00"), "2026-08-01"))))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.familyId").value(familyId.toString()));
    }

    @Test
    void nonMemberCannotCreateInFamily() throws Exception {
        User owner = createUser("owner2@example.com", "Owner", "Two");
        User stranger = createUser("stranger@example.com", "Stranger", "One");
        UUID familyId = createFamilyFor(owner);

        mockMvc.perform(post("/api/v1/transactions")
                .header("Authorization", "Bearer " + tokenFor(stranger))
                .header("X-Family-ID", familyId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request("EXPENSE", "Groceries", new BigDecimal("25.00"), "2026-08-18"))))
            .andExpect(status().isForbidden());
    }

    @Test
    void getTransaction() throws Exception {
        User user = createUser("user2@example.com", "User", "Two");
        Transaction tx = new Transaction();
        tx.setUser(user);
        tx.setType("EXPENSE");
        tx.setCategoryName("Transportation");
        tx.setAmount(new BigDecimal("12.50"));
        tx.setCurrency("USD");
        tx.setTransactionDate(LocalDate.now());
        tx.setStatus("PLANNED");
        tx.setSource("MANUAL");
        tx = transactionRepository.save(tx);

        mockMvc.perform(get("/api/v1/transactions/" + tx.getTransactionId())
                .header("Authorization", "Bearer " + tokenFor(user)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.transactionId").value(tx.getTransactionId().toString()));
    }

    @Test
    void listAndFilterTransactions() throws Exception {
        User user = createUser("user3@example.com", "User", "Three");
        for (int i = 0; i < 3; i++) {
            Transaction tx = new Transaction();
            tx.setUser(user);
            tx.setType("EXPENSE");
            tx.setCategoryName("Groceries");
            tx.setAmount(new BigDecimal("10.00").multiply(BigDecimal.valueOf(i + 1)));
            tx.setCurrency("USD");
            tx.setTransactionDate(LocalDate.now().minusDays(i));
            tx.setStatus("PLANNED");
            tx.setSource("MANUAL");
            transactionRepository.save(tx);
        }

        mockMvc.perform(get("/api/v1/transactions")
                .header("Authorization", "Bearer " + tokenFor(user))
                .param("filterType", "EXPENSE"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.transactions").isArray())
            .andExpect(jsonPath("$.transactions.length()").value(3))
            .andExpect(jsonPath("$.pagination.total").value(3));
    }

    @Test
    void updateTransaction() throws Exception {
        User user = createUser("user4@example.com", "User", "Four");
        Transaction tx = new Transaction();
        tx.setUser(user);
        tx.setType("EXPENSE");
        tx.setCategoryName("Entertainment");
        tx.setAmount(new BigDecimal("75.00"));
        tx.setCurrency("USD");
        tx.setTransactionDate(LocalDate.now());
        tx.setStatus("PLANNED");
        tx.setSource("MANUAL");
        tx = transactionRepository.save(tx);

        UpdateTransactionRequest update = new UpdateTransactionRequest(
            "Entertainment", new BigDecimal("80.00"), "Movie", null, null, null);

        mockMvc.perform(put("/api/v1/transactions/" + tx.getTransactionId())
                .header("Authorization", "Bearer " + tokenFor(user))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(update)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.amount").value(80.00))
            .andExpect(jsonPath("$.description").value("Movie"));
    }

    @Test
    void cannotUpdateCompletedTransaction() throws Exception {
        User user = createUser("user5@example.com", "User", "Five");
        Transaction tx = new Transaction();
        tx.setUser(user);
        tx.setType("EXPENSE");
        tx.setCategoryName("Housing");
        tx.setAmount(new BigDecimal("500.00"));
        tx.setCurrency("USD");
        tx.setTransactionDate(LocalDate.now());
        tx.setStatus("COMPLETED");
        tx.setSource("MANUAL");
        tx = transactionRepository.save(tx);

        UpdateTransactionRequest update = new UpdateTransactionRequest(null, new BigDecimal("600.00"), null, null, null, null);

        mockMvc.perform(put("/api/v1/transactions/" + tx.getTransactionId())
                .header("Authorization", "Bearer " + tokenFor(user))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(update)))
            .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void deleteTransaction() throws Exception {
        User user = createUser("user6@example.com", "User", "Six");
        Transaction tx = new Transaction();
        tx.setUser(user);
        tx.setType("EXPENSE");
        tx.setCategoryName("Other");
        tx.setAmount(new BigDecimal("5.00"));
        tx.setCurrency("USD");
        tx.setTransactionDate(LocalDate.now());
        tx.setStatus("PLANNED");
        tx.setSource("MANUAL");
        tx = transactionRepository.save(tx);

        mockMvc.perform(delete("/api/v1/transactions/" + tx.getTransactionId())
                .header("Authorization", "Bearer " + tokenFor(user)))
            .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/transactions/" + tx.getTransactionId())
                .header("Authorization", "Bearer " + tokenFor(user)))
            .andExpect(status().isNotFound());
    }

    @Test
    void cannotDeleteCompletedTransaction() throws Exception {
        User user = createUser("user7@example.com", "User", "Seven");
        Transaction tx = new Transaction();
        tx.setUser(user);
        tx.setType("EXPENSE");
        tx.setCategoryName("Other");
        tx.setAmount(new BigDecimal("5.00"));
        tx.setCurrency("USD");
        tx.setTransactionDate(LocalDate.now());
        tx.setStatus("COMPLETED");
        tx.setSource("MANUAL");
        tx = transactionRepository.save(tx);

        mockMvc.perform(delete("/api/v1/transactions/" + tx.getTransactionId())
                .header("Authorization", "Bearer " + tokenFor(user)))
            .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void cannotAccessOtherUserTransaction() throws Exception {
        User userA = createUser("userA@example.com", "A", "One");
        User userB = createUser("userB@example.com", "B", "One");

        Transaction tx = new Transaction();
        tx.setUser(userA);
        tx.setType("EXPENSE");
        tx.setCategoryName("Other");
        tx.setAmount(new BigDecimal("15.00"));
        tx.setCurrency("USD");
        tx.setTransactionDate(LocalDate.now());
        tx.setStatus("PLANNED");
        tx.setSource("MANUAL");
        tx = transactionRepository.save(tx);

        mockMvc.perform(get("/api/v1/transactions/" + tx.getTransactionId())
                .header("Authorization", "Bearer " + tokenFor(userB)))
            .andExpect(status().isForbidden());
    }

    @Test
    void familyMemberCanAccessFamilyTransaction() throws Exception {
        User owner = createUser("owner3@example.com", "Owner", "Three");
        User member = createUser("member@example.com", "Member", "One");
        UUID familyId = createFamilyFor(owner);

        Family family = familyRepository.findById(familyId).orElseThrow();

        FamilyMember memberLink = new FamilyMember();
        memberLink.setFamily(family);
        memberLink.setUser(member);
        memberLink.setRole("MEMBER");
        familyMemberRepository.save(memberLink);

        Transaction tx = new Transaction();
        tx.setUser(owner);
        tx.setFamily(family);
        tx.setType("EXPENSE");
        tx.setCategoryName("Education");
        tx.setAmount(new BigDecimal("200.00"));
        tx.setCurrency("USD");
        tx.setTransactionDate(LocalDate.now());
        tx.setStatus("PLANNED");
        tx.setSource("MANUAL");
        tx = transactionRepository.save(tx);

        mockMvc.perform(get("/api/v1/transactions/" + tx.getTransactionId())
                .header("Authorization", "Bearer " + tokenFor(member)))
            .andExpect(status().isOk());
    }

    @Test
    void invalidAmountIsRejected() throws Exception {
        User user = createUser("user8@example.com", "User", "Eight");

        mockMvc.perform(post("/api/v1/transactions")
                .header("Authorization", "Bearer " + tokenFor(user))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request("EXPENSE", "Groceries", new BigDecimal("-10.00"), "2026-08-18"))))
            .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void decimalPrecisionPreserved() throws Exception {
        User user = createUser("user9@example.com", "User", "Nine");

        mockMvc.perform(post("/api/v1/transactions")
                .header("Authorization", "Bearer " + tokenFor(user))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request("INCOME", "Other", new BigDecimal("123456789012.34"), "2026-08-18"))))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.amount").value(123456789012.34));
    }
}
