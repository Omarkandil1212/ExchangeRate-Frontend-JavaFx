# Exchange Application

This project is a JavaFX-based desktop application for currency exchange management. It utilizes modern Java features and libraries such as Retrofit for network operations, ControlsFX for enhanced UI components, and JavaFX for the graphical user interface.

## Prerequisites

Before you can run this application, make sure you have the following installed:
- Java JDK 16
- Maven (to handle project dependencies and build configuration)

## Dependencies

This project uses several dependencies managed through Maven:
- **JavaFX**: For the GUI components.
- **ControlsFX**: Provides additional UI controls and components.
- **Retrofit**: Used for HTTP API calls.
- **Socket.IO-client**: For real-time communication features.
- **FreeTTS**: Text-to-Speech support.
- **Gson**: For JSON serialization and deserialization.

## Building the Project

To build the project, navigate to the project directory and run the following Maven command:
bash
mvn clean install
After building the project, you can run the application using:
mvn javafx:run

### Main Features

**1. User Authentication:**

-   **Login**: Users can log into the application using their credentials.
-   **Registration**: New users can register by creating a new account with their details.

**2. Dashboard:**

-   Displays current exchange rates pulled from an external API.
-   Shows user-specific data such as account balance and recent transactions.

**3. Transaction Management:**

-   **Create Transactions**: Users can perform currency exchange transactions between each other.
-   **View Transactions**: Users can view a history of their past transactions.

**4. User Preferences:**
-   Allows users to configure settings such as notification preferences.

**5. Previous Information:**
-    Users can see the statistics and fluctuations of the rates over a specific date.
  
**6. Rate Prediction:**
-    Users can predict the rate on a specific date. 

**7. Notifications:**

-   Users can choose to receive updates via email or SMS, which are managed through user preferences.

**8. Accessibility Features:**

-   Includes text-to-speech support for users with visibility impairments.

### Project Structure and Key Files

The application is structured into several directories and key files, each serving a specific purpose:

## Project Structure

### Client-Side

- `src/main/java/com/omarkandil/exchange/`
  - `Alerts.java`: Utility class for displaying alert messages.
  - `Authentication.java`: Handles user authentication logic.
  - `Main.java`: Entry point of the application.
  - `OnPageCompleteListener.java`: Interface for page completion callbacks.
  - `PageCompleter.java`: Interface implemented by controllers for navigation.
  - `Parent.java`: Main controller for primary stage setup and navigation.
  - `SettingsManager.java`: Manages user settings and preferences.
  - `SocketManager.java`: Handles socket connections for real-time communication.
  - `VoiceOverService.java`: Provides text-to-speech functionality for accessibility.
  - `Component/ControllerClass.java`: The controller class for the respective app pages.

### Backend Services

- `src/main/java/com/omarkandil/exchange/api/`
  - `model/`: Contains model classes for API interactions, including requests and responses.
  - `ExchangeService.java`: Service interface defining API endpoints.

### UI and Resources

- `src/main/resources/`
  - `fxml/`: FXML files defining the layout of the UI.
  - `css/`: Stylesheets for customizing the appearance of the UI.
  - `notification-styles.css`: Styles for on-screen notifications.
