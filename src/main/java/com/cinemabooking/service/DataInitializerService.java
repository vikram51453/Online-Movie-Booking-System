package com.cinemabooking.service;

import com.cinemabooking.dto.ScreenRequest;
import com.cinemabooking.entity.*;
import com.cinemabooking.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataInitializerService implements CommandLineRunner {

    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final TheatreRepository theatreRepository;
    private final ScreenRepository screenRepository;
    private final ShowRepository showRepository;
    private final TheatreService theatreService;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.seed.enabled:true}")
    private boolean seedEnabled;

    @Value("${app.admin.email:admin@cinemabooking.com}")
    private String adminEmail;

    @Value("${app.admin.password:Admin@123456}")
    private String adminPassword;

    @Value("${app.admin.name:System Administrator}")
    private String adminName;

    @Value("${app.admin.phone:9876543210}")
    private String adminPhone;

    public DataInitializerService(UserRepository userRepository,
                                  MovieRepository movieRepository,
                                  TheatreRepository theatreRepository,
                                  ScreenRepository screenRepository,
                                  ShowRepository showRepository,
                                  TheatreService theatreService,
                                  PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.movieRepository = movieRepository;
        this.theatreRepository = theatreRepository;
        this.screenRepository = screenRepository;
        this.showRepository = showRepository;
        this.theatreService = theatreService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        initAdminUser();
        initDemoCustomer();

        if (seedEnabled && movieRepository.count() == 0) {
            initSampleData();
        } else if (seedEnabled) {
            updateMoviePostersIfDefault();
            initTamilNaduTheatresIfMissing();
        }
    }

    private void updateMoviePostersIfDefault() {
        java.util.Map<String, String> posters = new java.util.HashMap<>();
        posters.put("Inception", "/images/posters/inception.jpg");
        posters.put("Interstellar", "/images/posters/interstellar.jpg");
        posters.put("The Dark Knight", "/images/posters/the-dark-knight.jpg");
        posters.put("Dune: Part Two", "/images/posters/dune-part-two.jpg");
        posters.put("Oppenheimer", "/images/posters/oppenheimer.jpg");
        posters.put("Avatar: The Way of Water", "/images/posters/avatar-the-way-of-water.jpg");
        posters.put("Leo", "/images/posters/leo.jpg");
        posters.put("Jailer", "/images/posters/jailer.jpg");
        posters.put("Vikram", "/images/posters/vikram.jpg");
        posters.put("The Greatest of All Time (GOAT)", "/images/posters/the-greatest-of-all-time.jpg");
        posters.put("Coolie", "/images/posters/coolie.jpg");
        posters.put("Sardar 2", "/images/posters/sardar-2.jpg");
        posters.put("Jana Nayagan", "/images/posters/jana-nayagan.jpg");
        posters.put("The RajaSaab", "/images/posters/the-raja-saab.jpg");
        posters.put("They Call Him OG", "/images/posters/they-call-him-og.jpg");
        posters.put("Game Changer", "/images/posters/game-changer.jpg");
        posters.put("Ramayana: Part 1", "/images/posters/ramayana-part-1.jpg");
        posters.put("Border 2", "/images/posters/border-2.jpg");
        posters.put("Bhooth Bangla", "/images/posters/bhooth-bangla.jpg");
        posters.put("Jailer 2", "/images/posters/jailer-2.jpg");
        posters.put("Demonte Colony 3", "/images/posters/demonte-colony-3.jpg");
        posters.put("Mookuthi Amman 2", "/images/posters/mookuthi-amman-2.jpg");

        for (Movie m : movieRepository.findAll()) {
            if ("Ponniyin Selvan: Part 1".equals(m.getTitle())) {
                if (m.isActive()) {
                    m.setActive(false);
                    movieRepository.save(m);
                }
                continue;
            }
            String poster = posters.get(m.getTitle());
            if (poster != null && (m.getPosterUrl() == null || m.getPosterUrl().contains("unsplash"))) {
                m.setPosterUrl(poster);
                movieRepository.save(m);
            }
        }
    }

    private void initTamilNaduTheatresIfMissing() {
        String[][] tnTheatres = {
                // Chennai (3)
                {"SPI Sathyam Cinemas", "Chennai", "8 Thiruvika Road, Royapettah, Chennai - 600014"},
                {"PVR INOX Palazzo", "Chennai", "The Forum Vijaya Mall, Vadapalani, Chennai - 600026"},
                {"AGS Cinemas", "Chennai", "24 G.N. Chetty Road, T. Nagar, Chennai - 600017"},

                // Coimbatore (3)
                {"The Cinema (Brookefields)", "Coimbatore", "Brookefields Mall, Dr Krishnasamy Rd, Coimbatore - 641001"},
                {"KG Cinemas", "Coimbatore", "10 Race Course Road, Gopalapuram, Coimbatore - 641018"},
                {"Broadway Cinemas", "Coimbatore", "KMCH Campus, Avinashi Road, Coimbatore - 641014"},

                // Madurai (3)
                {"INOX Vishaal De Mal", "Madurai", "Vishaal De Mal, Gokhale Road, Madurai - 625002"},
                {"Thangam Grand Multiplex", "Madurai", "42 West Perumal Maistry Street, Madurai - 625001"},
                {"Gopuram Cinemas", "Madurai", "Melur Main Road, Mattuthavani, Madurai - 625107"},

                // Tiruchirappalli (3)
                {"LA Cinemas Maris Complex", "Tiruchirappalli", "1 Fort Station Road, Thillai Nagar, Tiruchirappalli - 620002"},
                {"Manghalam Multiplex", "Tiruchirappalli", "Crawford Main Road, Crawford, Tiruchirappalli - 620012"},
                {"Mega Star Cinemas", "Tiruchirappalli", "10th Cross, Thillai Nagar West, Tiruchirappalli - 620018"}
        };

        java.util.Set<String> existingNames = theatreRepository.findAll().stream()
                .map(t -> t.getName().trim().toLowerCase())
                .collect(java.util.stream.Collectors.toSet());

        for (String[] tData : tnTheatres) {
            if (!existingNames.contains(tData[0].trim().toLowerCase())) {
                Theatre t = new Theatre();
                t.setName(tData[0]);
                t.setCity(tData[1]);
                t.setAddress(tData[2]);
                t.setTotalScreens(2);
                t.setActive(true);
                Theatre savedTheatre = theatreRepository.save(t);

                ScreenRequest scr1 = new ScreenRequest();
                scr1.setTheatreId(savedTheatre.getId());
                scr1.setName("Screen 1 - 4K Dolby");
                scr1.setScreenType("Dolby Atmos 4K");
                scr1.setRowsCount(7);
                scr1.setColsCount(10);
                theatreService.createScreenWithSeats(scr1);

                ScreenRequest scr2 = new ScreenRequest();
                scr2.setTheatreId(savedTheatre.getId());
                scr2.setName("Screen 2 - Laser 3D");
                scr2.setScreenType("RGB Laser 3D");
                scr2.setRowsCount(7);
                scr2.setColsCount(10);
                theatreService.createScreenWithSeats(scr2);
            }
        }
    }

    private void initAdminUser() {
        if (!userRepository.existsByEmailIgnoreCase(adminEmail)) {
            User admin = new User();
            admin.setFullName(adminName);
            admin.setEmail(adminEmail.toLowerCase().trim());
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setPhone(adminPhone);
            admin.setRole(Role.ROLE_ADMIN);
            userRepository.save(admin);
        }
    }

    private void initDemoCustomer() {
        String demoEmail = "customer@cinemabooking.com";
        if (!userRepository.existsByEmailIgnoreCase(demoEmail)) {
            User customer = new User();
            customer.setFullName("John Doe");
            customer.setEmail(demoEmail);
            customer.setPassword(passwordEncoder.encode("Customer@123"));
            customer.setPhone("9123456780");
            customer.setRole(Role.ROLE_CUSTOMER);
            userRepository.save(customer);
        }
    }

    private void initSampleData() {
        // 1. Seed Movies with rich images
        List<Movie> movies = new ArrayList<>();

        Movie m1 = new Movie();
        m1.setTitle("Inception");
        m1.setDescription("A thief who steals corporate secrets through the use of dream-sharing technology is given the inverse task of planting an idea into the mind of a C.E.O.");
        m1.setGenre("Sci-Fi / Action");
        m1.setLanguage("English");
        m1.setDurationMinutes(148);
        m1.setReleaseDate(LocalDate.of(2010, 7, 16));
        m1.setRating(8.8);
        m1.setPosterUrl("/images/posters/inception.jpg");
        m1.setTrailerUrl("https://www.youtube.com/watch?v=YoHD9XEInc0");
        m1.setActive(true);
        movies.add(m1);

        Movie m2 = new Movie();
        m2.setTitle("Interstellar");
        m2.setDescription("When Earth becomes uninhabitable in the future, a farmer and ex-NASA pilot, Joseph Cooper, is tasked to pilot a spacecraft, along with a team of researchers, to find a new planet for humans.");
        m2.setGenre("Sci-Fi / Adventure");
        m2.setLanguage("English");
        m2.setDurationMinutes(169);
        m2.setReleaseDate(LocalDate.of(2014, 11, 7));
        m2.setRating(8.7);
        m2.setPosterUrl("/images/posters/interstellar.jpg");
        m2.setTrailerUrl("https://www.youtube.com/watch?v=zSWdZVtXT7E");
        m2.setActive(true);
        movies.add(m2);

        Movie m3 = new Movie();
        m3.setTitle("The Dark Knight");
        m3.setDescription("When the menace known as the Joker wreaks havoc and chaos on the people of Gotham, Batman must accept one of the greatest psychological and physical tests of his ability to fight injustice.");
        m3.setGenre("Action / Crime");
        m3.setLanguage("English");
        m3.setDurationMinutes(152);
        m3.setReleaseDate(LocalDate.of(2008, 7, 18));
        m3.setRating(9.0);
        m3.setPosterUrl("/images/posters/the-dark-knight.jpg");
        m3.setTrailerUrl("https://www.youtube.com/watch?v=EXeTwQWrcwY");
        m3.setActive(true);
        movies.add(m3);

        Movie m4 = new Movie();
        m4.setTitle("Dune: Part Two");
        m4.setDescription("Paul Atreides unites with Chani and the Fremen while seeking revenge against the conspirators who destroyed his family.");
        m4.setGenre("Sci-Fi / Adventure");
        m4.setLanguage("English");
        m4.setDurationMinutes(166);
        m4.setReleaseDate(LocalDate.of(2024, 3, 1));
        m4.setRating(8.6);
        m4.setPosterUrl("/images/posters/dune-part-two.jpg");
        m4.setTrailerUrl("https://www.youtube.com/watch?v=Way9Dexny3w");
        m4.setActive(true);
        movies.add(m4);

        Movie m5 = new Movie();
        m5.setTitle("Oppenheimer");
        m5.setDescription("The story of American scientist J. Robert Oppenheimer and his role in the development of the atomic bomb.");
        m5.setGenre("Biography / Drama");
        m5.setLanguage("English");
        m5.setDurationMinutes(180);
        m5.setReleaseDate(LocalDate.of(2023, 7, 21));
        m5.setRating(8.9);
        m5.setPosterUrl("/images/posters/oppenheimer.jpg");
        m5.setTrailerUrl("https://www.youtube.com/watch?v=uYPbbksJxIg");
        m5.setActive(true);
        movies.add(m5);

        Movie m6 = new Movie();
        m6.setTitle("Avatar: The Way of Water");
        m6.setDescription("Jake Sully lives with his newfound family formed on the extrasolar moon Pandora. Once a familiar threat returns to finish what was previously started, Jake must work with Neytiri and the army of the Na'vi race.");
        m6.setGenre("Action / Sci-Fi");
        m6.setLanguage("English");
        m6.setDurationMinutes(192);
        m6.setReleaseDate(LocalDate.of(2022, 12, 16));
        m6.setRating(7.6);
        m6.setPosterUrl("/images/posters/avatar-the-way-of-water.jpg");
        m6.setTrailerUrl("https://www.youtube.com/watch?v=d9MyW72ELq0");
        m6.setActive(true);
        movies.add(m6);

        Movie m7 = new Movie();
        m7.setTitle("Leo");
        m7.setDescription("Parthiban is a mild-mannered café owner in Himachal Pradesh who gets thrust into the limelight after fending off a gang of deadly robbers, attracting the attention of crime lords Antony and Harold Das who suspect he is their estranged kin Leo Das.");
        m7.setGenre("Action / Thriller");
        m7.setLanguage("Tamil");
        m7.setDurationMinutes(164);
        m7.setReleaseDate(LocalDate.of(2023, 10, 19));
        m7.setRating(7.8);
        m7.setPosterUrl("/images/posters/leo.jpg");
        m7.setTrailerUrl("https://www.youtube.com/watch?v=Po3jVh_yRxU");
        m7.setActive(true);
        movies.add(m7);

        Movie m8 = new Movie();
        m8.setTitle("Jailer");
        m8.setDescription("Muthuvel Pandian, a strict yet retired jailer living a peaceful domestic life, embarks on a relentless crusade against a ruthless antique smuggling syndicate after his police officer son goes missing.");
        m8.setGenre("Action / Crime");
        m8.setLanguage("Tamil");
        m8.setDurationMinutes(168);
        m8.setReleaseDate(LocalDate.of(2023, 8, 10));
        m8.setRating(7.9);
        m8.setPosterUrl("/images/posters/jailer.jpg");
        m8.setTrailerUrl("https://www.youtube.com/watch?v=xenOE1Tma0A");
        m8.setActive(true);
        movies.add(m8);

        Movie m9 = new Movie();
        m9.setTitle("Vikram");
        m9.setDescription("A covert black-ops agent investigates a string of masked serial killings involving high-profile narcotics trafficking, leading into an epic confrontation with dangerous cartel kingpin Sandhanam.");
        m9.setGenre("Action / Thriller");
        m9.setLanguage("Tamil");
        m9.setDurationMinutes(174);
        m9.setReleaseDate(LocalDate.of(2022, 6, 3));
        m9.setRating(8.3);
        m9.setPosterUrl("/images/posters/vikram.jpg");
        m9.setTrailerUrl("https://www.youtube.com/watch?v=OKBMCL-frPU");
        m9.setActive(true);
        movies.add(m9);

        Movie m11 = new Movie();
        m11.setTitle("The Greatest of All Time (GOAT)");
        m11.setDescription("Gandhi, an elite intelligence operative and hostage negotiator with the Special Anti-Terrorist Squad, is forced out of retirement when a lethal adversary from his covert past resurfaces.");
        m11.setGenre("Action / Sci-Fi");
        m11.setLanguage("Tamil");
        m11.setDurationMinutes(179);
        m11.setReleaseDate(LocalDate.of(2024, 9, 5));
        m11.setRating(7.7);
        m11.setPosterUrl("/images/posters/the-greatest-of-all-time.jpg");
        m11.setTrailerUrl("https://www.youtube.com/watch?v=jxCRlebieWc");
        m11.setActive(true);
        movies.add(m11);

        // 2026 Upcoming Movies - Tamil
        Movie m12 = new Movie();
        m12.setTitle("Coolie");
        m12.setDescription("A seasoned port worker becomes entangled in a high-stakes conflict involving international smuggling cartels operating along the Indian coast.");
        m12.setGenre("Action / Thriller");
        m12.setLanguage("Tamil");
        m12.setDurationMinutes(165);
        m12.setReleaseDate(LocalDate.of(2026, 11, 14));
        m12.setRating(8.5);
        m12.setPosterUrl("/images/posters/coolie.jpg");
        m12.setTrailerUrl("https://www.youtube.com/watch?v=qEl77Zq8fO4");
        m12.setActive(true);
        movies.add(m12);

        Movie m13 = new Movie();
        m13.setTitle("Sardar 2");
        m13.setDescription("Former spy agent Sardar is drawn back into the shadows when a sinister biological threat targets the country from deep within international waters.");
        m13.setGenre("Spy / Action / Thriller");
        m13.setLanguage("Tamil");
        m13.setDurationMinutes(158);
        m13.setReleaseDate(LocalDate.of(2026, 12, 18));
        m13.setRating(8.1);
        m13.setPosterUrl("/images/posters/sardar-2.jpg");
        m13.setTrailerUrl("https://www.youtube.com/watch?v=8b9tE3v4hYw");
        m13.setActive(true);
        movies.add(m13);

        Movie m14 = new Movie();
        m14.setTitle("Jana Nayagan");
        m14.setDescription("A righteous people's leader takes on deep-rooted political corruption and oligarchic networks to spark a democratic transformation.");
        m14.setGenre("Action / Political Drama");
        m14.setLanguage("Tamil");
        m14.setDurationMinutes(170);
        m14.setReleaseDate(LocalDate.of(2026, 10, 30));
        m14.setRating(8.7);
        m14.setPosterUrl("/images/posters/jana-nayagan.jpg");
        m14.setTrailerUrl("https://www.youtube.com/watch?v=d_kX6z_g8Lk");
        m14.setActive(true);
        movies.add(m14);

        // 2026 Upcoming Movies - Telugu
        Movie m15 = new Movie();
        m15.setTitle("The RajaSaab");
        m15.setDescription("A daring young man unearths ancestral secrets inside an eerie, haunted royal palace, navigating spirits and laughs to claim his legacy.");
        m15.setGenre("Horror / Romantic Comedy");
        m15.setLanguage("Telugu");
        m15.setDurationMinutes(162);
        m15.setReleaseDate(LocalDate.of(2026, 10, 23));
        m15.setRating(8.4);
        m15.setPosterUrl("/images/posters/the-raja-saab.jpg");
        m15.setTrailerUrl("https://www.youtube.com/watch?v=kYv3y5wB3Hk");
        m15.setActive(true);
        movies.add(m15);

        Movie m16 = new Movie();
        m16.setTitle("They Call Him OG");
        m16.setDescription("Ojas Gambheera, a ruthless underworld kingpin with a ten-year hiatus, resurfaces in Mumbai to seek retribution against former cohorts.");
        m16.setGenre("Action / Crime / Drama");
        m16.setLanguage("Telugu");
        m16.setDurationMinutes(168);
        m16.setReleaseDate(LocalDate.of(2026, 11, 20));
        m16.setRating(8.8);
        m16.setPosterUrl("/images/posters/they-call-him-og.jpg");
        m16.setTrailerUrl("https://www.youtube.com/watch?v=YgU-e4f0y_Q");
        m16.setActive(true);
        movies.add(m16);

        Movie m17 = new Movie();
        m17.setTitle("Game Changer");
        m17.setDescription("An uncompromising Indian Administrative Service (IAS) officer fights systemic electoral fraud to reform the governance machine.");
        m17.setGenre("Political Action / Thriller");
        m17.setLanguage("Telugu");
        m17.setDurationMinutes(172);
        m17.setReleaseDate(LocalDate.of(2026, 12, 25));
        m17.setRating(8.2);
        m17.setPosterUrl("/images/posters/game-changer.jpg");
        m17.setTrailerUrl("https://www.youtube.com/watch?v=b4O5X6Y_pQw");
        m17.setActive(true);
        movies.add(m17);

        // 2026 Upcoming Movies - Hindi
        Movie m18 = new Movie();
        m18.setTitle("Ramayana: Part 1");
        m18.setDescription("An epic cinematic rendition recounting the early journey and exile of Lord Rama, Sita, and Lakshmana, culminating in the golden deer episode.");
        m18.setGenre("Mythological Epic / Drama");
        m18.setLanguage("Hindi");
        m18.setDurationMinutes(180);
        m18.setReleaseDate(LocalDate.of(2026, 11, 5));
        m18.setRating(9.1);
        m18.setPosterUrl("/images/posters/ramayana-part-1.jpg");
        m18.setTrailerUrl("https://www.youtube.com/watch?v=3R1eZ4G_qPw");
        m18.setActive(true);
        movies.add(m18);

        Movie m19 = new Movie();
        m19.setTitle("Border 2");
        m19.setDescription("Valiant soldiers on the desert frontier face overwhelming hostile forces in a heroic stand to safeguard their motherland.");
        m19.setGenre("War / Action / Drama");
        m19.setLanguage("Hindi");
        m19.setDurationMinutes(165);
        m19.setReleaseDate(LocalDate.of(2026, 11, 27));
        m19.setRating(8.6);
        m19.setPosterUrl("/images/posters/border-2.jpg");
        m19.setTrailerUrl("https://www.youtube.com/watch?v=7uV8W9X_yZs");
        m19.setActive(true);
        movies.add(m19);

        Movie m20 = new Movie();
        m20.setTitle("Bhooth Bangla");
        m20.setDescription("A quirky paranormal investigator is dispatched to an infamous haunted mansion, sparking hilarious misadventures and eerie mysteries.");
        m20.setGenre("Horror / Comedy");
        m20.setLanguage("Hindi");
        m20.setDurationMinutes(148);
        m20.setReleaseDate(LocalDate.of(2026, 12, 11));
        m20.setRating(8.0);
        m20.setPosterUrl("/images/posters/bhooth-bangla.jpg");
        m20.setTrailerUrl("https://www.youtube.com/watch?v=p4Q5R6S_tUv");
        m20.setActive(true);
        movies.add(m20);

        Movie m21 = new Movie();
        m21.setTitle("Jailer 2");
        m21.setDescription("Muthuvel Pandian returns to confront an international crime cartel threatening his family and homeland, unleashing unbridled fury across borders.");
        m21.setGenre("Action / Crime / Drama");
        m21.setLanguage("Tamil");
        m21.setDurationMinutes(168);
        m21.setReleaseDate(LocalDate.of(2026, 10, 15));
        m21.setRating(8.5);
        m21.setPosterUrl("/images/posters/jailer-2.jpg");
        m21.setTrailerUrl("https://www.youtube.com/watch?v=xenOE1Tma0A");
        m21.setActive(true);
        movies.add(m21);

        Movie m22 = new Movie();
        m22.setTitle("Demonte Colony 3");
        m22.setDescription("The sinister supernatural curse of Lord John Demonte reawakens, dragging unsuspecting researchers into a deadly labyrinth of darkness and ancient spirits.");
        m22.setGenre("Horror / Mystery / Thriller");
        m22.setLanguage("Tamil");
        m22.setDurationMinutes(145);
        m22.setReleaseDate(LocalDate.of(2026, 11, 20));
        m22.setRating(7.9);
        m22.setPosterUrl("/images/posters/demonte-colony-3.jpg");
        m22.setTrailerUrl("https://www.youtube.com/watch?v=Po3jVh_yRxU");
        m22.setActive(true);
        movies.add(m22);

        Movie m23 = new Movie();
        m23.setTitle("Mookuthi Amman 2");
        m23.setDescription("The divine and witty Goddess Mookuthi Amman descends upon Earth once again to vanquish fake godmen and rectify modern socio-spiritual injustices with celestial grace and humor.");
        m23.setGenre("Fantasy / Comedy / Drama");
        m23.setLanguage("Tamil");
        m23.setDurationMinutes(152);
        m23.setReleaseDate(LocalDate.of(2026, 11, 5));
        m23.setRating(8.2);
        m23.setPosterUrl("/images/posters/mookuthi-amman-2.jpg");
        m23.setTrailerUrl("https://www.youtube.com/watch?v=D4qAQYLGZVM");
        m23.setActive(true);
        movies.add(m23);

        List<Movie> savedMovies = movieRepository.saveAll(movies);

        // 2. Seed Theatres & Screens with Seat Layouts
        Theatre t1 = new Theatre();
        t1.setName("Cineplex IMAX Downtown");
        t1.setCity("Metropolis");
        t1.setAddress("100 Broadway Ave, Downtown Plaza");
        t1.setTotalScreens(2);
        t1.setActive(true);
        theatreRepository.save(t1);

        Theatre t2 = new Theatre();
        t2.setName("PVR Premiere Heights");
        t2.setCity("Grandview");
        t2.setAddress("45 West River Rd, Royal Mall");
        t2.setTotalScreens(2);
        t2.setActive(true);
        theatreRepository.save(t2);

        Theatre t3 = new Theatre();
        t3.setName("Galaxy Multiplex Grand");
        t3.setCity("Silverlake");
        t3.setAddress("88 North Star Blvd, Galaxy Center");
        t3.setTotalScreens(2);
        t3.setActive(true);
        theatreRepository.save(t3);

        // Screens with seats
        ScreenRequest s1Req = new ScreenRequest();
        s1Req.setTheatreId(t1.getId());
        s1Req.setName("Screen 1 - IMAX 3D");
        s1Req.setScreenType("IMAX Laser 3D");
        s1Req.setRowsCount(7);
        s1Req.setColsCount(10);
        theatreService.createScreenWithSeats(s1Req);

        ScreenRequest s2Req = new ScreenRequest();
        s2Req.setTheatreId(t1.getId());
        s2Req.setName("Screen 2 - Dolby Atmos");
        s2Req.setScreenType("Dolby Atmos 4K");
        s2Req.setRowsCount(7);
        s2Req.setColsCount(10);
        theatreService.createScreenWithSeats(s2Req);

        ScreenRequest s3Req = new ScreenRequest();
        s3Req.setTheatreId(t2.getId());
        s3Req.setName("Screen 1 - Royal Gold");
        s3Req.setScreenType("Luxury Recliner");
        s3Req.setRowsCount(6);
        s3Req.setColsCount(8);
        theatreService.createScreenWithSeats(s3Req);

        ScreenRequest s4Req = new ScreenRequest();
        s4Req.setTheatreId(t3.getId());
        s4Req.setName("Screen 1 - 4DX Premiere");
        s4Req.setScreenType("4DX Motion");
        s4Req.setRowsCount(7);
        s4Req.setColsCount(10);
        theatreService.createScreenWithSeats(s4Req);

        initTamilNaduTheatresIfMissing();

        List<Screen> allScreens = screenRepository.findAll();

        // 3. Seed Shows starting from 2026-10-21 onwards
        LocalDate startShowDate = LocalDate.of(2026, 10, 21);
        List<Show> shows = new ArrayList<>();

        LocalTime[] times = {
                LocalTime.of(10, 30),
                LocalTime.of(14, 15),
                LocalTime.of(18, 0),
                LocalTime.of(21, 30)
        };
        BigDecimal[] prices = {
                new BigDecimal("200.00")
        };

        for (int dayOffset = 0; dayOffset < 5; dayOffset++) {
            LocalDate showDate = startShowDate.plusDays(dayOffset);

            for (int i = 0; i < savedMovies.size(); i++) {
                Movie movie = savedMovies.get(i);
                Screen screen = allScreens.get(i % allScreens.size());
                LocalTime startTime = times[i % times.length];
                BigDecimal basePrice = prices[i % prices.length];

                Show show = new Show(
                        movie,
                        screen,
                        showDate,
                        startTime,
                        startTime.plusMinutes(movie.getDurationMinutes() + 15),
                        basePrice
                );
                shows.add(show);
            }
        }

        showRepository.saveAll(shows);
    }
}
