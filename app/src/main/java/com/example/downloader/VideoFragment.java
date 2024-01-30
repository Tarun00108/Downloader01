package com.example.downloader;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class VideoFragment extends Fragment {

    VideoView vd;
    EditText videoLink;
    Button Download, PlayVideo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate( R.layout.fragment_video, container, false );

        vd = view.findViewById( R.id.Viewvideo );
        videoLink = view.findViewById( R.id.videolink );
        Download = view.findViewById( R.id.videobtn );
        PlayVideo = view.findViewById( R.id.playvideo );
        vd.setVisibility( View.GONE );
        PlayVideo.setVisibility( View.GONE );
        Download.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String videoLinkStr = videoLink.getText().toString();
                new DownloadVideoTask( getContext() ).execute( videoLinkStr );
            }
        } );

        PlayVideo.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playVideo();
            }
        } );

        return view;
    }

    private void playVideo() {
        if (vd != null) {
            vd.start();
        } else {
            Toast.makeText( getContext(), "No video to play. Download first.", Toast.LENGTH_SHORT ).show();
        }
    }

    private class DownloadVideoTask extends AsyncTask<String, Void, String> {
        private Context context;

        public DownloadVideoTask(Context context) {
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
                String videoLink = params[0];
                try {
                    // Open a connection to the URL
                    URL url = new URL( videoLink );
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.connect();

                    // Create a directory if it doesn't exist
                    File directory = Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_MOVIES );
                    if (!directory.exists()) {
                        directory.mkdirs();
                    }

                    String filename = getFileNameFromUrl( videoLink );
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
                vd.setVisibility( View.VISIBLE );
                PlayVideo.setVisibility( View.VISIBLE );
                Toast.makeText( context, "Download Complete", Toast.LENGTH_SHORT ).show();
                // Set up VideoView to play the downloaded video
                vd.setVideoURI( Uri.parse( filePath ) );
                MediaController mediaController = new MediaController( context );
                mediaController.setAnchorView( vd );
                vd.setMediaController( mediaController );
                vd.requestFocus();
            } else {
                Toast.makeText( context, "Download failed", Toast.LENGTH_SHORT ).show();
            }
        }

        private String getFileNameFromUrl(String url) {
            try {
                URL parsedUrl = new URL( url );
                String path = parsedUrl.getPath();
                return path.substring( path.lastIndexOf( '/' ) + 1 );
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return "default_filename.mp4";
            }
        }
    }
}
