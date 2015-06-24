package com.pointrestapp.pointrest;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.pointrestapp.pointrest.activities.MainScreenActivity;
import com.pointrestapp.pointrest.data.PuntiContentProvider;
import com.pointrestapp.pointrest.data.PuntiDbHelper;
import com.pointrestapp.pointrest.data.PuntiImagesDbHelper;

public class LocalNotification extends AsyncTask<Void, Void, Void> {

    private int mId;
    private Context mContext;
    private int mNotificationCount;
    
	public LocalNotification(Context context, int id) {
		this.mId = id;
    	mContext = context;
    }

    @Override
    protected Void doInBackground(Void... params) {
		
		 Intent resultIntent = new Intent(mContext, MainScreenActivity.class);
		 
		 Cursor cursor = mContext.getContentResolver().query(PuntiContentProvider.PUNTI_URI, null, PuntiDbHelper._ID + "=?",
						new String[]{mId + "" }, null);
		 
		 int nameIndex = cursor.getColumnIndex(PuntiDbHelper.NOME);
		 int descriptionIndex = cursor.getColumnIndex(PuntiDbHelper.DESCRIZIONE);	
		 
		 String name = "pointerest notification";
		 String description = "pointerest description";
		 if(cursor.moveToNext()){
			 name = cursor.getString(nameIndex); 
			 description = cursor.getString(descriptionIndex);
		 }
		 
		 //GET IMAGE ID
		 cursor = mContext.getContentResolver().query(PuntiContentProvider.PUNTI_IMAGES_URI, null, PuntiImagesDbHelper.PUNTO_ID + "=?",
					new String[]{mId + "" }, null);
		 
		 int idImageIndex = cursor.getColumnIndex(PuntiImagesDbHelper._ID);
		 
		 int idImage = 1;
		 if(cursor.moveToNext())
			 idImage = cursor.getInt(idImageIndex);
		 
		 	TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
	        // Adds the back stack for the Intent (but not the Intent itself).
	        stackBuilder.addParentStack(MainScreenActivity.class);
	        // Adds the Intent that starts the Activity to the top of the stack.
	        stackBuilder.addNextIntent(resultIntent);
		 
			PendingIntent resultPendingIntent = PendingIntent.getActivity(mContext, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
						 
			 if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
				 
				NotificationCompat.BigPictureStyle notiStyle = new NotificationCompat.BigPictureStyle();
		        notiStyle.setBigContentTitle(name);
		        notiStyle.setSummaryText("Pointerest");
		        
		        Bitmap remote_picture = null;
		        
		        try {
                	remote_picture = Glide.
                        with(mContext)
                        .load(Constants.BASE_URL + "immagini/" + idImage)
                        .asBitmap()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(100,100)
                        .get();
                 } catch (final ExecutionException e) {
                     Log.e("LOCALNOTIFICATION", e.getMessage());
                 } catch (final InterruptedException e) {
                     Log.e("LOCALNOTIFICATION", e.getMessage());
                 }
		        // Add the big picture to the style.
		        notiStyle.bigPicture(remote_picture);
		        
				 NotificationCompat.Builder notification = new NotificationCompat.Builder(mContext)
				.setSmallIcon(R.drawable.ic_launcher)
	            .setAutoCancel(true)
	            .setLargeIcon(remote_picture)
	            .setContentIntent(resultPendingIntent)
	            .setContentTitle(name)
	            .setContentText(description)
	            .setTicker("Pointerest");

				 notification.setStyle(notiStyle);
				 notification.setNumber(mNotificationCount++);
				 
				 NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);			
				 mNotificationManager.notify(Constants.NOTIFICATION_ID, notification.build());
				 
			 }else{
				 NotificationCompat.Builder notification = new NotificationCompat.Builder(mContext);
				 notification.setAutoCancel(true)
			     .setDefaults(Notification.DEFAULT_ALL)
			     .setWhen(System.currentTimeMillis())         
			     .setSmallIcon(R.drawable.ic_launcher)
			     .setTicker("Pointerest")            
			     .setContentTitle(name)
			     .setContentText(description)
			     .setDefaults(Notification.DEFAULT_LIGHTS| Notification.DEFAULT_SOUND)
			     .setContentIntent(resultPendingIntent)
			     .setAutoCancel(true);
				 
				 NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
				    notificationManager.notify(Constants.NOTIFICATION_ID, notification.build());
			 }	
			 
			 return null;

    }
}