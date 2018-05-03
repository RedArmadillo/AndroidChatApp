package group7.tcss450.uw.edu.chatapp.Front_End_Register_Login;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import group7.tcss450.uw.edu.chatapp.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment implements View.OnClickListener{

    private OnFragmentInteractionListener mListener;

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_login, container, false);
        Button b = (Button) v.findViewById(R.id.register_l_Button);
        b.setOnClickListener(view -> mListener.onRegisterLoadClicked());
        b = (Button) v.findViewById(R.id.loginButton2);
        b.setOnClickListener(this::onClick);
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View view) {
        if (mListener != null) switch (view.getId()) {
            case R.id.loginButton2:
                onLoginButtonPressed(getView());
                break;
            case R.id.register_l_Button:
                mListener.onRegisterLoadClicked();
            default:
                Log.wtf("", "Didn't Expect to see me.");
        }
    }



    public void onLoginButtonPressed(View v) {

        EditText username = getView().findViewById(R.id.loginUsername);
        EditText pasword = getView().findViewById(R.id.loginPassword);

        Credentials creds = new Credentials.Builder(
                username.getText().toString(), pasword.getText())
                .build();

        if (Validation(v, creds, pasword.getText().toString())) {

            mListener.onLoginAttempt(creds);
        } else {
            Log.d("NOT VALID", "FAILED VALIDATION");
        }

    }

    public static boolean Validation(View v, Credentials cred, String thepassword) {
        String userName = cred.getUsername();
        String password = thepassword;
        boolean isValid = false;
        boolean flaga = false;
        boolean flagb = false;
        Log.d("Validation", "Hello");
        if (userName.length() == 0) {
            ((EditText) v.findViewById(R.id.loginUsername)).setError("Username Can Not Be Empty!");
        } else {
            flaga = true;
        }
        if (password.length() == 0) {
            ((EditText) v.findViewById(R.id.loginPassword)).setError("Password Can Not Be Empty!");
        } else if (password.length() < 6) {
            ((EditText) v.findViewById(R.id.loginPassword)).setError("Password must be at least 6 characters");
        } else {
            flagb = true;
        }
        if (flaga & flagb) {
            isValid = true;
        }
        return isValid;

    }


    public interface OnFragmentInteractionListener {
        void onLoginAttempt(Credentials credentials);
        void onRegisterLoadClicked();
    }


    /**
     * Allows an external source to set an error message on this fragment. This may
     * be needed if an Activity includes processing that could cause login to fail.
     * @param err the error message to display.
     */
    public void setError(String err) {
//Log in unsuccessful for reason: err. Try again.
//you may want to add error stuffs for the user here.
        ((TextView) getView().findViewById(R.id.loginUsername))
                .setError("Login Unsuccessful");
    }


}
