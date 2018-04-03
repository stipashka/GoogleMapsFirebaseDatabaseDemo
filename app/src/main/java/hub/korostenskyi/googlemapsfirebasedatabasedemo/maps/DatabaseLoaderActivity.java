package hub.korostenskyi.googlemapsfirebasedatabasedemo.maps;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.HashMap;
import java.util.Objects;

import hub.korostenskyi.googlemapsfirebasedatabasedemo.R;

public class DatabaseLoaderActivity extends AppCompatActivity {
    private final int PICK_IMAGE = 1;

    private MarkerOptions marker;

    //Database and Storage
    private DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference userRef = ref.child("usr");
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference("img/");

    private Uri selectedImage = null;

    //UI
    private EditText name_editText, photoName_editText, desc_editText, date_editText;
    private Button send_btn, cancel_btn, pickImg_btn, button;
    private ImageView imageView, imageView1;

    //Data
    private String name, photoName, desc, date;

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
                name = name_editText.getText().toString();
                photoName = photoName_editText.getText().toString();
                desc = desc_editText.getText().toString();
                date = date_editText.getText().toString();

                if (name.equals("") || photoName.equals("") || desc.equals("") || date.equals("")){
                    Toast.makeText(DatabaseLoaderActivity.this, "All fields are required", Toast.LENGTH_LONG).show();
                } else if (selectedImage == null) {
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

                    //TODO: Add this bool later
                    boolean isChecked = false;

                    HashMap<String, String> imgInfo = new HashMap<>();
                    imgInfo.put("author", name);
                    imgInfo.put("photoName", photoName);
                    imgInfo.put("description", desc);
                    imgInfo.put("date", date);
                    imgInfo.put("location_lat", String.valueOf(marker.getPosition().latitude));
                    imgInfo.put("location_lon", String.valueOf(marker.getPosition().longitude));

                    userRef.push().setValue(imgInfo);

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

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    final File tmpFile = File.createTempFile("img", "png");
                    storageReference.child("img/");
                    storageReference.child(photoName.toLowerCase() + ".jpg").getFile(tmpFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Bitmap image = BitmapFactory.decodeFile(tmpFile.getAbsolutePath());
                            imageView1.setImageBitmap(image);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //Selecting an image
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
        imageView1 = findViewById(R.id.image_view1);

        pickImg_btn = findViewById(R.id.pickImg_btn);
        send_btn = findViewById(R.id.send_btn);
        cancel_btn = findViewById(R.id.cancel_btn);

        button = findViewById(R.id.button2);
    }
}
