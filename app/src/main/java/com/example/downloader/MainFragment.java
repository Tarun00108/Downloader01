package com.example.downloader;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
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

public class MainFragment extends Fragment {
    private ProgressBar progressBar;
    private TextView progressText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        EditText editTextLink = view.findViewById(R.id.check);
        progressBar = view.findViewById(R.id.progressBar);
        progressText = view.findViewById(R.id.progressText);
        Button buttonDownload = view.findViewById(R.id.get);
        buttonDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String downloadLink = editTextLink.getText().toString();
                new DownloadTask().execute(downloadLink);
            }
        });
        return view;
    }

    private class DownloadTask extends AsyncTask<String, Integer, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(getContext(), "Downloading Start", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Void doInBackground(String... params) {
            if (params.length > 0) {
                String downloadLink = params[0];
                try {
                    // Open a connection to the URL
                    URL url = new URL(downloadLink);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.connect();
                    String contentType = connection.getContentType();
                    String fileExtension = getFileExtension(contentType);
                    File directory;
                    if (contentType.startsWith("image")) {
                        directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                    } else if (contentType.startsWith("audio")) {
                        directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
                    } else if (contentType.startsWith("video")) {
                        directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
                    } else {
                        directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                    }
                    String filename = getFileNameFromUrl(downloadLink);
                    File outputFile = new File(directory, filename);
                    // Download and save the file
                    InputStream input = connection.getInputStream();
                    FileOutputStream output = new FileOutputStream(outputFile);
                    byte[] buffer = new byte[1024];
                    int length;
                    int fileSize = connection.getContentLength();
                    int downloadedSize = 0;
                    while ((length = input.read(buffer)) != -1) {
                        output.write(buffer, 0, length);
                        downloadedSize += length;
                        publishProgress(downloadedSize, fileSize);
                    }
                    output.close();
                    input.close();
                    connection.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if (values.length >= 2) {
                int downloadedSize = values[0];
                int fileSize = values[1];
                int progress = (int) (((float) downloadedSize / fileSize) * 100);
                progressBar.setProgress(progress);
                progressText.setText(progress + "%");
            } else {
            }
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            Toast.makeText(getContext(), "Downloading Complete", Toast.LENGTH_SHORT).show();
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
        private String getFileExtension(String contentType) {
            switch (contentType) {
                case "image/jpeg":
                    return "jpg";
                case "image/png":
                    return "png";
                case "audio/mpeg":
                    return "mp3";
                case "video/mp4":
                    return "mp4";
                default:
                    return "unknown";
            }
        }
    }
}
