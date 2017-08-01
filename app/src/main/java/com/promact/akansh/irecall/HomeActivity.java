package com.promact.akansh.irecall;

import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
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
import android.view.ViewGroup;
import android.view.WindowManager;
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
import com.firebase.client.MutableData;
import com.firebase.client.Transaction;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallbacks;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener , LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback{
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_VIDEO_CAPTURE = 2;
    private String name;
    double latitude;
    double longitude;
    private static String fileName;
    private static final int REQUEST_CODE_RESOLUTION = 1;
    private static final int SELECT_PICTURE = 100;
    private static final int SELECT_VIDEO = 500;
    private String pathVideoGallery;
    private String pathImgGallery;
    private static final int MEDIA_TYPE_VIDEO = 200;
    private static final String VIDEO_DIR_NAME = "videoDir";
    private FloatingActionMenu floatingActionMenu;
    private Firebase firebase;
    private String albumid;
    private String latitudeAlbum;
    private String longitudeAlbum;
    private String strCaption;
    private FirebaseAuth mAuth;
    //private FirebaseUser firebaseUser;
    private static final String TAG="HomeActivity";
    private GoogleApiClient mGoogleApiClient;
    private StorageReference storageReference;
    private Uri downloadUri;
    public String[] newStr = null;
    public int mapSize = 0;

    public double[] lati = new double[10];
    public double[] longit = new double[10];
    public String[] capti = new String[10];
    public int j=0;
    public String cap = "";
    public int trueCount = 0;
    public int count = 0;
    public Date dt = null;
    public AlbumDetails albumDetails;
    public List<AlbumDetails> arrayList = new ArrayList<>();
    public List<AlbumDetails> revList = new ArrayList<>();
    public Map<String, ArrayList<AlbumDetails>> revMap = new HashMap<>();
    public Marker marker;
    public long childrenCount=0;
    public boolean showLock = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_main);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.googleMap);
        mapFragment.getMapAsync(this);

        mAuth = FirebaseAuth.getInstance();

        storageReference = FirebaseStorage.getInstance().getReference();

        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
        mGoogleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this).addApi(Auth.GOOGLE_SIGN_IN_API, options).build();

        mGoogleApiClient.connect();

        FloatingActionButton floatActionCamera = (FloatingActionButton) findViewById(R.id.menu_camera_option);
        FloatingActionButton floatActionGallery = (FloatingActionButton) findViewById(R.id.menu_gallery_option);
        floatingActionMenu = (FloatingActionMenu) findViewById(R.id.floating_action_menu);
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.content_main);

        TextView txt = (TextView) findViewById(R.id.txtView1);
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        strCaption = "";
        try{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        } catch (SecurityException e){
            Log.e("Location: ", e.getMessage());


        }

        String userId;
        String email;
        String photoUri;

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

            //Toast.makeText(this, "logged in as: " + email, Toast.LENGTH_SHORT).show();
        }

        //Toast.makeText(this, "userid: " + userId, Toast.LENGTH_SHORT).show();
        Log.d(TAG, "userid: " + userId);
        Firebase.setAndroidContext(getApplicationContext());
        firebase = new Firebase("https://irecall-4dcd0.firebaseio.com/" + userId);

        loadLatLong();

        Log.d(TAG, "id:: " + albumid);

        NavigationView nav = (NavigationView) findViewById(R.id.nav_view);
        nav.setItemIconTintList(null);
        View newView = nav.getHeaderView(0); //Gets the header view from the header page, where all the widgets are kept.
        View viewSnackbar = findViewById(android.R.id.content);

        TextView txtUname = (TextView) newView.findViewById(R.id.usernm);
        TextView txtEmail = (TextView) newView.findViewById(R.id.emailNav);
        CircleImageView profilePic = (CircleImageView) newView.findViewById(R.id.imgProfile);

        txtUname.setText(name);
        txtEmail.setText(email);
        Glide.with(getApplicationContext())
                .load(photoUri)
                .centerCrop()
                .into(profilePic);
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
        /*if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
                    .addScope(Drive.SCOPE_APPFOLDER)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }

        googleApiClient.connect();*/
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        loadData(googleMap);
    }

    private void loadData(final GoogleMap googleMap) {
        firebase.orderByChild("Date").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                com.firebase.client.Transaction.Handler handler = new
                        Transaction.Handler() {
                            @Override
                            public Transaction.Result doTransaction(MutableData mutableData) {
                                childrenCount = mutableData.getChildrenCount();
                                return null;
                            }

                            @Override
                            public void onComplete(FirebaseError firebaseError, boolean b, DataSnapshot dataSnapshot) {

                            }
                        };
                firebase.runTransaction(handler);

                albumDetails = dataSnapshot.getValue(AlbumDetails.class);
                Log.d(TAG, "str length: " + childrenCount);

                arrayList.add(albumDetails);
                revMap.put(albumDetails.AlbumId, new ArrayList<AlbumDetails>());

                if (arrayList.size()==childrenCount) {
                    Log.d(TAG, "inside if condition");
                    for (int i=arrayList.size()-1;i>=0; i--) {
                        revList.add(arrayList.get(i));
                    }
                    Log.d("revList123", "revList size: " + revList.size() + " values: " + revList.get(0).caption);

                    for (String key : revMap.keySet()) {
                        for (int i=0; i<revList.size(); i++) {
                            AlbumDetails album = revList.get(i);
                            if(key.equals(album.AlbumId)){
                                ArrayList<AlbumDetails> imagesListOfAlbum = revMap.get(key);
                                imagesListOfAlbum.add(album);
                                revMap.put(key, imagesListOfAlbum);
                            }
                        }
                    }
                    Set<String> set = revMap.keySet();

                    for (String s2 : set) {
                        Log.d(TAG, "Key ----- : "+s2);
                        ArrayList<AlbumDetails> str = revMap.get(s2);
                        for (int i=0; i<str.size(); i++) {
                            Log.d(TAG, "Values ----- " + str.get(i).caption);
                        }

                        //map start
                        LatLng latLng = new LatLng(Double.parseDouble(str.get(0).Latitude)
                                , Double.parseDouble(str.get(0).Longitude));
                        Log.d(TAG, "LatLng" + latLng);

                        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                            @Override
                            public View getInfoWindow(Marker marker) {
                                return null;
                            }

                            @Override
                            public View getInfoContents(Marker marker) {
                                final ViewGroup viewGroup = new RelativeLayout(HomeActivity.this);
                                final View view = getLayoutInflater().inflate(
                                        R.layout.map_dialog, viewGroup, false);

                                final ImageView imageView = (ImageView) view.findViewById(R.id.imgPhotoMap);
                                final TextView title = (TextView) view.findViewById(R.id.titleMarker);

                                try {
                                    title.setText(marker.getTitle());
                                    Glide.with(getApplicationContext())
                                            //.load("https://firebasestorage.googleapis.com/v0/b/irecall-4dcd0.appspot.com/o/IRecall%2F" + map.get("Filename").toString() + "?alt=media&token=1")
                                            .load(marker.getSnippet())
                                            .into(imageView);
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }

                                return view;
                            }
                        });

                        marker = googleMap.addMarker(new MarkerOptions()
                                .position(latLng).title(str.get(0).caption)
                                .snippet("https://firebasestorage.googleapis.com/v0/b/irecall-4dcd0.appspot.com/o/IRecall%2F" + str.get(0).Filename + "?alt=media&token=1"));

                        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                            @Override
                            public void onInfoWindowClick(Marker marker) {
                                for (int i=0; i<revList.size(); i++) {
                                    if (revList.get(i).Latitude
                                            .equals(marker.getTitle())) {
                                        Log.d(TAG, "matched: "
                                                + revList.get(i).caption);
                                    }
                                }
                            }
                        });
                    }
                    Log.d(TAG, "revMap size: "+revMap.size());

                    final double lat1 = latitude;
                    final double long1 = longitude;
                    LatLng currLatLng = new LatLng(lat1, long1);
                    Log.d(TAG, "filename: " + albumDetails.Filename);
                    trueCount=0;
                    count=0;

                    String albumId = albumDetails.AlbumId;
                    String MediaId = albumDetails.MediaId;
                    final String filename = albumDetails.Filename;
                    final double lat_load = Double
                            .parseDouble(albumDetails.Latitude);
                    final double long_load = Double
                            .parseDouble(albumDetails.Longitude);
                    lati[j] = Double.parseDouble(albumDetails.Latitude);
                    longit[j] = Double.parseDouble(albumDetails.Longitude);
                    capti[j] = albumDetails.caption;

                    Log.i("values fetched ", albumId + " " + MediaId
                            + " " + filename + " " + cap + " " + lat_load + " "
                            + long_load + "Date: " + dt);

                    Log.d(TAG, "map Ready");

                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(currLatLng));
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
    }

    @Override
    protected void onStart() {
        super.onStart();

        Toast.makeText(HomeActivity.this, "Inside onStart() Method", Toast.LENGTH_SHORT).show();
        View viewSnackbar = findViewById(android.R.id.content);
        Snackbar.make(viewSnackbar, "logged in as:" + name, Snackbar.LENGTH_LONG).show();
        //db = FirebaseDatabase.getInstance();
        //firebaseUser = mAuth.getCurrentUser();
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
        longitude = location.getLongitude();

        Log.d(TAG, "lattitude: " + latitude + " longitude " + longitude);
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
        return file.length();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //DriveId driveId;
        long fileSize;

        switch (requestCode) {
            case REQUEST_IMAGE_CAPTURE:
                if (resultCode == RESULT_OK) {
                    Toast.makeText(this,
                            "from image chooser", Toast.LENGTH_SHORT).show();
                    showLock = true;
                    Bundle bundle = data.getExtras();
                    Bitmap bitmap1 = (Bitmap) bundle.get("data");

                    showPhotoDialog(bitmap1);
                }
                break;
            case REQUEST_VIDEO_CAPTURE:
                if (fileName != null && resultCode == RESULT_OK) {
                    File file = new File(fileName);
                    fileSize = getFileSize(file) / 1024;
                    showLock = true;

                    if ((fileSize / 1024) < 5) {
                        showVideoDialog();
                    } else {
                        Toast.makeText(this, "video file greater than 5MB", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case SELECT_PICTURE:
                if (data != null && resultCode == RESULT_OK) {
                    Uri selectedImageUri = data.getData();
                    showLock = true;
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
                    Uri selectedVideoUri = data.getData();
                    showLock = true;
                    try {
                        pathVideoGallery = getPath(HomeActivity.this, selectedVideoUri);

                        System.out.println(pathVideoGallery);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                    showSelectedVideoDialog();
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

        View viewSnackbar = findViewById(android.R.id.content);
        Snackbar.make(viewSnackbar, "Welcome back " + name, Snackbar.LENGTH_LONG).show();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }

        super.onPause();
    }

    public void showVideoDialog() {

        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(HomeActivity.this, R.style.AppCompatAlertDialog);
        LayoutInflater layoutInflater = this.getLayoutInflater();
        final ViewGroup viewGroup = new RelativeLayout(HomeActivity.this);
        final View dialogView = layoutInflater.inflate(R.layout.dialog_layout, viewGroup, false);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle("Upload Video");

        final EditText txtCaption = (EditText) dialogView.findViewById(R.id.txtBoxCaption);
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
                            System.out.println("Files: " + file.toString());

                            uploadVideoToFirebase(file, strCaption);
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
        try {
            alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams
                    .SOFT_INPUT_ADJUST_RESIZE);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        alertDialog.show();
    }

    public void uploadVideoToFirebase(File file, String caption) {
        final String timestamp = new SimpleDateFormat("yyyyMMddHHmmss", Locale.ENGLISH)
                .format(new Date());
        final String strCapt = caption;

        Uri fileVideo = Uri.fromFile(file);
        StorageReference videoRef = storageReference.child("IRecall")
                .child(fileVideo.getLastPathSegment());
        UploadTask videoUpload = videoRef.putFile(fileVideo);

        videoUpload.addOnFailureListener(HomeActivity.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Error while uploading the video from gallery");
            }
        }).addOnSuccessListener(HomeActivity.this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                String date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.ENGLISH)
                        .format(new Date());
                Log.d(TAG, "Video uploaded successfully");
                addDbValues("VID_" + timestamp + ".mp4", strCapt,
                        Double.parseDouble(latitudeAlbum),
                        Double.parseDouble(longitudeAlbum), "V", date);
                Toast.makeText(HomeActivity.this,
                        "Video uploaded successfully",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showSelectedImageDialog(Uri bitmap) {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(HomeActivity.this, R.style.AppCompatAlertDialog);
        LayoutInflater layoutInflater = this.getLayoutInflater();
        final ViewGroup viewGroup = new RelativeLayout(HomeActivity.this);
        final View dialogView = layoutInflater.inflate(R.layout.dialog_layout, viewGroup, false);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle("Upload Image");

        final EditText txtCaption = (EditText) dialogView.findViewById(R.id.txtBoxCaption);
        final ImageView photoImg = (ImageView) dialogView.findViewById(R.id.imgPhoto);

        photoImg.setImageURI(bitmap);
        dialogBuilder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                strCaption = txtCaption.getText().toString();
                Log.d(TAG, "caption: " + strCaption);

                uploadGalleryImageToFirebase(pathImgGallery);

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
        try {
            alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams
                    .SOFT_INPUT_ADJUST_RESIZE);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        alertDialog.show();
    }

    public void uploadGalleryImageToFirebase(String galleryImage) {
        Uri file = Uri.fromFile(new File(galleryImage));
        StorageReference galleryRef = storageReference.child("IRecall")
                .child(file.getLastPathSegment());
        UploadTask galleryImgUpload = galleryRef.putFile(file);

        galleryImgUpload.addOnFailureListener(HomeActivity.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Error while uploading the image from gallery");
            }
        }).addOnSuccessListener(HomeActivity.this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "Uploading image from gallery successful");
                Toast.makeText(HomeActivity.this,
                        "Gallery image successfully uploaded",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showSelectedVideoDialog() {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(HomeActivity.this, R.style.AppCompatAlertDialog);
        LayoutInflater layoutInflater = this.getLayoutInflater();
        final ViewGroup viewGroup = new RelativeLayout(HomeActivity.this);
        final View dialogView = layoutInflater.inflate(R.layout.dialog_layout, viewGroup, false);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle("Upload Video");

        final EditText txtCaption = (EditText) dialogView.findViewById(R.id.txtBoxCaption);
        Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(pathVideoGallery, MediaStore.Images.Thumbnails.MINI_KIND);
        final ImageView videoImg = (ImageView) dialogView.findViewById(R.id.imgPhoto);

        videoImg.setImageBitmap(thumbnail);

        dialogBuilder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                strCaption = txtCaption.getText().toString();
                Log.d(TAG, "caption: " + strCaption);

                uploadGalleryVideoToFirebase(pathVideoGallery);

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
        try {
            alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams
                    .SOFT_INPUT_ADJUST_RESIZE);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        alertDialog.show();
    }

    public void uploadGalleryVideoToFirebase(String galleryVideo) {
        Uri file = Uri.fromFile(new File(galleryVideo));
        StorageReference galleryRef = storageReference.child("IRecall")
                .child(file.getLastPathSegment());
        UploadTask galleryImgUpload = galleryRef.putFile(file);

        galleryImgUpload.addOnFailureListener(HomeActivity.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Error while uploading the video from gallery");
            }
        }).addOnSuccessListener(HomeActivity.this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "Video from gallery uploaded successfully.");
                Toast.makeText(HomeActivity.this,
                        "Gallery video successfully uploaded",
                        Toast.LENGTH_SHORT).show();
            }
        });
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

    private void addDbValues(String filename, String caption, double latitude, double longitude,
                           String mediaIdentify, String date) {
        Random random = new Random();

        Log.d(TAG, "Album123456: " + albumid);
        Log.d(TAG, "lat " + latitude + " long: " + longitude);

        Map<String, String> map = new HashMap<>();

        if (albumid.equalsIgnoreCase("")) {
            map.put("AlbumId", Integer.toString(random.nextInt(1081) + 20));
        } else {
            map.put("AlbumId", albumid);
        }

        map.put("MediaId", mediaIdentify+"_"+Integer.toString(random.nextInt(1081) + 20));
        map.put("Filename", filename);
        map.put("caption", caption);
        map.put("Latitude", Double.toString(latitude));
        map.put("Longitude", Double.toString(longitude));
        map.put("Date", date);

        firebase.push().setValue(map);
    }

    public void showPhotoDialog(Bitmap bmp) {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialog);
        LayoutInflater layoutInflater = this.getLayoutInflater();
        final ViewGroup viewGroup = new RelativeLayout(HomeActivity.this);
        final View dialogView = layoutInflater.inflate(R.layout.dialog_layout, viewGroup, false);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle("Upload Image");

        final EditText txtCaption = (EditText) dialogView.findViewById(R.id.txtBoxCaption);
        final ImageView photoImg = (ImageView) dialogView.findViewById(R.id.imgPhoto);

        photoImg.setImageBitmap(bmp);

        String positiveText = getString(android.R.string.ok);
        dialogBuilder.setPositiveButton(positiveText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        strCaption = txtCaption.getText().toString();
                        Log.d(TAG, "caption: " + strCaption);

                        uploadImageToFirebase(photoImg, strCaption);

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
        try {
            alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams
                    .SOFT_INPUT_ADJUST_RESIZE);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        alertDialog.show();
    }

    public void uploadImageToFirebase(ImageView photoImg, String caption) {
        final String timestamp = new SimpleDateFormat("yyyyMMddHHmmss", Locale.ENGLISH)
                .format(new Date());
        final String strCapt = caption;

        StorageReference imageStorage = storageReference.child("IRecall")
                .child("IMG_" + timestamp + ".jpg");
        photoImg.setDrawingCacheEnabled(true);
        photoImg.buildDrawingCache();

        Bitmap bitmap = photoImg.getDrawingCache();
        ByteArrayOutputStream outputStream =
                new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100,
                outputStream);
        byte[] data = outputStream.toByteArray();

        UploadTask uploadTask = imageStorage.putBytes(data);
        uploadTask.addOnFailureListener(HomeActivity.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(HomeActivity.this,
                        "Upload was unsuccessful",
                        Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(HomeActivity.this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(HomeActivity.this,
                        "Upload was successful",
                        Toast.LENGTH_SHORT).show();
                String date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.ENGLISH)
                        .format(new Date());
                addDbValues("IMG_" + timestamp + ".jpg", strCapt,
                        21.170240,72.831061, "I", date);
                downloadUri = taskSnapshot.getDownloadUrl();
                Log.d(TAG, "Uri: " + downloadUri);
            }
        });
    }

    private void loadLatLong() {
        albumid = "";
        firebase.startAt().addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map map = dataSnapshot.getValue(Map.class);
                newStr = new String[map.size()];
                mapSize = map.size();

                double lat = Double.parseDouble(map.get("Latitude").toString());
                double longi = Double.parseDouble(map.get("Longitude").toString());

                Log.d(TAG, "values-" + " latitude: " + lat + " longitude: " + longi);
                String album = calcDistance(latitude, longitude, lat, longi);
                Random random = new Random();
                newStr[j] = "lat: "+lat+" long: "+longi;

                if (map.size() > 0) {
                    if (album.equalsIgnoreCase("same")) {
                       albumid = map.get("AlbumId").toString();
                        Log.d(TAG,"album id:: "+ albumid);
                        longitudeAlbum = map.get("Longitude").toString();
                        latitudeAlbum = map.get("Latitude").toString();
                    } else {
                        albumid = Integer.toString(random.nextInt(1081) + 20);
                        longitudeAlbum = Double.toString(longitude);
                        latitudeAlbum = Double.toString(latitude);
                    }
                } else {
                    albumid = Integer.toString(random.nextInt(1081) + 20);
                    longitudeAlbum = Double.toString(longitude);
                    latitudeAlbum = Double.toString(latitude);
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

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "GoogleApiClient connection suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        //Log.d(TAG, "GoogleApiClient connection failed");

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
        Log.d(TAG, "Connected");
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("EXIT", true);
        startActivity(intent);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_logout) {
            if (mGoogleApiClient.isConnected()) {
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
        Auth.GoogleSignInApi.signOut(mGoogleApiClient)
                .setResultCallback(new ResultCallbacks<Status>() {
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
        });
    }
}