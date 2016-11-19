package com.bodekjan.uyweather.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.bodekjan.uyweather.R;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.view.IconicsImageView;
import com.mikepenz.material_design_iconic_typeface_library.MaterialDesignIconic;


/**
 * Created by bodekjan on 2016/4/8.
 */
public class SelectZoneDialog extends DialogFragment {
    public static final String EXTRA_DATA = "sgwgqwg1mofsfsfgw12f";
    private int langType=0;
    private int mValue;
    private LinearLayout urumqiBtn;
    private LinearLayout beijingBtn;
    private IconicsImageView urumqiimg;
    private IconicsImageView beijingimg;
    private Button submit;
    private IconicsDrawable activeIcon;
    private IconicsDrawable passiveIcon;
    public static SelectZoneDialog newInstance(int x){
        Bundle args=new Bundle();
        args.putSerializable(EXTRA_DATA, x);
        SelectZoneDialog fragment=new SelectZoneDialog();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        Dialog dialog=new Dialog(getActivity(), R.style.DialogStyle);
        LayoutInflater inflater = (LayoutInflater) getActivity()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_zone, null, false);
        activeIcon=new IconicsDrawable(getActivity())
                .icon(MaterialDesignIconic.Icon.gmi_check_circle)
                .color(Color.GREEN)
                .sizeDp(24);
        passiveIcon=new IconicsDrawable(getActivity())
                .icon(MaterialDesignIconic.Icon.gmi_check_circle)
                .color(Color.GRAY)
                .sizeDp(24);
        urumqiBtn=(LinearLayout)view.findViewById(R.id.dialog_urumqi_btn);
        urumqiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                urumqiimg.setImageDrawable(activeIcon);
                beijingimg.setImageDrawable(passiveIcon);
                langType=0;
            }
        });
        urumqiimg=(IconicsImageView)view.findViewById(R.id.dialog_urumqi_img);
        urumqiimg.setImageDrawable(passiveIcon);
        beijingBtn=(LinearLayout)view.findViewById(R.id.dialog_beijing_btn);
        beijingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                urumqiimg.setImageDrawable(passiveIcon);
                beijingimg.setImageDrawable(activeIcon);
                langType=1;
            }
        });
        beijingimg=(IconicsImageView)view.findViewById(R.id.dialog_beijing_img);
        beijingimg.setImageDrawable(passiveIcon);
        langType=mValue = (int)getArguments().getSerializable(EXTRA_DATA);
        switch (mValue){
            case 0:
                urumqiimg.setImageDrawable(activeIcon);
                break;
            case 1:
                beijingimg.setImageDrawable(activeIcon);
                break;
        }
        submit=(Button)view.findViewById(R.id.dialog_zone_submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendResult(Activity.RESULT_OK);
            }
        });
        dialog.setContentView(view);
        return dialog;
    }
    private void sendResult(int resultCode){
        if(getTargetFragment()==null)
            return;
        Intent i=new Intent();
        i.putExtra(EXTRA_DATA,langType);
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, i);
        this.dismiss();
    }
}