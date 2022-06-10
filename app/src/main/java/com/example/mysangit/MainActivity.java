package com.example.mysangit;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ListView listView;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.listView);

        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
//                        Toast.makeText(MainActivity.this, "run time permission given", Toast.LENGTH_SHORT).show();

                        ArrayList<File> mysongs = fetchSongs(Environment.getExternalStorageDirectory());
                        String[] items = new String[mysongs.size()];

                        for(int i=0;i<mysongs.size();i++){
                            items[i] = mysongs.get(i).getName().replace(".mp3","");
                        }

//                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1,items);
//                        listView.setAdapter(adapter);

                        MyAdaptor myAdaptor = new MyAdaptor(MainActivity.this, R.layout.my_layout, items);
                        listView.setAdapter(myAdaptor);

                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                Intent intent = new Intent(MainActivity.this,PlaySong.class);
                                String currentsong = listView.getItemAtPosition(i).toString();
                                intent.putExtra("songlist",mysongs);
                                intent.putExtra("currentsong",currentsong);
                                intent.putExtra("position",i);
                                startActivity(intent);
                            }
                        });
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                })
                .check();
    }

    public ArrayList<File> fetchSongs(File file){
        ArrayList arrayList;
        arrayList = new ArrayList();
        File[] songs = file.listFiles();
        if(songs != null){
            for(File myfiles : songs){
                if(myfiles.isDirectory() && !myfiles.isHidden()){
                    arrayList.addAll(fetchSongs(myfiles));
                }
                else{
                    if(myfiles.getName().endsWith(".mp3") && !myfiles.getName().startsWith(".")){
                        arrayList.add(myfiles);
                    }
                }
            }
        }
        return arrayList;
    }
}