package hub.korostenskyi.googlemapsfirebasedatabasedemo.maps;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import hub.korostenskyi.googlemapsfirebasedatabasedemo.R;

/**
 * Info: http://javasampleapproach.com/android/firebase-storage-get-list-files-display-image-firebase-ui-database-android
 */

public class PhotoPickActivity extends AppCompatActivity {

    //UI
    private ImageView imageView;
    private ListView listView;

    //Comp
    private List<String> list;

    //Storage
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference("img/");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_pick);
        initWidgets();



        listView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void updateUI(){
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);
    }

    private void initWidgets(){
        imageView = findViewById(R.id.image_view);
        listView = findViewById(R.id.list_view);
    }
}
