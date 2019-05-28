package com.example.icecream.ui.activity;

import android.Manifest;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.example.icecream.R;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import de.hdodenhof.circleimageview.CircleImageView;
import java.util.ArrayList;

import gun0912.tedbottompicker.TedBottomPicker;


public class SettingActivity extends AppCompatActivity {

  private CircleImageView profile;

  private Button confirmButton;

  private Uri selectedUri;
  private ViewGroup mSelectedImagesContainer;
  private RequestManager requestManager;

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_setting);
    profile = findViewById(R.id.setting_choose_profile);
    confirmButton = findViewById(R.id.setting_confirm);
    mSelectedImagesContainer = findViewById(R.id.selected_photos_container);
    requestManager = Glide.with(this);

    setProfile();
    setConfirmButton();
  }

  private void setProfile() {
    profile.setOnClickListener(v -> {
      PermissionListener permissionListener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
          TedBottomPicker.with(SettingActivity.this)
              .setSelectedUri(selectedUri)
              .setPeekHeight(1200)
              .show(uri -> {
                Log.d("ted", "uri: " + uri);
                Log.d("ted", "uri.getPath(): " + uri.getPath());
                selectedUri = uri;

                mSelectedImagesContainer.setVisibility(View.GONE);

                requestManager
                    .load(uri)
                    .into(profile);
              });

        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
          Toast.makeText(SettingActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
        }

      };

      checkPermission(permissionListener);
    });
  }

  private void setConfirmButton() {
    confirmButton.setOnClickListener(v -> {
//      Toast.makeText(SettingActivity.this, selectedUri.toString(), Toast.LENGTH_SHORT).show();

      onBackPressed();
    });
  }

  private void checkPermission(PermissionListener permissionlistener) {
    TedPermission.with(SettingActivity.this)
        .setPermissionListener(permissionlistener)
        .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
        .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        .check();
  }


}
