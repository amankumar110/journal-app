package in.amankumar110.journalapp.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.renderscript.ScriptGroup;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultCaller;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class ActionManager {

    private static ActivityResultLauncher<String> launcher;

    public static void setupLauncher(AppCompatActivity activity, ActivityResultCallback<Uri> callback) {
        launcher = activity.registerForActivityResult(new ActivityResultContracts.GetContent(),callback);
    }

    public static void requestImage() {

        launcher.launch("image/*");
    }
}
