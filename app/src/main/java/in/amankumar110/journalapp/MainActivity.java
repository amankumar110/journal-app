package in.amankumar110.journalapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import in.amankumar110.journalapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private FirebaseAuth auth;
    private final FirebaseAuth.AuthStateListener authStateListener = firebaseAuth -> {

            if(firebaseAuth.getCurrentUser() != null) {
                startActivity(new Intent(MainActivity.this, JournalsActivity.class));
            }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        auth = FirebaseAuth.getInstance();
        auth.addAuthStateListener(authStateListener);

        setContentView(binding.getRoot());

        setupSystemBar();
        binding.btnLogin.setOnClickListener(this::onLoginButtonClicked);
        binding.btnCreateAccount.setOnClickListener(v -> startActivity(new Intent(this, SignupActivity.class)));

    }

    private void setupSystemBar() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void onLoginButtonClicked(View v) {

        String email,password;

        email = binding.etEmail.getText().toString();
        password = binding.etPassword.getText().toString();

        if(email.isEmpty()) {
            Toast.makeText(this, "Email Cannot Be Empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Password Must Contain 6-8 Letters!", Toast.LENGTH_SHORT).show();
            return;
        }

        signIn(email,password);
    }


    private void signIn(String email, String password) {
        auth.signInWithEmailAndPassword(email,password).addOnSuccessListener(authResult -> {
            Toast.makeText(this, "Successful Signup", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this,JournalsActivity.class));
        });
    }
}