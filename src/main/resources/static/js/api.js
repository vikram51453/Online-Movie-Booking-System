/**
 * API & Utility Client for Cinema Booking System
 */

const API = (function () {
    let csrfToken = null;
    let csrfHeaderName = 'X-XSRF-TOKEN';

    function getCookie(name) {
        const value = `; ${document.cookie}`;
        const parts = value.split(`; ${name}=`);
        if (parts.length === 2) return parts.pop().split(';').shift();
        return null;
    }

    async function initCsrf() {
        try {
            const res = await fetch('/api/auth/csrf', { credentials: 'same-origin' });
            if (res.ok) {
                const data = await res.json();
                if (data.token) {
                    csrfToken = data.token;
                    csrfHeaderName = data.headerName || 'X-XSRF-TOKEN';
                }
            }
        } catch (e) {
            console.debug('CSRF init note:', e);
        }
        // Fallback to cookie if present
        if (!csrfToken) {
            csrfToken = getCookie('XSRF-TOKEN');
        }
    }

    // Initialize CSRF token eagerly
    initCsrf();

    async function request(url, options = {}) {
        const config = {
            credentials: 'same-origin',
            headers: {
                'Accept': 'application/json',
                ...options.headers
            },
            ...options
        };

        const method = (config.method || 'GET').toUpperCase();
        if (['POST', 'PUT', 'DELETE', 'PATCH'].includes(method)) {
            if (!csrfToken) {
                csrfToken = getCookie('XSRF-TOKEN');
            }
            if (csrfToken) {
                config.headers[csrfHeaderName] = csrfToken;
                config.headers['X-XSRF-TOKEN'] = csrfToken;
            }
            if (config.body && typeof config.body === 'object' && !(config.body instanceof FormData)) {
                config.headers['Content-Type'] = 'application/json';
                config.body = JSON.stringify(config.body);
            }
        }

        const response = await fetch(url, config);

        // Update CSRF token from cookie if refreshed
        const updatedCookieToken = getCookie('XSRF-TOKEN');
        if (updatedCookieToken) {
            csrfToken = updatedCookieToken;
        }

        let result;
        const contentType = response.headers.get('content-type');
        if (contentType && contentType.includes('application/json')) {
            result = await response.json();
        } else {
            result = { success: response.ok, message: await response.text() };
        }

        if (!response.ok) {
            const errorMsg = (result && result.message) ? result.message : `HTTP error: ${response.status}`;
            throw new Error(errorMsg);
        }

        return result;
    }

    return {
        get: (url) => request(url, { method: 'GET' }),
        post: (url, body) => request(url, { method: 'POST', body }),
        put: (url, body) => request(url, { method: 'PUT', body }),
        delete: (url) => request(url, { method: 'DELETE' }),

        async getCurrentUser() {
            try {
                const res = await this.get('/api/auth/me');
                return res.data;
            } catch (e) {
                return null;
            }
        },

        async login(email, password) {
            const res = await this.post('/api/auth/login', { email, password });
            return res.data;
        },

        async register(userData) {
            const res = await this.post('/api/auth/register', userData);
            return res.data;
        },

        async logout() {
            await this.post('/api/auth/logout', {});
            window.location.href = '/index.html';
        }
    };
})();

// Toast Notification Helper
function showToast(message, type = 'info') {
    let container = document.getElementById('toast-container');
    if (!container) {
        container = document.createElement('div');
        container.id = 'toast-container';
        document.body.appendChild(container);
    }

    const toast = document.createElement('div');
    toast.className = `cinema-toast ${type}`;

    let icon = 'bi-info-circle-fill';
    if (type === 'success') icon = 'bi-check-circle-fill';
    if (type === 'error') icon = 'bi-exclamation-triangle-fill';

    toast.innerHTML = `
        <i class="bi ${icon} fs-5"></i>
        <div>${escapeHtml(message)}</div>
    `;

    container.appendChild(toast);

    setTimeout(() => {
        toast.style.opacity = '0';
        toast.style.transform = 'translateY(10px)';
        toast.style.transition = 'all 0.3s ease';
        setTimeout(() => toast.remove(), 300);
    }, 4000);
}

function escapeHtml(text) {
    if (!text) return '';
    const div = document.createElement('div');
    div.innerText = text;
    return div.innerHTML;
}

function formatCurrency(amount) {
    if (amount === undefined || amount === null) return '₹0.00';
    return '₹' + Number(amount).toFixed(2);
}

function formatDate(dateStr) {
    if (!dateStr) return '';
    const date = new Date(dateStr);
    return date.toLocaleDateString('en-US', { weekday: 'short', month: 'short', day: 'numeric', year: 'numeric' });
}

function formatTime(timeStr) {
    if (!timeStr) return '';
    const parts = timeStr.split(':');
    if (parts.length >= 2) {
        let hours = parseInt(parts[0], 10);
        const minutes = parts[1];
        const ampm = hours >= 12 ? 'PM' : 'AM';
        hours = hours % 12;
        hours = hours ? hours : 12;
        return `${hours}:${minutes} ${ampm}`;
    }
    return timeStr;
}

function getQueryParam(name) {
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get(name);
}
