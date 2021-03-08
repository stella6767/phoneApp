package com.cos.phoneapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.cos.phoneapp.adapter.PhoneAdapter;
import com.cos.phoneapp.model.CMRespDto;
import com.cos.phoneapp.model.Phone;
import com.cos.phoneapp.service.PhoneService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity2";

    private RecyclerView rvPhoneList;
    private PhoneAdapter phoneAdapter;
    private FloatingActionButton fab;
    private List<Phone> phones = new ArrayList<>();

    PhoneService phoneService;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initData();

        fab.setOnClickListener(v -> {
            addContact();
        });

    }


    public void initView(){
        rvPhoneList = findViewById(R.id.rv_phone);
        fab = findViewById(R.id.fab_save);
        phoneService = PhoneService.retrofit.create(PhoneService.class);

    }

    public void initData(){
        Call<CMRespDto<List<Phone>>> call = phoneService.findAll();

        call.enqueue(new Callback<CMRespDto<List<Phone>>>() {
            @Override
            public void onResponse(Call<CMRespDto<List<Phone>>> call, Response<CMRespDto<List<Phone>>> response) {
                CMRespDto<List<Phone>> cmRespDto = response.body();
                phones = cmRespDto.getData();
                //어댑터에게 넘기기
                Log.d(TAG, "onResponse: 응답 받은 데이터"+phones);

                LinearLayoutManager manager = new LinearLayoutManager(MainActivity.this, RecyclerView.VERTICAL, false);
                rvPhoneList.setLayoutManager(manager);
                phoneAdapter = new PhoneAdapter(MainActivity.this, phones);

                rvPhoneList.setAdapter(phoneAdapter);

            }

            @Override
            public void onFailure(Call<CMRespDto<List<Phone>>> call, Throwable t) {
                Log.d(TAG, "onFailure: findAll() 실패");
            }
        });

    }


    public void addContact() {
        View dialogView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.add_phone, null);

        final EditText etName = dialogView.findViewById(R.id.name);
        final EditText etTel = dialogView.findViewById(R.id.tel);

        AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
        dlg.setTitle("연락처 등록");
        dlg.setView(dialogView);
        dlg.setPositiveButton("등록", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                createContact(etName.getText().toString(), etTel.getText().toString());
            }
        });
        dlg.setNegativeButton("닫기", null);
        dlg.show();

    }


    public void editContact(int position,Phone phone) {
        View dialogView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.add_phone, null);

        EditText etName = dialogView.findViewById(R.id.name);
        EditText etTel = dialogView.findViewById(R.id.tel);

        etName.setText(phone.getName());
        etTel.setText(phone.getTel());

        int id = (phone.getId()).intValue();

        Log.d(TAG, "editContact: position:" + position + "   id: " + id);

        AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
        dlg.setTitle("연락처 수정");
        dlg.setView(dialogView);
        dlg.setPositiveButton("수정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                updateContact(etName.getText().toString(), etTel.getText().toString(), position,id);
            }
        });

        dlg.setNegativeButton("삭제", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteContact(position,id);
            }
        });
        dlg.show();
    }



    public void createContact(String name, String email) {
        Long id = null;
        Phone phone = new Phone(id,name,email);

        Call<CMRespDto<Phone>> call = phoneService.save(phone);

        call.enqueue(new Callback<CMRespDto<Phone>>() {
            @Override
            public void onResponse(Call<CMRespDto<Phone>> call, Response<CMRespDto<Phone>> response) {
                CMRespDto<Phone> cmRespDto = response.body();
                Phone phone = cmRespDto.getData();
                phones.add(phone);
                phoneAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<CMRespDto<Phone>> call, Throwable t) {
                Log.d(TAG, "onFailure: 연락처 등록실패");
            }
        });
    }


    public void updateContact(String name, String email, int position, int id) {

        Phone phone = new Phone(null,name,email);
        Call<CMRespDto<Phone>> call = phoneService.update(id, phone);

        call.enqueue(new Callback<CMRespDto<Phone>>() {
            @Override
            public void onResponse(Call<CMRespDto<Phone>> call, Response<CMRespDto<Phone>> response) {
                CMRespDto<Phone> cmRespDto = response.body();
                Phone phone = cmRespDto.getData();
                phones.set(position, phone);
                phoneAdapter.notifyDataSetChanged();

            }

            @Override
            public void onFailure(Call<CMRespDto<Phone>> call, Throwable t) {
                Log.d(TAG, "onFailure: 연락처 수정 실패!");
            }
        });
    }

    public void deleteContact(int position,int id){


        Call<CMRespDto<Phone>> call = phoneService.delete(id);

        call.enqueue(new Callback<CMRespDto<Phone>>() {
            @Override
            public void onResponse(Call<CMRespDto<Phone>> call, Response<CMRespDto<Phone>> response) {
                CMRespDto<Phone> cmRespDto = response.body();
                Log.d(TAG, "onResponse: 삭제" + cmRespDto);
                phones.remove(position);
                phoneAdapter.notifyDataSetChanged();

            }

            @Override
            public void onFailure(Call<CMRespDto<Phone>> call, Throwable t) {
                Log.d(TAG, "onFailure: 연락처 삭제 실패");
            }
        });

    }
}