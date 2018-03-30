package hub.korostenskyi.googlemapsfirebasedatabasedemo.maps;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

import hub.korostenskyi.googlemapsfirebasedatabasedemo.R;

public class DatabaseLoaderActivity extends AppCompatActivity {

    private final int PICK_IMAGE = 1;

    private MarkerOptions marker;

    //Database and Storage
    private DatabaseReference markerRef = FirebaseDatabase.getInstance().getReference().child("marker/");
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference("img/");

    private Uri selectedImage = null;

    //UI
    private EditText name_editText, photoName_editText, desc_editText, date_editText;
    private Button send_btn, cancel_btn, pickImg_btn;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_loader);

        Intent intent = getIntent();
        marker = (MarkerOptions) Objects.requireNonNull(intent.getExtras()).get("marker");

        initWidgets();

        pickImg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, PICK_IMAGE);
            }
        });

        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = name_editText.getText().toString();
                String photoName = photoName_editText.getText().toString();
                String desc = desc_editText.getText().toString();

                String date = date_editText.getText().toString();
                if (selectedImage == null) {
                    Toast.makeText(DatabaseLoaderActivity.this, "Select an image!", Toast.LENGTH_LONG).show();
                } else {
                    StorageReference imgRef = storageReference.child(photoName.toLowerCase() + ".jpg");

                    imgRef.putFile(selectedImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(DatabaseLoaderActivity.this, "Image successfully uploaded", Toast.LENGTH_LONG).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(DatabaseLoaderActivity.this, "Failed to upload image", Toast.LENGTH_LONG).show();
                        }
                    });

                    //FIX IT!!!!
                    String url = storageReference.child("img/"+photoName.toLowerCase()+".jpg").getDownloadUrl().toString();
                    boolean isChecked = false;

                    markerRef.child("author").setValue(name);
                    markerRef.child("photoName").setValue(photoName);
                    markerRef.child("description").setValue(desc);
                    markerRef.child("isChecked").setValue(isChecked);
                    markerRef.child("date").setValue(date);
                    markerRef.child("location/latti").setValue(marker.getPosition().latitude);
                    markerRef.child("location/longi").setValue(marker.getPosition().longitude);
                    markerRef.child("image/id").setValue(selectedImage.hashCode());
                    markerRef.child("image/url").setValue(url);

                    Intent intent = new Intent(DatabaseLoaderActivity.this, MapsActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        });

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DatabaseLoaderActivity.this, MapsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && null != data) {
            selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            assert selectedImage != null;
            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            assert cursor != null;
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            imageView = findViewById(R.id.image_view);
            //imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            imageView.setImageURI(selectedImage);
        }
    }

    private void initWidgets() {
        name_editText = findViewById(R.id.name_editText);
        photoName_editText = findViewById(R.id.photoName_editText);
        desc_editText = findViewById(R.id.desc_editText);
        date_editText = findViewById(R.id.date_editText);

        imageView = findViewById(R.id.image_view);

        pickImg_btn = findViewById(R.id.pickImg_btn);
        send_btn = findViewById(R.id.send_btn);
        cancel_btn = findViewById(R.id.cancel_btn);
    }
}
