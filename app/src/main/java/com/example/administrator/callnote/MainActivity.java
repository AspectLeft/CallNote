package com.example.administrator.callnote;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    public static boolean service_state = false;
    public static int fragment_state = 0;

    Note note;
    SetWord setWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=12345678");

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                        != PackageManager.PERMISSION_GRANTED||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED
                )
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO))
            {
                Log.d("MainActivity", "shouldSPR");
            }
            else
            {
                Log.d("MainActivity", "!shouldSPR");
            }
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (service_state)
                {
                    fab.setBackgroundTintList(ColorStateList.valueOf(Color.DKGRAY));
                    stopService(new Intent(MainActivity.this, MSCService.class));

                }
                else
                {
                    fab.setBackgroundTintList(ColorStateList.valueOf(Color.YELLOW));
                    startService(new Intent(MainActivity.this, MSCService.class));

                }
                service_state = !service_state;
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        note = (Note)getFragmentManager().findFragmentById(R.id.fragment_note);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem menuItem_save_word = menu.findItem(R.id.action_save_word);
        MenuItem menuItem_delete_setted_words = menu.findItem(R.id.action_delete_setted_words);
        MenuItem menuItem_new_note = menu.findItem(R.id.action_new_note);
        switch (fragment_state)
        {
            case 0:
                menuItem_new_note.setVisible(true);
                menuItem_save_word.setVisible(false);
                menuItem_delete_setted_words.setVisible(false);
                break;
            case 1:
                menuItem_new_note.setVisible(false);
                menuItem_save_word.setVisible(true);
                menuItem_delete_setted_words.setVisible(true);
                break;

        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        else if (id == R.id.action_new_note)
        {
            startActivityForResult(new Intent(MainActivity.this, EditNote.class), Note.REQUEST_CODE_ADD_NOTE);
        }
        else if (id == R.id.action_save_word)
        {
            SetWord setWord = (SetWord)getFragmentManager().findFragmentById(R.id.main_frame);
            setWord.insertOnClick();

        }
        else if (id == R.id.action_delete_setted_words)
        {
            SetWord setWord = (SetWord)getFragmentManager().findFragmentById(R.id.main_frame);
            setWord.deleteOnClick();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            fragment_state = 0;
            invalidateOptionsMenu();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            if (setWord != null)
            {
                transaction.hide(setWord);
            }
            transaction.show(note);
            transaction.commit();
        } else if (id == R.id.nav_gallery) {
            fragment_state = 1;
            invalidateOptionsMenu();



            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            if (setWord == null)
            {
                setWord = new SetWord();
                transaction.add(R.id.main_frame, setWord);
            }
            else
            {
                transaction.show(setWord);
            }
            transaction.hide(note);
            transaction.commit();



        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {

        switch (requestCode) {
            case Note.REQUEST_CODE_ADD_NOTE:
            case Note.REQUEST_CODE_EDIT_NOTE:
                if (resultCode == Activity.RESULT_OK) {
                    Note note = (Note) getFragmentManager().findFragmentById(R.id.fragment_note);
                    note.refreshMemosListView();
                }
                break;

            default:
                break;

        }


        super.onActivityResult(requestCode, resultCode, data);
    }
}
