package com.emanuelle.easycontacts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.emanuelle.easycontacts.pojo.Contato;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * Created by Emanuelle on 19/09/2016.
 */
public class ListaContatosAdapter extends ArrayAdapter<Contato> {
    private LayoutInflater mInflater;
    private List<Contato> contatos = null;
    private int mViewResourceId;
    private List<Contato> contatosFiltered = new ArrayList<>();;

    public ListaContatosAdapter(Context context,int viewResourceId,  List<Contato> contatos) {
        super(context,viewResourceId, contatos);
        mInflater = (LayoutInflater)context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);

        this.contatos = contatos;
        mViewResourceId = viewResourceId;
        this.contatosFiltered.addAll(contatos);
    }

    public boolean filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        contatos.clear();
        boolean isEmpty = false;
        if (charText.length() == 0) {
            contatos.addAll(contatosFiltered);
        }
        else
        {
            for (Contato cnt : contatosFiltered)
            {
                if (cnt.getNome().toLowerCase().toLowerCase(Locale.getDefault()).contains(charText))
                {
                    contatos.add(cnt);

                }
                 if (cnt.getTelefone().toLowerCase().toLowerCase(Locale.getDefault()).contains(charText)){
                    contatos.add(cnt);
                }
                 if (cnt.getEndereco().toLowerCase().toLowerCase(Locale.getDefault()).contains(charText)){
                    contatos.add(cnt);

                }
            }
        }
        notifyDataSetChanged();
        if(contatos.size()== 0) {
            isEmpty = true;
        }
        return isEmpty;
    }

    @Override
    public int getCount() {
        return contatos.size();
    }

    @Override
    public Contato getItem(int position) {
        return contatos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertview, ViewGroup parent) {
        convertview = mInflater.inflate(mViewResourceId,null);

        TextView textViewNomeContato = (TextView) convertview.findViewById(R.id.text_view_nome_contatos);
        textViewNomeContato.setText(contatos.get(position).getNome());

        TextView textViewTelefone = (TextView)convertview.findViewById(R.id.text_view_nome_telefone);

        textViewTelefone.setText(contatos.get(position).getTelefone());



        return convertview;
    }

    public void sortByName(){
        try {
            Collections.sort(contatos, new Comparator<Contato>() {
                @Override
                public int compare(Contato lhs, Contato rhs) {
                    return lhs.getNome().compareTo(rhs.getNome());
                }
            });
        }catch (NullPointerException e){

        }

        notifyDataSetChanged();
    }

    public void sortByTelefone(){
        try {
            Collections.sort(contatos, new Comparator<Contato>() {
                @Override
                public int compare(Contato lhs, Contato rhs) {
                    return lhs.getTelefone().compareTo(rhs.getTelefone());
                }
            });
        }
        catch (NullPointerException e ){

        }
        notifyDataSetChanged();
    }
}
