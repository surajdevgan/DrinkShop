package suraj.dev.com.drinkshop;

import android.app.VoiceInteractor;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.PhoneNumber;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.szagurskii.patternedtextwatcher.PatternedTextWatcher;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;

public class MainActivity extends AppCompatActivity {
    static final int REQUEST_CODE = 1;
    RequestQueue requestQueue;
    MaterialEditText EName,EAddress,EBday;
    Button Register;
    String phone;
    String Name,Address,Bday;
    String SName;
    SharedPreferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //printkeyHAsh();  //First of all you need to call this method to get key hash. You need to call this method only once. Once you get the hashkey you can paste that key to your facebook developer account.
        requestQueue = Volley.newRequestQueue(this);
        preferences = getSharedPreferences("MyShared", MODE_PRIVATE);
        if(preferences.getBoolean("bool",false))
        {
            finish();
            startActivity(new Intent(this,HomeActivity.class));
        }




    }




    void printkeyHAsh()
    {
        try {
            PackageInfo info = getPackageManager().getPackageInfo("suraj.dev.com.drinkshop", PackageManager.GET_SIGNATURES);
            for (Signature signature: info.signatures)
            {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("Keyhash", Base64.encodeToString(md.digest(), Base64.DEFAULT));


            }
        }
        catch (Exception e)
        {

        }
    }

    public void BtnContinue(View view) {
StartLogin(LoginType.PHONE);
    }

    void StartLogin(LoginType loginType)
    {
        Intent i = new Intent(this, AccountKitActivity.class);
        AccountKitConfiguration.AccountKitConfigurationBuilder builder =
                new AccountKitConfiguration.AccountKitConfigurationBuilder(loginType,
                        AccountKitActivity.ResponseType.TOKEN);

        i.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,builder.build());
        startActivityForResult(i,REQUEST_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_CODE)
        {
            AccountKitLoginResult result = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
            if(result.getError()!=null)
            {
                Toast.makeText(this, ""+result.getError().getErrorType().getMessage(), Toast.LENGTH_LONG).show();
            }

            else if (result.wasCancelled())
            {
                Toast.makeText(this, "Canceled", Toast.LENGTH_SHORT).show();
            }

            else {

                if(result.getAccessToken()!=null)
                {



                    // Now code to check weather the userphone number is registered on server or not
                    AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                        @Override
                        public void onSuccess(Account account) {

                            phone = account.getPhoneNumber().toString();
                            Log.d("Asses",phone);
                            LoginUser();

                        }

                        @Override
                        public void onError(AccountKitError accountKitError) {

                        }
                    });


                }
            }
        }
    }

    void LoginUser()
    {
        final android.app.AlertDialog dialog = new SpotsDialog(MainActivity.this);
        dialog.show();
        dialog.setMessage("Please Wat.....");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Util.Login, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    String message = object.getString("message");


                    if (message.contains("Already registered"))
                    {
                        JSONArray array=object.getJSONArray("students");
                        dialog.dismiss();

                    for(int i=0;i<array.length();i++)
                    {
                        JSONObject object1=array.getJSONObject(i);
                        SName = object1.getString("NAME");

                    }

                        SharedPreferences.Editor myEdit = preferences.edit();
                        myEdit.putString("name",SName);
                        myEdit.putBoolean("bool",true);
                        myEdit.apply();
                        Toast.makeText(MainActivity.this, "Welcome Back", Toast.LENGTH_SHORT).show();
                        finish();
                        startActivity(new Intent(MainActivity.this,HomeActivity.class));

                        }



                    if(message.contains("Login Fail"))
                    {
                        dialog.dismiss();
                        showRegisterDialog();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    dialog.dismiss();
                    Toast.makeText(MainActivity.this, "SomeException", Toast.LENGTH_SHORT).show();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                Toast.makeText(MainActivity.this, "Volley Error", Toast.LENGTH_SHORT).show();

            }
        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<>();
                map.put("Phone",phone);
                return map;

            }
        };
        requestQueue.add(stringRequest);

    }

    void RegisterUser()
    {
        final android.app.AlertDialog dialog = new SpotsDialog(MainActivity.this);
        dialog.show();
        dialog.setMessage("Please Wat.....");
        StringRequest request = new StringRequest(Request.Method.POST, Util.Register, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject object = new JSONObject(response);
                    String message = object.getString("message");
                    if (message.contains("Record Inserted Sucessfully")) {

                        dialog.dismiss();
                        finish();
                        startActivity(new Intent(MainActivity.this,HomeActivity.class));



                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<>();
                map.put("Name",Name);
                map.put("Phone",phone);
                map.put("bday",Bday);
                map.put("address",Address);
                return map;
            }
        };
        requestQueue.add(request);
    }

    void showRegisterDialog()
    {
AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
builder.setTitle("Register");

        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.register_dialog,null);

        EName = view.findViewById(R.id.name);
        EAddress = view.findViewById(R.id.address);
        EBday = view.findViewById(R.id.bday);
        Register = view.findViewById(R.id.conti);

        EBday.addTextChangedListener(new PatternedTextWatcher("####-##-##"));


        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

Name = EName.getText().toString();
Address = EAddress.getText().toString();
Bday = EBday.getText().toString();

                SharedPreferences.Editor myEdit = preferences.edit();
                myEdit.putString("name",Name);
                myEdit.putBoolean("bool",true);
                myEdit.apply();

RegisterUser();
            }
        });

        builder.setView(view);
        builder.show();


    }
}
