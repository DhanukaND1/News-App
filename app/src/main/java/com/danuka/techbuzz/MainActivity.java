package com.danuka.techbuzz;

import com.danuka.techbuzz.News;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.LinearLayout;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ImageView menuIcon;
    private NavigationView navigationView;
    private boolean isFilteredView = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayout sportsButton = findViewById(R.id.sports_button);
        LinearLayout academicButton = findViewById(R.id.academic_button);
        LinearLayout eventsButton = findViewById(R.id.events_button);
        LinearLayout allNewsButton = findViewById(R.id.all_news_button);

        allNewsButton.setOnClickListener(v -> {
            fetchAndDisplayNews();
            allNewsButton.setVisibility(View.GONE);
            isFilteredView = false;
        });

        sportsButton.setOnClickListener(v -> {
            fetchSportsNews();
            isFilteredView = true;
            allNewsButton.setVisibility(View.VISIBLE);
        });

        academicButton.setOnClickListener(v -> {
            fetchAcademicNews();
            isFilteredView = true;
            allNewsButton.setVisibility(View.VISIBLE);
        });


        eventsButton.setOnClickListener(v -> {
            fetchEventsNews();
            isFilteredView = true;
            allNewsButton.setVisibility(View.VISIBLE);
        });

        drawerLayout = findViewById(R.id.drawer_layout);
        menuIcon = findViewById(R.id.menuIcon);
        navigationView = findViewById(R.id.navigation_view);

        menuIcon.setOnClickListener(view -> drawerLayout.openDrawer(GravityCompat.START));

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_user_info) {
                // startActivity(new Intent(MainActivity.this, UserInfoActivity.class));
            } else if (id == R.id.nav_developer_info) {
                // startActivity(new Intent(MainActivity.this, DeveloperInfoActivity.class));
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        fetchAndDisplayNews();
    }

    @Override
    public void onBackPressed() {
        if (isFilteredView) {
            fetchAndDisplayNews();
            isFilteredView = false;
            findViewById(R.id.all_news_button).setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }

    // Method to parse date string to Date object
    private Date parseDate(String dateString) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH);
        try {
            // Normalize month casing by forcing lowercase and then capitalize first letter
            String normalizedDate = normalizeDateString(dateString);
            return sdf.parse(normalizedDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Normalize the date string (e.g. "04 may 2025" -> "04 May 2025")
    private String normalizeDateString(String dateString) {
        if (dateString == null) return "";
        String[] parts = dateString.trim().split(" ");
        if (parts.length != 3) return dateString;
        String day = parts[0];
        String month = parts[1].substring(0,1).toUpperCase() + parts[1].substring(1).toLowerCase();
        String year = parts[2];
        return day + " " + month + " " + year;
    }

    // Fetch all news items from Firebase, filter, sort, and populate UI cards
    private void fetchAndDisplayNews() {
        DatabaseReference newsRef = FirebaseDatabase.getInstance().getReference("news");

        newsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<News> allNews = new ArrayList<>();
                Date today = new Date();

                for (DataSnapshot categorySnapshot : snapshot.getChildren()) { // sports, acedamic, events
                    for (DataSnapshot newsSnapshot : categorySnapshot.getChildren()) { // id1, id2, ...
                        News item = newsSnapshot.getValue(News.class);
                        if (item != null) {
                            Date newsDate = parseDate(item.date);
                            if (newsDate != null && !newsDate.after(today)) { // newsDate <= today
                                allNews.add(item);
                            }
                        }
                    }
                }

                // Sort descending by date
                Collections.sort(allNews, (a, b) -> {
                    Date dateA = parseDate(a.date);
                    Date dateB = parseDate(b.date);
                    if (dateA == null || dateB == null) return 0;
                    return dateB.compareTo(dateA); // Descending order (latest first)
                });

                populateNewsCards(allNews);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle Firebase errors here if needed
            }
        });
    }

    // Populate up to 6 news cards dynamically
    private void populateNewsCards(List<News> newsList) {
        int maxCards = 6;

        // Hide all cards
        for (int i = 1; i <= maxCards; i++) {
            int cardId = getResources().getIdentifier("newsCard" + i, "id", getPackageName());
            View card = findViewById(cardId);
            if (card != null) {
                card.setVisibility(View.GONE);
            }
        }

        // Populate and show available news
        for (int i = 0; i < Math.min(newsList.size(), maxCards); i++) {
            int cardId = getResources().getIdentifier("newsCard" + (i + 1), "id", getPackageName());
            View card = findViewById(cardId);
            if (card != null) {
                News item = newsList.get(i);

                TextView titleText = card.findViewById(R.id.newsTitle);
                TextView dateText = card.findViewById(R.id.newsDate);
                ImageView imageView = card.findViewById(R.id.newsImage);
                TextView descriptionText = card.findViewById(R.id.newsDescription);
                TextView readMoreText = card.findViewById(R.id.newsReadMore);

                titleText.setText(item.title);
                dateText.setText(item.date);
                descriptionText.setText(item.description);
                descriptionText.setVisibility(View.GONE);
                readMoreText.setText("Read More ▼");

                Glide.with(this).load(item.image).into(imageView);

                readMoreText.setOnClickListener(v -> {
                    if (descriptionText.getVisibility() == View.GONE) {
                        descriptionText.setVisibility(View.VISIBLE);
                        readMoreText.setText("Show Less ▲");
                    } else {
                        descriptionText.setVisibility(View.GONE);
                        readMoreText.setText("Read More ▼");
                    }
                });

                card.setVisibility(View.VISIBLE);
            }
        }
    }
    private void fetchSportsNews() {
        DatabaseReference sportsRef = FirebaseDatabase.getInstance().getReference("news/sports");

        sportsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<News> sportsNews = new ArrayList<>();
                Date today = new Date();

                for (DataSnapshot newsSnapshot : snapshot.getChildren()) {
                    News item = newsSnapshot.getValue(News.class);
                    if (item != null) {
                        Date newsDate = parseDate(item.date);
                        if (newsDate != null && !newsDate.after(today)) {
                            sportsNews.add(item);
                        }
                    }
                }

                // Sort by date (latest first)
                Collections.sort(sportsNews, (a, b) -> {
                    Date dateA = parseDate(a.date);
                    Date dateB = parseDate(b.date);
                    if (dateA == null || dateB == null) return 0;
                    return dateB.compareTo(dateA);
                });

                populateNewsCards(sportsNews);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Error fetching sports news: " + error.getMessage());
            }
        });
    }

    private void fetchAcademicNews() {
        DatabaseReference academicRef = FirebaseDatabase.getInstance().getReference("news/acedamic");

        academicRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<News> academicNews = new ArrayList<>();
                Date today = new Date();

                for (DataSnapshot newsSnapshot : snapshot.getChildren()) {
                    News item = newsSnapshot.getValue(News.class);
                    if (item != null) {
                        Date newsDate = parseDate(item.date);
                        if (newsDate != null && !newsDate.after(today)) {
                            academicNews.add(item);
                        }
                    }
                }

                Collections.sort(academicNews, (a, b) -> {
                    Date dateA = parseDate(a.date);
                    Date dateB = parseDate(b.date);
                    if (dateA == null || dateB == null) return 0;
                    return dateB.compareTo(dateA);
                });

                populateNewsCards(academicNews);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Error fetching academic news: " + error.getMessage());
            }
        });
    }

    private void fetchEventsNews() {
        DatabaseReference eventsRef = FirebaseDatabase.getInstance().getReference("news/events");

        eventsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<News> eventsNews = new ArrayList<>();
                Date today = new Date();

                for (DataSnapshot newsSnapshot : snapshot.getChildren()) {
                    News item = newsSnapshot.getValue(News.class);
                    if (item != null) {
                        Date newsDate = parseDate(item.date);
                        if (newsDate != null && !newsDate.after(today)) {
                            eventsNews.add(item);
                        }
                    }
                }

                Collections.sort(eventsNews, (a, b) -> {
                    Date dateA = parseDate(a.date);
                    Date dateB = parseDate(b.date);
                    if (dateA == null || dateB == null) return 0;
                    return dateB.compareTo(dateA);
                });

                populateNewsCards(eventsNews);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Error fetching events news: " + error.getMessage());
            }
        });
    }



}
