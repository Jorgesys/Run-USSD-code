package com.jorgesys.ussd;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private final static int MY_PERMISSIONS_REQUEST_CALL_PHONE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btnUSSD).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //shows RAM version *#*#3264#*#*"
                //Menu service : *#0011#
                //Show dialed numbers: *#*#8351#*#*
                //Show IMEI Number: *#06#
                //Show model: *#92782#
                //Vibration test: *#*#0842#*#*

                //Let's make a test getting the IMEI!

                requestUSSD("#222*1#");
            }
        });


    }

    private void requestUSSD(String USSD){

        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, MY_PERMISSIONS_REQUEST_CALL_PHONE);
            return;
        }

        String encodedHash = Uri.encode("#");
        USSD = USSD.replace("#", encodedHash);

        /* Reception of a USSD response is only supported in API 26+ */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { //API >= 26
            TelephonyManager manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            manager.sendUssdRequest(USSD, new TelephonyManager.UssdResponseCallback() {
                @Override
                public void onReceiveUssdResponse(TelephonyManager telephonyManager, String request, CharSequence response) {
                    super.onReceiveUssdResponse(telephonyManager, request, response);
                    Toast.makeText(getApplicationContext(), "onReceiveUssdResponse()" + response, Toast.LENGTH_LONG).show();
                }

                @Override
                public void onReceiveUssdResponseFailed(TelephonyManager telephonyManager, String request, int failureCode) {
                    super.onReceiveUssdResponseFailed(telephonyManager, request, failureCode);
                    Toast.makeText(getApplicationContext(), "onReceiveUssdResponseFailed()" + request, Toast.LENGTH_LONG).show();
                }
            }, new Handler());
        }else{      //API < 26
            Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" +USSD));
            startActivity(callIntent);
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CALL_PHONE : {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // PERMISO CONCEDIDO!
                } else {
                    Toast.makeText(getApplicationContext(), "Permission CALL_PHONE not granted!", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }



}
