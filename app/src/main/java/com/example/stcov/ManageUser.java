package com.example.stcov;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ManageUser extends AppCompatActivity {

    TextView email,firstname,lastname;
    Button delete;

    FirebaseUser user;
    StorageReference storageReference;
    String userId;
    FirebaseAuth fAuth;
    FirebaseFirestore fStor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_user);

        firstname = findViewById(R.id.firstname_user);
        lastname = findViewById(R.id.lastname_user);
        email = findViewById(R.id.email_user);
        delete = findViewById(R.id.delete);

        fAuth = FirebaseAuth.getInstance();
        fStor = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        userId = fAuth.getCurrentUser().getUid();
        user = fAuth.getCurrentUser();

        Intent data = getIntent();
        final String uid = data.getStringExtra("uid");

        DocumentReference documentReference = fStor.collection("users").document(uid);
       System.out.println("ManageUser Class");
       documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
           @Override
           public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
               if(!documentSnapshot.exists()){
                   Log.d("TAG", "onEvent: Document do not exists");
               }else {
                   Log.d("TAG", "DocumentSnapshot data: " + documentSnapshot.getData());
                   email.setText(documentSnapshot.getString("email"));
                   firstname.setText(documentSnapshot.getString("firstname"));
                   lastname.setText(documentSnapshot.getString("lastname"));
               }
           }
       });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                fStor.collection("users").document(uid).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(v.getContext(), "User Deleted", Toast.LENGTH_SHORT).show();
                        Log.d("TAG","DocumentSnapshot successfully deleted!");
                        Intent i = new Intent(v.getContext(),ShowData.class);
                        startActivity(i);
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG","Error deleting document",e);
                    }
                });

            }
        });


    }
}
