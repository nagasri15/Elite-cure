# Elite Cure - Medicine Reminder Application

![Elite Cure Banner](https://img.shields.io/badge/Elite%20Cure-Medicine%20Reminder-blue?style=for-the-badge)
![Java](https://img.shields.io/badge/Java-17+-orange?style=for-the-badge&logo=java)
![License](https://img.shields.io/badge/License-MIT-green?style=for-the-badge)

A **complete, unique, and responsive** medicine reminder web application built with **pure Java backend** (no Spring/Spring Boot) and modern **HTML, CSS, JavaScript** frontend.

---

## 🌟 Features

### Core Functionality
- ✅ **User Registration & Login** with secure password hashing (BCrypt)
- 💊 **Medicine Reminder Management** (CRUD operations)
- ⏰ **Smart Reminder System** with real-time notifications
- 🔔 **Audio Notifications** using Web Audio API
- 📱 **Fully Responsive Design** (mobile, tablet, desktop)
- 🎨 **Modern UI/UX** with smooth animations
- 🔒 **Secure Session Management**
- 💾 **H2 Database** (embedded, no external setup required)

### Reminder Features
- Set medicine name, dosage, and frequency
- Configure start date, end date, and specific time
- Add custom notes (e.g., "take after food")
- Real-time reminder notifications with sound
- Snooze reminders for 5 minutes
- Mark reminders as taken
- View all active reminders on dashboard

---

## 🏗️ Technology Stack

### Backend (Pure Java - NO Spring!)
- **Java 17+** - Core language
- **com.sun.net.httpserver.HttpServer** - HTTP server (no frameworks)
- **JDBC** - Database connectivity
- **H2 Database** - Embedded relational database
- **Gson** - JSON processing
- **BCrypt** - Password hashing

### Frontend
- **HTML5** - Markup
- **CSS3** - Modern styling with animations
- **Vanilla JavaScript** - No frameworks
- **Web Audio API** - Notification sounds

### Build Tool
- **Maven** - Dependency management and build

---

## 📁 Project Structure

```
EliteCure/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/elitecure/
│   │   │       ├── App.java                    # Main application entry point
│   │   │       ├── controller/
│   │   │       │   ├── AuthController.java     # Authentication endpoints
│   │   │       │   └── ReminderController.java # Reminder CRUD endpoints
│   │   │       ├── service/
│   │   │       │   ├── UserService.java        # User business logic
│   │   │       │   └── ReminderService.java    # Reminder business logic
│   │   │       ├── dao/
│   │   │       │   ├── UserDAO.java            # User data access
│   │   │       │   └── ReminderDAO.java        # Reminder data access
│   │   │       ├── model/
│   │   │       │   ├── User.java               # User entity
│   │   │       │   └── Reminder.java           # Reminder entity
│   │   │       └── util/
│   │   │           ├── DatabaseUtil.java       # Database connection
│   │   │           ├── SessionManager.java     # Session handling
│   │   │           └── JsonResponse.java       # JSON response helper
│   │   ├── resources/
│   │   │   └── schema.sql                      # Database schema
│   │   └── webapp/
│   │       ├── index.html                      # Landing page
│   │       ├── login.html                      # Login page
│   │       ├── register.html                   # Registration page
│   │       ├── dashboard.html                  # Main dashboard
│   │       ├── css/
│   │       │   └── style.css                   # Application styles
│   │       └── js/
│   │           ├── dashboard.js                # Dashboard functionality
│   │           └── reminder-notification.js    # Notification system
├── pom.xml                                     # Maven configuration
└── README.md                                   # This file
```

---

## 🚀 Setup & Installation

### Prerequisites
- **Java Development Kit (JDK) 17 or higher**
- **Maven 3.6+** (for dependency management)
- A modern web browser (Chrome, Firefox, Edge, Safari)

### Installation Steps

1. **Clone or Download the Project**
   ```bash
   cd EliteCure
   ```

2. **Build the Project**
   ```bash
   mvn clean compile
   ```

3. **Run the Application**
   ```bash
   mvn exec:java -Dexec.mainClass="com.elitecure.App"
   ```

   Alternatively, compile and run directly:
   ```bash
   mvn package
   java -cp target/classes:target/dependency/* com.elitecure.App
   ```

4. **Access the Application**
   
   Open your browser and navigate to:
   ```
   http://localhost:8080
   ```

5. **Login with Test Account**
   
   Use these credentials to test the application:
   - **Email:** `test@elitecure.com`
   - **Password:** `Test@123`

---

## 📝 Usage Guide

### 1. Register a New Account
- Click **"Register"** on the landing page
- Fill in your details:
  - Full Name (min 2 characters)
  - Email (valid format)
  - Password (min 8 characters with uppercase, lowercase, digit, and special character)
  - Confirm Password
- Password strength indicator shows real-time feedback
- Click **"Register"** to create account

### 2. Login
- Enter your email and password
- Optional: Check "Remember me"
- Click **"Login"** to access dashboard

### 3. Add Medicine Reminder
- On the dashboard, click **"+ Add Reminder"**
- Fill in reminder details:
  - **Medicine Name:** e.g., "Aspirin"
  - **Dosage:** e.g., "1 tablet" or "5 ml"
  - **Frequency:** Select from dropdown (Once a day, Twice a day, etc.)
  - **Start Date:** When to start reminders
  - **End Date:** (Optional) When to stop
  - **Time of Day:** Exact time for notification (HH:MM format)
  - **Notes:** (Optional) e.g., "Take after food"
- Click **"Save Reminder"**

### 4. View & Manage Reminders
- All reminders appear on the dashboard as cards
- Each card shows:
  - Medicine name and dosage
  - Time, frequency, start/end dates
  - Status badge (Active, Completed, Cancelled)
  - Notes (if any)
- Statistics show:
  - Total reminders
  - Due today count
  - Active reminders count

### 5. Edit or Delete Reminder
- Click the **✏️ (edit)** icon to modify
- Click the **🗑️ (delete)** icon to remove
- Confirm deletion when prompted

### 6. Receive Notifications
- When it's time for your medicine:
  - A notification popup appears in the top-right corner
  - A pleasant chime sound plays
  - The notification shows medicine details
- **Actions available:**
  - **Snooze for 5 min:** Dismisses and re-appears after 5 minutes
  - **Mark as Taken:** Marks reminder as completed
  - **Close (×):** Dismisses notification

---

## 🔧 Configuration

### Database Configuration
The application uses an **embedded H2 database** stored in the `./data` directory.

- **Database URL:** `jdbc:h2:./data/elitecure`
- **Username:** `sa`
- **Password:** (empty)

To modify database settings, edit `src/main/java/com/elitecure/util/DatabaseUtil.java`:
```java
private static final String DB_URL = "jdbc:h2:./data/elitecure;AUTO_SERVER=TRUE";
private static final String DB_USER = "sa";
private static final String DB_PASSWORD = "";
```

### Server Port
Default port is **8080**. To change, edit `src/main/java/com/elitecure/App.java`:
```java
private static final int PORT = 8080; // Change to your desired port
```

### Database Schema
The database schema is automatically initialized from `src/main/resources/schema.sql`.

**Tables:**
- `users` - Stores user accounts
- `reminders` - Stores medicine reminders

**Default Test User:**
- Email: `test@elitecure.com`
- Password: `Test@123` (hashed in database)

---

## 🎨 UI/UX Features

### Responsive Design
- **Desktop:** Full-width cards with multi-column grid
- **Tablet:** Optimized 2-column layout
- **Mobile:** Single column, touch-friendly buttons

### Animations
- ✨ Fade-in on page load
- 🎯 Hover effects on cards and buttons
- 📊 Smooth modal transitions
- 🔔 Slide-in notifications
- 💫 Pulse animation on reminder alerts

### Color Scheme
- **Primary:** Blue (#2563eb) - Professional and trustworthy
- **Secondary:** Green (#10b981) - Health and wellness
- **Accent:** Amber (#f59e0b) - Attention and warmth
- **Danger:** Red (#ef4444) - Warnings and deletions

---

## 🔐 Security Features

1. **Password Hashing:** BCrypt with salt (cost factor: 10)
2. **Email Validation:** Server-side and client-side
3. **Password Strength Check:**
   - Minimum 8 characters
   - Must contain uppercase, lowercase, digit, and special character
4. **Session Management:** UUID-based session tokens
5. **SQL Injection Prevention:** Prepared statements throughout
6. **XSS Protection:** Input sanitization

---

## 📡 API Endpoints

### Authentication
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/register` | Register new user |
| POST | `/api/login` | Authenticate user |
| POST | `/api/logout` | Invalidate session |

### Reminders
| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/api/reminders` | Get all user reminders | ✅ |
| GET | `/api/reminders/today` | Get today's reminders | ✅ |
| POST | `/api/reminders` | Create new reminder | ✅ |
| PUT | `/api/reminders/{id}` | Update reminder | ✅ |
| DELETE | `/api/reminders/{id}` | Delete reminder | ✅ |
| POST | `/api/reminders/{id}/taken` | Mark as taken | ✅ |

**Request/Response Format:** JSON

**Authentication:** Bearer token in `Authorization` header

---

## 🧪 Testing

### Test Account
- **Email:** `test@elitecure.com`
- **Password:** `Test@123`

### Manual Testing Checklist
- [ ] Register new user
- [ ] Login with correct credentials
- [ ] Login with incorrect credentials (should fail)
- [ ] Add new reminder
- [ ] Edit existing reminder
- [ ] Delete reminder with confirmation
- [ ] View reminder notifications at scheduled time
- [ ] Snooze notification
- [ ] Mark reminder as taken
- [ ] Logout and verify session cleared
- [ ] Test responsive design on mobile/tablet

---

## 🐛 Troubleshooting

### Issue: Port 8080 already in use
**Solution:** Change port in `App.java` or stop the process using port 8080.

### Issue: Database connection error
**Solution:** Ensure `./data` directory has write permissions. Delete `./data/elitecure.mv.db` and restart to recreate database.

### Issue: No notifications appearing
**Solution:** 
1. Check browser console for errors
2. Ensure audio is not muted
3. Click anywhere on the page first (browsers require user interaction for audio)

### Issue: Maven dependencies not downloading
**Solution:** Run `mvn clean install -U` to force update dependencies.

---

## 📚 Technical Highlights

### Why No Spring/Spring Boot?
This project demonstrates building a **complete web application using core Java** without heavy frameworks:
- Lightweight and fast startup
- Full control over HTTP handling
- Understanding of web fundamentals
- Minimal dependencies
- Easy to deploy and maintain

### Clean Architecture
- **Controller Layer:** HTTP request/response handling
- **Service Layer:** Business logic and validation
- **DAO Layer:** Database operations
- **Model Layer:** Entity definitions
- **Util Layer:** Helper functions and utilities

### Best Practices
- ✅ Separation of concerns
- ✅ Single Responsibility Principle
- ✅ DRY (Don't Repeat Yourself)
- ✅ Input validation (client & server)
- ✅ Error handling
- ✅ Code comments and documentation
- ✅ Responsive web design
- ✅ Progressive enhancement

---

## 🎯 Future Enhancements (Optional)

- [ ] Multiple reminders per day for same medicine
- [ ] Calendar view of all reminders
- [ ] Reminder history and statistics
- [ ] Export reminders as PDF
- [ ] Email/SMS notifications
- [ ] Multi-language support
- [ ] Dark mode theme
- [ ] PWA (Progressive Web App) support
- [ ] Medicine interaction warnings

---

## 📄 License

This project is created for educational purposes. Feel free to use and modify as needed.

---

## 👨‍💻 Author

**Elite Cure Development Team**

---

## 🙏 Acknowledgments

- H2 Database for embedded database solution
- Google Gson for JSON processing
- BCrypt for secure password hashing
- Web Audio API for notification sounds

---

## 📞 Support

For issues or questions:
1. Check the **Troubleshooting** section
2. Review the **Usage Guide**
3. Check browser console for errors
4. Verify Java and Maven versions

---

**Enjoy using Elite Cure! Never miss your medicine again! 💊⏰**
