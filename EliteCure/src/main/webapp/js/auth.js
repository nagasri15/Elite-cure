// Auth helper functions
// Shared utilities for authentication pages

// Validate email format
function validateEmail(email) {
    const pattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return pattern.test(email);
}

// Check if user is already logged in
function checkExistingSession() {
    const sessionId = localStorage.getItem('sessionId');
    const user = localStorage.getItem('user');
    
    if (sessionId && user) {
        // User already logged in, redirect to dashboard
        window.location.href = '/dashboard.html';
    }
}

// Call this on auth pages to redirect if already logged in
document.addEventListener('DOMContentLoaded', function() {
    // Only check on login/register pages
    if (window.location.pathname.includes('login.html') || 
        window.location.pathname.includes('register.html')) {
        checkExistingSession();
    }
});
