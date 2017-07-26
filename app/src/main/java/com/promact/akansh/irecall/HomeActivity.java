package com.promact.akansh.irecall;

import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.util.TypedValue;
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
import com.firebase.client.MutableData;
import com.firebase.client.Transaction;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.ResultCallbacks;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.OpenFileActivityBuilder;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener , LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback{
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_VIDEO_CAPTURE = 2;
    private Uri videoUri;
    //private String url;
    private String name;
    private String email;
    private String photoUri;
    private String videoUriStr;
    private TextView txt;
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
    private String pathVideoGallery;
    private String pathImgGallery;
    private static final int MEDIA_TYPE_VIDEO = 200;
    private static final String VIDEO_DIR_NAME = "videoDir";
    private FloatingActionMenu floatingActionMenu;
    private FloatingActionButton floatActionGallery;
    private FloatingActionButton floatActionCamera;
    private FirebaseDatabase db;
    private Firebase firebase;
    private LocationManager locationManager;
    private String albumid;
    private String latitudeAlbum;
    private String longitudeAlbum;
    private String strCaption;
    private String userId;
    private FirebaseAuth mAuth;
    //private FirebaseUser firebaseUser;
    private static final String TAG="HomeActivity";
    private DriveId folderDriveId;
    private String folderId1;
    private GoogleApiClient mGoogleApiClient;
    private String resourceUrl;
    private SupportMapFragment mapFragment;
    //private URL imgMap;
    //private String imgCaption;
    //private String imageUrl;
    private StorageReference storageReference;
    private StorageReference imageStorage;
    private StorageReference imageFolderStorage;
    private Uri downloadUri;
    private Uri downloadGalleryImgUri;
    public String[] newStr = null;
    public int k=0;
    public int mapSize = 0;
    public String mpLatLong = "";

    public double[] lati = new double[10];
    public double[] longit = new double[10];
    public String[] capti = new String[10];
    public int j=0;
    public String cap = "";
    public int trueCount = 0;
    public int count = 0;
    public String latOut = "";
    public String longOut = "";
    public String capt = "";
    public String filename1 = "";
    public String lat_forLoop = "";
    public String long_forLoop = "";
    public String latitudeFor = "";
    public String longitudeFor = "";
    public Date dt = null;
    public static int m=0;
    public String[] array = new String[20];
    public static String abc = "";
    public static String y="";
    public AlbumDetails albumDetails;
    public AlbumDetails albumDetails2;
    public AlbumDetails albumDetails3;
    public List<AlbumDetails> arrayList = new ArrayList<>();
    public List<AlbumDetails> revList = new ArrayList<>();
    public List<String> arrList = new ArrayList<>();
    public List<String> listArr = new ArrayList<>();
    public List<String> listNum = new ArrayList<>();
    public List<AlbumDetails> listArrNew = new ArrayList<>();
    public Map<String, String> mapDetails = new TreeMap<>();
    public StringBuilder stringBuilder = new StringBuilder();
    public StringBuilder strBuild = new StringBuilder();
    public Map<String, String> revMap = new TreeMap<>(Collections.<String>reverseOrder());
    public static int z=1;
    public Marker marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_main);

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.googleMap);
        mapFragment.getMapAsync(this);

        mAuth = FirebaseAuth.getInstance();

        storageReference = FirebaseStorage.getInstance().getReference();

        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
        mGoogleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this).addApi(Auth.GOOGLE_SIGN_IN_API, options).build();

        mGoogleApiClient.connect();

        floatActionCamera = (FloatingActionButton) findViewById(R.id.menu_camera_option);
        floatActionGallery = (FloatingActionButton) findViewById(R.id.menu_gallery_option);
        floatingActionMenu = (FloatingActionMenu) findViewById(R.id.floating_action_menu);
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.content_main);

        txt = (TextView) findViewById(R.id.txtView1);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        strCaption = "";
        try{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        } catch (SecurityException e){
            Log.e("Location: ", e.getMessage());


        }

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
    public void onMapReady(final GoogleMap googleMap) {
        final LayoutInflater layoutInflater = this.getLayoutInflater();

        loadData(googleMap, layoutInflater);
    }

    public GoogleApiClient getGoogleApiClient() {
        return googleApiClient;
    }

    public Map<String, List<AlbumDetails>> groupByCategory(List<AlbumDetails> abcd) {
        Map<String, List<AlbumDetails>> map = new TreeMap<String, List<AlbumDetails>>();
        for (AlbumDetails a : abcd) {
            List<AlbumDetails> group = map.get(a.AlbumId);
            if (group==null) {
                group = new ArrayList<>();
                map.put(a.Latitude, group);
            }
            group.add(a);
        }

        return map;
    }

    private void loadData(final GoogleMap googleMap, final LayoutInflater layoutInflater) {
        firebase.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                albumDetails2 = mutableData.getValue(AlbumDetails.class);
                Log.d(TAG, "details789: "+albumDetails2.caption);
                listArrNew.add(albumDetails2);

                z++;
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(FirebaseError firebaseError, boolean b, DataSnapshot dataSnapshot) {
                //displayMap(dataSnapshot, googleMap);

                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                while (iterator.hasNext()) {
                    Log.d(TAG, "Children: "+iterator.next());

                }
                //Log.d(TAG, "The new values are: "+listArrNew.get(listArrNew.size()-1).AlbumId);
            }
        });
        firebase.orderByChild("Date").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                int l=0;
                int w=0;
                //final Map map = dataSnapshot.getValue(Map.class);
                albumDetails = dataSnapshot.getValue(AlbumDetails.class);
                albumDetails3 = dataSnapshot.getValue(AlbumDetails.class);

                for (int i=arrayList.size()-1;i>=0; i--) {

                    //strBuild.append(str5[i] + "~");
                }


                arrayList.add(albumDetails);
                revList.add(albumDetails3);
                //if (revList.size())
                if (arrayList.size()==5) {
                    /*Log.d("revList123", "revList size: " + arrayList.size() + " values: " + arrayList.get(revList.size() - 1).caption);*/
                    array[m] = arrayList.get(arrayList.size()-1).AlbumId;
                    Log.d(TAG, "Array element [" + m + "]: " + array[m]);
                    for (int i=0; i<arrayList.size(); i++) {
                        Log.d(TAG, "Strings["+i+"]: " + arrayList.get(i).caption);
                    }
                    mapDetails.put("AlbumId", arrayList.get(arrayList.size()-1).AlbumId);
                    mapDetails.put("Date", arrayList.get(arrayList.size()-1).Date);
                    mapDetails.put("Filename", arrayList.get(arrayList.size()-1).Filename);
                    mapDetails.put("Latitude", arrayList.get(arrayList.size()-1).Latitude);
                    mapDetails.put("Longitude", arrayList.get(arrayList.size()-1).Longitude);
                    mapDetails.put("MediaId", arrayList.get(arrayList.size()-1).MediaId);
                    mapDetails.put("caption", arrayList.get(arrayList.size()-1).caption);

                    revMap.putAll(mapDetails);
                    Log.d(TAG, "Map reverse: " + revMap.get("AlbumId"));
                    Log.d(TAG, "Map:"+mapDetails.get("AlbumId"));

                    if (!listNum.contains(arrayList.get(arrayList.size()-1).AlbumId)) {
                        listNum.add(arrayList.get(arrayList.size() - 1).AlbumId);
                    } else if (listNum.size()==0) {
                        listNum.add(arrayList.get(arrayList.size() - 1).AlbumId);
                    }

                    for (String str : listNum) {
                        Log.d(TAG, "num Array: " + str + " size: " + listNum.size());
                    }

                    listArr.add(arrayList.get(arrayList.size()-1).caption);
                    listArr.add(arrayList.get(arrayList.size()-1).AlbumId);
                    listArr.add(arrayList.get(arrayList.size()-1).Date);
                    listArr.add(arrayList.get(arrayList.size()-1).Filename);
                    listArr.add(arrayList.get(arrayList.size()-1).Latitude);
                    listArr.add(arrayList.get(arrayList.size()-1).Longitude);
                    listArr.add(arrayList.get(arrayList.size()-1).MediaId);
                    for (int i=0; i<listArr.size(); i++) {
                        if (i!=listArr.size()-1) {
                            if (listArr.get(i)
                                    .contains(arrayList.get(arrayList.size()-1)
                                            .Latitude) &&
                                    listArr.get(i)
                                            .contains(arrayList.get(arrayList.size()-1)
                                                    .Longitude)) {
                                Log.d(TAG, "abcd: " + listArr.get(i));
                            }
                        }
                    }

                    for (int i=0; i<arrayList.size(); i++) {
                        if (i==(arrayList.size()-1)) {
                        /*Log.d(TAG, "abcd: " + i + " " + arrayList.get(i).caption);*/
                            //listArr.add(arrayList.get(i).caption);
                        }
/*
                    listArr.add(abcd);
                    Log.d(TAG, "abc: " + i + " " + .get(i).caption);*/
                    }
                    for (String str : listArr) {
                        Log.d(TAG, "abc: " + str);
                    }

                    final double lat1 = latitude;
                    final double long1 = longitude;
                    LatLng currLatLng = new LatLng(lat1, long1);
                    Log.d(TAG, "filename: " + albumDetails.Filename);
                /*final Marker marker;*/
                    trueCount=0;
                    count=0;

                        String albumId = albumDetails.AlbumId;
                        String MediaId = albumDetails.MediaId;
                        final String filename = albumDetails.Filename;
                        final String caption = albumDetails.caption;
                        final double lat_load = Double
                                .parseDouble(albumDetails.Latitude);
                        final double long_load = Double
                                .parseDouble(albumDetails.Longitude);

                    /*albumDetails.setAlbumId(map.get("AlbumId").toString());
                    albumDetails.setDate(map.get("Date").toString());
                    albumDetails.setFilename(map.get("Filename").toString());
                    albumDetails.setLatitude(map.get("Latitude").toString());
                    albumDetails.setLongitude(map.get("Longitude").toString());
                    albumDetails.setMediaId(map.get("MediaId").toString());
                    albumDetails.setCaption(map.get("caption").toString());

                    Collection<Map.Entry> mapEntry = map.entrySet();
                    for (Map.Entry entry : mapEntry) {
                        if (!entry.getValue().equals(y) && entry.getKey().equals("Latitude")) {
                            Log.d(TAG, "y value: " + y + "\n");
                        }

                        y = y + entry.getValue();
                    }*/

                        lati[j] = Double.parseDouble(albumDetails.Latitude);
                        longit[j] = Double.parseDouble(albumDetails.Longitude);
                        capti[j] = albumDetails.caption;

                        //dt = java.sql.Date.valueOf(map.get("Date").toString());
                        DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        Date dat = new Date();
                        try {
                            dt = df.parse(albumDetails.Date);
                            int largestNumb = Collections
                                    .min(Arrays.asList(
                                            Integer.parseInt
                                                    (Long.toString(dat.getTime()
                                                            - dt.getTime()))));
                            Log.d(TAG, "The time difference is: "
                                    + (dat.getTime() - dt.getTime())
                                    + " largest time difference: " + largestNumb
                                    + " and name is: " + albumDetails.Filename);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                        Log.d(TAG, "value of lati [" + j + "]: " + lati[j]);
                        Log.d(TAG, "value of longit[" + j + "]: " + lati[j]);

                        for (int i=0; i<arrayList.size(); i++) {
                            Log.d(TAG, "value of lati [" + i + "]: " + lati[i] + ";"
                                    + " value of lat_load: " + lat_load);
                            Log.d(TAG, "value of longit[" + i + "]: " + longit[i] + ";"
                                    + " value of long_load: " + long_load
                                    + " value of caption: " + capti[i]);


                            if (lat_load != lati[i] && long_load != longit[i]) {
                                cap = albumDetails.caption;
                            } else {
                                //if (i<(arrayList.size()-2) && (lati[i+1]==lati[i] && longit[i+1]==longit[i])) {
                                //cap = map.get("caption").toString();
                                capt = albumDetails.caption;
                                filename1 = albumDetails.Filename;
                                lat_forLoop = albumDetails.Latitude;
                                long_forLoop = albumDetails.Longitude;

                                trueCount++;
                                //}
                            }
                        }

                        Collections.sort(arrayList, new Comparator<AlbumDetails>() {
                            @Override
                            public int compare(AlbumDetails o1, AlbumDetails o2) {
                                Log.d(TAG, "dateCompare: "+o1.Date.compareTo(o2.Date)
                                        +" date: "+o1.Date+" o2: "+o2.Date);

                                return o1.Date.compareTo(o2.Date);
                            }
                        });
                        Log.d(TAG, "hoem: " + arrayList.get(arrayList.size()-1).caption);
                    /*
                    if (arrayList.get(arrayList.size()-1).Latitude.contains("" + lati[i])) {
                        Log.d(TAG, "option: yes " + i);
                    } else {
                        Log.d(TAG, "option: no "+i);
                    }*/

                    /*Log.d(TAG, "caption for loop: " + capt);
                    Log.d(TAG, "filename for loop: " + filename1);
                    Log.d(TAG, "caption for loop: " + lat_forLoop);
                    Log.d(TAG, "filename for loop: " + long_forLoop);

                    Log.d(TAG, "value of true count: " + trueCount);
                    count = trueCount;
                    if (trueCount == 1) {
                        latOut = latitudeFor;
                        longOut = longitudeFor;
                        Log.d(TAG, "caption for loop: " + capt);
                        Log.d(TAG, "filename for loop: " + filename1);
                        Log.d(TAG, "latitude for loop: " + lat_forLoop);
                        Log.d(TAG, "longitude for loop: " + long_forLoop);
                    }*/
                        //final LinkedList<AlbumDetails> arrayList = new LinkedList<AlbumDetails>();
                        //arrayList.add(albumDetails);
                        //Collections.reverse(arrayList);

                        Iterator<AlbumDetails> iterator = arrayList.iterator();
                        while (iterator.hasNext()) {
                            Log.d(TAG, "Iterator: " + l + ": " + iterator.next().caption);
                        }

                        Log.d(TAG, "tags: "+arrayList.get(arrayList.size()-1).caption);
                        //if (trueCount == 1) {
                            Log.d(TAG, "abcde");

                            Log.i("values fetched ", albumId + " " + MediaId
                                    + " " + filename + " " + cap + " " + lat_load + " "
                                    + long_load + "Date: " + dt);


                            Toast.makeText(HomeActivity.this,
                                    "map Ready", Toast.LENGTH_SHORT).show();

                            /*Log.d(TAG, "Marker: " + arrayList.get(0).caption);
                            Log.d(TAG, "Marker: " + arrayList.get(1).caption);
                            Log.d(TAG, "Marker: " + arrayList.get(2).caption);
                            Log.d(TAG, "Marker: " + arrayList.get(3).caption);*/
                           // Log.d(TAG, "MarkerOk: " + arrayList.get(4).caption);

                            for (int d=0; d<arrayList.size(); d++) {
                                LatLng latLng = new LatLng(Double.parseDouble(arrayList.get(d).Latitude)
                                        , Double.parseDouble(arrayList.get(d).Longitude));
                                Log.d(TAG, "LatLng" + latLng);

                                googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                                    @Override
                                    public View getInfoWindow(Marker marker) {
                                        return null;
                                    }

                                    @Override
                                    public View getInfoContents(Marker marker) {
                                        //showProgressDialog("Download", "Downloading Image..");
                                        final View view = getLayoutInflater().inflate(
                                                R.layout.map_dialog, null);

                                        final ImageView imageView = (ImageView) view.findViewById(R.id.imgPhotoMap);
                                        final TextView title = (TextView) view.findViewById(R.id.titleMarker);

                                        String lat_abc = marker.getTitle().split("~")[1]
                                                .split(", ")[0];
                                        String long_abc = marker.getTitle().split("~")[1]
                                                .split(", ")[1];

                                        try {
                                            title.setText(marker.getTitle().split("~")[0]);
                                            Glide.with(getApplicationContext())
                                                    //.load("https://firebasestorage.googleapis.com/v0/b/irecall-4dcd0.appspot.com/o/IRecall%2F" + map.get("Filename").toString() + "?alt=media&token=1")
                                                    .load(marker.getSnippet())
                                                    .into(imageView);
                                        } catch (Exception ex) {
                                            ex.printStackTrace();
                                        }
                                        //hideProgressDialog();

                                        return view;
                                    }
                                });
                                Log.d(TAG, "tag1: "+arrayList.get(d).caption);

                                Log.d(TAG, "Children count: " + dataSnapshot.getChildrenCount());
                                w++;

                                marker = googleMap.addMarker(new MarkerOptions().position(latLng)
                                        //.title(albumDetails.caption)
                                        // .title(arrayList.get(arrayList.size()-1).caption)
                                        .title(arrayList.get(d).caption
                                                + "~loc: "+arrayList.get(d).Latitude
                                                + ", long: "+arrayList.get(d).Longitude)
                                        .snippet("https://firebasestorage.googleapis.com/v0/b/irecall-4dcd0.appspot.com/o/IRecall%2F" + /*albumDetails.Filename*/arrayList.get(arrayList.size()-1).Filename + "?alt=media&token=1"));
                               // Log.d(TAG, "Marker: " + marker.getTitle());
                               // Log.d(TAG, "Marker: " + arrayList.get(d).caption);

                                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                                    @Override
                                    public void onMapClick(LatLng latLng) {
                                       /* if (marker.isInfoWindowShown()) {
                                            marker.hideInfoWindow();

                                        }*/
                                    }
                                });
                                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                    @Override
                                    public boolean onMarkerClick(Marker marker) {
                                        //marker.showInfoWindow();
                                        //Log.d(TAG, "Marker: " + marker.getTitle());
                                        return false;
                                    }
                                });
                            }

                            googleMap.moveCamera(CameraUpdateFactory.newLatLng(currLatLng));
                        /*} else {
                            Log.d(TAG, "same lati/longit");
                        }*/
/*
                    lati[j] = Double.parseDouble(map.get("Latitude").toString());
                    longit[j] = Double.parseDouble(map.get("Longitude").toString());*/

                        j++;
/*
                    LatLng latLng = new LatLng(lat_load, long_load);
                    Log.d(TAG, "LatLng" + latLng);*/
                    /*Toast.makeText(HomeActivity.this,
                            "map Ready", Toast.LENGTH_SHORT).show();*/

/*
                    googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                        @Override
                        public View getInfoWindow(Marker marker) {
                            return null;
                        }

                        @Override
                        public View getInfoContents(Marker marker) {
                            showProgressDialog("Download", "Downloading Image..");
                            final View view = getLayoutInflater().inflate(
                                    R.layout.map_dialog, null);

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
                            hideProgressDialog();

                            return view;
                        }
                    });

                    marker = googleMap.addMarker(new MarkerOptions().position(latLng)
                            .title(map.get("caption").toString())
                            .snippet("https://firebasestorage.googleapis.com/v0/b/irecall-4dcd0.appspot.com/o/IRecall%2F" + map.get("Filename").toString() + "?alt=media&token=1"));

                    googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                        @Override
                        public void onMapClick(LatLng latLng) {
                            if (marker.isInfoWindowShown()) {
                                marker.hideInfoWindow();
                            }
                        }
                    });
                    googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {
                            marker.showInfoWindow();

                            return true;
                        }
                    });

                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(currLatLng));
                    */
                        Log.d(TAG, "abcHead: " + trueCount);
                    l++;
                    m++;
                }

            }

            public ProgressDialog mProgressDialog;

            public void showProgressDialog(String title, String message) {
                if (mProgressDialog == null) {
                    mProgressDialog = new ProgressDialog(HomeActivity.this);
                    mProgressDialog.setTitle(title);
                    mProgressDialog.setMessage(message);
                    mProgressDialog.setIndeterminate(true);
                }

                mProgressDialog.show();
            }

            public void hideProgressDialog() {
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
            }

            private Drawable ImageOperations(Context context, String url, String saveFileName) {
                try {
                    InputStream is = (InputStream) this.fetch(url);
                    Drawable d = Drawable.createFromStream(is, "src");
                    return d;
                } catch (Exception ex) {
                    ex.printStackTrace();

                    return null;
                }
            }

            public Object fetch(String addr) throws MalformedURLException, IOException {
                URL url = new URL(addr);
                Object content = url.getContent();

                return content;
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

    private void displayMap(DataSnapshot dataSnapshot, GoogleMap googleMap) {
        albumDetails = dataSnapshot.getValue(AlbumDetails.class);
        arrayList.add(albumDetails);

        Log.d(TAG, "Map: "+arrayList.get(arrayList.size()-1).AlbumId);
    }

    /*final private ResultCallback<DriveResource.MetadataResult> metaDataCallback =
            new ResultCallback<DriveResource.MetadataResult>() {
                @Override
                public void onResult(@NonNull DriveResource.MetadataResult result) {
                    if (!result.getStatus().isSuccess()) {
                        Log.d(TAG, "Problem while trying to fetch metadata");
                        return;
                    }

                    Metadata metadata = result.getMetadata();
                    resourceUrl = metadata.getEmbedLink();
                    Log.d(TAG, "Title: " + metadata.getTitle()
                            + " id: " + metadata.getEmbedLink() + "\n"
                            + resourceUrl);
                }
            };*/

    @Override
    protected void onStart() {
        super.onStart();

        db = FirebaseDatabase.getInstance();
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
        return file.length();
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
            /*case REQUEST_CODE_OPENER:
                if (resultCode == RESULT_OK) {
                    driveId = data.getParcelableExtra(
                            OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);

                    Toast.makeText(this, "File upload was successful", Toast.LENGTH_SHORT).show();

                    Log.e("url", "url: http://drive.google.com/open?id=" + driveId.getResourceId());
                }
                break;*/
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

                            /*Drive.DriveApi.newDriveContents(googleApiClient)
                                    .setResultCallback(driveVideoContentsCallback);*/
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
        alertDialog.show();
    }

    public void uploadVideoToFirebase(File file, String caption) {
        final String timestamp = new SimpleDateFormat("yyyyMMddHHmmss")
                .format(new Date());
        final double lat = latitude;
        final double longi = longitude;
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
                downloadGalleryImgUri = taskSnapshot.getDownloadUrl();
                String date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
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

                /*Drive.DriveApi.newDriveContents(googleApiClient)
                        .setResultCallback(driveGalleryContentsCallback);*/
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
                downloadGalleryImgUri = taskSnapshot.getDownloadUrl();
                Log.d(TAG, "Uploading image from gallery successful");
                Toast.makeText(HomeActivity.this,
                        "Gallery image successfully uploaded",
                        Toast.LENGTH_SHORT).show();
            }
        });
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

                /*Drive.DriveApi.newDriveContents(googleApiClient)
                        .setResultCallback(driveVideoGalleryContentsCallback);*/
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
                downloadGalleryImgUri = taskSnapshot.getDownloadUrl();
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

        Toast.makeText(this, "albumId123456: "+albumid, Toast.LENGTH_SHORT).show();
        Log.i("Album123456 ", albumid);
        Log.d(TAG, "lat " + latitude + " long: " + longitude);

        Map<String, String> map = new HashMap<>();
        //Toast.makeText(this, "albumId"+albumid, Toast.LENGTH_SHORT).show();

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
        map.put("Date", date.toString());

        firebase.push().setValue(map);
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

                        /*Drive.DriveApi.newDriveContents(googleApiClient)
                                .setResultCallback(driveContentsCallback);*/
                        //upload to firebase storage

                        uploadImageToFirebase(photoImg, strCaption);

                        //upload end

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

    public void uploadImageToFirebase(ImageView photoImg, String caption) {
        final String timestamp = new SimpleDateFormat("yyyyMMddHHmmss")
                .format(new Date());
        final String strCapt = caption;
        final double lat = latitude;
        final double longi = longitude;

        imageStorage = storageReference.child("IRecall")
                .child("IMG_" + timestamp + ".jpg");
        imageFolderStorage = storageReference.child("IRecall/someFile.jpg");

        imageStorage.getName().equals(imageFolderStorage.getName());
        imageStorage.getPath().equals(imageFolderStorage.getPath());

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
                String date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
                        .format(new Date());
                addDbValues("IMG_" + timestamp + ".jpg", strCapt,
                        22.307159, 73.181219, "I", date);
                downloadUri = taskSnapshot.getDownloadUrl();
                Log.d(TAG, "Uri: " + downloadUri);
            }
        });
    }

   /* final private ResultCallback<DriveApi.DriveContentsResult> driveContentsCallback = new
            ResultCallback<DriveApi.DriveContentsResult>(){
                @Override
                public void onResult(@NonNull DriveApi.DriveContentsResult result) {
                    if (result.getStatus().isSuccess()) {
                        if (fileOperation) {
                            createFileGoogleDrive(result);
                        }
                    }
                }
            };*/

    final private ResultCallback<DriveApi.DriveContentsResult> driveGalleryContentsCallback = new
            ResultCallback<DriveApi.DriveContentsResult>(){
                @Override
                public void onResult(@NonNull DriveApi.DriveContentsResult result) {
                    if (result.getStatus().isSuccess()) {
                        if (fileOperation) {
                            //createGalleryGoogleDrive(result);
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
                            //createVideoGalleryGoogleDrive(result);
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
                        //createVideoGoogleDrive(result);
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

            //System.out.println("url:" + url);
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

            //System.out.println("url:" + url);
        } catch (IntentSender.SendIntentException e) {
            Log.w("Drive Config", "Unable to send intent", e);
        }
    }

    /*public void createVideoGoogleDrive(DriveApi.DriveContentsResult result) {
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

                    File videoFile = new File(img);
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

                    byteStream.write(bytes);
                    //outputStream.write(//byteStream.toByteArray());
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
                            23.332168306311473,
                            85.27587890625,
                            "I");
                }
            };*/


    private void addValues(String url, String caption, double latitude, double longitude,
                           String mediaIdentify) {
        Random random = new Random();

        Toast.makeText(this, "albumId123456: "+albumid, Toast.LENGTH_SHORT).show();
        Log.i("Album123456 ", albumid);
        Log.d(TAG, "lat " + latitude + " long: " + longitude);

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
        firebase.startAt().addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map map = dataSnapshot.getValue(Map.class);
                newStr = new String[map.size()];
                mapSize = map.size();

                double lat = Double.parseDouble(map.get("Latitude").toString());
                double longi = Double.parseDouble(map.get("Longitude").toString());

                Toast.makeText(HomeActivity.this, "lat: " + lat + "long: " + longi, Toast.LENGTH_SHORT).show();
                Log.i("values ", "lat: " + lat + "long: " + longi);
                String album = calcDistance(22.640452, 72.201820, lat, longi);
                Random random = new Random();
                newStr[j] = "lat: "+lat+" long: "+longi;

                if (map.size() > 0) {
                    if (album.equalsIgnoreCase("same")) {
                       albumid = map.get("AlbumId").toString();
                        Toast.makeText(HomeActivity.this,
                                "album id: " + albumid, Toast.LENGTH_SHORT).show();
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

                k++;
                /*mpLatLong = "";

                if (!mpLatLong.contains(newStr[5])) {
                    mpLatLong = mpLatLong + newStr[5];
                }
                Log.d(TAG, "mpLatLong: " + mpLatLong);
                /*mpLatLong = mpLatLong + " abc " + newStr[5];
                Log.d(TAG, "lat/long: " + newStr[5] + "ap:" + mpLatLong);
                String[] anc = mpLatLong.split("abc");*/
                /*for (int p=0; p<anc.length; p++) {
                    if (!anc[p].equals("")) {
                        Log.d(TAG, "sampleTesting: " + anc[p]);
                    }
                }*/
                //for (String a : anc) {
                    /*if (mpLatLong.contains(a)) {
                        countInt++;
                    }/*
                    if (countInt == 1) {
                        Log.d(TAG, "sampleTesting: " + a);
                    }*/
                    //int countInt = 0;
                    //Log.d(TAG, "latLong123: " + a);
                    //if (!a.equals("")) {
                       // countInt++;
                       // Log.d(TAG, "Count abc: " + countInt);
                       // Log.d(TAG, "existsOrNot" + mpLatLong.contains(a));
                    //}
               // }
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



            /*System.out.println("File url: " + "http://drive.google.com/open?id=" + result.getDriveFile().getDriveId());
            Toast.makeText(HomeActivity.this, "file created with content: " + result.getDriveFile().getDriveId()
                    , Toast.LENGTH_SHORT).show();*/

            addValues("sdlkfjsdf",
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
        //check if folder exists

        /*Query query = new Query.Builder()
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
                                                Drive.DriveApi.fetchDriveId(getGoogleApiClient(), folderId1)
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
                });*/

        //folderExistsCheck ends.
    }

  /* final private ResultCallback<DriveApi.DriveIdResult> idCallback = new
            ResultCallback<DriveApi.DriveIdResult>() {
                @Override
                public void onResult(@NonNull DriveApi.DriveIdResult result) {
                    if (!result.getStatus().isSuccess()) {
                        Log.d(TAG, "Can't find drive id");
                    }

                    folderDriveId = result.getDriveId();
                    Log.d(TAG, "Folder drive id: " + folderDriveId);

                    Drive.DriveApi.newDriveContents(getGoogleApiClient())
                            .setResultCallback(driveContentsCallback1);
                }
            };*/

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