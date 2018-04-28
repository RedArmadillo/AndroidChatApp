package group7.tcss450.uw.edu.chatapp.Front_End_Register_Login;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


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
        View v = inflater.inflate(R.layout.fragment_user_login, container, false);
        Button b = (Button) v.findViewById(R.id.login_button_login);
        b.setOnClickListener(view -> onLoginButtonPressed(v));
        Button d = (Button) v.findViewById(R.id.register_button_login);
        d.setOnClickListener(view -> mListener.onRegisterLoadClicked());

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
            case R.id.login_button_login:
                onLoginButtonPressed(view);
                break;
            case R.id.register_button_login:
                mListener.onRegisterLoadClicked();
            default:
                Log.wtf("", "Didn't Expect to see me.");
        }
    }

    public void onLoginButtonPressed(View v) {

        if (performValidation()) {
            EditText username = getView().findViewById(R.id.userText);
            EditText pasword = getView().findViewById(R.id.passText);

            Credentials creds = new Credentials.Builder(
                    username.getText().toString(), pasword.getText())
                    .build();

            mListener.onLoginAttempt(creds);
        }

    }
    //TODO
    private boolean performValidation() {
        return true;
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
        ((TextView) getView().findViewById(R.id.userText))
                .setError("Login Unsuccessful");
    }


}
