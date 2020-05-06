package com.example.stcov;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class Settings extends AppCompatActivity {
    EditText firstname,lastname,email;
    Button save,editPsw;
    ImageView profileImg;
    FirebaseAuth fAuth;
    FirebaseFirestore fStor;
    FirebaseUser user;
    String userId;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        firstname = findViewById(R.id.firstname);
        lastname = findViewById(R.id.lastname);
        email = findViewById(R.id.email);
        editPsw = findViewById(R.id.editPassword);
        profileImg = findViewById(R.id.profileImg);
        save = findViewById(R.id.save);

        fAuth = FirebaseAuth.getInstance();
        fStor = FirebaseFirestore.getInstance();
        user =  fAuth.getCurrentUser();
        userId = fAuth.getCurrentUser().getUid();
        storageReference = FirebaseStorage.getInstance().getReference();


        Intent data = getIntent();
        String nom = data.getStringExtra("nom");
        String prenom = data.getStringExtra("prenom");
        String m  = data.getStringExtra("mail");

        Log.d("TAG","onCreate " +nom+" "+prenom);

        email.setText(m);
        firstname.setText(prenom);
        lastname.setText(nom);

        Log.d("TAG","onCreate " +firstname.getText().toString()+" "+lastname.getText().toString());


        profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGallery,1000);
            }
        });

        StorageReference profileRef = storageReference.child("users/"+userId+"/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profileImg);
            }
        });


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(firstname.getText().toString().isEmpty() || lastname.getText().toString().isEmpty()){
                    Toast.makeText(Settings.this,"One Or More Fileds Are Empty",Toast.LENGTH_SHORT).show();
                    return;
                }
                final String mail = email.getText().toString();
                user.updateEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        DocumentReference docRef = fStor.collection("users").document(user.getUid());
                        Map<String,Object> edited = new HashMap<>();
                        edited.put("email",mail);
                        edited.put("firstname",firstname.getText().toString());
                        edited.put("lastname",lastname.getText().toString());
                        docRef.update(edited).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(Settings.this,"Profile Updated",Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(),Profile.class));
                                finish();
                            }
                        });
                        Toast.makeText(Settings.this,"Email is Changed",Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Settings.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        editPsw.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {

                final EditText resetMail = new EditText(v.getContext());
                final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
                passwordResetDialog.setTitle("Reset Password ?");
                passwordResetDialog.setMessage("Enter Your Email To Received Reset Link.");
                passwordResetDialog.setView(resetMail);

                passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // extract the email and send reset link
                        String mail = resetMail.getText().toString();
                        fAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(Settings.this, "Reset Link Sent To Your Email.", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Settings.this, "Error ! Reset Link is Not Sent" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });
                passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // close the dialog
                    }
                });

                passwordResetDialog.create().show();

            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            if (resultCode == Activity.RESULT_OK) {
                Uri imageUri = data.getData();
                //profileImage.setImageURI(imageUri);

                uploadImageToFirebase(imageUri);
            }
        }
    }



    private void uploadImageToFirebase(Uri imageUri) {
        final StorageReference fileRef = storageReference.child("users/"+userId+"/profile.jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                System.out.println("entre1");
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(profileImg);
                        Toast.makeText(Settings.this,"Successe",Toast.LENGTH_SHORT).show();
                        System.out.println("sortie1");
                    }
                });
                System.out.println("sortie2");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Settings.this,"Failed",Toast.LENGTH_SHORT).show();
            }
        });
    }

}
