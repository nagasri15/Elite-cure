// Dashboard JavaScript - Elite Cure

// Check authentication
const sessionId = localStorage.getItem('sessionId');
const user = JSON.parse(localStorage.getItem('user') || '{}');

if (!sessionId || !user.id) {
    window.location.href = '/login.html';
}

// Global state
let currentReminders = [];
let editingReminderId = null;

// Initialize dashboard
document.addEventListener('DOMContentLoaded', function() {
    initializeDashboard();
    loadReminders();
    
    // Set today's date as default for new reminders
    const today = new Date().toISOString().split('T')[0];
    document.getElementById('startDate').value = today;
    
    // Setup form submission
    document.getElementById('reminderForm').addEventListener('submit', handleFormSubmit);
});

// Initialize dashboard UI
function initializeDashboard() {
    // Set greeting
    const greeting = getGreeting();
    document.getElementById('greetingText').textContent = `${greeting}, ${user.fullName}!`;
    
    // Set current date
    const options = { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' };
    const dateStr = new Date().toLocaleDateString('en-US', options);
    document.getElementById('currentDate').textContent = dateStr;
}

// Get time-based greeting
function getGreeting() {
    const hour = new Date().getHours();
    if (hour < 12) return 'Good Morning';
    if (hour < 18) return 'Good Afternoon';
    return 'Good Evening';
}

// Load all reminders
async function loadReminders() {
    try {
        const response = await fetch('/api/reminders', {
            headers: {
                'Authorization': `Bearer ${sessionId}`
            }
        });
        
        if (response.status === 401) {
            logout();
            return;
        }
        
        const data = await response.json();
        const reminders = data.success ? data.data : data;
        currentReminders = Array.isArray(reminders) ? reminders : [];
        
        updateStats(currentReminders);
        displayReminders(currentReminders);
        
    } catch (error) {
        console.error('Error loading reminders:', error);
        showToast('Failed to load reminders', 'error');
    }
}

// Update statistics
function updateStats(reminders) {
    const total = reminders.length;
    const today = new Date().toISOString().split('T')[0];
    
    const todayCount = reminders.filter(r => {
        const start = r.startDate;
        const end = r.endDate || '9999-12-31';
        return start <= today && end >= today && r.status === 'ACTIVE';
    }).length;
    
    const activeCount = reminders.filter(r => r.status === 'ACTIVE').length;
    
    document.getElementById('totalReminders').textContent = total;
    document.getElementById('todayReminders').textContent = todayCount;
    document.getElementById('activeReminders').textContent = activeCount;
}

// Display reminders
function displayReminders(reminders) {
    const container = document.getElementById('remindersContainer');
    
    if (reminders.length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                <div class="empty-state-icon">💊</div>
                <h3>No reminders yet</h3>
                <p>Start by adding your first medicine reminder</p>
                <button class="btn btn-primary" onclick="openAddModal()">+ Add Your First Reminder</button>
            </div>
        `;
        return;
    }
    
    container.innerHTML = reminders.map(reminder => createReminderCard(reminder)).join('');
}

// Create reminder card HTML
function createReminderCard(reminder) {
    const statusBadge = `<span class="badge badge-${reminder.status.toLowerCase()}">${reminder.status}</span>`;
    
    return `
        <div class="reminder-card">
            <div class="reminder-header">
                <div class="reminder-title">
                    <h3>${reminder.medicineName}</h3>
                    <p class="reminder-dosage">${reminder.dosage}</p>
                </div>
                <div class="reminder-actions">
                    <button class="icon-btn" onclick="editReminder(${reminder.id})" title="Edit">✏️</button>
                    <button class="icon-btn" onclick="deleteReminder(${reminder.id})" title="Delete">🗑️</button>
                </div>
            </div>
            
            <div class="reminder-details">
                <div class="detail-item">
                    <span class="detail-label">Time</span>
                    <span class="detail-value">${formatTime(reminder.timeOfDay)}</span>
                </div>
                <div class="detail-item">
                    <span class="detail-label">Frequency</span>
                    <span class="detail-value">${reminder.frequency}</span>
                </div>
                <div class="detail-item">
                    <span class="detail-label">Start Date</span>
                    <span class="detail-value">${formatDate(reminder.startDate)}</span>
                </div>
                <div class="detail-item">
                    <span class="detail-label">End Date</span>
                    <span class="detail-value">${reminder.endDate ? formatDate(reminder.endDate) : 'Ongoing'}</span>
                </div>
            </div>
            
            ${reminder.notes ? `<p style="color: var(--text-light); font-size: 0.9rem; margin-bottom: 12px;">📝 ${reminder.notes}</p>` : ''}
            
            <div class="reminder-footer">
                ${statusBadge}
            </div>
        </div>
    `;
}

// Format time for display
function formatTime(time) {
    if (!time) return '';
    const [hours, minutes] = time.split(':');
    const h = parseInt(hours);
    const ampm = h >= 12 ? 'PM' : 'AM';
    const displayHour = h % 12 || 12;
    return `${displayHour}:${minutes} ${ampm}`;
}

// Format date for display
function formatDate(date) {
    if (!date) return '';
    return new Date(date + 'T00:00:00').toLocaleDateString('en-US', {
        month: 'short',
        day: 'numeric',
        year: 'numeric'
    });
}

// Open add reminder modal
function openAddModal() {
    editingReminderId = null;
    document.getElementById('modalTitle').textContent = 'Add New Reminder';
    document.getElementById('reminderForm').reset();
    
    const today = new Date().toISOString().split('T')[0];
    document.getElementById('startDate').value = today;
    
    document.getElementById('reminderModal').classList.add('active');
}

// Edit reminder
function editReminder(id) {
    const reminder = currentReminders.find(r => r.id === id);
    if (!reminder) return;
    
    editingReminderId = id;
    document.getElementById('modalTitle').textContent = 'Edit Reminder';
    
    document.getElementById('reminderId').value = reminder.id;
    document.getElementById('medicineName').value = reminder.medicineName;
    document.getElementById('dosage').value = reminder.dosage;
    document.getElementById('frequency').value = reminder.frequency;
    document.getElementById('startDate').value = reminder.startDate;
    document.getElementById('endDate').value = reminder.endDate || '';
    document.getElementById('timeOfDay').value = reminder.timeOfDay;
    document.getElementById('notes').value = reminder.notes || '';
    
    document.getElementById('reminderModal').classList.add('active');
}

// Close modal
function closeModal() {
    document.getElementById('reminderModal').classList.remove('active');
    editingReminderId = null;
}

// Handle form submission
async function handleFormSubmit(e) {
    e.preventDefault();
    
    const formData = {
        medicineName: document.getElementById('medicineName').value.trim(),
        dosage: document.getElementById('dosage').value.trim(),
        frequency: document.getElementById('frequency').value,
        startDate: document.getElementById('startDate').value,
        endDate: document.getElementById('endDate').value || null,
        timeOfDay: document.getElementById('timeOfDay').value,
        notes: document.getElementById('notes').value.trim()
    };
    
    const saveBtn = document.getElementById('saveBtn');
    saveBtn.disabled = true;
    saveBtn.textContent = 'Saving...';
    
    try {
        let response;
        
        if (editingReminderId) {
            // Update existing reminder
            formData.status = 'ACTIVE';
            response = await fetch(`/api/reminders/${editingReminderId}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${sessionId}`
                },
                body: JSON.stringify(formData)
            });
        } else {
            // Create new reminder
            response = await fetch('/api/reminders', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${sessionId}`
                },
                body: JSON.stringify(formData)
            });
        }
        
        const data = await response.json();
        
        if (data.success) {
            showToast(editingReminderId ? 'Reminder updated!' : 'Reminder added!', 'success');
            closeModal();
            loadReminders();
        } else {
            showToast(data.error || 'Operation failed', 'error');
        }
        
    } catch (error) {
        console.error('Error saving reminder:', error);
        showToast('An error occurred', 'error');
    } finally {
        saveBtn.disabled = false;
        saveBtn.textContent = 'Save Reminder';
    }
}

// Delete reminder
async function deleteReminder(id) {
    if (!confirm('Are you sure you want to delete this reminder?')) {
        return;
    }
    
    try {
        const response = await fetch(`/api/reminders/${id}`, {
            method: 'DELETE',
            headers: {
                'Authorization': `Bearer ${sessionId}`
            }
        });
        
        const data = await response.json();
        
        if (data.success) {
            showToast('Reminder deleted', 'success');
            loadReminders();
        } else {
            showToast(data.error || 'Delete failed', 'error');
        }
        
    } catch (error) {
        console.error('Error deleting reminder:', error);
        showToast('An error occurred', 'error');
    }
}

// Logout
async function logout() {
    try {
        await fetch('/api/logout', {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${sessionId}`
            }
        });
    } catch (error) {
        console.error('Logout error:', error);
    } finally {
        localStorage.removeItem('sessionId');
        localStorage.removeItem('user');
        window.location.href = '/login.html';
    }
}

// Show toast notification
function showToast(message, type) {
    const toast = document.createElement('div');
    toast.className = `alert alert-${type}`;
    toast.textContent = message;
    toast.style.position = 'fixed';
    toast.style.top = '20px';
    toast.style.right = '20px';
    toast.style.zIndex = '10000';
    toast.style.minWidth = '300px';
    
    document.body.appendChild(toast);
    
    setTimeout(() => {
        toast.remove();
    }, 3000);
}

// Close modal on outside click
document.getElementById('reminderModal').addEventListener('click', function(e) {
    if (e.target === this) {
        closeModal();
    }
});
