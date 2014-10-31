package com.example.funcam;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import com.example.funcam.database.FuncamDatabaseHelper;
import com.example.funcam.database.ImagesTable;
import com.squareup.picasso.Picasso;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

public class MyPhotosFragment extends Fragment {

	ListView listMyPhotos;

	ArrayList<String> webPhotoID = new ArrayList<String>();
	ArrayList<String> photoUploader = new ArrayList<String>();
	ArrayList<String> photoDescription = new ArrayList<String>();
	ArrayList<String> photoURL = new ArrayList<String>();
	ArrayList<String> photoLat = new ArrayList<String>();
	ArrayList<String> photoLon = new ArrayList<String>();
	ArrayList<String> photoDate = new ArrayList<String>();

	ProgressDialog mProgressDialog;
	
	private Context myPhotoContext;
	
	SharedPreferences sharedPref;
	Editor editor;		
	
	String uploader;
	
	File mediaStorageDir;
	
	public CustomListMyPhotos adapter;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View myPhotosView = inflater.inflate(R.layout.myphoto_layout, container, false);
		
	    mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
	              Environment.DIRECTORY_PICTURES), "tshepo_photofun");		
		
		myPhotoContext = getActivity();
		
		sharedPref = myPhotoContext.getSharedPreferences(AppSettings.MyPREFERENCES, Context.MODE_PRIVATE);
		editor = sharedPref.edit();
		
		uploader = sharedPref.getString(AppSettings.nameKey, "funcam_GuestUser");
		
		//read a list of photos from local database
		getLocalPhotos();

		adapter = new CustomListMyPhotos(getActivity(), webPhotoID, photoUploader, photoDescription, photoURL, photoLat, photoLon, photoDate);
		listMyPhotos = (ListView) myPhotosView.findViewById(R.id.lvMyDBPhotos);
		listMyPhotos.setAdapter(adapter);
		
		listMyPhotos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {        				
				final String photoID = webPhotoID.get(position);
				String photoDesc = photoDescription.get(position);
				String photoPath = photoURL.get(position);
				
				// custom dialog
				final Dialog dialog = new Dialog(myPhotoContext);
				dialog.setContentView(R.layout.photo_detail_view_local);
				dialog.setTitle("My Photo");
				
				// set the custom dialog components - text, image and button
				EditText text = (EditText) dialog.findViewById(R.id.etImageDescription);
				text.setText("" + photoDesc);
				ImageView imageView = (ImageView) dialog.findViewById(R.id.ivPhoto);
				
				//Log.d("funcam", "funcam local photos path2foto 1 = " + photoPath);
				Uri imageUri = Uri.fromFile(new File(photoPath));						
				Picasso.with(myPhotoContext)
				.load("file://" + imageUri)
				.resize(300, 300)
				.placeholder(R.drawable.photoholder)
				.into(imageView);		
					 
				Button dialogButton = (Button) dialog.findViewById(R.id.btClose);
				// if close button is clicked, close the custom dialog
				dialogButton.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
				
				//if save button then save and close dialog
				Button saveButton = (Button) dialog.findViewById(R.id.btUpdate);
				saveButton.setOnClickListener(new OnClickListener() {					
					@Override
					public void onClick(View v) {
						
						EditText myPhotoDesc = (EditText) dialog.findViewById(R.id.etImageDescription);
						String newDescription = myPhotoDesc.getText().toString();
						
				    	FuncamDatabaseHelper dbHelper = new FuncamDatabaseHelper(getActivity());
				    	SQLiteDatabase db = dbHelper.getWritableDatabase();
				    	
				    	ContentValues valuesImg = new ContentValues();
				    							
						//create content values
						valuesImg.put(ImagesTable.COLUMN_TITLE, newDescription);
						
						//update database using a prepared statement
						db.updateWithOnConflict(ImagesTable.TABLE_IMAGES, valuesImg, ImagesTable.COLUMN_ID + "=" + photoID, null, SQLiteDatabase.CONFLICT_ABORT);
				
				        //CLOSE THE DATABASE
				        db.close();
				        dbHelper.close();
				        
						clearResources();
				        dialog.dismiss();
					}
				});
								
				//if save button then save and close dialog
				Button deleteButton = (Button) dialog.findViewById(R.id.btDelete);
				deleteButton.setOnClickListener(new OnClickListener() {					
					@Override
					public void onClick(View v) {
						
				    	FuncamDatabaseHelper dbHelper = new FuncamDatabaseHelper(getActivity());
				    	SQLiteDatabase db = dbHelper.getWritableDatabase();
				    							
						//delete database row using a prepared statement
				    	db.delete(ImagesTable.TABLE_IMAGES, ImagesTable.COLUMN_ID + "=" + photoID, null);
				
				        //CLOSE THE DATABASE
				        db.close();
				        dbHelper.close();
				        
						clearResources();
				        dialog.dismiss();
					}
				});				
				
				dialog.show();
				
			}
		});
		
		return myPhotosView;
	}
		
	public void clearResources() {        
        adapter.clear();
        webPhotoID.clear();
        photoUploader.clear();
        photoDescription.clear();
        photoURL.clear();
        photoLat.clear();
        photoLon.clear();
        
        getLocalPhotos();
        adapter.notifyDataSetChanged();		
	}		

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}

	public void getLocalPhotos() {
    	FuncamDatabaseHelper dbHelper = new FuncamDatabaseHelper(getActivity());
    	SQLiteDatabase db = dbHelper.getReadableDatabase();
		db = getActivity().openOrCreateDatabase(ImagesTable.DATABASE_FUNCAM, SQLiteDatabase.CREATE_IF_NECESSARY, null);
		db.setLocale(Locale.getDefault());

		Cursor cur = db.query(ImagesTable.TABLE_IMAGES, null, null, null, null, null, null);
		cur.moveToFirst();

		while (cur.isAfterLast() == false) {
			String myPhotoID = cur.getString(0);
			String myPhotoDescription = cur.getString(1);
			String myPhotoLat = cur.getString(2);
			String myPhotoLon = cur.getString(3);
			String myPhotoDate = cur.getString(4);
			
			webPhotoID.add(myPhotoID);			
			photoUploader.add(uploader);				
			photoDescription.add(myPhotoDescription);  
			photoLat.add(myPhotoLat);
			photoLon.add(myPhotoLon);
			photoDate.add(myPhotoDate);

			String imgPath = mediaStorageDir.getPath() + File.separator + "IMG_"+ cur.getString(4) + ".jpg";			
			photoURL.add(imgPath);
			
			cur.moveToNext();
		}
		cur.close();
		db.close();
		dbHelper.close();
	}        	
	
}