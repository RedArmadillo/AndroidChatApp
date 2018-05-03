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
import android.widget.TextView;

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

    @Override
    public void onClick(View v) {
        if (mListener != null) switch (v.getId()) {
            case R.id.registerButton:
                if (RegistrationValidation(getView())) {
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
                }
            default:
                Log.wtf("", "Didn't Expect to see me.");
        }

    }

    public static boolean RegistrationValidation(View view) {
        Log.d("tag", view.toString());
        EditText username = (EditText) view.findViewById(R.id.registerNickname);
        EditText email = (EditText) view.findViewById(R.id.registerEmail);
        EditText fname = (EditText) view.findViewById(R.id.registerFName);
        EditText lname = (EditText) view.findViewById(R.id.registerLName);
        boolean isValid = false;
        Log.d("Validation", "Hello");
        EditText pasReg = (EditText) view.findViewById(R.id.registerPW1);
        EditText confirmPas = (EditText) view.findViewById(R.id.registerPW2);

        boolean flaga = false;
        boolean flagb = false;
        boolean flagc = false;
        boolean flagd = false;
        boolean flage = false;
        boolean flagf = false;


        if (username.getText().toString().length() == 0) {
            username.setError("Username Can Not Be Empty!");
        } else {
            flaga = true;
        }
        if (pasReg.getText().toString().length() == 0) {
            pasReg.setError("Password Can Not Be Empty!");
        } else if(pasReg.getText().toString().length() < 6 && pasReg.getText().toString().length() > 0) {
            pasReg.setError("Password must be at least 6 characters.");
        }
        else {
            flagb = true;
        }
        if(!confirmPas.getText().toString().equals(pasReg.getText().toString())) {
            confirmPas.setError("Passwords must be the same.");
        } else {
            flagc = true;
        }
        if (email.getText().toString().length() == 0) {
            email.setError("Email Can Not Be Empty!");
        } else {
            flagd = true;
        }
        if (fname.getText().toString().length() == 0) {
            fname.setError("Firstname Can Not Be Empty!");
        } else {
            flage = true;
        }
        if (lname.getText().toString().length() == 0) {
            lname.setError("LName Can Not Be Empty!");
        } else {
            flagf = true;
        }
        if (flaga & flagb & flagc & flagd & flage & flagf) {
            isValid = true;
        }
        return isValid;
    }

    public interface OnRegisterFragmentInteractionListener {
        void onRegisterAttempt(Credentials creds);
    }
}

