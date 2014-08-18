package com.westudio.android.pipe;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.westudio.android.protocol.AdRequest;
import com.westudio.android.protocol.AdResponse;
import com.westudio.android.protocol.Person;
import com.westudio.android.sdk.exceptions.ServiceClientError;
import com.westudio.android.sdk.http.ServiceCallback;
import com.westudio.android.sdk.http.ServiceClient;


public class MainActivity extends ActionBarActivity {

    private static final String LOG_TAG = "MainActivity";

    private Button btn;
    private EditText etName;
    private EditText etCountry;
    private EditText etCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn = (Button)findViewById(R.id.button);
        etName = (EditText)findViewById(R.id.name);
        etCountry = (EditText)findViewById(R.id.country);
        etCity = (EditText)findViewById(R.id.city);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ServiceClient client = ServiceClient.getInstance();
                client.setServiceUrl("http://*.*.*.*:8080");

                // mock a AdRequest instance
                AdRequest request = null;
                Person p = new Person();
                p.setAddress1("China");
                p.setAddress2("Shanghai");
                p.setCity(etCity.getText().toString());
                p.setCountry(etCountry.getText().toString());
                p.setFirstName(etName.getText().toString());
                p.setId(1);
                p.setLastName("He");
                p.setPostCode("021");
                request = AdRequest.newBuilder().setAdBB("BeepBoop").setPerson(p).build();
                client.invoke(request, "", AdRequest.class, AdResponse.class, new ServiceCallback<AdResponse>() {
                    @Override
                    public void onResponse(AdResponse response) {
                        Log.v(LOG_TAG, "response size: " + response.getAds().size());
                        Toast.makeText(MainActivity.this, response.getAds().get(0).getFirstName(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onErrorResponse(ServiceClientError error) {
                        Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
