package com.cinemabooking;

import com.cinemabooking.dto.BookingRequest;
import com.cinemabooking.dto.ScreenRequest;
import com.cinemabooking.dto.ScreenResponse;
import com.cinemabooking.entity.*;
import com.cinemabooking.repository.*;
import com.cinemabooking.service.BookingService;
import com.cinemabooking.service.TheatreService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class BookingIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private TheatreRepository theatreRepository;

    @Autowired
    private ScreenRepository screenRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private ShowRepository showRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private BookingSeatRepository bookingSeatRepository;

    @Autowired
    private TheatreService theatreService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private jakarta.persistence.EntityManager entityManager;

    private User customer1;
    private User customer2;
    private User adminUser;
    private Movie testMovie;
    private Theatre testTheatre;
    private Screen testScreen;
    private Show testShow;
    private List<Seat> testSeats;

    @BeforeEach
    void setUp() {
        bookingSeatRepository.deleteAll();
        bookingRepository.deleteAll();
        showRepository.deleteAll();
        seatRepository.deleteAll();
        screenRepository.deleteAll();
        theatreRepository.deleteAll();
        movieRepository.deleteAll();
        userRepository.deleteAll();

        // 1. Users
        customer1 = userRepository.save(new User("Customer One", "cust1@example.com", "pass123", "1111111111", Role.ROLE_CUSTOMER));
        customer2 = userRepository.save(new User("Customer Two", "cust2@example.com", "pass123", "2222222222", Role.ROLE_CUSTOMER));
        adminUser = userRepository.save(new User("Admin User", "admin@example.com", "pass123", "3333333333", Role.ROLE_ADMIN));

        // 2. Movie
        testMovie = new Movie();
        testMovie.setTitle("Inception");
        testMovie.setGenre("Sci-Fi");
        testMovie.setLanguage("English");
        testMovie.setDurationMinutes(148);
        testMovie.setActive(true);
        testMovie = movieRepository.save(testMovie);

        // 3. Theatre & Screen
        testTheatre = new Theatre();
        testTheatre.setName("Cineplex Test");
        testTheatre.setCity("Metropolis");
        testTheatre.setAddress("Main St");
        testTheatre.setTotalScreens(1);
        testTheatre.setActive(true);
        testTheatre = theatreRepository.save(testTheatre);

        ScreenRequest screenReq = new ScreenRequest();
        screenReq.setTheatreId(testTheatre.getId());
        screenReq.setName("Screen 1");
        screenReq.setScreenType("Standard 2D");
        screenReq.setRowsCount(4);
        screenReq.setColsCount(5); // 20 seats
        ScreenResponse screenResp = theatreService.createScreenWithSeats(screenReq);
        testScreen = screenRepository.findById(screenResp.getId()).orElseThrow();

        // 4. Show
        testShow = new Show();
        testShow.setMovie(testMovie);
        testShow.setScreen(testScreen);
        testShow.setShowDate(LocalDate.now().plusDays(1));
        testShow.setStartTime(LocalTime.of(19, 30));
        testShow.setEndTime(LocalTime.of(22, 0));
        testShow.setBasePrice(new BigDecimal("10.00"));
        testShow.setActive(true);
        testShow = showRepository.save(testShow);

        testSeats = seatRepository.findByScreenIdOrderByRowNameAscSeatNumberAsc(testScreen.getId());
    }

    @Test
    @DisplayName("Should retrieve show seats layout and availability")
    void testGetShowSeats() throws Exception {
        mockMvc.perform(get("/api/shows/" + testShow.getId() + "/seats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.showId", is(testShow.getId().intValue())))
                .andExpect(jsonPath("$.data.seats", hasSize(20)))
                .andExpect(jsonPath("$.data.seats[0].booked", is(false)));
    }

    @Test
    @DisplayName("Should successfully book seats with authoritative backend pricing")
    @WithMockUser(username = "cust1@example.com", roles = {"CUSTOMER"})
    void testSuccessfulBooking() throws Exception {
        Seat seat1 = testSeats.get(0); // Silver (1.0x -> 10.00)
        Seat seat2 = testSeats.get(1); // Silver (1.0x -> 10.00)

        BookingRequest req = new BookingRequest(testShow.getId(), List.of(seat1.getId(), seat2.getId()));

        MvcResult result = mockMvc.perform(post("/api/bookings")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.bookingReference", notNullValue()))
                .andExpect(jsonPath("$.data.seatCount", is(2)))
                .andExpect(jsonPath("$.data.totalAmount", is(20.00)))
                .andExpect(jsonPath("$.data.status", is("CONFIRMED")))
                .andExpect(jsonPath("$.data.paymentStatus", is("PAID")))
                .andReturn();

        // Verify the seats are now marked as booked in the seat layout API
        mockMvc.perform(get("/api/shows/" + testShow.getId() + "/seats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.seats[0].booked", is(true)))
                .andExpect(jsonPath("$.data.seats[1].booked", is(true)))
                .andExpect(jsonPath("$.data.seats[2].booked", is(false)));

        // Verify customer can view their bookings (testing lazy loading of bookingSeats)
        mockMvc.perform(get("/api/bookings/my"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data[0].seatNumbers", org.hamcrest.Matchers.hasSize(2)));
    }

    @Test
    @DisplayName("Prevent duplicate seat reservation (Conflict 409)")
    @WithMockUser(username = "cust1@example.com", roles = {"CUSTOMER"})
    void testDuplicateSeatBookingPrevention() throws Exception {
        Seat seat1 = testSeats.get(0);

        // Cust 1 books seat1
        BookingRequest req1 = new BookingRequest(testShow.getId(), List.of(seat1.getId()));
        mockMvc.perform(post("/api/bookings")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req1)))
                .andExpect(status().isCreated());

        // Cust 2 attempts to book the same seat1 -> Must be rejected with 409 Conflict
        BookingRequest req2 = new BookingRequest(testShow.getId(), List.of(seat1.getId()));
        mockMvc.perform(post("/api/bookings")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req2)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success", is(false)));
    }

    @Test
    @DisplayName("Should reject invalid booking request with empty seats")
    @WithMockUser(username = "cust1@example.com", roles = {"CUSTOMER"})
    void testInvalidBookingEmptySeats() throws Exception {
        BookingRequest req = new BookingRequest(testShow.getId(), List.of());

        mockMvc.perform(post("/api/bookings")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)));
    }

    @Test
    @DisplayName("Customer cannot access another customer's booking")
    @WithMockUser(username = "cust1@example.com", roles = {"CUSTOMER"})
    void testCustomerAccessRestriction() throws Exception {
        Seat seat = testSeats.get(5);
        BookingRequest req = new BookingRequest(testShow.getId(), List.of(seat.getId()));

        String resContent = mockMvc.perform(post("/api/bookings")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long bookingId = objectMapper.readTree(resContent).path("data").path("id").asLong();

        // Switch context to cust2
        // Cust 2 attempting to view Cust 1's booking must receive 403 Forbidden
        // We use @WithMockUser on custom request:
        mockMvc.perform(get("/api/bookings/" + bookingId)
                        .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user(
                                new com.cinemabooking.security.CustomUserDetails(customer2)
                        )))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Admin can view all bookings and dashboard stats")
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    void testAdminOperations() throws Exception {
        mockMvc.perform(get("/api/admin/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.totalMovies", is(1)))
                .andExpect(jsonPath("$.data.totalTheatres", is(1)));

        BookingRequest req = new BookingRequest(testShow.getId(), List.of(testSeats.get(0).getId()));
        bookingService.createBooking(req, customer1);

        entityManager.clear();

        mockMvc.perform(get("/api/admin/bookings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data[0].seatNumbers", org.hamcrest.Matchers.hasSize(1)));
    }
}
