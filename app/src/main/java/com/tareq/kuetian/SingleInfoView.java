package com.tareq.kuetian;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class SingleInfoView extends LinearLayout {
    private TextView infoOf, infoValue;
    private ImageView editInfo;
    private String infoOfStr, infoValueStr;
    private boolean showEditIcon;

    public SingleInfoView(Context context) {
        super(context);
        initializeViews(context);
    }

    public SingleInfoView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray;

        typedArray = context.obtainStyledAttributes(attrs, R.styleable.single_info_view, 0, 0);
        infoOfStr = typedArray.getString(R.styleable.single_info_view_infoOf);
        infoValueStr = typedArray.getString(R.styleable.single_info_view_infoValue);
        showEditIcon = typedArray.getBoolean(R.styleable.single_info_view_showEditIcon, true);

        typedArray.recycle();

        initializeViews(context);
    }

    public SingleInfoView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray;

        typedArray = context.obtainStyledAttributes(attrs, R.styleable.single_info_view, 0, 0);
        infoOfStr = typedArray.getString(R.styleable.single_info_view_infoOf);
        infoValueStr = typedArray.getString(R.styleable.single_info_view_infoValue);

        initializeViews(context);
    }

    public void ReadOnly() {
        editInfo.setVisibility(GONE);
    }


    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.single_info_view, this);
    }



    public void setValueClick(OnClickListener clk){
        infoValue.setOnClickListener(clk);
    }

    public String getText() {
        String str = infoValue.getText().toString();
        if (str.equals("not set"))
            str = "";
        return str;
    }

    public void setText(String str) {
        if(str!=null) {
            infoValue.setText(str);
            emptyValue(false);
        }
        else
            emptyValue();
    }

    @Override
    public void setOnClickListener(OnClickListener onClickListener) {
        editInfo.setOnClickListener(onClickListener);

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        editInfo = this.findViewById(R.id.editInfo);
        infoOf = this.findViewById(R.id.infoOf);
        infoValue = this.findViewById(R.id.infoValue);

        if (infoValue != null)
            infoOf.setText(infoOfStr);
        if (infoValueStr != null)
            infoValue.setText(infoValueStr);
        else
            emptyValue();

        if (!showEditIcon)
            editInfo.setVisibility(GONE);
    }

    private void emptyValue(boolean b) {
        if (b) {
            infoValue.setText("not set");
            infoValue.setTextColor(Color.GRAY);
        } else {
            infoValue.setTextColor(Color.BLACK);
        }

    }

    private void emptyValue() {
        emptyValue(true);
    }
}
