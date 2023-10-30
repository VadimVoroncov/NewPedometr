package com.example.newpedometr;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    public boolean active = true;
    private SensorManager sensorManager;// Объект для работы с датчиком
    private int count = 0;// Кол-во шагов
    private TextView text;// Ссылка на TextView
    private long lastUpdate;// Время последнего изменения состояния датчика

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text = findViewById(R.id.textView2);// находим текст
        text.setText(String.valueOf(count));// создаём объект для работы с датчиком
        //регистрируем класс, где будет реализован метод, вызываемый при изменении
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        //запускаем датчик
        sensorManager.registerListener((SensorEventListener) this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);

        lastUpdate = System.currentTimeMillis();
    }

    @Override
    protected void onResume() { //продолжить
        super.onResume();// подписываем на действие
        sensorManager.registerListener((SensorEventListener) this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);// запускаем датчик
    }

    @Override
    protected void onPause() {//пауза
        super.onPause();// подписываем действие
        //останавливаем датчик
        sensorManager.unregisterListener((SensorEventListener) this);
    }

    protected void onStoped(View view) { // функция паузы
        active = !active;// активно/неактивано
        if(!active){
            Button button = findViewById(R.id.button);
            button.setText("ВОЗОБНОВИТЬ");
        }
        else{
            Button button = findViewById(R.id.button);
            button.setText("ПАУЗА");
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {// функция изменения датчика
        if(sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER){// если акселометра
            float[] values = sensorEvent.values;// проекция ускорения на оси системы координат
            float x = values[0];// координата x
            float y = values[1];// координата y
            float z = values[2];// координата z

            //квадрат модуля ускорения телефона, деленный на квадрат
            //ускорения свободного падения
            float accelationSquareRoot = (x*x + y*y + z*z)
                    /(SensorManager.GRAVITY_EARTH* SensorManager.GRAVITY_EARTH);
            //текущее время
            long actualTime = System.currentTimeMillis();

            if(accelationSquareRoot >= 2){//если тряска сильная
                //если с момента начала тряски прошло меньше 200
                if(actualTime - lastUpdate < 200){
                    return;
                }
                lastUpdate = actualTime;// актуализируем время

                count++;// увеличиваем шаг
                text.setText(String.valueOf(count));//обновляем текст
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}