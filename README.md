# Weather-Reporting

### Objective ###
The weather reporter is a Java console application that does the following:
1. Fetches the top 50 cities' weather data from the AccuWeather API.
2. For all those 50 cities, it fetches the current weather data.
3. Combines the two datasets to extract the relevant data fields of the top 50 cities.
4. Creates a Google Excel sheet with the following columns:
    - Name
    - Country
    - Region
    - Timezone
    - Rank
    - Latitude
    - Longitude
    - Weather Text
    - Is Day Time
    - Temperature Celsius (C)
    - Temperature Fahrenheit (F)
    - Last Updated At
5. Creates a Google Excel sheet with the final report data.
6. Uploads the Excel sheet to Google Drive and generates a public link.
7. Sends the Excel sheet as attachment along with the public link to the email addresses configured.

**Note:** The application is configured to run as a cron job every week.

### Prerequisites ###
1. Register an app with AccuWeather at https://developer.accuweather.com and get the API key.
2. Create a Google Cloud project and enable the Google Sheets API and Google Drive API.
3. Create a service account and download the JSON key file.

### Steps to Run the Application ###
1. Clone the repository.
   ```bash
   git clone git@github.com:pratts/weather-reporter.git
   ```
2. Navigate to the project directory.
   ```bash
   cd weather-reporter
   ```
3. Rename the `.env.example` to `.env.sh` file in the root directory and update the values with your own API keys and credentials.
4. Make the `run.sh` file executable.
   ```bash
   chmod +x run.sh
   ```
5. Run the application.
   ```bash
   ./run.sh
   ```
