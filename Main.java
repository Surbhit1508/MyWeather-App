import org.json.JSONArray;
import org.json.JSONObject;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.swing.*;

// Define constants for temperature unit conversion
//private static final double KELVIN_TO_CELSIUS = 273.15;

public class CombinedApp extends JFrame {
    private JLabel cityLabel, temperatureLabel, conditionsLabel, unitLabel;
    private JTextField cityTextField;
    private JButton getWeatherButton;
    private JTextArea forecastTextArea;
    private JComboBox<String> temperatureUnitComboBox;
    private JLabel sunriseLabel, sunsetLabel, humidityLabel, windLabel;

    public CombinedApp() {
        setTitle("MyWeather App");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(9, 2));

        // Initialize UI components
        cityLabel = new JLabel("Enter City:");
        cityTextField = new JTextField();
        temperatureLabel = new JLabel("");
        conditionsLabel = new JLabel("");
        getWeatherButton = new JButton("Get Weather");
        forecastTextArea = new JTextArea(5, 20);
        unitLabel = new JLabel("Temperature Unit:");
        sunriseLabel = new JLabel("");
        sunsetLabel = new JLabel("");
        humidityLabel = new JLabel("");
        windLabel = new JLabel("");

        JScrollPane scrollPane = new JScrollPane(forecastTextArea);
        forecastTextArea.setEditable(false);

        // Dropdown for selecting temperature units
        String[] temperatureUnits = {"Celsius (°C)", "Fahrenheit (°F)", "Kelvin (K)"};
        temperatureUnitComboBox = new JComboBox<>(temperatureUnits);

        // Action listener for the "Get Weather" button
        getWeatherButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onGetWeatherClick();
            }
        });

        // Add UI components to the panel
        panel.add(cityLabel);
        panel.add(cityTextField);
        panel.add(temperatureLabel);
        panel.add(conditionsLabel);
        panel.add(unitLabel);
        panel.add(temperatureUnitComboBox);
        panel.add(sunriseLabel);
        panel.add(sunsetLabel);
        panel.add(humidityLabel);
        panel.add(windLabel);
        panel.add(new JLabel("5-Day Forecast:"));
        panel.add(scrollPane);
        panel.add(getWeatherButton);

        // Feedback section
        JLabel feedbackLabel = new JLabel("Please provide your feedback:");
        JTextArea feedbackTextArea = new JTextArea(5, 20);
        JScrollPane feedbackScrollPane = new JScrollPane(feedbackTextArea);
        JButton submitFeedbackButton = new JButton("Submit Feedback");

        // Action listener for the "Submit Feedback" button
        submitFeedbackButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String feedback = feedbackTextArea.getText();
                processFeedback(feedback);
            }
        });

        panel.add(feedbackLabel);
        panel.add(feedbackScrollPane);
        panel.add(submitFeedbackButton);

        add(panel);
    }

    // Define constants for temperature unit conversion
    private static final double KELVIN_TO_CELSIUS = 273.15;

    // Other constants
    private static final String API_URL = "http://api.openweathermap.org/data/2.5/";
    private static final String API_KEY = "YOUR_API_KEY"; // Replace with your API key from OpenWeatherMap

    private void onGetWeatherClick() {
        String city = cityTextField.getText();
        String currentWeatherUrl = API_URL + "weather?q=" + city + "&appid=" + API_KEY;
        String forecastUrl = API_URL + "forecast?q=" + city + "&appid=" + API_KEY;
    
        try {
            // Fetch current weather
            JSONObject currentWeather = fetchWeatherData(currentWeatherUrl);
            double temperature = currentWeather.getJSONObject("main").getDouble("temp") - KELVIN_TO_CELSIUS;
            String conditions = currentWeather.getJSONArray("weather").getJSONObject(0).getString("description");
    
            // Fetch 5-day forecast
            JSONArray forecastData = fetchForecastData(forecastUrl);
    
            // Update the UI with the weather data
            cityLabel.setText(bundle.getString("city") + ": " + city);
            temperatureLabel.setText(bundle.getString("temperature") + ": " + String.format("%.2f", temperature) + "°C");
            conditionsLabel.setText(bundle.getString("conditions") + ": " + conditions);
    
            // Display the 5-day forecast
            forecastTextArea.setText("");
            SimpleDateFormat dateFormat = new SimpleDateFormat(bundle.getString("date.format"));
            for (int i = 0; i < forecastData.length(); i++) {
                JSONObject forecastEntry = forecastData.getJSONObject(i);
                long timestamp = forecastEntry.getLong("dt");
                double forecastTemp = forecastEntry.getJSONObject("main").getDouble("temp") - KELVIN_TO_CELSIUS;
                String forecastConditions = forecastEntry.getJSONArray("weather").getJSONObject(0).getString("description");
    
                String forecastDate = dateFormat.format(new java.util.Date(timestamp * 1000));
    
                forecastTextArea.append(forecastDate + ": " + String.format("%.2f", forecastTemp) + "°C, " + forecastConditions + "\n");
            }
        } catch (IOException e) {
            handleNetworkError();
        } catch (Exception e) {
            handleGenericError();
        }
    }
    
    private void handleNetworkError() {
        JOptionPane.showMessageDialog(this, "Network error. Check your connection.", "Network Error", JOptionPane.ERROR_MESSAGE);
        clearWeatherData();
    }
    
    private void handleGenericError() {
        JOptionPane.showMessageDialog(this, "An error occurred.", "Error", JOptionPane.ERROR_MESSAGE);
        clearWeatherData();
    }
    
    private void clearWeatherData() {
        cityLabel.setText(bundle.getString("city") + ": ");
        temperatureLabel.setText(bundle.getString("temperature") + ": ");
        conditionsLabel.setText(bundle.getString("conditions") + ": ");
        forecastTextArea.setText("");
    }
    
    
    }

    private JSONObject fetchWeatherData(String apiUrl) throws IOException {
        // Make an HTTP GET request to the API
        HttpURLConnection connection = (HttpURLConnection) new URL(apiUrl).openConnection();
        connection.setRequestMethod("GET");

        // Read the response
        int responseCode = connection.getResponseCode();
        if (responseCode == 200) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // Parse and return the JSON response
            return new JSONObject(response.toString());
        } else {
            throw new IOException("HTTP response code: " + responseCode);
        }
    }

    private JSONArray fetchForecastData(String apiUrl) throws IOException {
        // Make an HTTP GET request to the API
        HttpURLConnection connection = (HttpURLConnection) new URL(apiUrl).openConnection();
        connection.setRequestMethod("GET");

        // Read the response
        int responseCode = connection.getResponseCode();
        if (responseCode == 200) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // Parse and return the JSON response
            JSONObject jsonResponse = new JSONObject(response.toString());
            return jsonResponse.getJSONArray("list");
        } else {
            throw new IOException("HTTP response code: " + responseCode);
        }
    }

    private void handleNetworkError() {
        cityLabel.setText("Network error. Check your connection.");
        temperatureLabel.setText("");
        conditionsLabel.setText("");
        forecastTextArea.setText("");
    }

    private void handleGenericError() {
        cityLabel.setText("An error occurred.");
        temperatureLabel.setText("");
        conditionsLabel.setText("");
        forecastTextArea.setText("");
    }

    private void processFeedback(String feedback) {
        // Process the feedback (in this example, we simply print it)
        System.out.println("Thank you for your feedback:");
        System.out.println(feedback);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new CombinedApp().setVisible(true);
            }
        });
    }
}
