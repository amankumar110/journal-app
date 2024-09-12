package in.amankumar110.journalapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.TimeUnit;

import in.amankumar110.journalapp.databinding.ActivitySignupBinding;
import in.amankumar110.journalapp.enums.ErrorMessages;
import in.amankumar110.journalapp.models.User;

public class SignupActivity extends AppCompatActivity {

    private ActivitySignupBinding binding;
    private FirebaseAuth auth;
    private WorkManager workManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        auth = FirebaseAuth.getInstance();
        workManager = WorkManager.getInstance(this);

        setContentView(binding.getRoot());

        setupSystemBar();

        binding.btnCreateAccount.setOnClickListener(v -> {
            String email = binding.etEmail.getText().toString();
            String password = binding.etPassword.getText().toString();
            String username = binding.etUsername.getText().toString();

            if (TextUtils.isEmpty(email)) {
                showToast("Email Cannot Be Empty!");
                return;
            }

            if (TextUtils.isEmpty(password)) {
                showToast("Password Must Contain 6-8 Letters!");
                return;
            }

            if (TextUtils.isEmpty(username)) {
                showToast("Username Cannot Be Empty!");
                return;
            }

            User user = new User(username);
            signup(email, password, user);
        });
    }

    private void setupSystemBar() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void signup(String email, String password, User user) {

        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener(authResult -> {
                        FirebaseUser firebaseUser = authResult.getUser();
                        if (firebaseUser != null) {
                            // Set display name
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(user.getUsername())
                                    .build();

                            firebaseUser.updateProfile(profileUpdates)
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            Log.d("SignupActivity", "User profile updated.");
                                            createUserAccount(user.getUsername());
                                            // Navigate to the JournalsActivity after updating the profile
                                            startActivity(new Intent(this, JournalsActivity.class));
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(this::handleSignupFailure);

        }
    }

    private void handleSignupFailure(Exception e) {
        if (e instanceof FirebaseAuthInvalidCredentialsException) {
            showToast(ErrorMessages.INCORRECT_EMAIL_OR_PASSWORD.toString());
        } else if (e instanceof FirebaseAuthUserCollisionException) {
            showToast(ErrorMessages.EMAIL_ALREADY_IN_USE.toString());
        } else if (e instanceof FirebaseAuthInvalidUserException) {
            showToast(ErrorMessages.EMAIL_ALREADY_IN_USE.toString());
        } else {
            showToast(ErrorMessages.SIGNUP_ERORR.toString());
        }
    }

    private void createUserAccount(String username) {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        Data inputData = new Data.Builder()
                .putString("username", username)
                .build();

        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(SignUpWorker.class)
                .setConstraints(constraints)
                .setInputData(inputData)
                .setBackoffCriteria(BackoffPolicy.LINEAR,
                        WorkRequest.MIN_BACKOFF_MILLIS,
                        TimeUnit.MILLISECONDS)
                .build();

        workManager.enqueue(workRequest);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public static class SignUpWorker extends Worker {

        public SignUpWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
            super(context, workerParams);
        }

        @NonNull
        @Override
        public Result doWork() {
            String username = getInputData().getString("username");

            if (username == null || username.isEmpty()) {
                return Result.failure();
            }

            FirebaseFirestore database = FirebaseFirestore.getInstance();
            CollectionReference users = database.collection("users");
            User user = new User(username);

            try {
                // Using Tasks.await to make the call synchronous
                Tasks.await(users.add(user));
                Log.d("SignUpWorker", "User added successfully");
                return Result.success();
            } catch (Exception e) {
                Log.e("SignUpWorker", "Exception adding user", e);
                return Result.retry();
            }
        }
    }
}
