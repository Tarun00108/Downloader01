package com.example.downloader;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.example.downloader.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        binding = ActivityMainBinding.inflate( getLayoutInflater() );
        setContentView( binding.getRoot() );
        textView = findViewById( R.id.mainEt );
        replaceFragment( new ImageFragment() );
        String text = "Download Anything";
        textView.setText( text );
        replaceFragment( new MainFragment() );
        binding.bottomNavigation.setOnItemSelectedListener( item -> {
            switch (item.getItemId()) {
                case R.id.home:
                    textView.setText( text );
                    replaceFragment( new MainFragment() );
                    break;
                case R.id.image:
                    String text1 = "Download Image";
                    textView.setText( text1 );
                    replaceFragment( new ImageFragment() );
                    break;
                case R.id.music:
                    String text2 = "Download Audio";
                    textView.setText( text2 );
                    replaceFragment( new MusicFragment() );
                    break;
                case R.id.video:
                    String text3 = "Download Video";
                    textView.setText( text3 );
                    replaceFragment( new VideoFragment() );
                    break;
            }

            return  true;
        } );
    }

    private  void  replaceFragment (Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace( R.id.frameLayout, fragment );
        fragmentTransaction.commit();
    }
}