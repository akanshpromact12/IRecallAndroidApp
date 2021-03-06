package com.promact.akansh.irecall;

import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResolvingResultCallbacks;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.ResultCallbacks;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveResource;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.events.ChangeListener;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponse;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.OpenFileActivityBuilder;
import com.google.android.gms.drive.events.CompletionEvent;
import com.google.android.gms.drive.events.DriveEventService;
import com.google.api.client.util.Maps;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener , LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback{
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_VIDEO_CAPTURE = 2;
    private Uri videoUri;
    private String url;
    private String name;
    private String email;
    private String photoUri;
    private String videoUriStr;
    private TextView txt;
    private EditText caption;
    private static String albumID1;
    double latitude;
    double longitude;
    private static String fileName;
    private static final int REQUEST_CODE_RESOLUTION = 1;
    private static final  int REQUEST_CODE_OPENER = 3;
    private static final int SELECT_PICTURE = 100;
    private static final int SELECT_VIDEO = 500;
    private GoogleApiClient googleApiClient;
    private boolean fileOperation = false;
    private Uri selectedVideoUri;
    private Uri selectedImageUri;
    private File photoFile;
    private File storage;
    private Uri imageUri;
    private String uriPath;
    private String img;
    private String pathVideoGallery;
    private String pathImgGallery;
    private static final int MEDIA_TYPE_VIDEO = 200;
    private static final String VIDEO_DIR_NAME = "videoDir";
    private static final String IMAGE_DIR_NAME = "images";
    private FloatingActionMenu floatingActionMenu;
    private FloatingActionButton floatActionGallery;
    private FloatingActionButton floatActionCamera;
    private boolean doubleBackToExitPressedOnce = false;
    private FirebaseDatabase db;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private Firebase firebase;
    private DatabaseReference dbRef;
    private LocationManager locationManager;
    private String albumid;
    private String strCaption;
    private String userId;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private static final String TAG="HomeActivity";
    private DriveId folderDriveId;
    private GoogleMap map;
    private DriveId folderId;
    private String folderId1;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_main);

        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
        mGoogleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this).addApi(Auth.GOOGLE_SIGN_IN_API, options).build();

        mGoogleApiClient.connect();

        floatActionCamera = (FloatingActionButton) findViewById(R.id.menu_camera_option);
        floatActionGallery = (FloatingActionButton) findViewById(R.id.menu_gallery_option);
        floatingActionMenu = (FloatingActionMenu) findViewById(R.id.floating_action_menu);
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.content_main);

        txt = (TextView) findViewById(R.id.txtView1);
        //caption = (EditText) findViewById(R.id.txtCaption);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        strCaption = "";
        try{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        } catch (SecurityException e){
            Log.e("Location: ", e.getMessage());


        }

        //Log.d(TAG, "user: " + firebaseUser.getUid());

        //userId = firebaseUser.getUid();

        if (SaveSharedPref.getToken(HomeActivity.this).length()==0) {
            Intent intent = getIntent();
            name = intent.getStringExtra("name");
            email = intent.getStringExtra("email");
            photoUri = intent.getStringExtra("photoUri");
            userId = intent.getStringExtra("userId");
        } else {
            name = SaveSharedPref.getUsername(getApplicationContext());
            email = SaveSharedPref.getEmail(getApplicationContext());
            photoUri = SaveSharedPref.getPhotoUri(getApplicationContext());
            userId = SaveSharedPref.getUserId(getApplicationContext());

            Toast.makeText(this, "logged in as: " + email, Toast.LENGTH_SHORT).show();
        }

        Toast.makeText(this, "userid: " + userId, Toast.LENGTH_SHORT).show();
        Firebase.setAndroidContext(getApplicationContext());
        firebase = new Firebase("https://irecall-4dcd0.firebaseio.com/" + userId);

        loadLatLong();

        Log.d(TAG, "id:: " + albumid);

        NavigationView nav = (NavigationView) findViewById(R.id.nav_view);
        View newView = nav.getHeaderView(0); //Gets the header view from the header page, where all the widgets are kept.
        View viewSnackbar = findViewById(android.R.id.content);

        TextView txtUname = (TextView) newView.findViewById(R.id.usernm);
        TextView txtEmail = (TextView) newView.findViewById(R.id.emailNav);
        CircleImageView profilePic = (CircleImageView) newView.findViewById(R.id.imgProfile);

        txtUname.setText(name);
        txtEmail.setText(email);
        Glide.with(getApplicationContext()).load(photoUri).into(profilePic);
        txt.setText(" ");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        floatActionCamera.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                showAlertDialogBox();

                floatingActionMenu.close(true);
            }
        });

        floatActionGallery.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                showGalleryDialog();

                floatingActionMenu.close(true);
            }
        });

        Snackbar.make(viewSnackbar, "Welcome " + name, Snackbar.LENGTH_LONG).show();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        relativeLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (floatingActionMenu.isOpened()) {
                    floatingActionMenu.close(true);
                }

                return true;
            }
        });

        nav.setNavigationItemSelectedListener(this);
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
                    .addScope(Drive.SCOPE_APPFOLDER)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }

        googleApiClient.connect();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.googleMap);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        loadData(googleMap);
        }

    public GoogleApiClient getGoogleApiClient() {
        return googleApiClient;
    }

    final ResultCallback<DriveFolder.DriveFolderResult> folderCallback = new ResultCallback<DriveFolder.DriveFolderResult>() {
        @Override
        public void onResult(@NonNull DriveFolder.DriveFolderResult result) {
            if (!result.getStatus().isSuccess()) {
                Log.d(TAG, "Error creating folder");
                return;
            }

            Log.d(TAG, "Folder successfully created.");
        }
    };

    private void loadData(final GoogleMap googleMap) {

        firebase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                final Map map = dataSnapshot.getValue(Map.class);
                final double lat1 = latitude;
                final double long1 = longitude;
                LatLng currLatLng = new LatLng(lat1, long1);

                if (map.size()>0) {
                    String albumId = map.get("AlbumId").toString();
                    String MediaId = map.get("MediaId").toString();
                    DriveId drive_id = DriveId.decodeFromString(map.get("URL").toString());
                    //String url = map.get("URL").toString();
                    String caption = map.get("caption").toString();
                    final double lat_load = Double.parseDouble(map.get("Latitude").toString());
                    final double long_load = Double.parseDouble(map.get("Longitude").toString());

                    Log.i("values fetched ", albumId + " " + MediaId
                            + " " + drive_id.encodeToString() + " " + caption + " " + lat_load + " "
                            + long_load);

                    LatLng latLng = new LatLng(lat_load, long_load);
                    googleMap.addMarker(new MarkerOptions().position(latLng).title("First Marker"));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(currLatLng));


                    DriveFile file = drive_id.asDriveFile();
                    file.getMetadata(googleApiClient).setResultCallback(metaDataCallback);

                    /*SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.googleMap);
                    mapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            LatLng latLng = new LatLng(Double.parseDouble(map.get("Latitude").toString()),
                                    Double.parseDouble(map.get("" +
                                            "Longitude").toString()));
                            LatLng currLoc = new LatLng(lat1, long1);
                            googleMap.addMarker(new MarkerOptions().position(latLng).title("First Marker"));
                            googleMap.moveCamera(CameraUpdateFactory.newLatLng(currLoc));
                        }
                    });*/
                }
            }

            final private ResultCallback<DriveResource.MetadataResult> metaDataCallback =
                    new ResultCallback<DriveResource.MetadataResult>() {
                        @Override
                        public void onResult(@NonNull DriveResource.MetadataResult result) {
                            if (!result.getStatus().isSuccess()) {
                                Log.d(TAG, "Problem while trying to fetch metadata");
                                return;
                            }

                            Metadata metadata = result.getMetadata();
                            Log.d(TAG, "Title: " + metadata.getTitle()
                            + " id: " + metadata.getEmbedLink());
                        }
                    };

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        db = FirebaseDatabase.getInstance();
        firebaseUser = mAuth.getCurrentUser();
    }

    public void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        long imageSizeLimit = 12 * 1024 * 1024;
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, imageSizeLimit);

        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
    }

    public void openVideoChooser() {
        Intent intent = new Intent();
        intent.setType("video/*");
        long maxVideoSize = 12 * 1024 * 1024;
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, maxVideoSize);

        startActivityForResult(Intent.createChooser(intent, "Select Video"), SELECT_VIDEO);
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        Toast.makeText(this, "lat " + latitude, Toast.LENGTH_SHORT).show();
        longitude = location.getLongitude();
        Toast.makeText(this, "longitude " + longitude, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("Latitude","disable");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Latitude","enable");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Latitude","status");
    }

    public void takingPicture(){
        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (pictureIntent.resolveActivity(getPackageManager()) != null){
            //pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(pictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    public void makeVideo(){
        Intent videoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        Uri fileUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);
        videoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        videoIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        if (videoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(videoIntent, REQUEST_VIDEO_CAPTURE);
        }
    }

    public void showGalleryDialog() {
        final CharSequence[] options = {"Select a Photo", "Select a Video"};
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        dialogBuilder.setTitle("Select from options given below");
        dialogBuilder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item){
                if (options[item].equals("Select a Photo")) {
                    openImageChooser();
                } else if (options[item].equals("Select a Video")) {
                    openVideoChooser();
                }
            }
        });

        dialogBuilder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialogObj = dialogBuilder.create();
        alertDialogObj.show(); //Shows the dialog box;
    }

    public void showAlertDialogBox(){
        final CharSequence[] options = {"Take a Photo", "Make a Video"};
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        dialogBuilder.setTitle("Select from options given below");
        dialogBuilder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item){
                if (options[item].equals("Take a Photo")) {
                    takingPicture();

                    dialog.cancel();
                } else if (options[item].equals("Make a Video")) {
                    makeVideo();

                    dialog.cancel();
                }
            }
        });

        dialogBuilder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialogObj = dialogBuilder.create();
        alertDialogObj.show(); //Shows the dialog box;
    }

    public static long getFileSize(File file) {
        long size = file.length();

        return size;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        DriveId driveId;
        long fileSize;

        switch (requestCode) {
            case REQUEST_IMAGE_CAPTURE:
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    Bitmap bitmap1 = (Bitmap) bundle.get("data");

                    showPhotoDialog(bitmap1);
                }
                break;
            case REQUEST_VIDEO_CAPTURE:
                if (fileName != null && resultCode == RESULT_OK) {
                    videoUri = data.getData();
                    File file = new File(fileName);
                    fileSize = getFileSize(file) / 1024;

                    if ((fileSize / 1024) < 5) {
                        showVideoDialog();
                    } else {
                        Toast.makeText(this, "video file greater than 5MB", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case REQUEST_CODE_OPENER:
                if (resultCode == RESULT_OK) {
                    driveId = data.getParcelableExtra(
                            OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);

                    Toast.makeText(this, "File upload was successful", Toast.LENGTH_SHORT).show();

                    Log.e("url", "url: http://drive.google.com/open?id=" + driveId.getResourceId());
                }
                break;
            case SELECT_PICTURE:
                if (data != null && resultCode == RESULT_OK) {
                    selectedImageUri = data.getData();
                    try {
                        pathImgGallery = getPath(HomeActivity.this, selectedImageUri);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                    showSelectedImageDialog(selectedImageUri);
                }
                break;
            case SELECT_VIDEO:
                if (data != null && resultCode == RESULT_OK) {
                    selectedVideoUri = data.getData();
                    try {
                        pathVideoGallery = getPath(HomeActivity.this, selectedVideoUri);

                        System.out.println(pathVideoGallery);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                    showSelectedVideoDialog(selectedVideoUri);
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    public static String getPath(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            //External Storage Provider
            if (isExternalStorageDoc(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equals(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadDoc(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumns(context, contentUri, null, null);
            } else if (isMediaDoc(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] { split[1] };

                return getDataColumns(context, contentUri, selection, selectionArgs);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumns(context, uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumns (Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;

        final String col = "_data";
        final String[] projection = { col };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);

            if (cursor != null && cursor.moveToFirst()) {
                final int colIndex = cursor.getColumnIndexOrThrow(col);

                return  cursor.getString(colIndex);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isExternalStorageDoc(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadDoc(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDoc(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
                    .addScope(Drive.SCOPE_APPFOLDER)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }

        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (googleApiClient != null) {
            googleApiClient.disconnect();
        }

        super.onPause();
    }

    public void showVideoDialog() {

        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(HomeActivity.this, R.style.AppCompatAlertDialog);
        LayoutInflater layoutInflater = this.getLayoutInflater();
        final View dialogView = layoutInflater.inflate(R.layout.dialog_layout, null);
        dialogBuilder.setView(dialogView);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");
        final TextView title = (TextView) dialogView.findViewById(R.id.txtTitle);
        final EditText txtCaption = (EditText) dialogView.findViewById(R.id.txtBoxCaption);
        title.setTypeface(typeface);
        title.setTextSize(24);
        title.setText(R.string.videoDialogTitle);

        final ImageView videoView = (ImageView) dialogView.findViewById(R.id.imgPhoto);

        Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(fileName, MediaStore.Images.Thumbnails.MINI_KIND);
        videoView.setImageBitmap(thumbnail);

        dialogBuilder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                strCaption = txtCaption.getText().toString();
                Log.d(TAG, "caption: " + strCaption);

                File sdcard = new File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                        VIDEO_DIR_NAME);
                System.out.println("Files: " + fileName);

                if (sdcard.exists()) {
                    File fileList[] = sdcard.listFiles();

                    for (File file : fileList) {

                        if (file.toString().equals(fileName)) {
                            //Toast.makeText(HomeActivity.this, "Files: " + file.toString(), Toast.LENGTH_SHORT).show();
                            System.out.println("Files: " + file.toString());

                            fileOperation = true;

                            Drive.DriveApi.newDriveContents(googleApiClient)
                                    .setResultCallback(driveVideoContentsCallback);
                        }
                    }
                }

            }
        });

        dialogBuilder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    public void showSelectedImageDialog(Uri bitmap) {
        //final Bitmap imgBitmap = image;
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(HomeActivity.this, R.style.AppCompatAlertDialog);
        LayoutInflater layoutInflater = this.getLayoutInflater();
        final View dialogView = layoutInflater.inflate(R.layout.dialog_layout, null);
        dialogBuilder.setView(dialogView);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");
        final EditText txtCaption = (EditText) dialogView.findViewById(R.id.txtBoxCaption);
        final TextView title = (TextView) dialogView.findViewById(R.id.txtTitle);
        title.setTypeface(typeface);
        title.setTextSize(24);
        title.setText(R.string.imageDialogTitle);

        //final EditText caption = (EditText) dialogView.findViewById(R.id.txtBoxCaption);
        final ImageView photoImg = (ImageView) dialogView.findViewById(R.id.imgPhoto);

        photoImg.setImageURI(bitmap);
        dialogBuilder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                strCaption = txtCaption.getText().toString();
                Log.d(TAG, "caption: " + strCaption);

                fileOperation = true;

                Drive.DriveApi.newDriveContents(googleApiClient)
                        .setResultCallback(driveGalleryContentsCallback);

                AlertDialog alert = dialogBuilder.create();
                alert.cancel();

            }
        });

        dialogBuilder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    public void showSelectedVideoDialog(Uri videoUri) {
        videoUriStr = videoUri.getPath();
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(HomeActivity.this, R.style.AppCompatAlertDialog);
        LayoutInflater layoutInflater = this.getLayoutInflater();
        final View dialogView = layoutInflater.inflate(R.layout.dialog_layout, null);
        dialogBuilder.setView(dialogView);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");
        final EditText txtCaption = (EditText) dialogView.findViewById(R.id.txtBoxCaption);
        final TextView title = (TextView) dialogView.findViewById(R.id.txtTitle);
        title.setTypeface(typeface);
        title.setTextSize(24);
        title.setText(R.string.videoDialogTitle);

        //final EditText caption = (EditText) dialogView.findViewById(R.id.txtBoxCaption);
        Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(pathVideoGallery, MediaStore.Images.Thumbnails.MINI_KIND);
        final ImageView videoImg = (ImageView) dialogView.findViewById(R.id.imgPhoto);

        videoImg.setImageBitmap(thumbnail);

        dialogBuilder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                strCaption = txtCaption.getText().toString();
                Log.d(TAG, "caption: " + strCaption);

                fileOperation = true;

                Drive.DriveApi.newDriveContents(googleApiClient)
                        .setResultCallback(driveVideoGalleryContentsCallback);

                AlertDialog alert = dialogBuilder.create();
                alert.cancel();

            }
        });

        dialogBuilder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    private static File getOutputMediaFile(int type) {
        File sdcard = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                VIDEO_DIR_NAME);

        if (!sdcard.exists()) {
            if (!sdcard.mkdirs()) {
                Log.d(VIDEO_DIR_NAME, "Oops! Failed create "
                        + VIDEO_DIR_NAME + " directory");
                return null;
            }
        }

        File mediaFile;
        if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(sdcard.getPath() + File.separator
            + "VID_" + System.currentTimeMillis() + ".mp4");
            fileName = sdcard.getPath() + File.separator
                    + "VID_" + System.currentTimeMillis() + ".mp4";
        } else {
            return null;
        }

        return mediaFile;
    }

    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private Bitmap bmp1;

    public void showPhotoDialog(Bitmap bmp) {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialog);
        LayoutInflater layoutInflater = this.getLayoutInflater();
        final View dialogView = layoutInflater.inflate(R.layout.dialog_layout, null);
        dialogBuilder.setView(dialogView);

        final EditText txtCaption = (EditText) dialogView.findViewById(R.id.txtBoxCaption);
        final ImageView photoImg = (ImageView) dialogView.findViewById(R.id.imgPhoto);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");
        final TextView title = (TextView) dialogView.findViewById(R.id.txtTitle);
        title.setTypeface(typeface);
        title.setTextSize(24);


        bmp1 = bmp;
        photoImg.setImageBitmap(bmp);

        String positiveText = getString(android.R.string.ok);
        dialogBuilder.setPositiveButton(positiveText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        fileOperation = true;
                        strCaption = txtCaption.getText().toString();
                        Log.d(TAG, "caption: " + strCaption);

                        Drive.DriveApi.newDriveContents(googleApiClient)
                                .setResultCallback(driveContentsCallback);

                        dialog.cancel();
                    }
                });

        dialogBuilder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    final private ResultCallback<DriveApi.DriveContentsResult> driveContentsCallback = new
            ResultCallback<DriveApi.DriveContentsResult>(){
                @Override
                public void onResult(@NonNull DriveApi.DriveContentsResult result) {
                    if (result.getStatus().isSuccess()) {
                        if (fileOperation) {
                            createFileGoogleDrive(result);
                        }
                    }
                }
            };

    final private ResultCallback<DriveApi.DriveContentsResult> driveGalleryContentsCallback = new
            ResultCallback<DriveApi.DriveContentsResult>(){
                @Override
                public void onResult(@NonNull DriveApi.DriveContentsResult result) {
                    if (result.getStatus().isSuccess()) {
                        if (fileOperation) {
                            createGalleryGoogleDrive(result);
                        } else {
                            openGalleryFromDrive();
                        }
                    }
                }
            };

    final private ResultCallback<DriveApi.DriveContentsResult> driveVideoGalleryContentsCallback = new
            ResultCallback<DriveApi.DriveContentsResult>(){
                @Override
                public void onResult(@NonNull DriveApi.DriveContentsResult result) {
                    if (result.getStatus().isSuccess()) {
                        if (fileOperation) {
                            createVideoGalleryGoogleDrive(result);
                        } else {
                            openGalleryFromDrive();
                        }
                    }
                }
            };

    final private ResultCallback<DriveApi.DriveContentsResult> driveVideoContentsCallback = new
            ResultCallback<DriveApi.DriveContentsResult>() {
                @Override
                public void onResult(@NonNull DriveApi.DriveContentsResult result) {
                    if (result.getStatus().isSuccess()) {
                        createVideoGoogleDrive(result);
                    } else {
                        openVideoFromDrive();
                    }

                }
            };

    public void openGalleryFromDrive() {
        IntentSender intentSender = Drive.DriveApi
                .newOpenFileActivityBuilder()
                .setMimeType(new String[] { "text/plain", "text/html", "image/png" })
                .build(googleApiClient);
        try {
            startIntentSenderForResult(intentSender, REQUEST_CODE_OPENER, null, 0, 0, 0);

            System.out.println("url:" + url);
        } catch (IntentSender.SendIntentException e) {
            Log.w("Drive Config", "Unable to send intent", e);
        }
    }

    public void openVideoFromDrive() {
        IntentSender intentSender = Drive.DriveApi
                .newOpenFileActivityBuilder()
                .setMimeType(new String[] { "text/plain", "text/html", "image/png", "video/mp4" })
                .build(googleApiClient);
        try {
            startIntentSenderForResult(intentSender, REQUEST_CODE_OPENER, null, 0, 0, 0);

            System.out.println("url:" + url);
        } catch (IntentSender.SendIntentException e) {
            Log.w("Drive Config", "Unable to send intent", e);
        }
    }

    public void createVideoGoogleDrive(DriveApi.DriveContentsResult result) {
        final DriveContents driveContents = result.getDriveContents();

        new Thread() {
            @Override
            public void run() {
                OutputStream outputStream = driveContents.getOutputStream();

                try {
                    ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

                    File sdcard = new File(
                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                            VIDEO_DIR_NAME);
                    byte[] bytes = null;
                    FileInputStream fis;
                    long mbSize = 0;

                    if (sdcard.exists()) {
                        File fileList[] = sdcard.listFiles();

                        for (File file : fileList) {

                            if (file.toString().equals(fileName)) {
                                //Toast.makeText(HomeActivity.this, "Files: " + file.toString(), Toast.LENGTH_SHORT).show();
                                System.out.println("Files: " + file.toString());

                                bytes = new byte[(int) file.length()];

                                fis = new FileInputStream(file);
                                fis.read(bytes); //this will convert file into byte array.
                                fis.close();
                            }
                        }
                    }

                    byteStream.write(bytes);
                    outputStream.write(byteStream.toByteArray());
                } catch (Exception ex) {
                    System.out.println("Error in video upload" + ex.getMessage());
                }

                if (outputStream != null) {
                    DriveFolder driveFolder = folderDriveId.asDriveFolder();
                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                            .setTitle("videoNew_" + System.currentTimeMillis() + ".mp4")
                            .setMimeType("video/mp4")
                            .setStarred(true).build();

                    driveFolder.createFile(googleApiClient, changeSet, driveContents)
                            .setResultCallback(fileVideoCallBack);


                    //openFileFromDrive();
                }
            }
        }.start();
    }

    public void createGalleryGoogleDrive(DriveApi.DriveContentsResult result) {
        final DriveContents driveContents = result.getDriveContents();

        new Thread() {
            @Override
            public void run() {
                OutputStream outputStream = driveContents.getOutputStream();

                try {
                    ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

                    File videoFile = new File(pathImgGallery);
                    String parent = videoFile.getParent();
                    File realPath = new File(parent);

                    File[] files = realPath.listFiles();
                    byte[] bytes = null;
                    FileInputStream fis;

                    for (File file : files) {
                        if (file.toString().equals(pathImgGallery)) {
                            System.out.println("Files: " + file.toString());

                            bytes = new byte[(int) file.length()];

                            fis = new FileInputStream(file);
                            fis.read(bytes); //this will convert file into byte array.
                            fis.close();
                        }
                    }

                    byteStream.write(bytes);
                    outputStream.write(byteStream.toByteArray());
                } catch (Exception ex) {
                    Log.e("Error in image upload", ex.getMessage());
                }

                DriveFolder driveFolder = folderDriveId.asDriveFolder();
                MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                        .setTitle("imageFromGallery_" + System.currentTimeMillis() + ".png")
                        .setMimeType("image/png")
                        .setStarred(true).build();

                driveFolder.createFile(googleApiClient, changeSet, driveContents)
                        .setResultCallback(fileCallBack);


                //openFileFromDrive();
            }
        }.start();
    }

    public void createVideoGalleryGoogleDrive(DriveApi.DriveContentsResult result) {
        final DriveContents driveContents = result.getDriveContents();

        new Thread() {
            @Override
            public void run() {
                OutputStream outputStream = driveContents.getOutputStream();

                try {
                    ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

                    File videoFile = new File(pathVideoGallery);
                    String parent = videoFile.getParent();
                    File realPath = new File(parent);

                    File[] files = realPath.listFiles();
                    byte[] bytes = null;
                    FileInputStream fis;

                    for (File file : files) {
                        if (file.toString().equals(pathVideoGallery)) {
                            System.out.println("Files: " + file.toString());

                            bytes = new byte[(int) file.length()];

                            fis = new FileInputStream(file);
                            fis.read(bytes); //this will convert file into byte array.
                            fis.close();
                        }
                    }

                    byteStream.write(bytes);
                    outputStream.write(byteStream.toByteArray());
                } catch (Exception ex) {
                    Log.e("Error in video upload", ex.getMessage());
                }

                DriveFolder driveFolder = folderDriveId.asDriveFolder();

                MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                        .setTitle("videoFromGallery_" + System.currentTimeMillis() + ".mp4")
                        .setMimeType("video/mp4")
                        .setStarred(true).build();

                driveFolder.createFile(googleApiClient, changeSet, driveContents)
                        .setResultCallback(fileVideoCallBack);


                //openFileFromDrive();
            }
        }.start();
    }

    public void createFileGoogleDrive(DriveApi.DriveContentsResult result) {
        final DriveContents driveContents = result.getDriveContents();

        new Thread() {
            @Override
            public void run() {

                OutputStream outputStream = driveContents.getOutputStream();

                try {
                    ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                    bmp1.compress(Bitmap.CompressFormat.PNG, 100, byteStream);

                    /*File videoFile = new File(img);
                    String parent = videoFile.getParent();
                    File realPath = new File(parent);

                    File[] files = realPath.listFiles();
                    byte[] bytes = null;
                    FileInputStream fis;

                    for (File file : files) {
                        if (file.toString().equals(img)) {
                            System.out.println("Files: " + file.toString());

                            bytes = new byte[(int) file.length()];

                            fis = new FileInputStream(file);
                            fis.read(bytes); //this will convert file into byte array.
                            fis.close();
                        }
                    }

                    byteStream.write(bytes);*/
                    outputStream.write(byteStream.toByteArray());
                } catch (Exception ex) {
                    Log.e("Error in image upload", ex.getMessage());
                }



                if (outputStream != null) {
                    DriveFolder driveFolder = folderDriveId.asDriveFolder();
                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                            .setTitle("IMG_" + System.currentTimeMillis() + ".png")
                            .setMimeType("image/png")
                            .setStarred(true).build();

                    driveFolder.createFile(googleApiClient, changeSet, driveContents)
                            .setResultCallback(fileCallBack);


                    //openFileFromDrive();
                }
            }
        }.start();
    }

    final private ResultCallback<DriveFolder.DriveFileResult> fileCallBack = new
            ResultCallback<DriveFolder.DriveFileResult>() {
                @Override
                public void onResult(@NonNull DriveFolder.DriveFileResult result) {
                    if (!result.getStatus().isSuccess()) {
                        Log.e("Drive Config: ", "Error while trying to create the file");
                        return;
                    }

                    System.out.println("File url: " + "http://drive.google.com/open?id=" + result.getDriveFile().getDriveId());
                    Toast.makeText(HomeActivity.this, "file created with content: " + result.getDriveFile().getDriveId()
                            , Toast.LENGTH_SHORT).show();

                    //String dateFormat = new SimpleDateFormat("yyyyMMdd-HH:mm:ss").format(new Date());
                    addValues(result.getDriveFile().getDriveId().encodeToString(),
                            strCaption,
                            2000.0,
                            160.45,
                            "I");
                }
            };


    private void addValues(String url, String caption, double latitude, double longitude,
                           String mediaIdentify) {
        Random random = new Random();

        Toast.makeText(this, "albumId123456: "+albumid, Toast.LENGTH_SHORT).show();
        Log.i("Album123456 ", albumid);

        Map<String, String> map = new HashMap<>();
        //Toast.makeText(this, "albumId"+albumid, Toast.LENGTH_SHORT).show();

        if (albumid.equalsIgnoreCase("")) {
            map.put("AlbumId", Integer.toString(random.nextInt(1081) + 20));
        } else {
            map.put("AlbumId", albumid);
        }

        map.put("MediaId", mediaIdentify+"_"+Integer.toString(random.nextInt(1081) + 20));
        map.put("URL", url);
        map.put("caption", caption);
        map.put("Latitude", Double.toString(latitude));
        map.put("Longitude", Double.toString(longitude));

        firebase.push().setValue(map);
    }

    private void loadLatLong() {
        albumid = "";
        firebase.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map map = dataSnapshot.getValue(Map.class);

                double lat = Double.parseDouble(map.get("Latitude").toString());
                double longi = Double.parseDouble(map.get("Longitude").toString());

                Toast.makeText(HomeActivity.this, "lat: " + lat + "long: " + longi, Toast.LENGTH_SHORT).show();
                Log.i("values ", "lat: " + lat + "long: " + longi);
                String album = calcDistance(2000.0, 160.45, lat, longi);
                Random random = new Random();

                if (map.size() > 0) {
                    if (album.equalsIgnoreCase("same")) {
                       albumid = map.get("AlbumId").toString();
                        Toast.makeText(HomeActivity.this,
                                "album id: " + albumid, Toast.LENGTH_SHORT).show();
                        Log.d(TAG,"album id:: "+ albumid);

                    }
                } else {
                    albumid = Integer.toString(random.nextInt(1081) + 20);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        Log.d(TAG,"album id1 "+ albumid);
    }

    private String calcDistance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));

        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        dist = dist * 1.609344;

        Log.d(TAG, "Distance -> " + dist);

        String albumid;
        if (dist < 1) {
            albumid = "same";
        } else {
            albumid = "different";
        }

        return albumid;
    }

    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }

    final private ResultCallback<DriveFolder.DriveFileResult> fileVideoCallBack = new ResultCallback<DriveFolder.DriveFileResult>() {
        @Override
        public void onResult(@NonNull DriveFolder.DriveFileResult result) {
            if (!result.getStatus().isSuccess()) {
                Log.e("Drive Config: ", "Error while trying to create the file");
                return;
            }



            System.out.println("File url: " + "http://drive.google.com/open?id=" + result.getDriveFile().getDriveId());
            Toast.makeText(HomeActivity.this, "file created with content: " + result.getDriveFile().getDriveId()
                    , Toast.LENGTH_SHORT).show();

            addValues(result.getDriveFile().getDriveId().toString(),
                    strCaption,
                    2000.0,
                    160.45,
                    "V");
        }
    };

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("connectionGoogleDrive", "GoogleApiClient connection suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i("connectionGoogleDrive", "GoogleApiClient connection failed");

        if (!connectionResult.hasResolution()) {
            GoogleApiAvailability.getInstance().getErrorDialog(this, connectionResult.getErrorCode(), 0).show();

            return;
        }

        try {
            connectionResult.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);
        } catch (Exception ex) {
            Log.e("Drive Config", ex.getMessage());
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        MetadataChangeSet changeSet = new MetadataChangeSet.Builder().setTitle("IRecall").build();
        /*Drive.DriveApi.getRootFolder(getGoogleApiClient()).createFolder(
                getGoogleApiClient(), changeSet).setResultCallback(folderCallback);*/
        //check if folder exists

        Query query = new Query.Builder()
                .addFilter(Filters.and(Filters.eq(SearchableField.TITLE, "IRecall"),
                        Filters.eq(SearchableField.TRASHED, false))).build();
        Drive.DriveApi.query(getGoogleApiClient(), query)
                .setResultCallback(new ResultCallback<DriveApi.MetadataBufferResult>() {
                    @Override
                    public void onResult(@NonNull DriveApi.MetadataBufferResult result) {
                        if (!result.getStatus().isSuccess()) {
                            Log.d(TAG, "Cannot create folder");
                        } else {
                            boolean folderFound = false;

                            for (Metadata metadata : result.getMetadataBuffer()) {
                                if (metadata.getTitle().equalsIgnoreCase("IRecall")) {
                                    Log.d(TAG, "Folder found...");
                                    folderId1 = metadata.getEmbedLink();
                                    Log.d(TAG, "folderId: " + folderId1.split("=")[1]);
                                    Drive.DriveApi.fetchDriveId(getGoogleApiClient(), folderId1.split("=")[1])
                                            .setResultCallback(idCallback);
                                    folderFound = true;
                                    break;
                                }
                            }

                            if (!folderFound) {
                                Log.d(TAG, "Folder not found");

                                MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                        .setTitle("IRecall").build();
                                Drive.DriveApi.getRootFolder(getGoogleApiClient())
                                        .createFolder(getGoogleApiClient(), changeSet)
                                        .setResultCallback(new ResultCallbacks<DriveFolder.DriveFolderResult>() {
                                            @Override
                                            public void onSuccess(@NonNull DriveFolder.DriveFolderResult result) {
                                                Log.d(TAG, "Folder created successfully");
                                                folderId1 = result.getDriveFolder().getDriveId().getResourceId();
                                                Log.d(TAG, "folderId: " + folderId1);
                                                Drive.DriveApi.fetchDriveId(getGoogleApiClient(), folderId1.toString())
                                                        .setResultCallback(idCallback);
                                            }

                                            @Override
                                            public void onFailure(@NonNull Status status) {
                                                Log.d(TAG, "Folder created unsuccessfully");
                                            }
                            });
                            }
                        }
                    }
                });

        //folderExistsCheck ends.
    }

    final private ResultCallback<DriveApi.DriveIdResult> idCallback = new
            ResultCallback<DriveApi.DriveIdResult>() {
                @Override
                public void onResult(@NonNull DriveApi.DriveIdResult result) {
                    if (!result.getStatus().isSuccess()) {
                        Log.d(TAG, "Can't find drive id");
                    }

                    folderDriveId = result.getDriveId();
                    Log.d(TAG, "Folder drive id: " + folderDriveId);
                    /*
                    Drive.DriveApi.newDriveContents(getGoogleApiClient())
                            .setResultCallback(driveContentsCallback1);*/
                }
            };

    @Override
    public void onBackPressed(){
        finish();
       /* if (doubleBackToExitPressedOnce) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("EXIT", true);
            startActivity(intent);
        }

        if (floatingActionMenu.isOpened()) {
            floatingActionMenu.close(true);
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 3000);

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please press BACK button to exit", Toast.LENGTH_SHORT).show();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);

        *//*AlertDialog.Builder alertBuilder = new AlertDialog.Builder(HomeActivity.this);
        alertBuilder.setTitle(R.string.exit);
        alertBuilder.setMessage(R.string.exitMsg);
        alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                *//**//*Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("EXIT", true);
                startActivity(intent);
                finish();*//**//*

                dialog.cancel();
            }
        });*//*

        *//*alertBuilder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialogObj = alertBuilder.create();
        alertDialogObj.show();*/
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_logout) {
            if (googleApiClient.isConnected()) {
                signOut();
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    private void signOut() {
        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallbacks<Status>() {
                    @Override
                    public void onSuccess(@NonNull Status status) {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);

                        SharedPreferences.Editor editor = SaveSharedPref.getSharedPreferences(getApplicationContext()).edit();

                        editor.clear();
                        editor.apply();
                        finish();

                        startActivity(intent);
                    }

                    @Override
                    public void onFailure(@NonNull Status status) {
                        Log.d(TAG, "Logout unsuccessful");
                    }
                }
        );/*
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {

                    }
                });*/
    }
}
