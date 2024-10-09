package com.example.telalogin;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.telalogin.databinding.ActivityGraphicsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
public class Graphics extends AppCompatActivity  {
    private ActivityGraphicsBinding binding;
    private DatabaseReference dataReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGraphicsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
    }
}

