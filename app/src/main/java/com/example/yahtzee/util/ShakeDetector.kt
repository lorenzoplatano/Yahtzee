package com.example.yahtzee.util

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.sqrt

// Classe ShakeDetector per rilevare i movimenti di shake del dispositivo
class ShakeDetector(
    private val onShake: () -> Unit
) : SensorEventListener {

    private var lastShakeTime: Long = 0
    private val shakeThresholdGravity = 3f // Soglia di gravità per considerare un movimento come shake
    private val shakeSlopTimeMs = 2500 // Tempo minimo tra due shake consecutivi in millisecondi
    private var isRegistered = false


    // Gestione degli eventi del sensore
    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return
        if (event.sensor.type != Sensor.TYPE_ACCELEROMETER) return

        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        val gX = x / SensorManager.GRAVITY_EARTH
        val gY = y / SensorManager.GRAVITY_EARTH
        val gZ = z / SensorManager.GRAVITY_EARTH

        val gForce = sqrt((gX * gX + gY * gY + gZ * gZ).toDouble()).toFloat()

        if (gForce > shakeThresholdGravity) {
            val now = System.currentTimeMillis()
            if (lastShakeTime + shakeSlopTimeMs > now) {
                return
            }
            lastShakeTime = now
            onShake()
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    // Registra il listener per il sensore accelerometro
    fun register(sensorManager: SensorManager) {
        if (!isRegistered) {
            val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            if (accelerometer != null) {
                sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI)
                isRegistered = true
            }
        }
    }

    // Deregistra il listener per il sensore accelerometro
    fun unregister(sensorManager: SensorManager) {
        if (isRegistered) {
            sensorManager.unregisterListener(this)
            isRegistered = false
        }
    }
}