package com.example.Fangyan.Fragment;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.Fangyan.Activity.FavoriteActivity;
import com.example.Fangyan.Activity.HistoryActivity;
import com.example.Fangyan.Activity.MainActivity;
import com.example.Fangyan.R;
import com.example.Fangyan.helper.GetNewsHelper;

public class Fragment_Info extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public Fragment_Info() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return a new instance of fragment Fragment_Info.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_Info newInstance(String param1, String param2) {
        Fragment_Info fragment = new Fragment_Info();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fragment__info, container, false);


        TextView favBtn = view.findViewById(R.id.textView3);
        TextView hisBtn = view.findViewById(R.id.textView5);

        favBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), FavoriteActivity.class));

                Animatoo.animateSlideLeft(getContext());
            }
        });


        hisBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), HistoryActivity.class));
                Animatoo.animateSlideLeft(getContext());
            }
        });


        Switch swich = view.findViewById(R.id.switch1);
        swich.setChecked(false);

        swich.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                ((MainActivity) getActivity()).setOnline(!b);


            }
        });

        Switch swich2 = view.findViewById(R.id.switch2);
        swich2.setChecked(false);

        swich2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                setLight(getActivity(), b ? 5: 100);

            }
        });

        TextView qk = view.findViewById(R.id.textView10);
        qk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new GetNewsHelper(view.getContext()).clear();
            }
        });


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

    }


    private void setLight(Activity context, int brightness) {
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.screenBrightness = Float.valueOf(brightness) * (1f / 255f);
        context.getWindow().setAttributes(lp);

    }
}
