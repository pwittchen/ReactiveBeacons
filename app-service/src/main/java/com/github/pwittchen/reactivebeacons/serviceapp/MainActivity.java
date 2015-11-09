package com.github.pwittchen.reactivebeacons.serviceapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity {
  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    final Intent intent = new Intent(MainActivity.this, BeaconService.class);

    findViewById(R.id.b_start_service).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        startService(intent);
        showToast("service started");
      }
    });

    findViewById(R.id.b_stop_service).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        stopService(intent);
        showToast("service stopped");
      }
    });
  }

  private void showToast(String message) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
  }
}
