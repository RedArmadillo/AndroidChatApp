package group7.tcss450.uw.edu.chatapp.Fragment;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import group7.tcss450.uw.edu.chatapp.R;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class AccountSettingFragment extends Fragment {
    private static final int PICK_IMAGE_REQUEST = 71;
    private static final int PASSWORD_ITEM = 0;
    private static final int AVATAR_ITEM = 1;
    private static final String TAG = "AccountSettingFragment";

    private RecyclerView mRecycleView;
    private ImageView mImage;
    private TextView mUsername, mEmail, mPassword;
    private Uri filePath;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private List<String> mList = new ArrayList<>();
    private SettingViewAdapter mAdapter;
    public AccountSettingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_account_setting, container, false);


        mUsername = v.findViewById(R.id.acc_setting_username);
        mEmail = v.findViewById(R.id.acc_setting_email);

        SharedPreferences prefs =
                getActivity().getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        mUsername.setText(prefs.getString(getString(R.string.keys_prefs_username), ""));
        mEmail.setText(mUsername.getText() + "@armadillo");
        mImage = v.findViewById(R.id.acc_setting_avatar);
        mImage.setWillNotCacheDrawing(true);
        // Create a storage reference from our app
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        // Users click on avatar to change it
        mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the dialog that let users choose image from device
                getDialog();
            }
        });

        // A recycler view to display setting items
        mRecycleView = v.findViewById(R.id.acc_setting_rv);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext().getApplicationContext());
        mRecycleView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        mRecycleView.setLayoutManager(mLayoutManager);
        mRecycleView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new SettingViewAdapter(mList);
        mRecycleView.setAdapter(mAdapter);

        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mList.size() == 0) {
            populateList();
        }
        // Create a reference with path and name
        StorageReference pathReference = storageReference.child("images/avatars/" + mUsername.getText().toString());

        // Now loading their avatar into the image view
        mImage.setImageDrawable(null);
        mImage.setImageBitmap(null);
        Glide.with(getContext())
                .using(new FirebaseImageLoader())
                .load(pathReference)
                .error(R.drawable.armadillo)
                .into(mImage);
    }

    // Populate the recycler view list
    private void populateList() {
        mList.add(PASSWORD_ITEM, "Update Password");
        mList.add(AVATAR_ITEM, "Change avatar");
    }

    private void getDialog() {
        AlertDialog builder =  new AlertDialog.Builder(getActivity()).create();
        builder.setMessage("Upload your own avatar");
        // Allow user to choose image locally
        builder.setButton(AlertDialog.BUTTON_POSITIVE, "CHOOSE", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                chooseImage();
            }
        });

        builder.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL", (dialog, which) -> dialog.dismiss());
        builder.show();

    }
    // Handle upload to Firebase storage
    private void uploadImage() {
        if(filePath != null) {
            // As a convention, all users' avatar will be saved in the folder images/avatars/<their username as file name>
            StorageReference ref = storageReference.child(getString(R.string.keys_firebase_avatars_folder) + mUsername.getText().toString());
            UploadTask uploadTask = ref.putFile(filePath);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    Toast.makeText(getContext(), "Failed " + exception.getMessage(), Toast.LENGTH_SHORT).show();

                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(getContext(), "Uploaded", Toast.LENGTH_SHORT).show();

                }
            });
        }

    }

    // Open new file chooser
    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    // Return from chooseImage
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                // Change the image view inside
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                mImage.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            // More importantly, upload to Firebase storage
            uploadImage();
        }
    }
    private void loadFragment(Fragment frag) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.landingContainer, frag)
                .addToBackStack(null);
        // Commit the transaction
        transaction.commit();
    }

    private class SettingViewAdapter extends RecyclerView.Adapter<SettingViewAdapter.ViewHolder> {
        private List<String> mList;

        public SettingViewAdapter(List<String> list) {
            mList = list;
        }
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_setting_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.mTitle.setText(mList.get(position));
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // User chooses change password or set avatar
                    switch (position) {
                        case PASSWORD_ITEM:
                            loadFragment(new ResetPasswordFragment());
                            break;
                        case AVATAR_ITEM:
                            getDialog();
                            break;
                        default:
                            break;
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public View mView;
            public TextView mTitle;
            public ViewHolder(View itemView) {
                super(itemView);
                mView = itemView;
                mView.setClickable(true);
                mTitle = (TextView) itemView.findViewById(R.id.settingTitle);
            }
        }
    }

}
