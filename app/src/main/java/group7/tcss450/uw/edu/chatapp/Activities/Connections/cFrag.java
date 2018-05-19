package group7.tcss450.uw.edu.chatapp.Activities.Connections;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import group7.tcss450.uw.edu.chatapp.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link cFrag.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link cFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class cFrag extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public cFrag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment cFrag.
     */
    // TODO: Rename and change types and number of parameters
    public static cFrag newInstance(String param1, String param2) {
        cFrag fragment = new cFrag();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View vt = inflater.inflate(R.layout.fragment_c, container, false);

        ImageButton b =  vt.findViewById(R.id.b);
        ImageButton b2 = vt.findViewById(R.id.b4);
        ImageButton b3 = vt.findViewById(R.id.b5);
        b.setOnClickListener(v -> getFragmentManager()
                .beginTransaction()
                .replace(R.id.FLAY, new CurrentConnections()).addToBackStack(null)
                .commit());

        b2.setOnClickListener(v -> getFragmentManager()
                .beginTransaction()
                .replace(R.id.FLAY, new OutgoingConnections()).addToBackStack(null)
                .commit());
        b3.setOnClickListener(v -> getFragmentManager()
                .beginTransaction()
                .replace(R.id.FLAY, new IncomingConnections()).addToBackStack(null)
                .commit());
        // Inflate the layout for this fragment
        return vt;
    }



}
