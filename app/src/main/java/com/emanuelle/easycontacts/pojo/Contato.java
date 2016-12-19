package com.emanuelle.easycontacts.pojo;

import android.Manifest;
import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;
import android.support.v7.app.AppCompatActivity;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by Emanuelle on 18/09/2016.
 */
public class Contato extends AppCompatActivity {
    private static final int PERMISSIONS_REQUEST_WRITE_CONTACTS = 1;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 2;

    private String nome,telefone,email,endereco;
    Context contx;
    Activity act;
    private List<Contato> contatos = new ArrayList<Contato>();

    public Contato(){
    }
    public Contato(Context contx,Activity act){
        this.contx = contx;
        this.act = act;
    }

    public Contato(String nome,String telefone,String email,String endereco){
        this.nome = nome;
        this.telefone = telefone;
        this.email = email;
        this.endereco = endereco;


    }
    public Contato(Context contx,Activity act,String nome,String telefone,String email,String endereco){
        this.contx = contx;
        this.act = act;
        this.nome = nome;
        this.telefone = telefone;
        this.email = email;
        this.endereco = endereco;



    }

    public void addContact(String nome,String telefone,String email,String endereco) {

        ArrayList<ContentProviderOperation> lista = new ArrayList<ContentProviderOperation>(); //OK

        int checkPermission = ContextCompat.checkSelfPermission(contx, Manifest.permission.WRITE_CONTACTS);
        //ainda nao tenho permissao
        if (checkPermission != PackageManager.PERMISSION_GRANTED) {
            //solicitar permissao
            String[] permissions = new String[]{Manifest.permission.WRITE_CONTACTS};
            ActivityCompat.requestPermissions(act,permissions, PERMISSIONS_REQUEST_WRITE_CONTACTS);

        } else {
            ContentProviderOperation.Builder bld = ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI);

            bld.withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null);
            bld.withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null);
            lista.add(bld.build());

            //nome
            bld = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI);
            bld.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0);
            bld.withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
            bld.withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, nome);
            lista.add(bld.build());


            //telefone
            bld = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI);
            bld.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0);
            bld.withValue(ContactsContract.Data.MIMETYPE,ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
            bld.withValue(ContactsContract.CommonDataKinds.Phone.NUMBER,telefone);
            bld.withValue(ContactsContract.CommonDataKinds.Phone.TYPE,ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
            lista.add(bld.build());


            //endereco
            bld = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI);
            bld.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0);
            bld.withValue(ContactsContract.Data.MIMETYPE,ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE);
            bld.withValue(ContactsContract.CommonDataKinds.StructuredPostal.DATA,endereco);
            bld.withValue(ContactsContract.CommonDataKinds.StructuredPostal.TYPE,ContactsContract.CommonDataKinds.StructuredPostal.TYPE_HOME);
            lista.add(bld.build());


            //email
            bld = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI);
            bld.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0);
            bld.withValue(ContactsContract.Data.MIMETYPE,ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE);
            bld.withValue(ContactsContract.CommonDataKinds.Email.DATA,email);
            bld.withValue(ContactsContract.CommonDataKinds.Email.TYPE,ContactsContract.CommonDataKinds.Email.TYPE_HOME);
            lista.add(bld.build());



            // Asking the Contact provider to create a new contact
            try {
                contx.getContentResolver().applyBatch(ContactsContract.AUTHORITY, lista);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(contx, "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        }

    }

    String SELECTION = ContactsContract.Data.MIMETYPE + "=? OR " + ContactsContract.Data.MIMETYPE + "=? OR " + ContactsContract.Data.MIMETYPE+"=?";
    String[] SELECTION_ARGS = new String[]
            {ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE,
                    ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,
                    ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE};

    public List<Contato> getAllContatos() {
        ContentResolver cr = contx.getContentResolver();

        int checkPermission = ContextCompat.checkSelfPermission(contx, Manifest.permission.READ_CONTACTS);
        if (checkPermission != PackageManager.PERMISSION_GRANTED) {
            //solicitar permissao
            String[] permissions = new String[]{Manifest.permission.READ_CONTACTS};
            ActivityCompat.requestPermissions(act, permissions, PERMISSIONS_REQUEST_READ_CONTACTS);
            //se o usuario aceitar automaticamente vai executar onRequestPermissionsResult

        } else {
            Cursor nomes = cr.query(ContactsContract.Contacts.CONTENT_URI,
                    null, ContactsContract.Contacts.HAS_PHONE_NUMBER + " != 0", null, ContactsContract.Contacts._ID + " ASC");//nome

            Cursor dados = cr.query(ContactsContract.Data.CONTENT_URI, null,
                    SELECTION,SELECTION_ARGS,
                    ContactsContract.Data.CONTACT_ID + " ASC"); //email , telefone e endereco

            int idIndex = nomes.getColumnIndexOrThrow(ContactsContract.Contacts._ID);
            int nameIndex = nomes.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME);
            int detailIdIndex = dados.getColumnIndexOrThrow(ContactsContract.Data.CONTACT_ID);
            int dadosIndex = dados.getColumnIndexOrThrow(ContactsContract.Data.DATA1);
            int mimeIdx = dados.getColumnIndexOrThrow(ContactsContract.Data.MIMETYPE);

            boolean hasDetails = dados.moveToNext();
            String contactName = "",contactPhone = "",contactEmail = "",contactAddress = "";

            while (nomes.moveToNext()) {
                long id = nomes.getLong(idIndex);
                contactName = nomes.getString(nameIndex);

                if (hasDetails) {
                    long cDetailIdIndex = dados.getLong(detailIdIndex);
                    while (cDetailIdIndex <= id && hasDetails) {
                        if (cDetailIdIndex == id) {
                            dados.getString(dadosIndex);
                            String mimetype = dados.getString(mimeIdx);
                            if(mimetype.equals(ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)) {
                                contactEmail = dados.getString(dadosIndex);
                            }
                            else if(mimetype.equals(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)){
                                contactPhone = dados.getString(dadosIndex);
                            }else if(mimetype.equals(ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE)){
                                contactAddress = dados.getString(dadosIndex);

                            }

                        }

                        hasDetails = dados.moveToNext();
                        if (hasDetails) {
                            cDetailIdIndex = dados.getLong(detailIdIndex);
                        }//
                    }///fim while

                    contatos.add((new Contato(contactName,contactPhone,contactEmail,contactAddress)));
                    contactEmail="";
                    contactAddress="";
                    contactPhone="";
                }//fim if



            }//fim while
            nomes.close();
            dados.close();
        }//fim if
        return  contatos;
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {
            case PERMISSIONS_REQUEST_WRITE_CONTACTS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    addContact(nome,telefone,email,endereco);

                }
            }
            case PERMISSIONS_REQUEST_READ_CONTACTS:{
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getAllContatos();

                }
            }

        }
    }

    public String getNome(){
        return nome;
    }
    public String getTelefone(){
        return telefone;
    }
    public String getEmail(){
        return email;
    }
    public String getEndereco(){
        return endereco;
    }


    public void setNome(String nome){
        this.nome = nome;
    }
    public void setTelefone(String telefone){
        this.telefone = telefone;
    }
    public void  setEmail(String email){

        this.email = email;
    }
    public void  setEndereco(String endereco){
        this.endereco = endereco;
    }





}
