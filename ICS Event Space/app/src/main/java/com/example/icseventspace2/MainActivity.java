package com.example.icseventspace2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AlertDialog;
import android.content.DialogInterface;
import java.text.DateFormatSymbols;
import java.time.LocalDate;
import java.time.Month;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    RecyclerView recyclerView;
    RecyclerAdapter adapter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayList<ArrayList<String>> posts_contents = new ArrayList<>();
    private int logo_clicks = 0;
    public ImageView add_btn, app_logo, hamb_menu_button;
    private DateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy | h:mm a");
    private DateFormat monthDateFormat = new SimpleDateFormat("MMMM");
    private DateFormat yearDateFormat = new SimpleDateFormat("yyyy");
    private EditText post_title_field, post_content_field;
    public TextView view_post_title, view_post_date, view_post_content, latest_post_title, latest_post_date,
            latest_post_content, add_post_title, post_id, TV_profile_name;
    public ConstraintLayout view_post_layout, add_post_layout, hamb_menu_layout, latest_post_layout, progress_bar_layout;
    public LinearLayout editPostButtons_layout;
    public AppCompatButton post_button;
    private String id, update_title, update_content, current_user, logged_in, filter, formatted_currMonth;

    private Intent intent;

    private DateFormatSymbols dfs = new DateFormatSymbols();
    private LocalDate currentdate = LocalDate.now();
    private Month currentMonth = currentdate.getMonth();
    private Year currentYear = Year.of(currentdate.getYear());
    private Year previousYear = currentYear.minusYears(1);
    private boolean valid_year;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        intent = getIntent();
        logged_in = intent.getStringExtra("logged_in");
        current_user = intent.getStringExtra("current_user");

        progress_bar_layout = findViewById(R.id.progress_bar_layout);

        add_btn = findViewById(R.id.add_btn);
        add_post_layout = findViewById(R.id.add_post);

        post_title_field = findViewById(R.id.post_title_field);
        post_content_field = findViewById(R.id.post_content_field);

        view_post_title = findViewById(R.id.view_post_title);
        view_post_date = findViewById(R.id.view_post_date);
        view_post_content = findViewById(R.id.view_post_content);

        view_post_layout = findViewById(R.id.view_post_layout);

        latest_post_title = findViewById(R.id.latest_post_title);
        latest_post_date = findViewById(R.id.latest_post_date);
        latest_post_content = findViewById(R.id.latest_post_content);

//        for edit post
        editPostButtons_layout = findViewById(R.id.edit_post_buttons); // to visible
        add_post_title = findViewById(R.id.add_post_title); // to EDIT POST
        post_button = findViewById(R.id.add_post_button); // to gone

        // hamb menu
        // hamb menu
        hamb_menu_layout = findViewById(R.id.hamb_menu_layout);
        app_logo = findViewById(R.id.logo);
        hamb_menu_button = findViewById(R.id.hamb_menu_icon);

        latest_post_layout = findViewById(R.id.latest_post_layout);

        post_id = findViewById(R.id.post_id);

//        populate_posts_contents();

        TV_profile_name = findViewById(R.id.profile_name);

        // for spinner
        Spinner spinner = findViewById(R.id.posts_filter_spinner);
        ArrayAdapter<CharSequence> adapterr = ArrayAdapter.createFromResource(this, R.array.posts_filter, R.layout.color_spinner_layout);
        adapterr.setDropDownViewResource(R.layout.spinner_dropdown_layout);
        spinner.setAdapter(adapterr);
        spinner.setDropDownVerticalOffset(60);
        spinner.setOnItemSelectedListener(this);

        post_content_field.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    post_content_field.setGravity(Gravity.LEFT);
                    post_content_field.setGravity(Gravity.LEFT);
                } else {
                    post_content_field.setGravity(Gravity.CENTER);
                }
            }
        });
        if (logged_in == null || logged_in.equals("false")) {
            current_user = "";
            logged_in = "false";
        }
        else {
            int lastDashIndex = current_user.lastIndexOf("-");
            String profile_name = current_user.substring(0, lastDashIndex) + "\n"+ current_user.substring(lastDashIndex + 1);

            hamb_menu_button.setVisibility(View.VISIBLE);
            add_btn.setVisibility(View.VISIBLE);
            TV_profile_name.setText(profile_name);
        }
    }

    public void open_login_page(View view) {
        logo_clicks += 1;
        if (logo_clicks == 7 & current_user.equals("")) {
            logo_clicks = 0;
            Toast.makeText(getApplicationContext(), "Opening Login Page", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this, LoginPage.class));
        }
    }

    public void add_announcement(View view) {
        post_title_field.setText("");
        post_content_field.setText("");
        add_post_layout.setVisibility(View.VISIBLE);
    }

    private void populate_posts_contents() {
        try {
            db.collection("Posts").orderBy("timestamp", Query.Direction.DESCENDING)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {

                                    Timestamp timestamp = document.getTimestamp("timestamp");
                                    Date date = timestamp.toDate();

                                    ArrayList<String> post = new ArrayList<>();
                                    post.add(document.getString("title"));
                                    post.add(dateFormat.format(date));
                                    post.add(document.getString("content"));
                                    post.add(document.getId());

                                    valid_year = yearDateFormat.format(date).equals(currentYear.toString()) || yearDateFormat.format(date).equals(previousYear.toString());

                                    if (filter.equals("This Month") && monthDateFormat.format(date).equals(capitalize(currentMonth.toString().toLowerCase())) && valid_year) {
                                        posts_contents.add(post);
                                    }
                                    else if ((filter.equals("All Posts"))) {
                                        posts_contents.add(post);
                                    }
                                    else if ((filter.equals("Last 3 Months") && getLastThreeMonths(capitalize(currentMonth.toString().toLowerCase())).contains(monthDateFormat.format(date)))
                                    && valid_year) {
                                        posts_contents.add(post);
                                    }
                                    else if ((filter.equals("Last 9 Months") && getLastNineMonths(capitalize(currentMonth.toString().toLowerCase())).contains(monthDateFormat.format(date)))
                                    && valid_year) {
                                        posts_contents.add(post);
                                    }

                                }

                                recyclerView = findViewById(R.id.recycler_view);
                                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                adapter = new RecyclerAdapter(getApplicationContext(), posts_contents, view_post_layout, view_post_title, view_post_date, view_post_content,
                                        editPostButtons_layout, add_post_title, post_title_field, post_content_field, add_post_layout, post_button, recyclerView, app_logo,
                                        hamb_menu_button, add_btn, latest_post_layout, post_id, logged_in);
                                recyclerView.setAdapter(adapter);

                                try {
                                    latest_post_title.setText(posts_contents.get(0).get(0));
                                    latest_post_date.setText(posts_contents.get(0).get(1));
                                    latest_post_content.setText(posts_contents.get(0).get(2));
                                } catch (Exception e) {
                                }
                            }
                        }
                    });
        } catch (Exception e) {
        }

    }

    public void close_add_post(View view) {
        add_post_layout.setVisibility(View.GONE);
        recreate();
    }

    public void add_post(View view) {

        String post_title = post_title_field.getText().toString().trim();
        String post_content = post_content_field.getText().toString().replaceAll("\\s+$", "");

        if (post_title.trim().isEmpty() || post_content.trim().isEmpty()) {
            Toast.makeText(this, "Please fill in all the fields", Toast.LENGTH_SHORT).show();
        } else if (post_title.trim().length() > 50) {
            Toast.makeText(this, "Please limit your title to 50 characters only", Toast.LENGTH_SHORT).show();
        } else {
            // Create a new user with a first and last name
            Map<String, Object> post = new HashMap<>();
            post.put("title", post_title);
            post.put("content", post_content);
            post.put("timestamp", FieldValue.serverTimestamp());
            post.put("posted_by", current_user);

            //Add a new document with a generated ID
            db.collection("Posts").document().set(post)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(MainActivity.this, "Post added", Toast.LENGTH_SHORT).show();
                            post_title_field.setText("");
                            post_content_field.setText("");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, "Post not added", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    public void close_post_view(View view) {
        view_post_layout.setVisibility(View.GONE);
//        enable_views();
    }

    public void view_latest_post(View view) {

        if (logged_in.equals("false")) {
            try {
                view_post_layout.setVisibility(View.VISIBLE);
                view_post_title.setText(posts_contents.get(0).get(0));
                view_post_date.setText(posts_contents.get(0).get(1));
                view_post_content.setText(posts_contents.get(0).get(2));
                post_id.setText(posts_contents.get(0).get(3));
            } catch (Exception e) {
                view_post_layout.setVisibility(View.GONE);
            }
        }
        else {
            edit_latest_post();
        }
    }

    private void edit_latest_post() {
        try {
            add_post_layout.setVisibility(View.VISIBLE);
            add_post_title.setText("EDIT POST");
            post_title_field.setText(posts_contents.get(0).get(0));
            post_content_field.setText(posts_contents.get(0).get(2));
            editPostButtons_layout.setVisibility(View.VISIBLE);
            post_button.setVisibility(View.GONE);
            post_id.setText(posts_contents.get(0).get(3));
        } catch (Exception e) {
            add_post_layout.setVisibility(View.GONE);
        }
    }

    public void open_hamb_menu(View view) {
//        disable_views();
        hamb_menu_layout.setVisibility(View.VISIBLE);
    }
    public void clost_hamb_menu(View view) {
        hamb_menu_layout.setVisibility(View.GONE);
//        enable_views();
    }
    public void log_out(View view) {

        AlertDialog.Builder myAlertBuilder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.MyAlertDialogTheme));
        myAlertBuilder.setTitle("Log out?");

        myAlertBuilder.setPositiveButton("Log out", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                intent.removeExtra("current_user");
                intent.removeExtra("logged_in");
                recreate();
            }
        });

        myAlertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(getApplicationContext(), "Log out canceled", Toast.LENGTH_SHORT).show();
            }
        });
        myAlertBuilder.show();
    }
    public void update_post(View view) {
        id = post_id.getText().toString();
        update_title = post_title_field.getText().toString().trim();
        update_content = post_content_field.getText().toString().replaceAll("\\s+$", "");
        Map<String, Object> data = new HashMap<>();
        data.put("title", update_title);
        data.put("content", update_content);

        if (update_title.trim().isEmpty() || update_content.trim().isEmpty()) {
            Toast.makeText(this, "Please fill in all the fields", Toast.LENGTH_SHORT).show();
        } else {
            db.collection("Posts").document(id).update(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(MainActivity.this, "Updated successfully", Toast.LENGTH_SHORT).show();
                    post_title_field.setText("");
                    post_content_field.setText("");
                    recreate();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity.this, "Update unsuccessful", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void delete_post(View view) {
        id = post_id.getText().toString();
        update_title = post_title_field.getText().toString().trim();
        update_content = post_content_field.getText().toString().replaceAll("\\s+$", "");

        AlertDialog.Builder myAlertBuilder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.MyAlertDialogTheme));
        myAlertBuilder.setTitle("Delete Post?");

        if (update_title.trim().isEmpty() || update_content.trim().isEmpty()) {
        } else {
            myAlertBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    db.collection("Posts").document(id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(MainActivity.this, "Deleted successfully", Toast.LENGTH_SHORT).show();
                            recreate();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, "Deletion unsuccessful", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

            myAlertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Toast.makeText(getApplicationContext(), "Deletion Canceled", Toast.LENGTH_SHORT).show();
                }
            });
            myAlertBuilder.show();
        }
    }
    public void open_change_pass(View view) {
        Intent intent = new Intent(MainActivity.this, LoginPage.class);
        intent.putExtra("change_pass", "true");
        intent.putExtra("current_user", current_user);
        startActivity(intent);
    }
    public void open_contactUs(View view) {
        startActivity(new Intent(MainActivity.this, ContactUsPage.class));
    }
    public void nothing(View view) {
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String selected_filter = adapterView.getItemAtPosition(i).toString();

        if (selected_filter.equals("Posts from this month")) {
            filter = "This Month";
            posts_contents.clear();
            populate_posts_contents();
        }
        else if (selected_filter.equals(("Posts from last 3 months"))) {
           filter = "Last 3 Months";
           posts_contents.clear();
           populate_posts_contents();
        }
        else if (selected_filter.equals(("Posts from last 9 months"))){
            filter = "Last 9 Months";
            posts_contents.clear();
            populate_posts_contents();
        }
        else if (selected_filter.equals("All Posts")) {
            filter = "All Posts";
            posts_contents.clear();
            populate_posts_contents();
        }
        Toast.makeText(this, "All posts from " +filter, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    public static List<String> getLastThreeMonths(String currentMonth) {
        Month month = Month.valueOf(currentMonth.toUpperCase());

        List<String> lastThreeMonths = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            lastThreeMonths.add(month.getDisplayName(TextStyle.FULL, Locale.ENGLISH));
            month = month.minus(1);
        }

        return lastThreeMonths;
    }

    public static List<String> getLastNineMonths(String currentMonth) {
        Month month = Month.valueOf(currentMonth.toUpperCase());
        List<String> lastNineMonths = new ArrayList<>();

        for (int i = 0; i < 9; i++) {
            lastNineMonths.add(month.getDisplayName(TextStyle.FULL, Locale.ENGLISH));
            month = month.minus(1);
        }

        return lastNineMonths;
    }
    public static String capitalize(String inputString) {

        char firstLetter = inputString.charAt(0);
        char capitalFirstLetter = Character.toUpperCase(firstLetter);
        return inputString.replace(inputString.charAt(0), capitalFirstLetter);
    }

}
