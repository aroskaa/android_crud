package com.example.simplecrud;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;
import java.util.List;
import android.widget.ArrayAdapter;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.ListView;
import android.view.LayoutInflater;

import android.app.AlertDialog;
import android.widget.LinearLayout;
import android.content.DialogInterface;




public class MainActivity extends AppCompatActivity {

    EditText editTextName, editTextPhone, editTextEmail;
    Button buttonSave;
    Button buttonShowAll;
    DatabaseHandler db;
    ListView listViewUsers;
    UserAdapter userAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DatabaseHandler(this);

        editTextName = findViewById(R.id.editTextName);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextEmail = findViewById(R.id.editTextEmail);
        buttonSave = findViewById(R.id.buttonSave);

        buttonSave.setOnClickListener(v -> {
            String name = editTextName.getText().toString();
            String phone = editTextPhone.getText().toString();
            String email = editTextEmail.getText().toString();

            if (!name.isEmpty() && !phone.isEmpty() && !email.isEmpty()) {
                db.addUser(new User(0, name, phone, email));
                Toast.makeText(MainActivity.this, "Data disimpan", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Harap isi semua field", Toast.LENGTH_SHORT).show();
            }
        });

        buttonShowAll = findViewById(R.id.buttonShowAll);

        final TextView textViewUsers = findViewById(R.id.textViewUsers);
        buttonShowAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userAdapter = new UserAdapter(MainActivity.this, db.getAllUsers());
                listViewUsers.setAdapter(userAdapter);
            }
        });

        listViewUsers = findViewById(R.id.listViewUsers);
    }

    class UserAdapter extends ArrayAdapter<User> {
        UserAdapter(Context context, List<User> users) {
            super(context, R.layout.user_item, users);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.user_item, parent, false);
            }

            User user = getItem(position);

            TextView textViewUserName = convertView.findViewById(R.id.textViewUserName);
            textViewUserName.setText(user.getName());

            Button buttonUpdate = convertView.findViewById(R.id.buttonUpdate);

            buttonUpdate.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Update User");

                    View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.dialog_update, null);
                    final EditText inputName = viewInflated.findViewById(R.id.editTextNewName);
                    inputName.setText(user.getName());
                    final EditText inputPhone = viewInflated.findViewById(R.id.editTextNewPhone);
                    inputPhone.setText(user.getPhone());
                    final EditText inputEmail = viewInflated.findViewById(R.id.editTextNewEmail);
                    inputEmail.setText(user.getEmail());

                    builder.setView(viewInflated);

                    builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String newName = inputName.getText().toString();
                            String newPhone = inputPhone.getText().toString();
                            String newEmail = inputEmail.getText().toString();

                            User updatedUser = new User(user.getId(), newName, newPhone, newEmail);
                            int rowsAffected = db.updateUser(updatedUser);
                            if (rowsAffected > 0) {
                                Toast.makeText(getContext(), "Data pengguna berhasil diperbarui", Toast.LENGTH_SHORT).show();
                                // Perbarui ListView setelah pengguna diperbarui
                                userAdapter.clear();
                                userAdapter.addAll(db.getAllUsers());
                                userAdapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(getContext(), "Gagal memperbarui data pengguna", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.show();
                }
            });

            Button buttonDelete = convertView.findViewById(R.id.buttonDelete);
            buttonDelete.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    db.deleteUser(user);
                    remove(user);
                    notifyDataSetChanged();
                }
            });

            return convertView;
        }
    }

}

