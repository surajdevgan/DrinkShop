package suraj.dev.com.drinkshop;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import com.facebook.accountkit.AccountKit;
import android.view.MenuItem;
import android.widget.TextView;

public class HomeActivity extends AppCompatActivity {
    SharedPreferences sh;
    SharedPreferences.Editor editor;
    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        tv = findViewById(R.id.name);
        sh = getSharedPreferences("MyShared",MODE_PRIVATE);
        editor = sh.edit();
        tv.setText(sh.getString("name",""));

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(1, 101, 0, "Logout");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case 101:
                editor.clear();
                editor.apply();
                AccountKit.logOut();
                finish();
                startActivity(new Intent(this,MainActivity.class));
                break;


        }
        return true;
    }
}
