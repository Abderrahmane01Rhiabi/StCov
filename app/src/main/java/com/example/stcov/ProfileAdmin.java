package com.example.stcov;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.squareup.picasso.Picasso;

public class ProfileAdmin extends AppCompatActivity {

    public static final String TAG = "Activity_Register";
    FirebaseAuth fAuth;
    FirebaseFirestore fStor;
    Button resendCode, settings, home,Users;
    TextView verifyMsg, firstName, lastName, email;
    FirebaseUser user;
    StorageReference storageReference;
    String userId;
    ImageView profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_admin);

        resendCode = findViewById(R.id.resendCode);
        verifyMsg = findViewById(R.id.verifyMsg);
        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        email = findViewById(R.id.Email);
        profileImage = findViewById(R.id.profileImage);
        Users = findViewById(R.id.users);

        home = findViewById(R.id.btn_home);
        settings = findViewById(R.id.btn_set);


        resendCode = findViewById(R.id.resendCode);
        verifyMsg = findViewById(R.id.verifyMsg);

        fAuth = FirebaseAuth.getInstance();
        fStor = FirebaseFirestore.getInstance();

        storageReference = FirebaseStorage.getInstance().getReference();

        userId = fAuth.getCurrentUser().getUid();
        user = fAuth.getCurrentUser();

        if (!user.isEmailVerified()) {
            verifyMsg.setVisibility(View.VISIBLE);
            resendCode.setVisibility(View.VISIBLE);

            resendCode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(v.getContext(), "Verification Email Has been Sent.", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: Email not sent " + e.getMessage());
                        }
                    });
                }
            });
        }

        System.out.println("Avant");
        DocumentReference documentReference = fStor.collection("users").document(userId);
        System.out.println("De dans");
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(documentSnapshot.exists()){
                    System.out.println("Acceder 1");
                    email.setText(documentSnapshot.getString("email"));
                    firstName.setText(documentSnapshot.getString("firstname"));
                    lastName.setText(documentSnapshot.getString("lastname"));
                    System.out.println("Acceder 2");
                }else {
                    Log.d(TAG, "onEvent: Document do not exists");
                }
            }
        });
        System.out.println("Apres");

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(),Settings.class);
                i.putExtra("prenom",firstName.getText().toString());
                i.putExtra("nom",lastName.getText().toString());
                i.putExtra("mail",email.getText().toString());
                startActivity(i);

            }
        });


        StorageReference profileRef = storageReference.child("users/"+userId+"/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profileImage);

            }
        });


    }


    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();//logout
        startActivity(new Intent(getApplicationContext(),Login.class));
        finish();

    }
}
