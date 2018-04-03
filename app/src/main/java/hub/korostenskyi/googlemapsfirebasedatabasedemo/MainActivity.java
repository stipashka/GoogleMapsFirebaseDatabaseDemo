package hub.korostenskyi.googlemapsfirebasedatabasedemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import hub.korostenskyi.googlemapsfirebasedatabasedemo.maps.PhotoPickActivity;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initWidgets();
    }

    @Override
    protected void onStart() {
        super.onStart();
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PhotoPickActivity.class);
                //Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });
    }
    private void initWidgets(){
        floatingActionButton = findViewById(R.id.floating_btn);
    }
}
