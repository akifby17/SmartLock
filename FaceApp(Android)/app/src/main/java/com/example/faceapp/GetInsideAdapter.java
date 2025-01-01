package com.example.faceapp;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class GetInsideAdapter extends RecyclerView.Adapter<GetInsideAdapter.PersonViewHolder> {
    /*Veri tabanında veriler tablo halinde tutulduğu için doğrudan Androide gösterilemiyor.
    * Api ye istek atıldığında veri JSON formatında geliyor.
    * JSON olarak gelen veri önce uygun bir nesne (Persons) haline getiriliyor.
    * Sonrasında CustomAdapter sınıfı yardımı ile oluşturulan Persons nesnesinin özellikleri
    * Recyliviewe aktarılıyor böylelikle veri tabanındaki veri dolaylı yollardan ekranda gösteriliyor
    * Bu sınıf bir CustomAdapter sınıfı Getİnside aktivitesi için kullanılıyor
    * */
    private String nameSurname;
    String hour;
    private ArrayList<Persons> personsList;
    public GetInsideAdapter(ArrayList<Persons> personsList) {
        this.personsList = personsList;
    }
    @NonNull
    @Override
    public PersonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_inside, parent, false);
        return new PersonViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull PersonViewHolder holder, int position) {
        Persons person = personsList.get(position);
        nameSurname=person.getName()+" "+person.getSurName();
        holder.nameSurname.setText(nameSurname);
        holder.hour.setText("Giriş Saati:"+person.getDate());
    }
    @Override
    public int getItemCount() {
        return personsList.size();
    }
    public static class PersonViewHolder extends RecyclerView.ViewHolder {
        TextView nameSurname; TextView personId; TextView hour;
        public PersonViewHolder(@NonNull View itemView) {
            super(itemView);
            nameSurname = itemView.findViewById(R.id.tvInsideName);
            hour=itemView.findViewById(R.id.tvEntryTime);
        }
    }
}
