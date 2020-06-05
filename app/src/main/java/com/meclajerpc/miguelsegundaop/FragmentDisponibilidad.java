package com.meclajerpc.miguelsegundaop;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;


public class FragmentDisponibilidad extends Fragment{

    TextView sensoresDisponibles;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fragment_disponibilidad, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        sensoresDisponibles = getView().findViewById(R.id.tV_Resultado);

        SensorManager mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);

        List<Sensor> listaSensores = mSensorManager.getSensorList(Sensor.TYPE_ALL);
        for(Sensor sensor: listaSensores) {
            sensoresDisponibles.append(sensor.getName()+"\n Rango maximo: "+sensor.getMaximumRange()+"\n\n");
        }

    }

    //Ciclo de vida

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop () {
        super.onStop();
    }

    @Override
    public void onDestroyView () {
        super.onDestroyView();
    }

    @Override
    public void onDestroy () {
        super.onDestroy();
    }

    @Override
    public void onDetach () {
        super.onDetach();
    }

}
