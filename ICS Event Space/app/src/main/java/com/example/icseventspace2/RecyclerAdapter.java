package com.example.icseventspace2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    ArrayList<ArrayList<String>> posts_contents;
    Context context;
    ConstraintLayout view_post_layout, add_post_layout, latest_post_layout;
    TextView view_post_title, view_post_content, view_post_date, add_post_title, post_id;
    LinearLayout editPostButtons_layout;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    EditText post_title_field, post_content_field;
    AppCompatButton post_button;
    String logged_in = "false";
    RecyclerView recyclerView;

    ImageView app_logo, hamb_menu_button, add_btn;


    public RecyclerAdapter(Context context, ArrayList<ArrayList<String>> posts_contents, ConstraintLayout view_post_layout,
                           TextView view_post_title, TextView view_post_date, TextView view_post_content, LinearLayout editPostButtons_layout,
                           TextView add_post_title, EditText post_title_field, EditText post_content_field, ConstraintLayout add_post_layout,
                           AppCompatButton post_button, RecyclerView recyclerView, ImageView app_logo, ImageView hamb_menu_button, ImageView add_btn,
                           ConstraintLayout latest_post_layout, TextView post_id, String logged_in) {
        this.posts_contents = posts_contents;
        this.context = context;
        this.view_post_layout = view_post_layout;
        this.view_post_title = view_post_title;
        this.view_post_date = view_post_date;
        this.view_post_content = view_post_content;
        this.editPostButtons_layout = editPostButtons_layout;
        this.add_post_title = add_post_title;
        this.post_title_field = post_title_field;
        this.post_content_field = post_content_field;
        this.add_post_layout = add_post_layout;
        this.post_button = post_button;
        this.recyclerView = recyclerView;
        this.app_logo = app_logo;
        this.hamb_menu_button = hamb_menu_button;
        this.add_btn = add_btn;
        this. latest_post_layout = latest_post_layout;
        this.post_id = post_id;
        this.logged_in = logged_in;

    }

    @NonNull
    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.post_design, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        try {
            String title = posts_contents.get(position).get(0);
            String date = posts_contents.get(position).get(1);
            String content = posts_contents.get(position).get(2);
            String id = posts_contents.get(position).get(3);

            holder.post_title.setText(title);
            holder.post_date.setText(date);
            holder.post_content.setText(content.trim());


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    post_id.setText(id);

                    if (logged_in.equals("true")) {
                        add_post_layout.setVisibility(View.VISIBLE);
                        editPostButtons_layout.setVisibility(View.VISIBLE);
                        add_post_title.setText("EDIT POST");
                        post_title_field.setText(title);
                        post_content_field.setText(content);
                        post_button.setVisibility(View.GONE);
                    }
                    else {
                        view_post_layout.setVisibility(View.VISIBLE);
                        view_post_title.setText(title);
                        view_post_date.setText(date);
                        view_post_content.setText(content);
                    }
                }
            });
        } catch (Exception e) {}


    }

    @Override
    public int getItemCount() {
        return posts_contents.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView post_title, post_date, post_content;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            post_title = itemView.findViewById(R.id.post_title);
            post_date = itemView.findViewById(R.id.post_date);
            post_content = itemView.findViewById(R.id.post_content);
        }
    }
}
