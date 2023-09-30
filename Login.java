import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class EnhancedLoginApp extends Application {

    // Define the file where user data is stored
    private static final String USER_DB_FILE = "user_db.txt";

    // Create a map to store user data (username and password)
    private Map<String, String> userDatabase = new HashMap<>();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Enhanced Login Page");

        // Load user data from the file into memory
        loadUserDatabase();

        // Create a grid for the login interface
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20, 20, 20, 20));
        grid.setVgap(10);
        grid.setHgap(10);

        // Username label and input field
        Label usernameLabel = new Label("Username:");
        GridPane.setConstraints(usernameLabel, 0, 0);
        TextField usernameInput = new TextField();
        GridPane.setConstraints(usernameInput, 1, 0);

        // Password label and input field
        Label passwordLabel = new Label("Password:");
        GridPane.setConstraints(passwordLabel, 0, 1);
        PasswordField passwordInput = new PasswordField();
        GridPane.setConstraints(passwordInput, 1, 1);

        // Login and Register buttons
        Button loginButton = new Button("Login");
        GridPane.setConstraints(loginButton, 1, 2);

        Button registerButton = new Button("Register");
        GridPane.setConstraints(registerButton, 2, 2);

        // Message label to display login or registration status
        Label messageLabel = new Label();
        GridPane.setConstraints(messageLabel, 1, 3);

        // Action for the Login button
        loginButton.setOnAction(e -> {
            String username = usernameInput.getText();
            String password = passwordInput.getText();
            if (authenticate(username, password)) {
                messageLabel.setText("Login successful!");
                // Implement session management here
            } else {
                messageLabel.setText("Login failed. Please try again.");
            }
        });

        // Action for the Register button
        registerButton.setOnAction(e -> {
            String username = usernameInput.getText();
            String password = passwordInput.getText();
            if (registerUser(username, password)) {
                messageLabel.setText("Registration successful!");
            } else {
                messageLabel.setText("Registration failed. Username already exists.");
            }
        });

        // Add UI components to the grid
        grid.getChildren().addAll(usernameLabel, usernameInput, passwordLabel, passwordInput, loginButton, registerButton, messageLabel);

        // Create the application scene
        Scene scene = new Scene(grid, 400, 200);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Authenticates the user based on username and password
    private boolean authenticate(String username, String password) {
        String storedPassword = userDatabase.get(username);
        return storedPassword != null && storedPassword.equals(password);
    }

    // Registers a new user if the username doesn't already exist
    private boolean registerUser(String username, String password) {
        if (!userDatabase.containsKey(username)) {
            userDatabase.put(username, password);
            saveUserDatabase(); // Save the updated user database to the file
            return true;
        }
        return false; // Registration failed if username already exists
    }

    // Loads the user database from a file into memory
    private void loadUserDatabase() {
        try (BufferedReader reader = new BufferedReader(new FileReader(USER_DB_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    userDatabase.put(parts[0], parts[1]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Saves the user database from memory to the file
    private void saveUserDatabase() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USER_DB_FILE))) {
            for (Map.Entry<String, String> entry : userDatabase.entrySet()) {
                writer.write(entry.getKey() + ":" + entry.getValue() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
