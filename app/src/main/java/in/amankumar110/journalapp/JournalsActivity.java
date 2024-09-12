package in.amankumar110.journalapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import in.amankumar110.journalapp.adapters.JournalsAdapter;
import in.amankumar110.journalapp.databinding.ActivityJournalsBinding;
import in.amankumar110.journalapp.models.Journal;

public class JournalsActivity extends AppCompatActivity {

    //Binding
    private ActivityJournalsBinding binding;

    // Authentication
    private FirebaseAuth auth;
    private FirebaseUser user;

    // FireStore
    private FirebaseFirestore firestore;
    private CollectionReference usersReference;

    //Storage
    private StorageReference storageReference;

    //Recyclerview
    private List<Journal> journalList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityJournalsBinding.inflate(getLayoutInflater());
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        firestore = FirebaseFirestore.getInstance();
        usersReference = firestore.collection("journals");
        journalList = new ArrayList<>();

        setContentView(binding.getRoot());
        setupSystemBars();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        showJournals();

        binding.fab.setOnClickListener(v -> {
            startActivity(new Intent(this, AddJournalActivity.class));
        });
    }


    private void setupSystemBars() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void showJournals() {
        usersReference.get().addOnSuccessListener(usersDocuments -> {

            for(QueryDocumentSnapshot journalSnapshot : usersDocuments) {
                Journal journal = journalSnapshot.toObject(Journal.class);
                journalList.add(journal);
            }

            JournalsAdapter adapter = new JournalsAdapter(journalList,this);
            binding.rvJournals.setAdapter(adapter);
            adapter.notifyDataSetChanged();

        }).addOnFailureListener(e -> Toast.makeText(this, "Something Went Wrong, Try again!", Toast.LENGTH_SHORT).show());

        JournalsAdapter adapter = new JournalsAdapter(journalList,this);
        binding.rvJournals.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.action_add) {
            startActivity(new Intent(this, AddJournalActivity.class));
        } else if(item.getItemId() == R.id.action_sign_out) {
            auth.signOut();
            startActivity(new Intent(this,MainActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }
}