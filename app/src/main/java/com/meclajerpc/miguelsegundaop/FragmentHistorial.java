package com.meclajerpc.miguelsegundaop;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class FragmentHistorial extends Fragment {

    /* ================================== FIREBASE ================================== */
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    /* ================================== VISTA ================================== */
    //Datos Movimiento
    TextView acelerometro, aceleroacion, cantPasos;
    //Datos posicion
    TextView distancia;
    //Datos de entorno
    TextView ambiente, luz;
    //Botones
    Button consultar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fragment_historial, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        FirebaseApp.initializeApp(getActivity());
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        //Vistas
        acelerometro = getView().findViewById(R.id.valAcelerometro);

        aceleroacion = getView().findViewById(R.id.valAceleracion);

        cantPasos = getView().findViewById(R.id.valPasos);

        distancia = getView().findViewById(R.id.valorDistancia);

        ambiente = getView().findViewById(R.id.valTemperatura);

        luz = getView().findViewById(R.id.valIluminacion);

        consultar = getView().findViewById(R.id.btnConsultar);
        consultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                consultarBD();
            }
        });


    }

    public void consultarBD(){

        databaseReference.child("Registro/COMPLETO").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot objSnapshot : dataSnapshot.getChildren()) {

                    RegistroCompleto mRegistroCompleto = objSnapshot.getValue(RegistroCompleto.class);

                    acelerometro.setText(mRegistroCompleto.getAcelerometro());

                    aceleroacion.setText(mRegistroCompleto.getAceleracion());

                    cantPasos.setText(mRegistroCompleto.getPodometro());

                    distancia.setText(mRegistroCompleto.getProximidad());

                    ambiente.setText(mRegistroCompleto.getTemperatura());

                    luz.setText(mRegistroCompleto.getIluminacion());

                }
                databaseReference.child("Registro/COMPLETO").removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Consulta cancelada...", Toast.LENGTH_SHORT);
            }
        });

    }

}
