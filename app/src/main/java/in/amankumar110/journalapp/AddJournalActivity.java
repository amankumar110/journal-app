package in.amankumar110.journalapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import in.amankumar110.journalapp.databinding.ActivityAddJournalBinding;
import in.amankumar110.journalapp.helper.ActionManager;
import in.amankumar110.journalapp.models.Journal;

public class AddJournalActivity extends AppCompatActivity {


    private ActivityAddJournalBinding binding;

    private FirebaseAuth auth;
    private FirebaseUser user;
    private String userId;
    private String username;
    private WorkManager workManager;

    private StorageReference storageReference;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityAddJournalBinding.inflate(getLayoutInflater());
        auth = FirebaseAuth.getInstance();
        workManager = WorkManager.getInstance(this);
        user = auth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();

        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ActionManager.setupLauncher(this, uri -> {
            this.imageUri = uri;
            binding.postImageView.setImageURI(uri);
        });



        if (user != null) {
            this.userId = user.getUid();
            this.username = user.getDisplayName();
        }


        binding.postSaveJournalButton.setOnClickListener(v -> {

            String thoughts = binding.postDescriptionEt.getText().toString();
            String title = binding.postTitleEt.getText().toString();
            Timestamp timeStamp = new Timestamp(new Date());

            Journal journal = new Journal();
            journal.setThoughts(thoughts);
            journal.setTitle(title);
            journal.setTimeAdded(timeStamp);
            journal.setUserName(username);
            journal.setUserId(userId);

            saveJournal(journal);
            startActivity(new Intent(this,JournalsActivity.class));
        });

        binding.postCameraButton.setOnClickListener(v -> ActionManager.requestImage());

    }

    private void saveJournal(Journal journal) {
        if (imageUri == null) {
            // No image selected, save journal without image URL
            saveJournalToDatabase(journal, null);
        } else {
            // Image selected, upload image first
            StorageReference filePath = storageReference.child("journal_images")
                    .child("Image_" + journal.getTimeAdded().toString());

            filePath.putFile(imageUri).addOnSuccessListener(taskSnapshot ->
                    filePath.getDownloadUrl().addOnSuccessListener(uri ->
                            saveJournalToDatabase(journal, uri.toString())
                    ).addOnFailureListener(e -> {
                        // Handle error when getting the download URL
                    })
            ).addOnFailureListener(e -> {
                Toast.makeText(this, "Image Couldn't Be Uploaded,Try Again!", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void saveJournalToDatabase(Journal journal, String imageUrl) {
        // Set imageUrl only if it's not null
        if (imageUrl != null) {
            journal.setImageUrl(imageUrl);
        } else {
            journal.setImageUrl(null);  // Explicitly setting to null for Firebase
        }

        // Convert the Journal object to JSON
        String journalJson = new Gson().toJson(journal);

        // Prepare the input data for the WorkManager
        Data inputData = new Data.Builder()
                .putString("journal", journalJson)
                .build();

        // Define constraints for the work request
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        // Create the work request
        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(AddJournalWorker.class)
                .setConstraints(constraints)
                .setInputData(inputData)
                .setBackoffCriteria(BackoffPolicy.LINEAR, WorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.MILLISECONDS)
                .build();

        // Enqueue the work request
        workManager.enqueue(workRequest);
    }


    public static class AddJournalWorker extends Worker {

        public AddJournalWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
            super(context, workerParams);
        }

        @NonNull
        @Override
        public Result doWork() {
            String journalJson = getInputData().getString("journal");
            Journal journal = new Gson().fromJson(journalJson, Journal.class);

            CollectionReference journalsReference = FirebaseFirestore.getInstance().collection("journals");
            journalsReference.add(journal)
                    .addOnSuccessListener(documentReference -> {
                        // Log success if needed
                    })
                    .addOnFailureListener(e -> {
                        // Handle failure
                    });

            return Result.success();
        }
    }

}