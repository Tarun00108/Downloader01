package com.example.downloader;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class MusicFragment extends Fragment {
    EditText musicLink;
    Button musicBtn, Playbtn, Stopbtn;
    MediaPlayer mediaPlayer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate( R.layout.fragment_music, container, false );
        musicLink = view.findViewById( R.id.musiclink );
        musicBtn = view.findViewById( R.id.musicbtn );
        Playbtn = view.findViewById( R.id.play );
        Stopbtn = view.findViewById( R.id.stop );

        musicBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String musicLinkStr = musicLink.getText().toString();
                new DownloadMusicTask( getContext() ).execute( musicLinkStr );
            }
        } );

        Playbtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer != null) {
                    mediaPlayer.start();
                } else {
                    Toast.makeText(getContext(), "No music to play. Download first.", Toast.LENGTH_SHORT).show();
                }
            }
        } );

        Stopbtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.pause();
            }
        } );

        return view;
    }

    private class DownloadMusicTask extends AsyncTask<String, Void, String> {
        private Context context;

        public DownloadMusicTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText( context, "Downloading Start", Toast.LENGTH_SHORT ).show();
        }

        @Override
        protected String doInBackground(String... params) {
            if (params.length > 0) {
                String musicLink = params[0];
                try {
                    // Open a connection to the URL
                    URL url = new URL( musicLink );
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.connect();

                    // Create a directory if it doesn't exist
                    File directory = Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_MUSIC );
                   /* if (!directory.exists()) {
                        directory.mkdirs();
                    }
*/
                    String filename = getFileNameFromUrl( musicLink );
                    File outputFile = new File( directory, filename );

                    // Download and save the file
                    InputStream input = connection.getInputStream();
                    FileOutputStream output = new FileOutputStream( outputFile );
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = input.read( buffer )) != -1) {
                        output.write( buffer, 0, length );
                    }
                    output.close();
                    input.close();
                    connection.disconnect();

                    return outputFile.getPath();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String filePath) {
            super.onPostExecute( filePath );
            if (filePath != null) {
                Toast.makeText( context, "Download Complete", Toast.LENGTH_SHORT ).show();
                // Set up MediaPlayer to play the downloaded music
                mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource( filePath );
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText( context, "Download failed", Toast.LENGTH_SHORT ).show();
            }
        }

        private String getFileNameFromUrl(String url) {
            try {
                URL parsedUrl = new URL(url);
                String path = parsedUrl.getPath();
                // Decode the URL-encoded string
                String decodedFileName = URLDecoder.decode(path.substring(path.lastIndexOf('/') + 1), StandardCharsets.UTF_8.name());
                return decodedFileName;
            } catch (MalformedURLException | UnsupportedEncodingException e) {
                e.printStackTrace();
                return "default_filename";
            }
        }
    }
}
