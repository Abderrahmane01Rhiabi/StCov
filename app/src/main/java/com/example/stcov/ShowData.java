package com.example.stcov;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class ShowData extends AppCompatActivity {


    FirebaseFirestore firebaseFirestore;
    RecyclerView mFirestorelist;
    FirestoreRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_data);

        firebaseFirestore = FirebaseFirestore.getInstance();
        mFirestorelist = findViewById(R.id.recyclerView);

        //Query
        Query query = firebaseFirestore.collection("users").whereEqualTo("role","user");

        firebaseFirestore.collection("users").whereEqualTo("role","user").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("TAG", document.getId() + " => " + document.getData());
                    }
                } else {
                    Log.d("TAG", "Error getting documents: ", task.getException());
                }
            }
        });

        FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .build();

         adapter = new FirestoreRecyclerAdapter<User, UserViewHolder>(options) {
            @NonNull
            @Override
            public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_single,parent,false);
                return new UserViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull User model) {
                holder.lastname.setText(model.getLastname());
                holder.firstname.setText(model.getFirstname());
                holder.email.setText(model.getEmail());
            }
        };
         mFirestorelist.setHasFixedSize(true);
         mFirestorelist.setLayoutManager(new LinearLayoutManager(this));
         mFirestorelist.setAdapter(adapter);
    }

    private class UserViewHolder extends RecyclerView.ViewHolder{
        TextView firstname,lastname,email;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            firstname = itemView.findViewById(R.id.prenom);
            lastname = itemView.findViewById(R.id.nom);
            email = itemView.findViewById(R.id.email);

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }
}
