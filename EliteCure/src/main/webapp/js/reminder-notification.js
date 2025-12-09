// Reminder Notification System - Elite Cure
// This script handles real-time reminder notifications with animations and sound

let notificationCheckInterval = null;
let shownNotifications = new Set();
let audioContext = null;
let notificationSound = null;

// Initialize notification system
document.addEventListener('DOMContentLoaded', function() {
    initializeNotificationSystem();
});

// Initialize the notification system
function initializeNotificationSystem() {
    // Check for reminders every 30 seconds
    checkReminders();
    notificationCheckInterval = setInterval(checkReminders, 30000);
    
    // Initialize audio context on user interaction (required by browsers)
    document.addEventListener('click', initializeAudio, { once: true });
}

// Initialize Web Audio API
function initializeAudio() {
    if (!audioContext) {
        try {
            audioContext = new (window.AudioContext || window.webkitAudioContext)();
            console.log('Audio context initialized');
        } catch (e) {
            console.warn('Web Audio API not supported:', e);
        }
    }
}

// Check for reminders that are due
async function checkReminders() {
    const sessionId = localStorage.getItem('sessionId');
    if (!sessionId) return;
    
    try {
        const response = await fetch('/api/reminders/today', {
            headers: {
                'Authorization': `Bearer ${sessionId}`
            }
        });
        
        if (!response.ok) return;
        
        const data = await response.json();
        const reminders = data.success ? data.data : data;
        
        if (!Array.isArray(reminders)) return;
        
        const now = new Date();
        const currentTime = `${String(now.getHours()).padStart(2, '0')}:${String(now.getMinutes()).padStart(2, '0')}`;
        
        reminders.forEach(reminder => {
            // Check if this reminder is due now (within the current minute)
            if (reminder.timeOfDay && reminder.timeOfDay.substring(0, 5) === currentTime) {
                const notificationKey = `${reminder.id}-${currentTime}`;
                
                // Only show if we haven't shown this notification in this minute
                if (!shownNotifications.has(notificationKey)) {
                    showReminderNotification(reminder);
                    shownNotifications.add(notificationKey);
                    
                    // Clear old notifications from the set (keep last 100)
                    if (shownNotifications.size > 100) {
                        const first = shownNotifications.values().next().value;
                        shownNotifications.delete(first);
                    }
                }
            }
        });
        
    } catch (error) {
        console.error('Error checking reminders:', error);
    }
}

// Show reminder notification with animation
function showReminderNotification(reminder) {
    // Play notification sound
    playNotificationSound();
    
    // Create notification element
    const notification = document.createElement('div');
    notification.className = 'reminder-notification';
    notification.innerHTML = `
        <div class="notification-header">
            <span class="notification-icon">⏰</span>
            <span>Medicine Reminder</span>
        </div>
        <h3 style="margin: 12px 0; font-size: 1.5rem;">${reminder.medicineName}</h3>
        <div style="margin-bottom: 16px;">
            <p style="margin: 4px 0;"><strong>Dosage:</strong> ${reminder.dosage}</p>
            <p style="margin: 4px 0;"><strong>Frequency:</strong> ${reminder.frequency}</p>
            ${reminder.notes ? `<p style="margin: 4px 0;"><strong>Note:</strong> ${reminder.notes}</p>` : ''}
        </div>
        <div class="notification-actions">
            <button class="btn btn-secondary" onclick="snoozeReminder(${reminder.id}, this.parentElement.parentElement)">
                Snooze 5 min
            </button>
            <button class="btn" style="background: white; color: var(--primary-color);" 
                    onclick="markReminderTaken(${reminder.id}, this.parentElement.parentElement)">
                Mark as Taken
            </button>
        </div>
        <button class="close" onclick="closeNotification(this.parentElement)" 
                style="position: absolute; top: 12px; right: 12px; color: white;">&times;</button>
    `;
    
    document.body.appendChild(notification);
    
    // Auto-dismiss after 2 minutes if not interacted with
    setTimeout(() => {
        if (notification.parentElement) {
            closeNotification(notification);
        }
    }, 120000);
}

// Play notification sound using Web Audio API
function playNotificationSound() {
    if (!audioContext) {
        initializeAudio();
    }
    
    if (!audioContext) return;
    
    try {
        // Create a simple pleasant notification sound
        const oscillator1 = audioContext.createOscillator();
        const oscillator2 = audioContext.createOscillator();
        const gainNode = audioContext.createGain();
        
        oscillator1.connect(gainNode);
        oscillator2.connect(gainNode);
        gainNode.connect(audioContext.destination);
        
        // Set frequencies for a pleasant chord
        oscillator1.frequency.value = 523.25; // C5
        oscillator2.frequency.value = 659.25; // E5
        
        oscillator1.type = 'sine';
        oscillator2.type = 'sine';
        
        // Envelope
        gainNode.gain.setValueAtTime(0, audioContext.currentTime);
        gainNode.gain.linearRampToValueAtTime(0.3, audioContext.currentTime + 0.05);
        gainNode.gain.exponentialRampToValueAtTime(0.01, audioContext.currentTime + 0.5);
        
        oscillator1.start(audioContext.currentTime);
        oscillator2.start(audioContext.currentTime);
        oscillator1.stop(audioContext.currentTime + 0.5);
        oscillator2.stop(audioContext.currentTime + 0.5);
        
        // Play again after a short delay for double chime effect
        setTimeout(() => {
            const osc1 = audioContext.createOscillator();
            const osc2 = audioContext.createOscillator();
            const gain = audioContext.createGain();
            
            osc1.connect(gain);
            osc2.connect(gain);
            gain.connect(audioContext.destination);
            
            osc1.frequency.value = 659.25; // E5
            osc2.frequency.value = 783.99; // G5
            
            osc1.type = 'sine';
            osc2.type = 'sine';
            
            gain.gain.setValueAtTime(0, audioContext.currentTime);
            gain.gain.linearRampToValueAtTime(0.3, audioContext.currentTime + 0.05);
            gain.gain.exponentialRampToValueAtTime(0.01, audioContext.currentTime + 0.5);
            
            osc1.start(audioContext.currentTime);
            osc2.start(audioContext.currentTime);
            osc1.stop(audioContext.currentTime + 0.5);
            osc2.stop(audioContext.currentTime + 0.5);
        }, 200);
        
    } catch (error) {
        console.warn('Error playing notification sound:', error);
    }
}

// Snooze reminder
function snoozeReminder(reminderId, notificationElement) {
    closeNotification(notificationElement);
    
    // Show snooze message
    const toast = document.createElement('div');
    toast.className = 'alert alert-info';
    toast.textContent = 'Reminder snoozed for 5 minutes';
    toast.style.position = 'fixed';
    toast.style.top = '20px';
    toast.style.right = '20px';
    toast.style.zIndex = '10000';
    
    document.body.appendChild(toast);
    
    setTimeout(() => toast.remove(), 3000);
    
    // Re-show notification after 5 minutes
    setTimeout(() => {
        // Find the reminder from current reminders
        const sessionId = localStorage.getItem('sessionId');
        if (!sessionId) return;
        
        fetch('/api/reminders', {
            headers: {
                'Authorization': `Bearer ${sessionId}`
            }
        })
        .then(response => response.json())
        .then(reminders => {
            const reminder = reminders.find(r => r.id === reminderId);
            if (reminder && reminder.status === 'ACTIVE') {
                showReminderNotification(reminder);
            }
        })
        .catch(error => console.error('Error fetching reminder for snooze:', error));
        
    }, 5 * 60 * 1000); // 5 minutes
}

// Mark reminder as taken
async function markReminderTaken(reminderId, notificationElement) {
    const sessionId = localStorage.getItem('sessionId');
    if (!sessionId) return;
    
    try {
        const response = await fetch(`/api/reminders/${reminderId}/taken`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${sessionId}`
            }
        });
        
        const data = await response.json();
        
        if (data.success) {
            closeNotification(notificationElement);
            
            // Show success message
            const toast = document.createElement('div');
            toast.className = 'alert alert-success';
            toast.textContent = '✅ Marked as taken!';
            toast.style.position = 'fixed';
            toast.style.top = '20px';
            toast.style.right = '20px';
            toast.style.zIndex = '10000';
            
            document.body.appendChild(toast);
            
            setTimeout(() => toast.remove(), 3000);
            
            // Reload reminders if on dashboard
            if (typeof loadReminders === 'function') {
                loadReminders();
            }
        }
        
    } catch (error) {
        console.error('Error marking reminder as taken:', error);
    }
}

// Close notification with animation
function closeNotification(element) {
    element.style.animation = 'slideOutRight 0.5s ease';
    
    setTimeout(() => {
        if (element.parentElement) {
            element.remove();
        }
    }, 500);
}

// Add slideOutRight animation to the page
const style = document.createElement('style');
style.textContent = `
    @keyframes slideOutRight {
        from {
            transform: translateX(0);
            opacity: 1;
        }
        to {
            transform: translateX(500px);
            opacity: 0;
        }
    }
`;
document.head.appendChild(style);

// Cleanup on page unload
window.addEventListener('beforeunload', function() {
    if (notificationCheckInterval) {
        clearInterval(notificationCheckInterval);
    }
});
