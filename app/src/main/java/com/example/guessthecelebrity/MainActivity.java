package com.example.guessthecelebrity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {
    ImageView imageView;
    Button b0;
    Button b1;
    Button b2;
    Button b3;
    int size;
    ArrayList<String> imgurl;
    ArrayList<String> name;
    String correctname;

    public class DownloadImage extends AsyncTask<String,Void,Bitmap>
    {

        @Override
        protected Bitmap doInBackground(String... urls) {

            try {
                URL url=new URL(urls[0]);
                HttpURLConnection urlConnection=null;
                urlConnection=(HttpURLConnection) url.openConnection();

                urlConnection.connect();
                InputStream inputStream=urlConnection.getInputStream();
                //InputStreamReader reader=new InputStreamReader(inputStream);
                Bitmap b= BitmapFactory.decodeStream(inputStream);
                return b;
             } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    public class DownloadTask extends AsyncTask<String,Void,String>
    {

        @Override
        protected String doInBackground(String... urls) {

            URL url;
            String result="";
            HttpURLConnection urlConnection=null;


            try {
                url = new URL(urls[0]);
                urlConnection=(HttpURLConnection) url.openConnection();
                urlConnection.connect();
                InputStream inputStream=urlConnection.getInputStream();
                InputStreamReader reader=new InputStreamReader(inputStream);
                int data=reader.read();
                while(data!=-1)
                {
                    char ch=(char) data;
                    result+=ch;
                    data=reader.read();
                }
                return result;
            }
             catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    public void set()
    {
        int index=(int)(Math.random()*size);
        correctname=name.get(index);
        DownloadImage image=new DownloadImage();
        Bitmap b= null;
        try {
            b = image.execute(imgurl.get(index)).get();
            imageView.setImageBitmap(b);
            int a[]=new int[4];
            do {
                a[0]= (int)(Math.random()*size);
                a[1]=(int)(Math.random()*size);
                a[2]=(int)(Math.random()*size);
                a[3]=(int)(Math.random()*size);
            }while(a[0]==index||a[1]==index|a[2]==index|a[3]==index);
            int ct=(int)(Math.random()*4);
            a[ct]=index;
            b0.setText(name.get(a[0]));
            b1.setText(name.get(a[1]));
            b2.setText(name.get(a[2]));
            b3.setText(name.get(a[3]));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void celebrity(View view)
    {
        Button button=(Button) view;
        String text=String.valueOf(button.getText());
        if(text==correctname)
            Toast.makeText(this,"Correct",Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this,"The correct answer was"+correctname,Toast.LENGTH_SHORT).show();
        set();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView=findViewById(R.id.imageView);
               b0=findViewById(R.id.button0);
               b1=findViewById(R.id.button1);
               b2=findViewById(R.id.button2);
               b3=findViewById(R.id.button3);
           imgurl=new ArrayList<String>();
             size=0;
             name=new ArrayList<String>();
        DownloadTask task=new DownloadTask();
        try {
            String result=task.execute("http://www.posh24.se/kandisar").get();
            String crucial[]=result.split("<div class=\"sidebarContainer\">");
            result=crucial[0];


            Pattern p=Pattern.compile("<img src=\"(.*?)\"");
            Matcher m=p.matcher(result);
            while(m.find())
            {
                imgurl.add(m.group(1));
                size++;
            }

            p=Pattern.compile("alt=\"(.*?)\"");
            m=p.matcher(result);

            while(m.find())
                name.add(m.group(1));
            set();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
