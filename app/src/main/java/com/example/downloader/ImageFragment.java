package com.example.downloader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

public class ImageFragment extends Fragment {
    ImageView img;
    EditText edt1;
    Button bt1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_image, container, false);
        img = view.findViewById(R.id.ig);
        edt1 = view.findViewById(R.id.imglink);
        bt1 = view.findViewById(R.id.imgbtn);
        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String downloadLink = edt1.getText().toString();
                new DownloadImg().execute(downloadLink);
            }
        });
        return view;
    }
    public class DownloadImg extends AsyncTask<String, Integer, Bitmap> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(getContext(), "Downloading Start", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            if (params.length > 0) {
                String downloadLink = params[0];
                try {
                    // Open a connection to the URL
                    URL url = new URL(downloadLink);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.connect();
                    // Create a directory if it doesn't exist
                    File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                   /* if (!directory.exists()) {
                        directory.mkdirs();
                    }*/
                    String filename = getFileNameFromUrl(downloadLink);
                    File outputFile = new File(directory, filename);
                    // Download and save the file
                    InputStream input = connection.getInputStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(input);
                    FileOutputStream output = new FileOutputStream(outputFile);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output); // Save the bitmap as JPEG
                    output.close();
                    input.close();
                    connection.disconnect();
                    return bitmap;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            img.setImageBitmap(bitmap);
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
    }
}
