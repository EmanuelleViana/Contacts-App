package com.emanuelle.easycontacts.ui;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

//import com.google.android.gms.identity.intents.Address;

import com.emanuelle.easycontacts.pojo.Contato;
import com.emanuelle.easycontacts.R;

public class AddContactActivity extends AppCompatActivity   {
    EditText mName, mEmailAddress, mPhoneNumber, mAddress;
    Button  sendW, oMaps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitycontact);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //set only portrait orientation
        this.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        mEmailAddress = (EditText) findViewById(R.id.editTextMail);
        mPhoneNumber = (EditText) findViewById(R.id.editTextPhone);
        mName = (EditText) findViewById(R.id.editTextName);
        mAddress = (EditText) findViewById(R.id.editTextAddress);

        sendW = (Button) findViewById(R.id.button2);
        oMaps = (Button) findViewById(R.id.button3);

        setTitle("Adicionar Contato");



        sendW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendWhatsapp(mPhoneNumber.getText().toString());

            }
        });

        oMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMaps();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add, menu);

        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_save:
                if(!(mName.getText().toString().equals("")) && !(mPhoneNumber.getText().toString().equals(""))) {
                    Contato contato = new Contato(getApplicationContext(), AddContactActivity.this);
                    contato.addContact(mName.getText().toString(), mPhoneNumber.getText().toString(),
                            mEmailAddress.getText().toString(), mAddress.getText().toString());
                    Toast.makeText(getApplicationContext(), "Contato adicionado", Toast.LENGTH_SHORT).show();
                }
                else {

                    Toast.makeText(getApplicationContext(), "Campos nao podem ficar em branco", Toast.LENGTH_SHORT).show();

                }
                break;


        }
        return super.onOptionsItemSelected(item);
    }

    void sendWhatsapp(String number) {
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
            Uri uri = Uri.parse("smsto:" + number);
            Intent i = new Intent(Intent.ACTION_SENDTO, uri);
            i.setPackage("com.whatsapp");
            startActivity(Intent.createChooser(i, ""));
        } catch (PackageManager.NameNotFoundException e){
            Toast.makeText(this,  e.getMessage(), Toast.LENGTH_SHORT).show();


        }
    }


    void openMaps(){
        String endereco = mAddress.getText().toString();
        Uri uri = Uri.parse("geo:0,0?q=" + endereco);
        Intent intent = new Intent(Intent.ACTION_VIEW,uri);
        intent.setPackage("com.google.android.apps.maps");
        startActivity(intent);
    }

}
