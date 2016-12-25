package com.bodekjan.uyweather.dialog;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bodekjan.uyweather.R;
import com.bodekjan.uyweather.activities.MainActivity;
import com.bodekjan.uyweather.util.CommonHelper;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import java.io.File;

/**
 * Created by bodekjan on 2016/12/18.
 */
public class ShareDialog extends Dialog {
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 2;
    public ShareDialog(Context context) {
        super(context);
    }

    public ShareDialog(Context context, int theme) {
        super(context, theme);
    }

    public static class Builder {
        private Context context;
        private String title;
        private String message;
        private String negativeButtonText;
        private View contentView;
        private DialogInterface.OnClickListener negativeButtonClickListener;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        /**
         * Set the Dialog message from resource
         *
         * @return
         */
        public Builder setMessage(int message) {
            this.message = (String) context.getText(message);
            return this;
        }

        /**
         * Set the Dialog title from resource
         *
         * @param title
         * @return
         */
        public Builder setTitle(int title) {
            this.title = (String) context.getText(title);
            return this;
        }

        /**
         * Set the Dialog title from String
         *
         * @param title
         * @return
         */

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setContentView(View v) {
            this.contentView = v;
            return this;
        }


        public Builder setNegativeButton(int negativeButtonText,
                                         DialogInterface.OnClickListener listener) {
            this.negativeButtonText = (String) context
                    .getText(negativeButtonText);
            this.negativeButtonClickListener = listener;
            return this;
        }

        public Builder setNegativeButton(String negativeButtonText,
                                         DialogInterface.OnClickListener listener) {
            this.negativeButtonText = negativeButtonText;
            this.negativeButtonClickListener = listener;
            return this;
        }
        private Bitmap getDiskBitmap(String pathString)
        {
            Bitmap bitmap = null;
            try
            {
                File file = new File(pathString);
                if(file.exists())
                {
                    bitmap = BitmapFactory.decodeFile(pathString);
                }
            } catch (Exception e)
            {
                e.printStackTrace();
                // TODO: handle exception
            }
            return bitmap;
        }
        private UMShareListener umShareListener = new UMShareListener() {
            @Override
            public void onResult(SHARE_MEDIA platform) {
                LayoutInflater inflater = ((MainActivity)context).getLayoutInflater();
                View layout = inflater.inflate(R.layout.custom_toast, (ViewGroup) ((MainActivity)context).findViewById(R.id.exittoast));
                TextView text = (TextView) layout.findViewById(R.id.tvTextToast);
                text.setText(context.getResources().getText(R.string.snack_sharesuccess).toString());
                Toast toast = new Toast(context);
                toast.setGravity(Gravity.CENTER | Gravity.BOTTOM, 0,180);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.setView(layout);
                toast.show();
            }

            @Override
            public void onError(SHARE_MEDIA platform, Throwable t) {
                LayoutInflater inflater = ((MainActivity)context).getLayoutInflater();
                View layout = inflater.inflate(R.layout.custom_toast, (ViewGroup) ((MainActivity)context).findViewById(R.id.exittoast));
                TextView text = (TextView) layout.findViewById(R.id.tvTextToast);
                text.setText(context.getResources().getText(R.string.snack_shareerror).toString());
                Toast toast = new Toast(context);
                toast.setGravity(Gravity.CENTER | Gravity.BOTTOM, 0,180);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.setView(layout);
                toast.show();
                if(t!=null){
                    Log.e("wechat","wechat : "+t.getMessage());
                }
            }

            @Override
            public void onCancel(SHARE_MEDIA platform) {
                LayoutInflater inflater = ((MainActivity)context).getLayoutInflater();
                View layout = inflater.inflate(R.layout.custom_toast, (ViewGroup) ((MainActivity)context).findViewById(R.id.exittoast));
                TextView text = (TextView) layout.findViewById(R.id.tvTextToast);
                text.setText(context.getResources().getText(R.string.snack_shareerror).toString());
                Toast toast = new Toast(context);
                toast.setGravity(Gravity.CENTER | Gravity.BOTTOM, 0,180);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.setView(layout);
                toast.show();
            }
        };
        public ShareDialog create() {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // instantiate the dialog with the custom Theme
            final ShareDialog dialog = new ShareDialog(context, R.style.Dialog);
            View layout = inflater.inflate(R.layout.dialog_share, null);
            dialog.addContentView(layout, new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            // set the dialog title
            ((TextView) layout.findViewById(R.id.title)).setText(title);
            // set the cancel button
            if (negativeButtonText != null) {
                ((Button) layout.findViewById(R.id.negativeButton))
                        .setText(negativeButtonText);
                if (negativeButtonClickListener != null) {
                    ((Button) layout.findViewById(R.id.negativeButton))
                            .setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    negativeButtonClickListener.onClick(dialog,
                                            DialogInterface.BUTTON_NEGATIVE);
                                }
                            });
                }
            } else {
                layout.findViewById(R.id.negativeButton).setVisibility(
                        View.GONE);
            }
            // set the content message
            if (message != null) {
                //((TextView) layout.findViewById(R.id.message)).setText(message);
            } else if (contentView != null) {
                // if no message set
                // add the contentView to the dialog body
                ((LinearLayout) layout.findViewById(R.id.content))
                        .removeAllViews();
                ((LinearLayout) layout.findViewById(R.id.content))
                        .addView(contentView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
            }
            LinearLayout forMoment=(LinearLayout)layout.findViewById(R.id.formoment);
            forMoment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ((ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) )
                    {
                        ActivityCompat.requestPermissions(((MainActivity)context),new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_CALL_PHONE);
                        // 这里加一下权限
                    }else{
                        Bitmap bitmap = getDiskBitmap(CommonHelper.screenShotPic);
                        UMImage img = new UMImage(context, bitmap);
                        new ShareAction(((MainActivity)context)).setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE)
                                .withText( "Yurtum，[url=http://www.baidu.com]http://www.baidu.com[/url]" )
                                .withMedia( img )
                                .setCallback(umShareListener)
                                .share();
                    }
                }
            });
            LinearLayout forFriend=(LinearLayout)layout.findViewById(R.id.forfriend);
            forFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ((ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) )
                    {
                        ActivityCompat.requestPermissions(((MainActivity)context),new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_CALL_PHONE);
                        // 这里加一下权限
                    }else{
                        Bitmap bitmap = getDiskBitmap(CommonHelper.screenShotPic);
                        UMImage img = new UMImage(context, bitmap);
                        new ShareAction(((MainActivity)context)).setPlatform(SHARE_MEDIA.WEIXIN)
                                .withText( "Yurtum，[url=http://www.baidu.com]http://www.baidu.com[/url]" )
                                .withMedia( img )
                                .setCallback(umShareListener)
                                .share();
                    }
                }
            });
            dialog.setContentView(layout);
            return dialog;
        }
    }
}