package de.tap.easy_xkcd.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.tap.xkcd_reader.R;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditTagsActivity extends AppCompatActivity  implements View.OnClickListener {

    public static final String COMIC_ID = "comic_id";
    private final String TAG = "XKCDTagger";

    private String myComicID;

    private EditText myTagList;
    private Button saveTagsButton;

    //private DatabaseReference tagDatabase; /// Tags -> list of comics.
    private DatabaseReference comicDatabase; /// Comics -> list of tags.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_tags);

        myComicID = Integer.toString(getIntent().getExtras().getInt(COMIC_ID));
        Log.d(TAG, "Starting tag editor for comic ID: " + myComicID);

        myTagList = (EditText) findViewById(R.id.tag_list_edit);
        saveTagsButton = (Button) findViewById(R.id.save_tags_button);
        saveTagsButton.setOnClickListener(this);

        //tagDatabase = FirebaseDatabase.getInstance().getReference().child("tags");
        comicDatabase = FirebaseDatabase.getInstance().getReference().child("comics");

        loadTagsFromDatabase();
    }

    @Override
    public void onClick(View view) {
        Button clickedButton = (Button) view;

        if (clickedButton == saveTagsButton) {
            saveTagsToDatabase();
        }
    }

    private void loadTagsFromDatabase() {
        myTagList.setText("");
        DatabaseReference comicTag = comicDatabase.child(myComicID);
        comicTag.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String tagData = dataSnapshot.getValue(String.class);
                myTagList.setText(tagData);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "Error occurred reading tag for comic: " + databaseError.getMessage());
            }
        });
    }

    private void saveTagsToDatabase() {
        String tagData = myTagList.getText().toString();
        DatabaseReference comicTag = comicDatabase.child(myComicID);
        comicTag.setValue(tagData);
        finish();
    }
}
