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
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bodekjan.uyweather.R;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.view.IconicsImageView;
import com.mikepenz.material_design_iconic_typeface_library.MaterialDesignIconic;


/**
 * Created by bodekjan on 2016/4/8.
 */
public class SelectLanguageDialog extends DialogFragment {
    public static final String EXTRA_DATA = "sgwgqwg1fgw12f";
    private int langType=0;
    private int mValue;
    private LinearLayout chineseBtn;
    private LinearLayout uyghurBtn;
    private IconicsImageView chineseimg;
    private IconicsImageView uyghurimg;
    private Button submit;
    private IconicsDrawable activeIcon;
    private IconicsDrawable passiveIcon;
    public static SelectLanguageDialog newInstance(int x){
        Bundle args=new Bundle();
        args.putSerializable(EXTRA_DATA, x);
        SelectLanguageDialog fragment=new SelectLanguageDialog();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        Dialog dialog=new Dialog(getActivity(), R.style.DialogStyle);
        LayoutInflater inflater = (LayoutInflater) getActivity()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_lang, null, false);
        activeIcon=new IconicsDrawable(getActivity())
                .icon(MaterialDesignIconic.Icon.gmi_check_circle)
                .color(Color.GREEN)
                .sizeDp(24);
        passiveIcon=new IconicsDrawable(getActivity())
                .icon(MaterialDesignIconic.Icon.gmi_check_circle)
                .color(Color.GRAY)
                .sizeDp(24);
        chineseBtn=(LinearLayout)view.findViewById(R.id.dialog_chinese_btn);
        chineseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uyghurimg.setImageDrawable(passiveIcon);
                chineseimg.setImageDrawable(activeIcon);
                langType=1;
            }
        });
        chineseimg=(IconicsImageView)view.findViewById(R.id.dialog_chinese_img);
        chineseimg.setImageDrawable(passiveIcon);
        uyghurBtn=(LinearLayout)view.findViewById(R.id.dialog_uyghur_btn);
        uyghurBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uyghurimg.setImageDrawable(activeIcon);
                chineseimg.setImageDrawable(passiveIcon);
                langType=0;
            }
        });
        uyghurimg=(IconicsImageView)view.findViewById(R.id.dialog_uyghur_img);
        uyghurimg.setImageDrawable(passiveIcon);
        langType=mValue = (int)getArguments().getSerializable(EXTRA_DATA);
        switch (mValue){
            case 0:
                uyghurimg.setImageDrawable(activeIcon);
                break;
            case 1:
                chineseimg.setImageDrawable(activeIcon);
                break;
        }
        submit=(Button)view.findViewById(R.id.dialog_lang_submit);
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