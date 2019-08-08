package com.example.travelmantic;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.ui.auth.data.model.Resource;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class DealActivity extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private static final int PICTURE_RESULT = 42;
    EditText txtTitle;
    EditText txtDesc;
    EditText txtPrice;
    ImageView mImageView;
    private String imageUri;
    TravelDeal deal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deal);
//        FirebaseUtil.openFbReference("traveldeals",this);
        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase;
        mDatabaseReference = FirebaseUtil.mDatabaseReference;

        txtTitle = findViewById(R.id.text_name);
        txtPrice = findViewById(R.id.text_price);
        txtDesc = findViewById(R.id.text_desc);
        mImageView = findViewById(R.id.image);

        Intent intent = getIntent();

        TravelDeal deal = (TravelDeal) intent.getSerializableExtra("Deal");
        if(deal == null){
            deal = new TravelDeal();
        }
        this.deal = deal;
        txtTitle.setText(deal.getTitle());
        txtDesc.setText(deal.getDescrp());
        txtPrice.setText(deal.getPrice());
//        showImage(deal.getImageUri());
        Button buttonImage = findViewById(R.id.btnImage);
        buttonImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(intent.createChooser(intent, "Insert picture"), PICTURE_RESULT);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_menu, menu);
        if(FirebaseUtil.isAdmin){
            menu.findItem(R.id.delete_menu).setVisible(true);
            menu.findItem(R.id.save_menu).setVisible(true);
            enableEditTexts(true);
        }
        else{
            menu.findItem(R.id.delete_menu).setVisible(false);
            menu.findItem(R.id.save_menu).setVisible(false);
            enableEditTexts(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.save_menu:
                saveDeal();
                Toast.makeText(this, "Deal saved", Toast.LENGTH_LONG).show();
                clearDeal();
                backToList();
                return true;
            case R.id.delete_menu:
                deleteDeal();
                Toast.makeText(this, "Deal delete", Toast.LENGTH_LONG).show();
                backToList();
                return true;

                default:
                    return super.onOptionsItemSelected(item);

        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICTURE_RESULT && resultCode == RESULT_OK){
            final Uri imageUri = data.getData();
            final StorageReference ref = FirebaseUtil.mStorageRef.child(imageUri.getLastPathSegment());
            ref.putFile(imageUri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                    String uri = taskSnapshot.getStorage().getDownloadUrl().toString();
//                    deal.setImageUri(uri);
//                    showImage(uri);

                    String picName = taskSnapshot.getStorage().getPath();
                    deal.setImageUri(imageUri.toString());
                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            deal.setImageUri(uri.toString());
                            showImage(uri.toString());

                        }
                    });
                }
            });
        }
    }


//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == PICTURE_RESULT && resultCode == RESULT_OK) {
//            Uri imageUri = data.getData();
//            StorageReference ref = FirebaseUtil.mStorageRef.child(imageUri.getLastPathSegment());
//            ref.putFile(imageUri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                    String url = taskSnapshot.getDownloadUrl().toString();
//                    String pictureName = taskSnapshot.getStorage().getPath();
//                    deal.setImageUri(url);
//                    deal.setImageName(pictureName);
//                    Log.d("Url: ", url);
//                    Log.d("Name", pictureName);
//                    showImage(url);
//                }
//            });
//
//        }
//    }

    private void saveDeal() {
        deal.setTitle(txtTitle.getText().toString());
        deal.setPrice(txtPrice.getText().toString());
        deal.setDescrp(txtDesc.getText().toString());

        if(deal.getId() == null){
            mDatabaseReference.push().setValue(deal);
        }
        else{
            mDatabaseReference.child(deal.getId()).setValue(deal);
        }
    }

    private void deleteDeal(){
        if(deal == null){
            Toast.makeText(this, "Please save deal before deleting", Toast.LENGTH_LONG).show();
            return;
        }

        mDatabaseReference.child(deal.getId()).removeValue();
    }

    private void backToList(){
        Intent intent = new Intent(this, ListActivity.class);
        startActivity(intent);
    }

    private void clearDeal() {
        txtDesc.setText("");
        txtTitle.setText("");
        txtPrice.setText("");

        txtTitle.requestFocus();

    }

    private void enableEditTexts(boolean isEnabled){
        txtPrice.setEnabled(isEnabled);
        txtDesc.setEnabled(isEnabled);
        txtPrice.setEnabled(isEnabled);
    }

    private void showImage(String uri){
        if(uri != null && uri.isEmpty() == false){
            int width = Resources.getSystem().getDisplayMetrics().widthPixels;
            Picasso.get()
                    .load(uri)
                    .resize(width, width * 2/3)
                    .centerCrop()
                    .into(mImageView);
        }
    }
}
