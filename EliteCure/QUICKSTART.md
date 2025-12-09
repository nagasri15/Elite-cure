# Quick Start Guide - Elite Cure

## Running the Application

### Option 1: Using Maven (Recommended)
```bash
mvn exec:java "-Dexec.mainClass=com.elitecure.App"
```

### Option 2: Compile and Run
```bash
# Compile first
mvn clean package

# Run the JAR
java -cp "target/classes;target/dependency/*" com.elitecure.App
```

### Option 3: Direct Java Execution
```bash
# From project root
mvn clean compile
mvn exec:java "-Dexec.mainClass=com.elitecure.App"
```

## Accessing the Application

Once the server is running, you'll see:
```
╔════════════════════════════════════════════════════════╗
║     Elite Cure - Medicine Reminder Application        ║
║  Server running on: http://localhost:8080              ║
╚════════════════════════════════════════════════════════╝
```

Open your browser and navigate to: **http://localhost:8080**

## Test Credentials

- **Email:** test@elitecure.com
- **Password:** Test@123

## Quick Test Flow

1. **Landing Page** → Click "Login"
2. **Login** → Use test credentials or register new account
3. **Dashboard** → View empty state or existing reminders
4. **Add Reminder:**
   - Click "+ Add Reminder" button
   - Fill in details:
     - Medicine Name: "Aspirin"
     - Dosage: "1 tablet"
     - Frequency: "Twice a day"
     - Start Date: Today
     - Time: Set to current time + 2 minutes
     - Notes: "Take after food"
   - Click "Save Reminder"
5. **Wait for Notification:**
   - After 2 minutes, you'll hear a chime sound
   - A notification popup will appear
   - Test "Snooze" or "Mark as Taken" buttons
6. **Edit/Delete:**
   - Use ✏️ icon to edit reminders
   - Use 🗑️ icon to delete (with confirmation)

## Stopping the Server

Press `Ctrl + C` in the terminal where the server is running.

## Troubleshooting

### Port Already in Use
If port 8080 is busy, edit `src/main/java/com/elitecure/App.java`:
```java
private static final int PORT = 8080; // Change to 8081, 8082, etc.
```

### Database Reset
Delete the database to start fresh:
```bash
# Remove database files
rm -rf data/
# Or on Windows
rmdir /s data
```

Then restart the application - it will recreate the database with the test user.

### No Notifications
1. Click anywhere on the page first (browsers require user interaction for audio)
2. Check browser console for errors (F12)
3. Ensure audio is not muted
4. Set a reminder for 1-2 minutes from now to test

## Features to Test

✅ User Registration (password strength indicator)
✅ User Login/Logout
✅ Add Medicine Reminder
✅ Edit Reminder
✅ Delete Reminder (with confirmation)
✅ View all reminders
✅ Real-time notifications (with sound)
✅ Snooze functionality
✅ Mark as taken
✅ Responsive design (resize browser)
✅ Form validation (try invalid inputs)

## Browser Compatibility

Tested and works on:
- ✅ Google Chrome 90+
- ✅ Microsoft Edge 90+
- ✅ Firefox 88+
- ✅ Safari 14+

## Mobile Testing

The application is fully responsive. To test on mobile:
1. Open Developer Tools (F12)
2. Click device emulation icon
3. Select iPhone, iPad, or Android device
4. Test all features

Enjoy Elite Cure! 💊⏰
