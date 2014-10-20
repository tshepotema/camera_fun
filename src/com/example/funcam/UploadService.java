package com.example.funcam;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Locale;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.protocol.HTTP;

import com.example.funcam.database.FuncamDatabaseHelper;
import com.example.funcam.database.ImagesTable;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class UploadService extends Activity {

	TextView tvTotalImages, tvUploadedImages, tvLocalImages, tvWifiActive;
	Button btUploadAll, btClose;
	
	Integer localImages;
	String localImagePath;
	
	File mediaStorageDir;
	
	ProgressDialog mProgressDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.upload_service_layout);
		
		initializeLayout();
		
	    mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
	              Environment.DIRECTORY_PICTURES), "tshepo_photofun");
	    
		btUploadAll.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				//start service to upload all images
				uploadAllImages();
			}
		});
	    
		btClose.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				onPause();
			}
		});
	}

	private void initializeLayout() {
		tvTotalImages = (TextView) findViewById(R.id.tvTotalImages);
		tvUploadedImages = (TextView) findViewById(R.id.tvUploadedImages);
		tvLocalImages = (TextView) findViewById(R.id.tvLocalImages);
		tvWifiActive = (TextView) findViewById(R.id.tvWifiActive);
		btUploadAll = (Button) findViewById(R.id.btUploadAll);
		btClose = (Button) findViewById(R.id.btClose);
								
    	FuncamDatabaseHelper dbHelper = new FuncamDatabaseHelper(this);
    	SQLiteDatabase db = dbHelper.getReadableDatabase();
		db = openOrCreateDatabase(ImagesTable.DATABASE_FUNCAM, SQLiteDatabase.CREATE_IF_NECESSARY, null);
		db.setLocale(Locale.getDefault());

		Cursor curLocal = db.query(ImagesTable.TABLE_IMAGES, new String[] {ImagesTable.COLUMN_ID}, ImagesTable.COLUMN_UPLOADED + " = 0", null, null, null, null);
		localImages = curLocal.getCount();

		Cursor curUploaded = db.query(ImagesTable.TABLE_IMAGES, new String[] {ImagesTable.COLUMN_ID},  ImagesTable.COLUMN_UPLOADED + " = 1", null, null, null, null);
		Integer uploadedImages = curUploaded.getCount();
		
		Integer totalImages = localImages + uploadedImages;				
		
		curLocal.close();
		curUploaded.close();
		
		db.close();
		dbHelper.close();
		
		tvTotalImages.setText(totalImages.toString());
		tvUploadedImages.setText(uploadedImages.toString());
		tvLocalImages.setText(localImages.toString());

		if (isWifiActive()) {
			tvWifiActive.setText("Wifi Status: Active");			
		} else {
			tvWifiActive.setText("Wifi Status: Not Active");						
		}
		
		if ((localImages > 0) && (isWifiActive())) {
			btUploadAll.setEnabled(true);
		} else {
			btUploadAll.setEnabled(false);			
		}

	}
	
	public boolean isWifiActive() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (mWifi.isConnected()) {
		    return true;
		}
		return false;
	}
	
	public boolean uploadSinglePhoto(Integer photoID, String pathToImage) {		
		return false;
	}
	
	public void uploadAllImages() {
    	FuncamDatabaseHelper dbHelper = new FuncamDatabaseHelper(this);
    	SQLiteDatabase db = dbHelper.getReadableDatabase();
		db = openOrCreateDatabase(ImagesTable.DATABASE_FUNCAM, SQLiteDatabase.CREATE_IF_NECESSARY, null);
		db.setLocale(Locale.getDefault());

		Cursor cur = db.query(ImagesTable.TABLE_IMAGES, null, ImagesTable.COLUMN_UPLOADED + " = 0", null, null, null, null);
		cur.moveToFirst();

		while (cur.isAfterLast() == false) {
			String myPhotoID = cur.getString(0);
			String myPhotoDescription = cur.getString(1);
			String myPhotoLat = cur.getString(2);
			String myPhotoLon = cur.getString(3);
			String myPhotoDate = cur.getString(4);

			localImagePath = mediaStorageDir.getPath() + File.separator + "IMG_"+ cur.getString(4) + ".jpg";			
	    	
			UploadPhoto photoUpload = new UploadPhoto(this);				
			photoUpload.execute(localImagePath, myPhotoDescription, myPhotoDate, myPhotoLat, myPhotoLon, myPhotoID);
			
			cur.moveToNext();
		}
		cur.close();
		db.close();
		dbHelper.close();		
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		finish();
	}
	
	
	public class UploadPhoto extends AsyncTask<String, Void, Void> {
		 
	     //private final String NAMESPACE = "http://105.235.168.210/DevChallange2/";
	     private final String URL = "http://105.235.168.210/DevChallange2/PhotoFun.asmx";
	     private final String SOAP_ACTION = "http://105.235.168.210/DevChallange2/UploadPhoto";
	     //private final String METHOD_NAME = "/UploadPhoto";
	     private Context mContext;
	 
	     public UploadPhoto(Context baseContext) {
	          this.mContext = baseContext;
	     }
	 
	     protected void onPreExecute() {
            /*super.onPreExecute();
            // Create a progress dialog
            mProgressDialog = new ProgressDialog(mContext);
            // Set progress dialog title
            mProgressDialog.setTitle("Funcam");
            // Set progress dialog message
            mProgressDialog.setMessage("Uploading photos...");
            mProgressDialog.setIndeterminate(false);
            // Show progress dialog
            mProgressDialog.show();*/	 
	     }
	 
	     @Override
	     protected Void doInBackground(String... params) {
	 
	          try {
	              String filePath = "file:" + params[0];
	              String fileDescription = params[1];
	              String fileTimeStamp = params[2];
	              String filePhotoLat = params[3];
	              String filePhotoLon = params[4];
	              String myPhotoID = params[5];
	              
	              Bitmap selectedImage = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), Uri.parse(filePath));
	 
	              Bitmap resizedImage;             
	              resizedImage = getResizedBitmap(selectedImage,500,(int)(selectedImage.getWidth()/(selectedImage.getHeight()/500.0)));
	             
	              ByteArrayOutputStream baos = new ByteArrayOutputStream();
	              resizedImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
	              byte[] byteArrayImage = baos.toByteArray();
	              String encodedImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
	 
	              HttpPost httppost = new HttpPost(URL);
	              StringEntity se;
	 
	              String SOAPRequestXML = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
	                        + "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
	                        + "<soap:Body>"
	                        + "<UploadPhoto xmlns=\"http://105.235.168.210/DevChallange2/\">"
	                        + "<PhotoTimeStamp>" + fileTimeStamp + "</PhotoTimeStamp>"
	                        + "<PhotoDescription>" + fileDescription + "</PhotoDescription>"
	                        + "<Uploader>Danie</Uploader>"
	                        + "<Latitude>" + filePhotoLat + "</Latitude>"
	                        + "<Longitude>" + filePhotoLon + "</Longitude>"
	                        + "<file>"
	                        + encodedImage
	                        + "</file>"
	                        + "<fileExtention>jpg</fileExtention>"
	                        + "</UploadPhoto>"
	                        + "</soap:Body>" + "</soap:Envelope>";
	              se = new StringEntity(SOAPRequestXML, HTTP.UTF_8);
	 
	              se.setContentType("text/xml");
	              httppost.setHeader("Content-Type", "text/xml;charset=UTF-8");
	              httppost.setHeader( "SOAPAction", SOAP_ACTION );
	 
	              httppost.setHeader("Accept",
	                        "text/xml,application/text+xml,application/soap+xml");
	               httppost.setEntity(se);
	 
	              HttpClient httpclient = new DefaultHttpClient();
	              BasicHttpResponse httpResponse = (BasicHttpResponse) httpclient
	                        .execute(httppost);   
	              
	              
					FuncamDatabaseHelper dbHelper = new FuncamDatabaseHelper(mContext);
					SQLiteDatabase db = dbHelper.getWritableDatabase();
					
					ContentValues valuesImg = new ContentValues();
					
					//create content values
					valuesImg.put(ImagesTable.COLUMN_UPLOADED, "1");
					
					//update database using a prepared statement
					db.updateWithOnConflict(ImagesTable.TABLE_IMAGES, valuesImg, ImagesTable.COLUMN_ID + "=" + myPhotoID, null, SQLiteDatabase.CONFLICT_IGNORE);
					
					//CLOSE THE DATABASE
					db.close();
					dbHelper.close();

	              Log.d("funcam HTTP RESPONSE", httpResponse.getStatusLine().toString());
	          } catch (Exception ex) {
	              Log.d("funcam EXCEPTION", ex.getMessage());
	          }
	          return null;
	     }
	 
	     @Override
	     protected void onPostExecute(Void result) {
	    	 /*btUploadAll.setEnabled(false);
	    	 mProgressDialog.dismiss();*/
	     }
	    
	     public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
	        int width = bm.getWidth();
	        int height = bm.getHeight();
	        float scaleWidth = ((float) newWidth) / width;
	        float scaleHeight = ((float) newHeight) / height;
	        Matrix matrix = new Matrix();
	 
	        matrix.postScale(scaleWidth, scaleHeight);
	 
	        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
	        return resizedBitmap;
	     }
	 
	}	

}
