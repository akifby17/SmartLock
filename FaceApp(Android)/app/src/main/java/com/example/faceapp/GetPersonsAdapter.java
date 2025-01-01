package com.example.faceapp;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
/*Buradaki CustomAdapter de aynı GetInsideAdapter sınıfındaki mantıkla aynı
* Bu sınıfta Kayıtlı Personel Listesini getiren bir CustomAdapter özelliği gösteriyor.
* */

public class GetPersonsAdapter extends RecyclerView.Adapter<GetPersonsAdapter.PersonViewHolder> {
    private String nameSurname;
    private ArrayList<Persons> personsList;
    public GetPersonsAdapter(ArrayList<Persons> personsList) {
        this.personsList = personsList;
    }
    @NonNull
    @Override
    public PersonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_person, parent, false);
        return new PersonViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull PersonViewHolder holder, int position) {
        Persons person = personsList.get(position);
        nameSurname=person.getName()+" "+person.getSurName();
        holder.nameSurname.setText(nameSurname);
        holder.personId.setText("Personel No:"+person.getUserId());
        holder.date.setText("Kayıt Tarihi:"+person.getDate());

    }
    @Override
    public int getItemCount() {
        return personsList.size();
    }
    public static class PersonViewHolder extends RecyclerView.ViewHolder {
        TextView nameSurname; TextView personId; TextView date;
        public PersonViewHolder(@NonNull View itemView) {
            super(itemView);
            nameSurname = itemView.findViewById(R.id.tvPName);
            personId=itemView.findViewById(R.id.tvPersonNo);
            date=itemView.findViewById(R.id.tvRegistrationDate);

        }
    }
}
