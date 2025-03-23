package cz.cvut.zan.stepavi2.weatherapp.util

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import cz.cvut.zan.stepavi2.weatherapp.MainActivity
import cz.cvut.zan.stepavi2.weatherapp.R
import cz.cvut.zan.stepavi2.weatherapp.data.repository.WeatherRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class WeatherAlarmReceiver : BroadcastReceiver() {
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("WeatherAlarmReceiver", "Received alarm broadcast at ${SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(System.currentTimeMillis())}")
        val city = intent.getStringExtra("city") ?: return
        Log.d("WeatherAlarmReceiver", "City: $city")

        val weatherRepository = WeatherRepository(context)
        createNotificationChannel(context)

        val hasNotificationPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }

        if (!hasNotificationPermission) {
            Log.w("WeatherAlarmReceiver", "Cannot send notification: POST_NOTIFICATIONS permission not granted")
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            val result = weatherRepository.getCurrentWeather(city)
            result.onSuccess { response ->
                val temp = response.currentWeather.temperature.toString() + "°C"
                val notificationIntent = Intent(context, MainActivity::class.java).apply {
                    putExtra("city", city)
                    putExtra("navigate_to_details", true)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                val pendingIntent = android.app.PendingIntent.getActivity(
                    context,
                    city.hashCode(),
                    notificationIntent,
                    android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
                )

                val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle("Weather in $city")
                    .setContentText("Current temperature: $temp")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .build()

                with(NotificationManagerCompat.from(context)) {
                    notify(city.hashCode(), notification)
                    Log.d("WeatherAlarmReceiver", "Notification sent for $city: $temp")
                }
            }.onFailure { e ->
                Log.e("WeatherAlarmReceiver", "Failed to fetch weather for $city: ${e.message}")
            }
        }
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = android.app.NotificationChannel(
                CHANNEL_ID,
                "Weather Alerts",
                android.app.NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Weather notifications for selected cities"
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
            manager.createNotificationChannel(channel)
            Log.d("WeatherAlarmReceiver", "Notification channel created")
        }
    }

    companion object {
        const val CHANNEL_ID = "weather_alert_channel"
    }
}