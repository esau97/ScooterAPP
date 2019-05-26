package com.kidev.adrian.scooterapp.activities;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.kidev.adrian.scooterapp.R;
import com.kidev.adrian.scooterapp.util.CallbackRespuesta;
import com.kidev.adrian.scooterapp.util.ConectorTCP;
import com.kidev.adrian.scooterapp.util.Util;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Button loginButton;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Nos conectamos con el servidor
        ConectorTCP.getInstance();

        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void login () {
        TextView textoNick = findViewById(R.id.nickLogin);
        TextView textoPass = findViewById(R.id.passLogin);

        //TODO: Pasar a la constante de idioma
        if (textoNick.getText().toString().isEmpty()) {
            Util.crearErrorDialog(this, "Debes introducir el nick");
        } else if (textoPass.getText().toString().isEmpty()) {
            Util.crearErrorDialog(this, "Debes introducir la pass");
        }

        Map<String,String> parametros = new HashMap<>();
        parametros.put("nick", textoNick.getText().toString());
        parametros.put("pass", textoPass.getText().toString());

        loginButton.setEnabled(false);

        final Activity activity = this;

        ConectorTCP.getInstance().realizarConexion("login", parametros, new CallbackRespuesta() {
            @Override
            public void success(Map<String, String> contenido) {
                Log.i("Conexión exitosa", "La conexión se ha realizado satisfactoriamente");

                Intent intent = new Intent(getApplication(), MenuActivity.class);
                for (Map.Entry<String, String> entry : contenido.entrySet()) {
                    intent.putExtra(entry.getKey(), entry.getValue());
                }

                startActivity(intent);
            }

            @Override
            public void error(Map<String, String> contenido, Util.CODIGO codigoError) {
                Log.e("Error de conexión", codigoError.toString());
                Util.crearErrorDialog(activity, contenido.get("error"));
                loginButton.setEnabled(true);
            }
        });
    }
}
