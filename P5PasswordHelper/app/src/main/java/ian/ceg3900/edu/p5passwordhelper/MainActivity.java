package ian.ceg3900.edu.p5passwordhelper;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    RequestQueue mQueue;
    private final String apiURL = "https://ceg3900-passwordhelper.appspot.com/check-password/";
    private EditText passwordEdit;
    private TextView resultView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mQueue = Volley.newRequestQueue(this);

        passwordEdit = (EditText) findViewById(R.id.password_edit);
        resultView = (TextView) findViewById(R.id.password_check_results);

        Button checkPasswordButton = (Button) findViewById(R.id.check_password_button);
        checkPasswordButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.check_password_button) {
            submitPasswordCheck();
        }
    }

    private void submitPasswordCheck() {
        String password = passwordEdit.getText().toString();
        String requestURL = apiURL + password;
        resultView.setText("Checking password against common dumps, please wait...");
        StringRequest stringRequest = new StringRequest(Request.Method.GET, requestURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                resultView.setText(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                resultView.setText(error.getMessage());
            }
        });

        mQueue.add(stringRequest);
    }
}
