package com.meclajerpc.miguelsegundaop;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class FragmentEstadoActual extends Fragment implements SensorEventListener {

    /* ================================== FIREBASE ================================== */
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    /* ================================== VISTA ================================== */
    //Datos Movimiento
    TextView valX, valY, valZ, valAcelX, valAcelY, valAcelZ, cantPasos;
    //Datos posicion
    TextView distancia;
    //Datos de entorno
    TextView ambiente, luz;
    //Switches
    Switch swAcelerometro, swAceleracion, swPodometro, swProximidad, swtemperatura, swIluminacion;

    //Botones
    Button completa, continua;


    /* ================================== SONSORES ================================== */
    //Servicio de sensores
    private SensorManager mSensorManager;

    //Principales Sensores de movimiento
    public Sensor acelerometro;
    public Sensor acelerometroLinear;
    public Sensor podometro;
    //Principales Sensores de posicion
    public Sensor proximidad;
    //Principales Sensores de entorno
    public Sensor temperaturaAmbiente;
    public Sensor iluminacion;

    /* ================================== FORMATO PARA PRESENTACION ================================== */
    public DecimalFormat formato = new DecimalFormat("###.####");
    Date date = new Date();
    public DateFormat hourFormat = new SimpleDateFormat("HH:mm:ss");
    public DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    /* =========================================== OBJETOS =========================================== */
    RegistroTotalContinuo registro;

    /* =========================================== BANDERAS =========================================== */
    public int cantidadCapturas = 0;
    public boolean capturar = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fragment_estado_actual, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        /* ================================== INSTANCIAS DE VISTAS ================================== */
        //Vistas
        valX = getView().findViewById(R.id.valAcelerometro);
        valY = getView().findViewById(R.id.valActualY);
        valZ = getView().findViewById(R.id.valActualZ);

        valAcelX = getView().findViewById(R.id.valActualAceleracionX);
        valAcelY = getView().findViewById(R.id.valActualAceleracionY);
        valAcelZ = getView().findViewById(R.id.valAceleracion);

        cantPasos = getView().findViewById(R.id.valPasos);

        distancia = getView().findViewById(R.id.valorDistancia);

        ambiente = getView().findViewById(R.id.valTemperatura);
        luz = getView().findViewById(R.id.valIluminacion);

        swAcelerometro = getView().findViewById(R.id.switchAcelerometro);
        swAceleracion = getView().findViewById(R.id.switchAceleracion);
        swPodometro = getView().findViewById(R.id.switchPodometro);
        swProximidad = getView().findViewById(R.id.switchProximidad);
        swtemperatura = getView().findViewById(R.id.switchTemperatura);
        swIluminacion = getView().findViewById(R.id.switchIluminacion);

        completa = getView().findViewById(R.id.buttonCapturaCompleta);
        continua = getView().findViewById(R.id.buttonCapturaContinua);

        /* ================================== INSTANCIA DE SENSORES ================================== */
        //Servicio de sensores
        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        acelerometro = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        Log.i( "EVENTO", "manager creado" );
        //Principales Sensores de movimiento
        acelerometro = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, acelerometro, SensorManager.SENSOR_DELAY_NORMAL);
        acelerometroLinear = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        mSensorManager.registerListener(this, acelerometroLinear, SensorManager.SENSOR_DELAY_NORMAL);
        podometro = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        mSensorManager.registerListener(this, podometro, SensorManager.SENSOR_DELAY_NORMAL);
        //Principales Sensores de posicion
        proximidad = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        mSensorManager.registerListener(this, proximidad, SensorManager.SENSOR_DELAY_NORMAL);
        //Principales Sensores de entorno
        temperaturaAmbiente = mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        mSensorManager.registerListener(this, temperaturaAmbiente, SensorManager.SENSOR_DELAY_NORMAL);
        iluminacion = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        mSensorManager.registerListener(this, iluminacion, SensorManager.SENSOR_DELAY_NORMAL);


        /* ================================== INSTANCIA FIREBASE ================================== */
        FirebaseApp.initializeApp(getActivity()); //inicia la base datos
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        //activa el guardado de datos de forma persistente en caso de no tener conexion
        //  a internet.
        databaseReference.keepSynced(true);

        //OBJETOS
        registro = new RegistroTotalContinuo();

        //Eventos
        completa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RegistroCompleto mRegistroCompleto = new RegistroCompleto();

                if( swAcelerometro.isChecked() )
                    mRegistroCompleto.setAcelerometro( "X: " + valX.getText() + ". Y: " + valY.getText() + ". Z: " + valZ.getText() + "." );
                if( swAceleracion.isChecked() )
                    mRegistroCompleto.setAceleracion(  "X: " + valAcelX.getText() + " Y: " + valAcelY.getText() + " Z: " + valAcelZ.getText() + "."  );
                if( swPodometro.isChecked() )
                    mRegistroCompleto.setPodometro( cantPasos.getText().toString() + " pasos." );
                if( swProximidad.isChecked() )
                    mRegistroCompleto.setProximidad(  distancia.getText().toString() + "."  );
                if( swtemperatura.isChecked() )
                    mRegistroCompleto.setTemperatura(  ambiente.getText().toString() + "."  );
                if( swIluminacion.isChecked() )
                    mRegistroCompleto.setIluminacion(  luz.getText().toString() + "."  );

                mRegistroCompleto.setHora( hourFormat.format(date) );
                mRegistroCompleto.setFecha( dateFormat.format(date) );

                databaseReference.child("Registro").child(mRegistroCompleto.getID()).setValue( mRegistroCompleto );

            }
        });
        continua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                capturar = true;
            }
        });

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (cantidadCapturas > 100){
            capturar = false;
            cantidadCapturas = 0;
        }

        Log.i( "EVENTO", formato.format(event.values[0]) );

        switch (event.sensor.getType()){
            // ======================= Sensores de movimiento =======================
            case Sensor.TYPE_ACCELEROMETER:
                valX.setText( formato.format(event.values[0]) + " m/s^2" );
                valY.setText( formato.format(event.values[1]) + " m/s^2" );
                valZ.setText( formato.format(event.values[2]) + " m/s^2" );

                if(capturar){
                    registro.setSensor("ACCELEROMETER");
                    registro.setValor("X: "+ formato.format(event.values[0]) + " m/s^2" + ", Y: " + formato.format(event.values[1]) + " m/s^2" + ", Z: " + formato.format(event.values[2]) + " m/s^2.");
                    registro.setHora( hourFormat.format(date) );
                    registro.setFecha( dateFormat.format(date) );

                    databaseReference.child("Registro").child(registro.getID()).setValue( registro );
                    cantidadCapturas++;
                }
                break;
            case Sensor.TYPE_LINEAR_ACCELERATION:
                valAcelX.setText( formato.format(event.values[0]) + " m/s^2" );
                valAcelY.setText( formato.format(event.values[1]) + " m/s^2" );
                valAcelZ.setText( formato.format(event.values[2]) + " m/s^2" );

                if(capturar){
                    Log.i("Estado","Cantidad: "+cantidadCapturas);
                    registro.setSensor("LINEAR_ACCELERATION");
                    registro.setValor("X: "+ formato.format(event.values[0]) + " m/s^2" + ", Y: " + formato.format(event.values[1]) + " m/s^2" + ", Z: " + formato.format(event.values[2]) + " m/s^2.");
                    registro.setHora(hourFormat.format(date));
                    registro.setFecha(dateFormat.format(date));

                    databaseReference.child("Registro").child(registro.getID()).setValue( registro );
                    cantidadCapturas++;
                }

                break;
            case Sensor.TYPE_STEP_COUNTER:
                cantPasos.setText( event.values[0] + "" );

                if(capturar){
                    registro.setSensor("STEP_COUNTER");
                    registro.setValor( formato.format(event.values[0]) + " pasos.");
                    registro.setHora(hourFormat.format(date));
                    registro.setFecha(dateFormat.format(date));

                    databaseReference.child("Registro").child(registro.getID()).setValue( registro );
                    cantidadCapturas++;
                }

                break;
            // ======================== Sensores de posicion ========================
            case Sensor.TYPE_PROXIMITY:
                distancia.setText( formato.format(event.values[0]) + " cm" );

                if(capturar){
                    registro.setSensor("PROXIMITY");
                    registro.setValor( formato.format(event.values[0]) + " cm.");
                    registro.setHora(hourFormat.format(date));
                    registro.setFecha(dateFormat.format(date));

                    databaseReference.child("Registro").child(registro.getID()).setValue( registro );
                    cantidadCapturas++;
                }

                break;
            // ======================== Sensores de entorno =========================
            case Sensor.TYPE_AMBIENT_TEMPERATURE:
                ambiente.setText( event.values[0] + " °C" );

                if(capturar){
                    registro.setSensor("AMBIENT_TEMPERATURE");
                    registro.setValor( formato.format(event.values[0]) + " °C.");
                    registro.setHora(hourFormat.format(date));
                    registro.setFecha(dateFormat.format(date));

                    databaseReference.child("Registro").child(registro.getID()).setValue( registro );
                    cantidadCapturas++;
                }

                break;
            case Sensor.TYPE_LIGHT:
                luz.setText( event.values[0] + " lx" );

                if(capturar){
                    Log.i("Estado","Cantidad: "+cantidadCapturas);
                    registro.setSensor("LIGHT");
                    registro.setValor( formato.format(event.values[0]) + " lx.");
                    registro.setHora( hourFormat.format(date) );
                    registro.setFecha( dateFormat.format(date) );

                    databaseReference.child("Registro").child(registro.getID()).setValue( registro );
                    cantidadCapturas++;
                }

                break;

        }

        date = new Date();

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    //Ciclo de vida
    @Override
    public void onResume() {
        super.onResume();

        mSensorManager.registerListener(this, acelerometro, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, acelerometroLinear, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, podometro, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, proximidad, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, temperaturaAmbiente, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, iluminacion, SensorManager.SENSOR_DELAY_NORMAL);

    }

    @Override
    public void onPause() {
        super.onPause();

        mSensorManager.unregisterListener(this);

    }

    @Override
    public void onStop () {
        super.onStop();
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

class RegistroTotalContinuo{

    public String sensor;
    public String valor;
    public String hora;
    public String fecha;

    public RegistroTotalContinuo(){ }
    public RegistroTotalContinuo(String sensor, String valor, String hora, String fecha) {
        this.sensor = sensor;
        this.valor = valor;
        this.hora = hora;
        this.fecha = fecha;
    }

    public String getID() {
        String id = fecha + "-" + hora;
        id = id.replace("/",":");
        id = sensor + "/" + id;
        return id;
    }

    public void setSensor(String sensor) { this.sensor = sensor; }
    public void setValor(String valor) { this.valor = valor; }
    public void setHora(String hora) { this.hora = hora; }
    public void setFecha(String fecha) { this.fecha = fecha; }

}

class RegistroCompleto{

    public String acelerometro = "Acelerometro: ";
    public String aceleracion = "Aceleracion: ";
    public String podometro = "Podometro: ";
    public String proximidad = "Proximidad: ";
    public String temperatura = "Temperatura: ";
    public String iluminacion = "Iluminacion: ";
    public String hora;
    public String fecha;


    public RegistroCompleto() { }
    public RegistroCompleto(String acelerometro, String aceleracion, String podometro, String proximidad, String temperatura, String iluminacion, String hora, String fecha) {
        this.acelerometro = acelerometro;
        this.aceleracion = aceleracion;
        this.podometro = podometro;
        this.proximidad = proximidad;
        this.temperatura = temperatura;
        this.iluminacion = iluminacion;
        this.hora = hora;
        this.fecha = fecha;
    }

    public String getID() {
        String id = fecha + "-" + hora;
        id = id.replace("/",":");
        id = "COMPLETO/" + id;
        return id;
    }

    public void setAcelerometro(String acelerometro) { this.acelerometro = this.acelerometro + acelerometro; }
    public void setAceleracion(String aceleracion) { this.aceleracion = this.aceleracion + aceleracion; }
    public void setPodometro(String podometro) { this.podometro = this.podometro + podometro; }
    public void setProximidad(String proximidad) { this.proximidad = this.proximidad + proximidad; }
    public void setTemperatura(String temperatura) { this.temperatura = this.temperatura + temperatura; }
    public void setIluminacion(String iluminacion) { this.iluminacion = this.iluminacion + iluminacion; }
    public void setHora(String hora) { this.hora = hora; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public String getAcelerometro() { return acelerometro; }
    public String getAceleracion() { return aceleracion; }
    public String getPodometro() { return podometro; }
    public String getProximidad() { return proximidad; }
    public String getTemperatura() { return temperatura; }
    public String getIluminacion() { return iluminacion; }
    public String getHora() { return hora; }
    public String getFecha() { return fecha; }
}
