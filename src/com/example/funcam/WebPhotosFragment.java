package com.example.funcam;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.squareup.picasso.Picasso;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.funcam.Camera.ImageCaptureFragment.ErrorDialogFragment;
import com.example.funcam.location.*;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class WebPhotosFragment extends Fragment 
	implements LocationListener, GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener {

	ListView list;

	ArrayList<String> webPhotoID = new ArrayList<String>();
	ArrayList<String> photoUploader = new ArrayList<String>();
	ArrayList<String> photoDescription = new ArrayList<String>();
	ArrayList<String> photoURL = new ArrayList<String>();
	ArrayList<String> photoLat = new ArrayList<String>();
	ArrayList<String> photoLon = new ArrayList<String>();
	ArrayList<String> photoDate = new ArrayList<String>();

	ProgressDialog mProgressDialog;
	
	private Context webPhotoContext;

    // Stores the current instantiation of the location client in this object
    private LocationClient mLocationClient;
	
    public String gpsLat, gpsLon, mapDistance;
    
	SharedPreferences sharedPref;
	Editor editor;		    
	
	TextView textLocation;
    
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View webPhotosView = inflater.inflate(R.layout.webphoto_layout, container, false);
		
		webPhotoContext = getActivity();
		
		sharedPref = webPhotoContext.getSharedPreferences(AppSettings.MyPREFERENCES, Context.MODE_PRIVATE);
		editor = sharedPref.edit();
		
		mapDistance = sharedPref.getString(AppSettings.distanceKey, "50");
				
        mLocationClient = new LocationClient(webPhotoContext, this, this);
				
		return webPhotosView;
	}
	
	public void FetchPhotosInArea() {		
		// get the posted photos
		//new HttpAsyncTask().execute("http://105.235.168.210/DevChallange2/PhotoFun.asmx/FetchPhotosInArea?Latitude=-26.0111618041992&Longitude=28.1099967956543&Distance=10");		
		new HttpAsyncTask().execute("http://105.235.168.210/DevChallange2/PhotoFun.asmx/FetchPhotosInArea", gpsLat, gpsLon, mapDistance);		
		
	}

    /**
     * Verify that Google Play services is available before making a request.
     *
     * @return true if Google Play services is available, otherwise false
     */
    private boolean servicesConnected() {

        // Check that Google Play services is available
        int resultCode =
                GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());

        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d(LocationUtils.APPTAG, getString(R.string.play_services_available));

            // Continue
            return true;
        // Google Play services was not available for some reason
        } else {
            // Display an error dialog
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, getActivity(), 0);
            if (dialog != null) {
                ErrorDialogFragment errorFragment = new ErrorDialogFragment();
                errorFragment.setDialog(dialog);
                errorFragment.show(getFragmentManager(), LocationUtils.APPTAG);
            }
            return false;
        }
    }
	
	public void connectionNotAvailable() {
        // Create a progress dialog
        mProgressDialog = new ProgressDialog(getActivity());
        // Set progress dialog title
        mProgressDialog.setTitle("Funcam");
        // Set progress dialog message
        mProgressDialog.setMessage("Location services not avalable");
        mProgressDialog.setIndeterminate(true);
        // Show progress dialog
        mProgressDialog.show();            
	}

    /*
     * Called by Location Services when the request to connect the
     * client finishes successfully. At this point, you can
     * request the current location or start periodic updates
     */
    @Override
    public void onConnected(Bundle bundle) {
		// Get the current location
		Location currentLocation = mLocationClient.getLastLocation();
		
		// Display the current location in the UI
        String locationCoords = LocationUtils.getLatLng(getActivity(), currentLocation);                		
        Log.d("camerafun", "WEBPHOTOS updated coordinates trying to update lat, lon");		
        if (locationCoords.length() > 1) {
        	String[] parts = locationCoords.split(",");
    		gpsLat = parts[0];
    		gpsLon = parts[1];
            Log.d("camerafun", "WEBPHOTOS updated coordinates lat = " + gpsLat + ", lon = " + gpsLon);
            FetchPhotosInArea();
        } else {
        	connectionNotAvailable();
        }
		
    }

    /*Called by Location Services if the connection to the location client drops because of an error.*/
    @Override
    public void onDisconnected() {
		//connection dialog
		connectionNotAvailable();
    }

    /* Called by Location Services if the attempt to Location Services fails.*/
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    	Log.d("cameraResult", "funcam cameraResult -- connection failed for some reason - in method onConnectionFailed ");
        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
            try {

                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(
                        getActivity(),
                        LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);

                /*
                * Thrown if Google Play services canceled the original
                * PendingIntent
                */

            } catch (IntentSender.SendIntentException e) {
        		//connection dialog
        		connectionNotAvailable();

                // Log the error
                e.printStackTrace();
            }
        } else {

            // If no resolution is available, display a dialog to the user with the error.
            showErrorDialog(connectionResult.getErrorCode());
        }
    }	

    /**
     * Show a dialog returned by Google Play services for the
     * connection error code
     *
     * @param errorCode An error code returned from onConnectionFailed
     */
    private void showErrorDialog(int errorCode) {

        // Get the error dialog from Google Play services
        Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
            errorCode,
            getActivity(),
            LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);

        // If Google Play services can provide an error dialog
        if (errorDialog != null) {

            // Create a new DialogFragment in which to show the error dialog
            ErrorDialogFragment errorFragment = new ErrorDialogFragment();

            // Set the dialog in the DialogFragment
            errorFragment.setDialog(errorDialog);

            // Show the error dialog in the DialogFragment
            errorFragment.show(getFragmentManager(), LocationUtils.APPTAG);
        }
    }
	
    public static String GET(String url, String Lat, String Lon, String mapDistance){
        InputStream inputStream;
        String result = "";
        try {

            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();
            
            HttpPost post = new HttpPost(url);

            JSONObject jsonRequest = new JSONObject();
            
            jsonRequest.put("Latitude", Lat);
            jsonRequest.put("Longitude", Lon);
            jsonRequest.put("Distance", mapDistance);
            
            StringEntity reqEntity = new StringEntity(jsonRequest.toString());
            reqEntity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            post.setEntity(reqEntity);
            
            // make POST request to the url
            HttpResponse httpResponse = httpclient.execute(post);

            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Tshepo - Error connecting to remote resource";

        } catch (Exception e) {
        	e.printStackTrace();
            //Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line;
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        	inputStream.close();
        return result;

    }
    	
    public class HttpAsyncTask extends AsyncTask<String, Void, String> {
    	
    	@Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progress dialog
            mProgressDialog = new ProgressDialog(webPhotoContext);
            // Set progress dialog title
            mProgressDialog.setTitle("Funcam Web photos");
            // Set progress dialog message
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);
            // Show progress dialog
            mProgressDialog.show();
        }

        @Override
        protected String doInBackground(String... urls) {
            return GET(urls[0], urls[1], urls[2], urls[3]);
        }

        // onPostExecute process the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            try {
                JSONArray response = new JSONArray(result);
                for (int i = 0; i < response.length(); i++) {
                    JSONObject webPhotosObj = response.getJSONObject(i);

                    String photoID = webPhotosObj.getString("PhotoID");
                    String uploader = webPhotosObj.getString("Uploader");
                    String description = webPhotosObj.getString("PhotoDescription");
                    String webPhotoURL = webPhotosObj.getString("ImagePath");
                    String webPhotoLat = webPhotosObj.getString("Latitude");
                    String webPhotoLon = webPhotosObj.getString("Longitude");
                    String webPhotoDate = webPhotosObj.getString("PhotoTimeStamp");                    
                    
                    webPhotoID.add(photoID);
                    photoUploader.add(uploader);
                    photoDescription.add(description);                    
                    photoURL.add(webPhotoURL);
                    photoLat.add(webPhotoLat);
                    photoLon.add(webPhotoLon);
                    photoDate.add(webPhotoDate);
                    
                    //Log.d("postExecute", "WebPhotosFragment postExecute --> user =  [" + uploader + "] photoID = " + photoID);                                        
                } 
                
        		CustomList adapter = new CustomList(getActivity(), webPhotoID, photoUploader, photoDescription, photoURL, photoLat, photoLon, photoDate);
        		list = (ListView) getActivity().findViewById(R.id.lvWebphotos);
        		list.setAdapter(adapter);
        		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        			@Override
        			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {        				
        				final String lat = photoLat.get(position);
        				final String lon = photoLon.get(position);
        				//String photoID = webPhotoID.get(position);
        				String uploader = photoUploader.get(position);
        				final String photoDesc = photoDescription.get(position);
        				final String photoUri = photoURL.get(position);
        				
        				// custom dialog
        				final Dialog dialog = new Dialog(webPhotoContext);
        				dialog.setContentView(R.layout.photo_detail_view);
        				dialog.setTitle("Uploader: " + uploader);
        	 
        				// set the custom dialog components - text, image and button
        				TextView text = (TextView) dialog.findViewById(R.id.tvImageDescription);
        				text.setText("" + photoDesc);
        				
        				textLocation = (TextView) dialog.findViewById(R.id.tvPhotoLocation);        				
        				textLocation.setText("GPS: " + lat + "," + lon);
        				
        				getAddress(lat, lon);
        				
        				ImageView imageView = (ImageView) dialog.findViewById(R.id.ivPhoto);
        				
        				Picasso.with(webPhotoContext)
        				.load(photoUri)
        				.placeholder(R.drawable.photoholder)
        				.into(imageView);		
        				        	 
        				Button dialogButton = (Button) dialog.findViewById(R.id.btClose);
        				dialogButton.setOnClickListener(new OnClickListener() {
        					@Override
        					public void onClick(View v) {
        						dialog.dismiss();
        					}
        				});
			        	 
						Button dialogPegman = (Button) dialog.findViewById(R.id.btPegman);
						dialogPegman.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								dialog.dismiss();
						    	    	        
						        Bundle mapBundleDetails = new Bundle();
						        mapBundleDetails.putDouble("gps_lat", Double.parseDouble(lat));
						        mapBundleDetails.putDouble("gps_lon", Double.parseDouble(lon));
						        
						        Intent openStreetView = new Intent("com.example.funcam.STREETVIEW");
						        openStreetView.putExtras(mapBundleDetails);
						        startActivity(openStreetView);
								
							}
						});
			        	 
						Button dialogShare = (Button) dialog.findViewById(R.id.btShare);
						dialogShare.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								dialog.dismiss();
						    	    	        
						        Bundle photoBundleDetails = new Bundle();
						        photoBundleDetails.putString("share_image_url", photoUri);
						        photoBundleDetails.putString("share_image_description", photoDesc);
						        
						        Intent openStreetView = new Intent("com.example.funcam.SOCIALSHAREMEDIA");
						        openStreetView.putExtras(photoBundleDetails);
						        startActivity(openStreetView);
								
							}
						});
        	 
        				dialog.show();        				
        				
        			}
        		});
                
        		//close the progress dialog
        		mProgressDialog.dismiss();
                                    
            } catch (JSONException e) {
                e.printStackTrace();
            }                            
        }

    }

    public void getAddress(String lat, String lon) {

        // In Gingerbread and later, use Geocoder.isPresent() to see if a geocoder is available.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD && !Geocoder.isPresent()) {
            // No geocoder is present
            return;
        }

        if (servicesConnected()) {
            // Start the background task
            (new GetAddressTask(getActivity())).execute(lat, lon);
        }
    }

    /**
     * An AsyncTask that calls getFromLocation() in the background.
     * The class uses the following generic types:
     * Location - A {@link android.location.Location} object containing the current location,
     *            passed as the input parameter to doInBackground()
     * Void     - indicates that progress units are not used by this subclass
     * String   - An address passed to onPostExecute()
     */
    protected class GetAddressTask extends AsyncTask<String, Void, String> {

        // Store the context passed to the AsyncTask when the system instantiates it.
        Context localContext;

        // Constructor called by the system to instantiate the task
        public GetAddressTask(Context context) {

            // Required by the semantics of AsyncTask
            super();

            // Set a Context for the background task
            localContext = context;
        }

        /**
         * Get a geocoding service instance, pass latitude and longitude to it, format the returned
         * address, and return the address to the UI thread.
         */
        @Override
        protected String doInBackground(String... params) {
            /*
             * Get a new geocoding service instance, set for localized addresses. This example uses
             * android.location.Geocoder, but other geocoders that conform to address standards
             * can also be used.
             */
            Geocoder geocoder = new Geocoder(localContext, Locale.getDefault());

            // Get the current location from the input parameter list
            String lat = params[0];
            String lon = params[1];

            // Create a list to contain the result address
            List <Address> addresses = null;

            // Try to get an address for the current location. Catch IO or network problems.
            try {

                /*
                 * Call the synchronous getFromLocation() method with the latitude and
                 * longitude of the current location. Return at most 1 address.
                 */
                addresses = geocoder.getFromLocation(Double.parseDouble(lat),Double.parseDouble(lon), 1);

                // Catch network or other I/O problems.
                } catch (IOException exception1) {

                    // Log an error and return an error message
                    Log.e(LocationUtils.APPTAG, getString(R.string.IO_Exception_getFromLocation));

                    // print the stack trace
                    exception1.printStackTrace();

                    // Return an error message
                    return (getString(R.string.IO_Exception_getFromLocation));

                // Catch incorrect latitude or longitude values
                } catch (IllegalArgumentException exception2) {

                    // Construct a message containing the invalid arguments
                    String errorString = getString(
                            R.string.illegal_argument_exception,lat,lon);
                    // Log the error and print the stack trace
                    Log.e(LocationUtils.APPTAG, errorString);
                    exception2.printStackTrace();

                    //
                    return errorString;
                }
                // If the reverse geocode returned an address
                if (addresses != null && addresses.size() > 0) {

                    // Get the first address
                    Address address = addresses.get(0);

                    // Format the first line of address
                    String addressText = getString(R.string.address_output_string,

                            // If there's a street address, add it
                            address.getMaxAddressLineIndex() > 0 ?
                                    address.getAddressLine(0) : "",

                            // Locality is usually a city
                            address.getLocality(),

                            // The country of the address
                            address.getCountryName()
                    );

                    // Return the text
                    return addressText;

                // If there aren't any addresses, post a message
                } else {
                  return getString(R.string.no_address_found);
                }
        }

        /**
         * A method that's called once doInBackground() completes. Set the text of the
         * UI element that displays the address. This method runs on the UI thread.
         */
        @Override
        protected void onPostExecute(String address) {

            // Set the address in the UI
        	textLocation.setText("Location: " + address);
        }
    }
        

    /* Called when the Activity is restarted, even before it becomes visible.*/
    @Override
    public void onStart() {
        super.onStart();
        /* Connect the client. Don't re-start any requests here; instead, wait for onResume() */
        mLocationClient.connect();
    }
    

	@Override
	public void onLocationChanged(Location location) {
		//do nothing here for now		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		//do nothing here for now		
	}

	@Override
	public void onProviderEnabled(String provider) {
		//do nothing here for now		
	}

	@Override
	public void onProviderDisabled(String provider) {
		//do nothing here for now		
	}
		
		
}