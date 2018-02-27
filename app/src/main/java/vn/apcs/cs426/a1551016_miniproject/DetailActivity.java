package vn.apcs.cs426.a1551016_miniproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

public class DetailActivity extends AppCompatActivity {

    CarRepairDetail carRepairDetail = null;
    TextView name;
    TextView openStatus;
    TextView address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.info_detail_layout);

        /**
         * get the values passed through the MapsActivity and update the DetailActivity's UI
         */
        Intent intent = getIntent();
        carRepairDetail = new CarRepairDetail(
                intent.getStringExtra("title"),
                intent.getStringExtra("open"),
                intent.getStringExtra("address"));

        name = (TextView) findViewById(R.id.name);
        openStatus = (TextView) findViewById(R.id.openStatus);
        address = (TextView) findViewById(R.id.address);

        name.setText(carRepairDetail.getTitle());
        openStatus.setText(carRepairDetail.getOpeningStatus());
        address.setText(carRepairDetail.getAddress());
    }
}
