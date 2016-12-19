package com.emanuelle.easycontacts.ui;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.support.v7.widget.SearchView;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.emanuelle.easycontacts.pojo.Contato;
import com.emanuelle.easycontacts.ListaContatosAdapter;
import com.emanuelle.easycontacts.R;

import java.util.List;

public class List_ContactsActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private ListView mListView;
    private List<Contato> contatos;
    private ListaContatosAdapter contatosAdapter;
    private TextView noContacts;
    private  Context ctx;
    private int flagOrder = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list__contacts);

        noContacts = (TextView)findViewById(R.id.no_contacts_found);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        ctx = getApplicationContext();

        setListAdapter(contatosAdapter);
        registerForContextMenu(getListView());



        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?>adapter,View v, int position,long id){

                String nome = contatos.get(position).getNome();
                String telefone = contatos.get(position).getTelefone();
                String email = contatos.get(position).getEmail();
                String endereco = contatos.get(position).getEndereco();

                Intent intent = new Intent(List_ContactsActivity.this,Contact_DetailActivity.class);
                intent.putExtra("Nome",nome);
                intent.putExtra("Telefone",telefone);
                intent.putExtra("Email",email);
                intent.putExtra("Endereco",endereco);

                startActivity(intent);


            }


        });
    }



    @Override
    public void onResume() {
        noContacts.setText("");
        ctx = getApplicationContext();
        Contato c = new Contato(ctx, List_ContactsActivity.this);

        contatos = c.getAllContatos();
        contatosAdapter = new ListaContatosAdapter(ctx, R.layout.item_list_contatos, contatos);

        super.onResume();
        update();

        SharedPreferences myPrefs = this.getSharedPreferences("myPrefs", MODE_WORLD_READABLE);
        flagOrder = myPrefs.getInt("flagOrder",0);

        if (flagOrder == 1)  {
            contatosAdapter.sortByName();

        }
        else if (flagOrder == 2){
            contatosAdapter.sortByTelefone();

        }

    }
       @Override
       public void onSaveInstanceState(Bundle out){
          out.putInt("flagOrder",flagOrder);
                      super.onSaveInstanceState(out);

       }



    @Override
    public void onPause(){
        super.onPause();
        SharedPreferences myPrefs = this.getSharedPreferences("myPrefs", MODE_WORLD_READABLE);
        SharedPreferences.Editor prefsEditor = myPrefs.edit();
        prefsEditor.putInt("flagOrder",flagOrder);
        prefsEditor.commit();
    }


    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Sempre chame a superclasse para que possa
        // restaurar a hierarquia da view
        super.onRestoreInstanceState(savedInstanceState);

        // Restaura estados membros da instância salva
        flagOrder = savedInstanceState.getInt("flagOrder");
    }



    protected void setListAdapter(ListaContatosAdapter adapter) {
        getListView().setAdapter(adapter);
    }

    protected ListView getListView() {
        mListView = (ListView) findViewById(R.id.lista);
        return mListView;
    }


    private void update(){
        setListAdapter(contatosAdapter);
        contatosAdapter.notifyDataSetChanged();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.contact_list_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.menu_search);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
            final SearchManager searchManager =
                    (SearchManager) this.getSystemService(Context.SEARCH_SERVICE);

            final SearchView searchView = (SearchView) searchItem.getActionView();
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setOnQueryTextListener(this);

        }
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_contact:
                startActivity();
                break;
            case R.id.ordenarN:
                contatosAdapter.sortByName();
                flagOrder = 1;
                break;
            case R.id.ordenarT:
                contatosAdapter.sortByTelefone();
                flagOrder = 2;

                break;
            case R.id.menu_profileface:
                Intent intent = new Intent(List_ContactsActivity.this,DataFacebookActivity.class);
                startActivity(intent);
                break;

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        noContacts.setText("");
        if(contatosAdapter.filter(newText)){
            noContacts.setText(R.string.no_contacts_found);

        }

        return true;
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        menu.add("Discar");
        menu.add("Enviar SMS");
        menu.add("Enviar Email");
        menu.add("Enviar WhatsApp");
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int position = ((AdapterView.AdapterContextMenuInfo)item.getMenuInfo()).position;

        if (item.getTitle() == "Discar") {
            openPhone(contatos.get(position).getTelefone());
        } else if (item.getTitle() == "Enviar SMS") {
            sendSMS(contatos.get(position).getTelefone());
        } else if (item.getTitle() == "Enviar Email") {
            sendEmail(contatos.get(position).getEmail());
        } else if (item.getTitle() == "Enviar WhatsApp") {
            sendWhatsapp(contatos.get(position).getTelefone());
        } else {
            return false;
        }
        return true;

    }


    public void openPhone(String telefone){
        Uri uri = Uri.parse("tel:"+telefone);
        Intent intent = new Intent(Intent.ACTION_DIAL,uri);

        startActivity(intent);
    }

    public void sendSMS(String telefone){
        Intent smsIntent = new Intent(android.content.Intent.ACTION_VIEW);
        smsIntent.setType("vnd.android-dir/mms-sms");
        smsIntent.putExtra("address",telefone);
        startActivity(smsIntent);
    }
    public void sendEmail(String email){
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"+email));
        startActivity(Intent.createChooser(intent,"Enviar Email.."));
    }

    public void sendWhatsapp(String number) {
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
            //remove ddd
            if(number.contains("031319") && number.length() > 13){
               number =  number.replaceAll("031319","");
            }
            else if(number.contains("031 (31) 9") && number.length() > 17){
                number =  number.replaceAll("031 (31) 9","");

            }

            Uri uri = Uri.parse("smsto:" + number);
            Intent i = new Intent(Intent.ACTION_SENDTO, uri);
            i.setPackage("com.whatsapp");
            startActivity(Intent.createChooser(i, ""));
        } catch (PackageManager.NameNotFoundException e){
            Toast.makeText(this, "O WhatsApp não está instalado " , Toast.LENGTH_SHORT).show();


        }
    }


    public void startActivity() {
        Intent secondActivity = new Intent(this, AddContactActivity.class);
        startActivity(secondActivity);
    }


}
