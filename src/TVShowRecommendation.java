import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class TVShowRec {
    public static void main(String[] args) {
        
        System.out.println("Welcome!");
        System.out.println("Please include your preferences before recommended shows appear.");
        Scanner scanner = new Scanner(System.in);

        try {
            
            System.out.print("Enter a similar TV show: ");
            String showName = scanner.nextLine();

            
            System.out.print("Enter the minimum rating (0-10), or -1 for no rating necessary (high rating may reduce results): ");
            double minRating = scanner.nextDouble();

            
            scanner.nextLine();

            
            String[] genres = {
                "Action", "Comedy", "Drama", "Fantasy", "Mystery", "Horror", "No Preference"
            };

            
            System.out.println("Choose a genre preference (enter the corresponding number):");
            for (int i = 0; i < genres.length; i++) {
                System.out.println((i + 1) + ". " + genres[i]);
            }

            
            System.out.print("Enter the number corresponding to your favorite genre: ");
            int genreChoice = scanner.nextInt();

            
            if (genreChoice < 1 || genreChoice > genres.length) {
                System.out.println("Invalid genre choice. Using the default 'No Preference' genre.");
                genreChoice = 1;
            }

            String genrePreference = genres[genreChoice - 1];

            // TV Maze API URL for searching similar shows
            String apiUrl = "https://api.tvmaze.com/search/shows?q=" + showName;

            
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();

            if (responseCode == 200) {
                
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                reader.close();

                
                JSONArray results = new JSONArray(response.toString());

                // Display similar shows that meet the criteria
                int maxShows = 5; 
                int numShowsDisplayed = 0;

                System.out.println("Similar TV Shows:");

                for (int i = 0; i < results.length() && numShowsDisplayed < maxShows; i++) {
                    JSONObject showObject = results.getJSONObject(i).getJSONObject("show");
                    String similarShowTitle = showObject.getString("name");
                    JSONObject ratingObject = showObject.getJSONObject("rating");

                    
                    JSONArray genresArray = showObject.getJSONArray("genres");
                    boolean hasGenre = false;

                    if (genrePreference.equals("No Preference")) {
                        hasGenre = true; 
                    } else {
                        for (int j = 0; j < genresArray.length(); j++) {
                            String genre = genresArray.getString(j);
                            if (genre.equalsIgnoreCase(genrePreference)) {
                                hasGenre = true;
                                break;
                            }
                        }
                    }

                    // Check if the show meets the rating criteria or no rating is necessary
                    if ((ratingObject.isNull("average") && minRating == -1) ||
                            (!ratingObject.isNull("average") && ratingObject.getDouble("average") >= minRating)) {
                        if (hasGenre) {
                            System.out.println(similarShowTitle);
                            numShowsDisplayed++;
                        }
                    }
                }

                if (numShowsDisplayed == 0) {
                    System.out.println("No similar TV shows found.");
                }
            } else {
                System.out.println("Failed to retrieve data from the TV Maze API.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            
            scanner.close();
        }
    }
}
