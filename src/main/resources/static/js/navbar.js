/**
 * Dynamic Navbar & Footer Manager for Cinema Booking System
 */

document.addEventListener('DOMContentLoaded', async () => {
    await renderNavbar();
    renderFooter();
});

async function renderNavbar() {
    const navContainer = document.getElementById('navbar-container');
    if (!navContainer) return;

    let user = null;
    try {
        user = await API.getCurrentUser();
    } catch (e) {
        console.debug('Guest session');
    }

    const currentPath = window.location.pathname.split('/').pop() || 'index.html';

    const isAdmin = user && user.role === 'ROLE_ADMIN';

    navContainer.innerHTML = `
    <nav class="navbar navbar-expand-lg cinema-navbar">
        <div class="container">
            <a class="cinema-brand" href="/index.html">
                <i class="bi bi-film"></i> Cine<span>Sphere</span>
            </a>

            <button class="navbar-toggler text-dark border-0" type="button" data-bs-toggle="collapse" data-bs-target="#cinemaNav">
                <i class="bi bi-list fs-2 text-dark"></i>
            </button>

            <div class="collapse navbar-collapse" id="cinemaNav">
                <ul class="navbar-nav me-auto mb-2 mb-lg-0 ms-lg-4">
                    <li class="nav-item">
                        <a class="nav-link ${currentPath === 'index.html' || currentPath === '' ? 'active' : ''}" href="/index.html">
                            <i class="bi bi-house me-1"></i> Home
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link ${currentPath === 'movies.html' || currentPath === 'movie-details.html' ? 'active' : ''}" href="/movies.html">
                            <i class="bi bi-camera-reels me-1"></i> Movies
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link ${currentPath === 'theatres.html' ? 'active' : ''}" href="/theatres.html">
                            <i class="bi bi-geo-alt me-1"></i> Theatres
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link ${currentPath === 'shows.html' ? 'active' : ''}" href="/shows.html">
                            <i class="bi bi-calendar-event me-1"></i> Showtimes
                        </a>
                    </li>
                    ${user ? `
                    <li class="nav-item">
                        <a class="nav-link ${currentPath === 'my-bookings.html' ? 'active' : ''}" href="/my-bookings.html">
                            <i class="bi bi-ticket-perforated me-1"></i> My Bookings
                        </a>
                    </li>` : ''}
                    ${isAdmin ? `
                    <li class="nav-item dropdown">
                        <a class="nav-link dropdown-toggle text-warning ${currentPath.startsWith('admin') ? 'active' : ''}" href="#" role="button" data-bs-toggle="dropdown">
                            <i class="bi bi-shield-lock me-1"></i> Admin Portal
                        </a>
                        <ul class="dropdown-menu shadow-sm">
                            <li><a class="dropdown-item ${currentPath === 'admin-dashboard.html' ? 'active' : ''}" href="/admin-dashboard.html"><i class="bi bi-speedometer2 me-2"></i>Dashboard</a></li>
                            <li><a class="dropdown-item ${currentPath === 'admin-movies.html' ? 'active' : ''}" href="/admin-movies.html"><i class="bi bi-film me-2"></i>Manage Movies</a></li>
                            <li><a class="dropdown-item ${currentPath === 'admin-theatres.html' ? 'active' : ''}" href="/admin-theatres.html"><i class="bi bi-building me-2"></i>Manage Theatres</a></li>
                            <li><a class="dropdown-item ${currentPath === 'admin-shows.html' ? 'active' : ''}" href="/admin-shows.html"><i class="bi bi-clock me-2"></i>Manage Shows</a></li>
                            <li><a class="dropdown-item ${currentPath === 'admin-bookings.html' ? 'active' : ''}" href="/admin-bookings.html"><i class="bi bi-receipt me-2"></i>Manage Bookings</a></li>
                        </ul>
                    </li>` : ''}
                </ul>

                <div class="d-flex align-items-center gap-2">
                    ${user ? `
                    <div class="dropdown">
                        <button class="btn btn-cinema-outline btn-cinema-sm dropdown-toggle" type="button" data-bs-toggle="dropdown">
                            <i class="bi bi-person-circle me-1"></i> ${escapeHtml(user.fullName.split(' ')[0])}
                        </button>
                        <ul class="dropdown-menu dropdown-menu-end shadow-sm">
                            <li><a class="dropdown-item" href="/profile.html"><i class="bi bi-person me-2"></i>My Profile</a></li>
                            <li><a class="dropdown-item" href="/my-bookings.html"><i class="bi bi-ticket-perforated me-2"></i>My Bookings</a></li>
                            <li><hr class="dropdown-divider"></li>
                            <li><a class="dropdown-item text-danger" href="javascript:void(0)" onclick="API.logout()"><i class="bi bi-box-arrow-right me-2"></i>Logout</a></li>
                        </ul>
                    </div>
                    ` : `
                    <a href="/login.html" class="btn btn-cinema-outline btn-cinema-sm">
                        <i class="bi bi-box-arrow-in-right me-1"></i> Login
                    </a>
                    <a href="/register.html" class="btn btn-cinema btn-cinema-sm">
                        <i class="bi bi-person-plus me-1"></i> Register
                    </a>
                    `}
                </div>
            </div>
        </div>
    </nav>
    `;
}

function renderFooter() {
    const footerContainer = document.getElementById('footer-container');
    if (!footerContainer) return;

    footerContainer.innerHTML = `
    <footer class="cinema-footer">
        <div class="container">
            <div class="row g-4 mb-4">
                <div class="col-lg-4">
                    <div class="cinema-brand mb-3">
                        <i class="bi bi-film"></i> Cine<span>Sphere</span>
                    </div>
                    <p class="text-secondary small">
                        Next-generation Online Movie Ticket Booking Platform designed for an unparalleled cinema reservation experience.
                    </p>
                    <div class="d-flex gap-3 text-secondary">
                        <a href="#"><i class="bi bi-facebook fs-5"></i></a>
                        <a href="#"><i class="bi bi-twitter-x fs-5"></i></a>
                        <a href="#"><i class="bi bi-instagram fs-5"></i></a>
                        <a href="#"><i class="bi bi-youtube fs-5"></i></a>
                    </div>
                </div>
                <div class="col-sm-6 col-lg-2">
                    <h6 class="fw-bold mb-3" style="color: var(--text-pure);">Quick Links</h6>
                    <ul class="list-unstyled small mb-0 d-flex flex-column gap-2">
                        <li><a href="/index.html">Home</a></li>
                        <li><a href="/movies.html">All Movies</a></li>
                        <li><a href="/theatres.html">Theatres</a></li>
                        <li><a href="/shows.html">Showtimes</a></li>
                    </ul>
                </div>
                <div class="col-sm-6 col-lg-3">
                    <h6 class="fw-bold mb-3" style="color: var(--text-pure);">Experience</h6>
                    <ul class="list-unstyled small mb-0 d-flex flex-column gap-2">
                        <li><span class="text-secondary"><i class="bi bi-badge-hd me-1"></i> IMAX with Laser</span></li>
                        <li><span class="text-secondary"><i class="bi bi-soundwave me-1"></i> Dolby Atmos Sound</span></li>
                        <li><span class="text-secondary"><i class="bi bi-shield-check me-1"></i> Instant Seat Hold</span></li>
                        <li><span class="text-secondary"><i class="bi bi-credit-card me-1"></i> Secure Fast Checkout</span></li>
                    </ul>
                </div>
                <div class="col-lg-3">
                    <h6 class="fw-bold mb-3" style="color: var(--text-pure);">Cinema Support</h6>
                    <p class="small text-secondary mb-2">Need help with your booking?</p>
                    <p class="small mb-1" style="color: var(--text-primary);"><i class="bi bi-envelope me-2 text-danger"></i>support@cinesphere.com</p>
                    <p class="small" style="color: var(--text-primary);"><i class="bi bi-telephone me-2 text-danger"></i>+1 (800) 555-CINE</p>
                </div>
            </div>
            <div class="border-top border-secondary border-opacity-25 pt-3 d-flex flex-wrap justify-content-between align-items-center small text-muted">
                <div>&copy; ${new Date().getFullYear()} CineSphere Inc. All rights reserved.</div>
                <div>Premier Online Cinema Ticket Reservation System</div>
            </div>
        </div>
    </footer>
    `;
}
