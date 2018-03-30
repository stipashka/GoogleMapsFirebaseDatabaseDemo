package hub.korostenskyi.googlemapsfirebasedatabasedemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import hub.korostenskyi.googlemapsfirebasedatabasedemo.maps.MapsActivity;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private FloatingActionButton floatingActionButton;

    private List<String> list;

    DatabaseReference referenceRoot = FirebaseDatabase.getInstance().getReference();
    //DatabaseReference referenceChild = referenceRoot.child("coordinates");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initWidgets();

        //FirebaseApp.initializeApp(MainActivity.this);
        //FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//        if (firebaseUser == null)
//            goLoginScreen();
//        else {
//            String name = firebaseUser.getDisplayName();
//            Toast.makeText(this, "Welcome back, " + name + " ^_^", Toast.LENGTH_LONG).show();
//        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });
    }

    private void updateUI(){
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);
    }

    //    private void goLoginScreen() {
//        Intent intent = new Intent(this, LoginActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);
//    }
//
//    private void logOut(){
//        FirebaseAuth.getInstance().signOut();
//        LoginManager.getInstance().logOut();
//        goLoginScreen();
//    }

    private void initWidgets(){
        listView = findViewById(R.id.coordinates_listView);
        floatingActionButton = findViewById(R.id.floating_btn);
    }
}
