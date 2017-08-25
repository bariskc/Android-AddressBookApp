package com.example.bariskoc.addressbook;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bariskoc.addressbook.model.MyContext;
import com.example.bariskoc.addressbook.model.Kisi;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.cketti.mailto.EmailIntentBuilder;

public class MainActivity extends AppCompatActivity {

    Button btnYeni;
    ListView listView;
    ProgressDialog progressDialog;
    FirebaseDatabase database;
    DatabaseReference myRef;
    BaseAdapter baseAdapter;
    LayoutInflater layoutInflater;
    ArrayList<Kisi> gelen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.listView);
        btnYeni = (Button) findViewById(R.id.btnYeni);

        btnYeni.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, YeniActivity.class);
                intent.putExtra("yeni", true);
                startActivity(intent);
            }
        });

        layoutInflater = layoutInflater.from(this);

        baseAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                if (gelen == null)
                    return 0;
                return gelen.size();

            }

            @Override
            public Object getItem(int i) {
                return null;
            }

            @Override
            public long getItemId(int i) {
                return 0;
            }

            @Override
            public View getView(int i, View view, ViewGroup viewGroup) {
                if (view == null) {
                    view = layoutInflater.inflate(R.layout.mylist_item, null);
                }
                final Kisi basilacakKisi = gelen.get(i);
                TextView txtad = view.findViewById(R.id.viewTxtAd);
                TextView txtsoyad = view.findViewById(R.id.viewtxtSoyad);
                Button btnara = view.findViewById(R.id.viewBtnAra);
                Button btnmail = view.findViewById(R.id.viewBtnMail);

                final int pos = i;
                btnara.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (TextUtils.isEmpty(gelen.get(pos).getTelefon())) {
                            Toast.makeText(MainActivity.this, R.string.msgtelefonnumarasiyok, Toast.LENGTH_SHORT).show();
                        } else {
                            Intent intent = new Intent(Intent.ACTION_CALL);
                            intent.setData(Uri.parse("tel:"+gelen.get(pos).getTelefon()));
                            if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.CALL_PHONE},100);

                                return;
                            }
                            startActivity(intent);
                        }


                    }
                });

                btnmail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (TextUtils.isEmpty(gelen.get(pos).getMail())) {
                            Toast.makeText(MainActivity.this, R.string.msgmailadresiyok, Toast.LENGTH_SHORT).show();
                        } else {
                            /*Intent intent = new Intent(Intent.ACTION_SEND);
                            intent.setType("message/rfc822");
                            intent.putExtra(Intent.EXTRA_EMAIL,gelen.get(pos).getMail());
                            intent.putExtra(Intent.EXTRA_SUBJECT, "Bu mail AddressBook Uygulamasından");
                            intent.putExtra(Intent.EXTRA_TEXT,"Deneme");*/

                            Kisi kisi = gelen.get(pos);
                            EmailIntentBuilder.from(MainActivity.this)
                                    .to(kisi.getMail())
                                    .subject("via AdressBook App")
                                    .body(kisi.getAd() + " " + kisi.getSoyad())
                                    .start();

                            //startActivity(intent.createChooser(intent,"Mail Gönder"));
                        }
                    }
                });

                txtad.setText(basilacakKisi.getAd());
                txtsoyad.setText(basilacakKisi.getSoyad());

                txtad.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(MainActivity.this, YeniActivity.class);
                        intent.putExtra("yeni", false);
                        intent.putExtra("kisi", gelen.get(pos).getId());
                        startActivity(intent);
                    }
                });
                txtad.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        showProgressDialog("Lütfen bekleyin", "Kayıt siliniyor");
                        database = FirebaseDatabase.getInstance();
                        myRef = database.getReference().child("kisiler").child(gelen.get(pos).getId());
                        myRef.removeValue();
                        dbGetir();
                        return true;
                    }
                });
                return view;
            }
        };
        listView.setAdapter(baseAdapter);

/*        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                //Kisi seciliKisi = MyContext.Kisiler.get(position);
                ArrayAdapter<Kisi> adapter = (ArrayAdapter<Kisi>) listView.getAdapter();
                Kisi seciliKisi = adapter.getItem(position);
                Intent intent = new Intent(MainActivity.this, YeniActivity.class);
                intent.putExtra("yeni", false);
                intent.putExtra("kisi", seciliKisi.getId());
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                ArrayAdapter<Kisi> adapter = (ArrayAdapter<Kisi>) listView.getAdapter();
                String id = adapter.getItem(position).getId();


                database = FirebaseDatabase.getInstance();
                myRef = database.getReference().child("kisiler").child(id);
                myRef.removeValue();
                dbGetir();
                return true;
            }
        });*/

    }

    @Override
    protected void onStart() {
        dbGetir();
        super.onStart();


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==100) {
            boolean izinVerildiMi = grantResults[0] == PackageManager.PERMISSION_GRANTED;
            if (!izinVerildiMi) {
                Toast.makeText(this, R.string.aramamsgiznigerekli, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void dbGetir() {
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference().child("kisiler");
        showProgressDialog("Lütfen bekleyin", "Veri tabanı bağlantısı kuruluyor");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                hideProgressDialog();
                gelen = new ArrayList<Kisi>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Kisi g = postSnapshot.getValue(Kisi.class);
                    gelen.add(g);
                }
                if (gelen.size() == 0) return;
                //ArrayAdapter<Kisi> adapter = new ArrayAdapter<Kisi>(MainActivity.this, R.layout.mylist_item, gelen);
                baseAdapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    public void showProgressDialog(String title, String message) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(message);
            progressDialog.setTitle(title);
            progressDialog.setIndeterminate(true);
        }
        progressDialog.show();

    }

    public void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}

