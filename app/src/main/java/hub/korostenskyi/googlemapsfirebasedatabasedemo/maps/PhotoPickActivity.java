package hub.korostenskyi.googlemapsfirebasedatabasedemo.maps;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import hub.korostenskyi.googlemapsfirebasedatabasedemo.R;


public class PhotoPickActivity extends AppCompatActivity {
    //UI
    private ImageView imageView;
    private ListView listView;

    //Comp
    @NonNull
    private ArrayList<String> photoNames = new ArrayList<>();

    //Storage
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference("img/");

    //Database
    private DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference userRef = ref.child("usr");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_pick);
        initWidgets();

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                collectPhoneNumbers((Map<String,Object>) Objects.requireNonNull(dataSnapshot.getValue()));
                updateUI();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                getImageByName(photoNames.get(i));
            }
        });
    }

    private void getImageByName(String photoName){
        try {
            final File tmpFile = File.createTempFile("img", "png");
            storageReference.child("img/");
            storageReference.child(photoName.toLowerCase() + ".jpg").getFile(tmpFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap image = BitmapFactory.decodeFile(tmpFile.getAbsolutePath());
                    imageView.setImageBitmap(image);

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void collectPhoneNumbers(Map<String,Object> users) {
        //iterate through each user, ignoring their UID
        for (Map.Entry<String, Object> entry : users.entrySet()){
            //Get user map
            Map singleUser = (Map) entry.getValue();

            //Get phone field and append to list
            photoNames.add(String.valueOf(singleUser.get("photoName")));
        }

        System.out.println(photoNames.toString());
    }

    private void updateUI(){
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_list_item_1, photoNames);
        listView.setAdapter(adapter);

        Toast.makeText(PhotoPickActivity.this, "UI updated", Toast.LENGTH_SHORT).show();
    }

    private void initWidgets(){
        imageView = findViewById(R.id.image_view);
        listView = findViewById(R.id.list_view);
    }
}
