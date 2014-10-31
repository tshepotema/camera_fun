package com.example.funcam;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import com.squareup.picasso.Picasso;

//import com.squareup.picasso.Picasso;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomList extends ArrayAdapter<String> {
	
	private final Activity context;
	
	@SuppressWarnings("unused")
	private final ArrayList<String> webPhotoID;
	private final ArrayList<String> photoUploader;
	private final ArrayList<String> photoDescription;
	private final ArrayList<String> photoURL;
	@SuppressWarnings("unused")
	private final ArrayList<String> photoLat;
	@SuppressWarnings("unused")
	private final ArrayList<String> photoLon;
	private final ArrayList<String> photoDate;
	
	public CustomList(Activity context, ArrayList<String> webPhotoID2, ArrayList<String> photoUploader2, ArrayList<String> photoDescription2
			, ArrayList<String> photoURL2, ArrayList<String> photoLat2, ArrayList<String> photoLon2, ArrayList<String> photoDate2) {
		super(context, R.layout.webphoto_list_row, webPhotoID2);
		this.context = context;
		this.webPhotoID = webPhotoID2;
		this.photoUploader = photoUploader2;
		this.photoDescription = photoDescription2;
		this.photoURL = photoURL2;
		this.photoLat = photoLat2;
		this.photoLon = photoLon2;
		this.photoDate = photoDate2;
	}

	@SuppressLint({ "ViewHolder", "InflateParams" })
	@Override
	public View getView(int position, View view, ViewGroup parent) {
		LayoutInflater inflater = context.getLayoutInflater();
		
		View rowView = inflater.inflate(R.layout.webphoto_list_row, null, true);
		
		TextView txtPhotoNumber = (TextView) rowView.findViewById(R.id.tvPhotoNumber);
		TextView txtPhotoUploader = (TextView) rowView.findViewById(R.id.tvPhotoUploader);
		TextView txtPhotoDescription = (TextView) rowView.findViewById(R.id.tvDescription);
		ImageView imageView = (ImageView) rowView.findViewById(R.id.list_image);
		
		long timestamp = Long.parseLong(photoDate.get(position)) * 1000;		
		Calendar cal = Calendar.getInstance(Locale.ENGLISH);
	    cal.setTimeInMillis(timestamp);
	    String dateCaptured = DateFormat.format("dd-MMM-yyyy", cal).toString();		
		
		txtPhotoNumber.setText(dateCaptured);
		txtPhotoUploader.setText(photoUploader.get(position));
		txtPhotoDescription.setText(photoDescription.get(position));
		
		String imageURL = photoURL.get(position);
		
		Picasso.with(context)
			.load(imageURL)
			.placeholder(R.drawable.photoholder)
			.into(imageView);		
				
		return rowView;
	}
				
}