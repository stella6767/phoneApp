package com.cos.phoneapp.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cos.phoneapp.MainActivity;
import com.cos.phoneapp.model.Phone;
import com.cos.phoneapp.R;

import java.util.List;

//어댑터와 RecyclerView와 연결(Databinding 사용금지)(MVVM 사용금지)
public class PhoneAdapter extends RecyclerView.Adapter<PhoneAdapter.MyviewHolder>{

    private static final String TAG = "PhoneAdapter";

    private MainActivity mainActivity;
    private List<Phone> phones;

    public PhoneAdapter(MainActivity mainActivity, List<Phone> phones) {
        this.mainActivity = mainActivity;
        this.phones = phones;
    }

    @NonNull
    @Override
    public MyviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater)parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.phone_item,parent,false);

        return new MyviewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyviewHolder holder, int position) {
        holder.setItem(phones.get(position));
    }

    @Override
    public int getItemCount() {
        return phones.size();
    }

    public class MyviewHolder extends RecyclerView.ViewHolder{

        private TextView name,tel;

        public MyviewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            tel = itemView.findViewById(R.id.tel);

            itemView.setOnClickListener(v -> {
                Log.d(TAG, "클릭됨"+getAdapterPosition());
                Phone phone = phones.get(getAdapterPosition());
                mainActivity.editContact(getAdapterPosition(),phone);
            });
        }

        public void setItem(Phone phone){
            name.setText(phone.getName());
            tel.setText(phone.getTel());

        }
    }
}
