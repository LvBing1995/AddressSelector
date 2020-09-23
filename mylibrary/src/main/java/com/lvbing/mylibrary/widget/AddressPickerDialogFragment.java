package com.lvbing.mylibrary.widget;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.lvbing.mylibrary.R;
import com.lvbing.mylibrary.beannew.CommonAddressBean;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.FragmentManager;

/**
 * author: lvbingisdad
 * date: On 2020/9/23
 */


public class AddressPickerDialogFragment extends AppCompatDialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        Dialog dialog = new Dialog(getActivity(), R.style.BottomDialog);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(R.layout.dialog_fragment_address_picker);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.dialogSlideAnim);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        ImageView close = dialog.findViewById(R.id.address_closeId);
        AddressPickerView addressPickerView = dialog.findViewById(R.id.address_pickerviewId);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        addressPickerView.setOnAddressPickerSure(new AddressPickerView.OnAddressPickerSureListener() {
            @Override
            public void onSureClick(CommonAddressBean province, CommonAddressBean city, CommonAddressBean country, CommonAddressBean street) {
                if (mAddressListener != null) mAddressListener.onSureClick(province,city,country,street);
            }
        });
        addressPickerView.getmTvSure().setVisibility(View.GONE);
        return dialog;

    }
    private OnAddressListener mAddressListener;
    /**
     * 点确定回调这个接口
     */
    public interface OnAddressListener {
        void onSureClick(CommonAddressBean province, CommonAddressBean city, CommonAddressBean country, CommonAddressBean street);
    }

    public void setOnAddressSure(OnAddressListener listener) {
        this.mAddressListener = listener;
    }
    @Override
    public void show(@NonNull FragmentManager manager, @Nullable String tag) {
        if (!isAdded()) super.show(manager, tag);
    }


}
