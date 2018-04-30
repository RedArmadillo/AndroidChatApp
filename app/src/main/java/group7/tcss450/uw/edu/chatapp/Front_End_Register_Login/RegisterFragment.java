package group7.tcss450.uw.edu.chatapp.Front_End_Register_Login;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import group7.tcss450.uw.edu.chatapp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment implements View.OnClickListener {

    private OnRegisterFragmentInteractionListener mListener;
    Credentials mCredentils;

    public RegisterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_register, container, false);
        Button b = (Button) v.findViewById(R.id.registerButton);
        b.setOnClickListener(view -> onClick(b));
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof LoginFragment.OnFragmentInteractionListener) {
            mListener = (OnRegisterFragmentInteractionListener) context;
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

    //TODO Perform Validation!!
    private boolean performValidation() {
        return true;
    }

    @Override
    public void onClick(View v) {
        if (mListener != null) switch (v.getId()) {
            case R.id.registerButton:
                EditText username = (EditText)getView().findViewById(R.id.registerNickname);
                Log.d("In On Click", username.getText().toString());
                EditText password = (EditText)getView().findViewById(R.id.registerPW1);
                Credentials.Builder build = new Credentials.Builder(username.getText().toString(), password.getText());
//                EditText confirmPassword = (EditText)v.findViewById(R.id.confirmPass);
                EditText FName = (EditText)getView().findViewById(R.id.registerFName);
                EditText LNaem = (EditText)getView().findViewById(R.id.registerLName);
                EditText Email = (EditText)getView().findViewById(R.id.registerEmail);
                if(!FName.getText().toString().equals("")) {
                    build.addFirstName(FName.getText().toString());
                }
                if(!LNaem.getText().toString().equals("")) {
                    build.addLastName(LNaem.getText().toString());
                }
                if(!Email.getText().toString().equals("")) {
                    build.addEmail(Email.getText().toString());
                }
                mCredentils = build.build();
                mListener.onRegisterAttempt(mCredentils);
                break;
            default:
                Log.wtf("", "Didn't Expect to see me.");
        }

    }

    public interface OnRegisterFragmentInteractionListener {
        void onRegisterAttempt(Credentials creds);
    }
}

