package com.example.faceapp;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class PersonsAdapter extends RecyclerView.Adapter<PersonsAdapter.PersonViewHolder>
/*Bu CustomAdapter sınıfı ise veri tabanından gelen persons nesnesine ekranda uygun formatta göstermek için oluşturuldu
*
* */

{
    private String nameSurname;
    private String loginDate;
    private String logouttDate;
    private ArrayList<Persons> personsList;
    public PersonsAdapter(ArrayList<Persons> personsList) {
        this.personsList = personsList;
    }
    @NonNull
    @Override
    public PersonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_person, parent, false);
        return new PersonViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull PersonViewHolder holder, int position) {
        Persons person = personsList.get(position);
        nameSurname=person.getName()+" "+person.getSurName();
        loginDate=person.getLoginDate();
        logouttDate=person.getLogoutDate();
        holder.nameSurname.setText(nameSurname);
        holder.tvLogin.setText(loginDate);
        holder.tvLogout.setText(logouttDate);
    }
    @Override
    public int getItemCount() {
        return personsList.size();
    }
    public static class PersonViewHolder extends RecyclerView.ViewHolder {
        TextView tvLogin;TextView tvLogout; TextView nameSurname;
        public PersonViewHolder(@NonNull View itemView) {
            super(itemView);
            nameSurname = itemView.findViewById(R.id.tvUserName);
            tvLogin=itemView.findViewById(R.id.tvInput);
            tvLogout=itemView.findViewById(R.id.tvOutput);
        }
    }
}
