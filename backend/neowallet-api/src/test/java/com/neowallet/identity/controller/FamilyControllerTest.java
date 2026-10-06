package com.neowallet.identity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neowallet.identity.dto.*;
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

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class FamilyControllerTest {

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
    private FamilyInvitationRepository familyInvitationRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("TRUNCATE TABLE neowallet.audit_logs, neowallet.family_invitations, neowallet.family_members, neowallet.families, neowallet.user_preferences, neowallet.user_profiles, neowallet.users CASCADE");
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

    private String tokenForEmail(UUID familyId, String email) {
        return familyInvitationRepository.findAll().stream()
            .filter(i -> i.getFamily().getFamilyId().equals(familyId) && i.getEmail().equalsIgnoreCase(email))
            .findFirst()
            .orElseThrow()
            .getToken();
    }

    private UUID createFamilyFor(User owner) throws Exception {
        String token = tokenFor(owner);
        var result = mockMvc.perform(post("/api/v1/families")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new CreateFamilyRequest("Smiths", "USD"))))
            .andExpect(status().isCreated())
            .andReturn();
        return UUID.fromString(objectMapper.readTree(result.getResponse().getContentAsString()).get("familyId").asText());
    }

    @Test
    void createFamily() throws Exception {
        User owner = createUser("owner1@example.com", "Owner", "One");
        mockMvc.perform(post("/api/v1/families")
                .header("Authorization", "Bearer " + tokenFor(owner))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new CreateFamilyRequest("Smiths", "USD"))))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("Smiths"))
            .andExpect(jsonPath("$.ownerId").value(owner.getUserId().toString()))
            .andExpect(jsonPath("$.memberCount").value(1));
    }

    @Test
    void cannotCreateFamilyIfAlreadyInOne() throws Exception {
        User owner = createUser("owner2@example.com", "Owner", "Two");
        createFamilyFor(owner);
        mockMvc.perform(post("/api/v1/families")
                .header("Authorization", "Bearer " + tokenFor(owner))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new CreateFamilyRequest("New", "USD"))))
            .andExpect(status().isConflict());
    }

    @Test
    void getFamilyAsMember() throws Exception {
        User owner = createUser("owner3@example.com", "Owner", "Three");
        UUID familyId = createFamilyFor(owner);
        mockMvc.perform(get("/api/v1/families/" + familyId)
                .header("Authorization", "Bearer " + tokenFor(owner)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.familyId").value(familyId.toString()));
    }

    @Test
    void nonMemberCannotGetFamily() throws Exception {
        User owner = createUser("owner4@example.com", "Owner", "Four");
        User stranger = createUser("stranger1@example.com", "Stranger", "One");
        UUID familyId = createFamilyFor(owner);
        mockMvc.perform(get("/api/v1/families/" + familyId)
                .header("Authorization", "Bearer " + tokenFor(stranger)))
            .andExpect(status().isForbidden());
    }

    @Test
    void memberCannotUpdateFamily() throws Exception {
        User owner = createUser("owner5@example.com", "Owner", "Five");
        UUID familyId = createFamilyFor(owner);
        User member = createUser("member1@example.com", "Member", "One");

        String token = tokenFor(member);
        mockMvc.perform(post("/api/v1/families/" + familyId + "/invitations")
                .header("Authorization", "Bearer " + tokenFor(owner))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new FamilyInvitationRequest(member.getEmail(), "MEMBER"))));

        mockMvc.perform(post("/api/v1/family-invitations/" + familyInvitationRepository.findAll().get(0).getToken() + "/accept")
                .header("Authorization", "Bearer " + token));

        mockMvc.perform(put("/api/v1/families/" + familyId)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new UpdateFamilyRequest("NewName", null))))
            .andExpect(status().isForbidden());
    }

    @Test
    void ownerCanUpdateMemberRole() throws Exception {
        User owner = createUser("owner6@example.com", "Owner", "Six");
        User member = createUser("member2@example.com", "Member", "Two");
        UUID familyId = createFamilyFor(owner);

        mockMvc.perform(post("/api/v1/families/" + familyId + "/invitations")
                .header("Authorization", "Bearer " + tokenFor(owner))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new FamilyInvitationRequest(member.getEmail(), "MEMBER"))));

        String token = tokenFor(member);
        mockMvc.perform(post("/api/v1/family-invitations/" + familyInvitationRepository.findAll().get(0).getToken() + "/accept")
                .header("Authorization", "Bearer " + token));

        UUID memberId = familyMemberRepository.findByUser_UserIdAndLeftAtIsNull(member.getUserId()).get().getMemberId();

        mockMvc.perform(put("/api/v1/families/" + familyId + "/members/" + memberId)
                .header("Authorization", "Bearer " + tokenFor(owner))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new UpdateFamilyMemberRequest("RESTRICTED"))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.role").value("RESTRICTED"));
    }

    @Test
    void cannotChangeOwnerRole() throws Exception {
        User owner = createUser("owner7@example.com", "Owner", "Seven");
        UUID familyId = createFamilyFor(owner);
        UUID memberId = familyMemberRepository.findByUser_UserIdAndLeftAtIsNull(owner.getUserId()).get().getMemberId();

        mockMvc.perform(put("/api/v1/families/" + familyId + "/members/" + memberId)
                .header("Authorization", "Bearer " + tokenFor(owner))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new UpdateFamilyMemberRequest("MEMBER"))))
            .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void cannotRemoveOwner() throws Exception {
        User owner = createUser("owner8@example.com", "Owner", "Eight");
        UUID familyId = createFamilyFor(owner);
        UUID memberId = familyMemberRepository.findByUser_UserIdAndLeftAtIsNull(owner.getUserId()).get().getMemberId();

        mockMvc.perform(delete("/api/v1/families/" + familyId + "/members/" + memberId)
                .header("Authorization", "Bearer " + tokenFor(owner)))
            .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void memberCannotRemoveOtherMember() throws Exception {
        User owner = createUser("owner9@example.com", "Owner", "Nine");
        User member1 = createUser("member3@example.com", "Member", "Three");
        User member2 = createUser("member4@example.com", "Member", "Four");
        UUID familyId = createFamilyFor(owner);

        for (User m : new User[]{member1, member2}) {
            mockMvc.perform(post("/api/v1/families/" + familyId + "/invitations")
                    .header("Authorization", "Bearer " + tokenFor(owner))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(new FamilyInvitationRequest(m.getEmail(), "MEMBER"))));
        }

        mockMvc.perform(post("/api/v1/family-invitations/" + tokenForEmail(familyId, member1.getEmail()) + "/accept")
                .header("Authorization", "Bearer " + tokenFor(member1)));
        mockMvc.perform(post("/api/v1/family-invitations/" + tokenForEmail(familyId, member2.getEmail()) + "/accept")
                .header("Authorization", "Bearer " + tokenFor(member2)));

        UUID member2Id = familyMemberRepository.findByUser_UserIdAndLeftAtIsNull(member2.getUserId()).get().getMemberId();

        mockMvc.perform(delete("/api/v1/families/" + familyId + "/members/" + member2Id)
                .header("Authorization", "Bearer " + tokenFor(member1)))
            .andExpect(status().isForbidden());
    }

    @Test
    void inviteAndAccept() throws Exception {
        User owner = createUser("owner10@example.com", "Owner", "Ten");
        User invitee = createUser("invitee1@example.com", "Invited", "One");
        UUID familyId = createFamilyFor(owner);

        var inviteResult = mockMvc.perform(post("/api/v1/families/" + familyId + "/invitations")
                .header("Authorization", "Bearer " + tokenFor(owner))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new FamilyInvitationRequest(invitee.getEmail(), "MEMBER"))))
            .andExpect(status().isCreated())
            .andReturn();

        String token = objectMapper.readTree(inviteResult.getResponse().getContentAsString()).get("token").asText();

        mockMvc.perform(post("/api/v1/family-invitations/" + token + "/accept")
                .header("Authorization", "Bearer " + tokenFor(invitee)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.familyId").value(familyId.toString()))
            .andExpect(jsonPath("$.role").value("MEMBER"));
    }

    @Test
    void cannotAcceptForWrongUser() throws Exception {
        User owner = createUser("owner11@example.com", "Owner", "Eleven");
        User invitee = createUser("invitee2@example.com", "Invited", "Two");
        User attacker = createUser("attacker@example.com", "Attacker", "One");
        UUID familyId = createFamilyFor(owner);

        mockMvc.perform(post("/api/v1/families/" + familyId + "/invitations")
                .header("Authorization", "Bearer " + tokenFor(owner))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new FamilyInvitationRequest(invitee.getEmail(), "MEMBER"))));

        String token = familyInvitationRepository.findAll().get(0).getToken();

        mockMvc.perform(post("/api/v1/family-invitations/" + token + "/accept")
                .header("Authorization", "Bearer " + tokenFor(attacker)))
            .andExpect(status().isForbidden());
    }

    @Test
    void cannotReplayAcceptedInvitation() throws Exception {
        User owner = createUser("owner12@example.com", "Owner", "Twelve");
        User invitee = createUser("invitee3@example.com", "Invited", "Three");
        UUID familyId = createFamilyFor(owner);

        mockMvc.perform(post("/api/v1/families/" + familyId + "/invitations")
                .header("Authorization", "Bearer " + tokenFor(owner))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new FamilyInvitationRequest(invitee.getEmail(), "MEMBER"))));

        String token = familyInvitationRepository.findAll().get(0).getToken();

        mockMvc.perform(post("/api/v1/family-invitations/" + token + "/accept")
                .header("Authorization", "Bearer " + tokenFor(invitee)))
            .andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/family-invitations/" + token + "/accept")
                .header("Authorization", "Bearer " + tokenFor(invitee)))
            .andExpect(status().isNotFound());
    }

    @Test
    void deleteFamily() throws Exception {
        User owner = createUser("owner13@example.com", "Owner", "Thirteen");
        UUID familyId = createFamilyFor(owner);

        mockMvc.perform(delete("/api/v1/families/" + familyId)
                .header("Authorization", "Bearer " + tokenFor(owner))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new DeleteFamilyRequest("DELETE"))))
            .andExpect(status().isNoContent());
    }

    @Test
    void cannotDeleteFamilyWithOtherMembers() throws Exception {
        User owner = createUser("owner14@example.com", "Owner", "Fourteen");
        User member = createUser("member5@example.com", "Member", "Five");
        UUID familyId = createFamilyFor(owner);

        mockMvc.perform(post("/api/v1/families/" + familyId + "/invitations")
                .header("Authorization", "Bearer " + tokenFor(owner))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new FamilyInvitationRequest(member.getEmail(), "MEMBER"))));

        mockMvc.perform(post("/api/v1/family-invitations/" + familyInvitationRepository.findAll().get(0).getToken() + "/accept")
                .header("Authorization", "Bearer " + tokenFor(member)));

        mockMvc.perform(delete("/api/v1/families/" + familyId)
                .header("Authorization", "Bearer " + tokenFor(owner))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new DeleteFamilyRequest("DELETE"))))
            .andExpect(status().isForbidden());
    }

    @Test
    void cannotInviteWithAdminRole() throws Exception {
        User owner = createUser("owner15@example.com", "Owner", "Fifteen");
        User invitee = createUser("invitee4@example.com", "Invited", "Four");
        UUID familyId = createFamilyFor(owner);

        mockMvc.perform(post("/api/v1/families/" + familyId + "/invitations")
                .header("Authorization", "Bearer " + tokenFor(owner))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new FamilyInvitationRequest(invitee.getEmail(), "ADMIN"))))
            .andExpect(status().isUnprocessableEntity());
    }

}
