package com.emanuelle.easycontacts.ui;

import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.emanuelle.easycontacts.R;

public class Contact_DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact__detail);


        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        String nome = getIntent().getExtras().getString("Nome");
        String telefone = getIntent().getExtras().getString("Telefone");
        String email = getIntent().getExtras().getString("Email");
        String endereco = getIntent().getExtras().getString("Endereco");

        setTitle(nome);
        TextView tvPhone = (TextView)findViewById(R.id.tvPhone);
        tvPhone.setText(telefone);
        TextView tvEmail = (TextView)findViewById(R.id.tvEmail);
        tvEmail.setText(email);
        TextView tvEndereco = (TextView)findViewById(R.id.tvEndereco);
        tvEndereco.setText(endereco);

    }
}
