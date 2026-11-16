# SproutBuddy
 
**Your personal plant care reminder.**
 
SproutBuddy is a gardening web app that helps people keep real-life plants healthy by tracking each plant, reminding them when to water and give sunlight, and making plant care feel a little more fun with a friendly look.
 
> **Class project:** CS160 Software Engineering, San José State University (SJSU), Fall 2026.
 
## Team
 
Guadalupe Carrillo Vega, Matilda Verdejo, Hung Phan, Kyle James
 
---
 
## Features
 
- **Plant Details Database:** a database of plant information that users pick from, plus a way to add custom plants that are not in the database.
- **Seasonal Themes:** the app's look changes with the season.
- **Plant Profiles:** a profile for each individual plant, with care and watering info and optional customization such as a photo and a name.
- **Scheduling and Reminders:** watering reminders, plus a way to export a schedule to give to a caregiver.
- **Social Aspects:** connecting with friends and ability to see their progress.
- **Dashboard:** the app's opening screen, with important notifications such as watering reminders first.
---
 
## Tech stack
 
- **Backend:** Java 21, Java Servlets and JSP (Servlet API 4.0)
- **Server:** Apache Tomcat 9
- **Database:** MySQL (driver: MySQL Connector/J 9.6.0, included in `lib/`)
- **Frontend:** Plain HTML, CSS and JavaScript (no framework)
- **IDE:** Eclipse (Dynamic Web Project)
Backend code follows a simple layered structure: **servlets** handle requests, **DAOs** talk to the database, **models** hold the data, and **JSP** pages render the screens.
 
---
 
## Set up
 
### Prerequisites
 
- **JDK 21**
- **Apache Tomcat 9.0.x**
- **MySQL** (8.x recommended)
- **Eclipse IDE for Enterprise Java and Web Developers** (or another IDE that can run a Tomcat web project)
### 1. Clone the repository
 
```bash
git clone <repository-url>
cd cs160project_f26
```
 
### 2. Set up the database
 
Run the script in `database/sproutbuddy.sql` in MySQL. It creates the `sproutbuddy` database, its tables (`users`, `plant_species`, `user_plants`, `care_templates`, `care_tasks`) and starter plant data.
 
```bash
mysql -u root -p < database/sproutbuddy.sql
```
 
### 3. Create your local database settings file
 
The file `src/main/java/sproutbuddy/util/DBConnection.java` is listed in `.gitignore` so nobody's database password gets committed. **Each teammate keeps their own local copy.** If you do not have it yet, create it with this content and fill in your own values:
 
```java
package sproutbuddy.util;
 
import java.sql.Connection;
import java.sql.DriverManager;
 
public class DBConnection {
 
    private static final String URL      = "jdbc:mysql://localhost:3306/sproutbuddy"; // use your MySQL port
    private static final String USER     = "root";                                    // your MySQL user
    private static final String PASSWORD = "your-password-here";                      // your MySQL password
 
    public static Connection getConnection() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
```
 
### 4. Import and run in Eclipse
 
1. **File → Import → Existing Projects into Workspace**, and select the repository folder.
2. Make sure the project uses **Java 21** and a **Tomcat 9** runtime (Project → Properties → Targeted Runtimes).
3. Make sure `lib/mysql-connector-j-9.6.0.jar` is on the build path.
4. Right-click the project, then **Run As → Run on Server**, and pick Tomcat 9.
5. Open `http://localhost:8080/SproutBuddy/`. The home page sends you to the log-in screen.
---
 
## Project structure
 
```
cs160project_f26/
├── database/
│   └── sproutbuddy.sql          # Creates the database, tables, and starter data
├── lib/                         # MySQL Connector/J
├── src/main/
│   ├── java/sproutbuddy/
│   │   ├── servlet/             # Request handlers (login, garden, reminders, ...)
│   │   ├── dao/                 # Database access (users, plants, species, care tasks)
│   │   ├── model/               # Data classes (User, UserPlant, PlantSpecies, CareTask)
│   │   └── util/                # DBConnection (local, git-ignored), PasswordUtil
│   └── webapp/
│       ├── WEB-INF/
│       │   ├── web.xml          # Servlet mappings
│       │   └── jspf/            # Shared JSP fragments (settings popup)
│       ├── css/
│       │   ├── auth.css         # Log-in and sign-up styling (the original look)
│       │   ├── app.css          # Dashboard, plant profile, add plant
│       │   └── seasons.css      # Seasonal color themes
│       ├── js/
│       │   ├── theme.js         # Applies the seasonal theme and runs the settings popup
│       │   └── (store.js, common.js, garden.js, plant.js: early static UI prototypes)
│       ├── images/
│       ├── index.jsp            # Redirects to the log-in page
│       ├── login.html, register.html
│       ├── garden.jsp           # Main dashboard (reminders, garden, friends)
│       ├── plantProfile.jsp     # A single plant's page
│       ├── addPlant.jsp         # Add a plant from the catalog
│       ├── reminders.jsp, plantCatalog.jsp
│       └── garden.html, plant.html   # Early static UI prototypes with sample data
└── README.md
```
 
---
 
## App pages and routes
 
| Route | Servlet | What it does |
| --- | --- | --- |
| `/login` | `LoginServlet` | Signs a user in and sends them to the dashboard |
| `/register` | `RegisterServlet` | Creates an account |
| `/logout` | `LogoutServlet` | Ends the session |
| `/account` | `AccountServlet` | Updates the user's name and email |
| `/garden` | `GardenServlet` | Dashboard with reminders, garden and friends tabs |
| `/plant-profile?id=…` | `PlantProfileServlet` | One plant's profile and care details |
| `/plant-catalog` | `PlantCatalogServlet` | Browse the plant species database |
| `/add-plant` | `AddPlantServlet` | Add a plant to the user's garden |
| `/delete-plant` | `DeletePlantServlet` | Remove a plant from the garden |
| `/reminders` | `ReminderServlet` | View and complete care tasks |
 
---
 
*Built for CS160 Software Engineering at San José State University.*
 
