package com.handipressante.handipressante;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ToiletSheetActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toilet_sheet);

        // get info from parent view
        Intent intent = getIntent();
        Integer id  = intent.getIntExtra("idSheet", -1);

        getActionBar().setLogo(R.drawable.back_icon);
        getActionBar().setTitle("Liste");

        fillToiletSheet(id);

        ActionBar actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(true);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            // Something else
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_toilet_sheet, menu);
        return true;
    }

    

    public void getSheet(int id){
        Log.d("Test", "test");
    }

    public void fillToiletSheet(int id){
        OnlineDataModel odm = new OnlineDataModel(getBaseContext());
        //Sheet sheetDownload = odm.getSheet(id);
       // Log.d("Beginning display of sheet", sheetDownload.get_name());

        // Set icon whether adapted toilet or not
        ImageView img= (ImageView) findViewById(R.id.handicapped);
        img.setImageResource(R.drawable.handicap_icon);

        // Set toilet's name
        TextView name=(TextView)findViewById(R.id.toilet_name);
        //name.setText(sheetDownload.get_name());
        name.setText(String.valueOf(id));
        // Set toilet's description (wiki)
        TextView description=(TextView)findViewById(R.id.toilet_description);
        description.setText("bla bla bla bla bla bla bla bla bla");


        /*ImageButton navigation_button = (ImageButton) findViewById(R.id.map_button);
        navigation_button.setBackgroundResource(R.drawable.navigation_icon);*/

        /*ImageButton comment_button = (ImageButton) findViewById(R.id.comment_button);
        comment_button.setBackgroundResource(R.drawable.icon_addrate);*/

    }
}
