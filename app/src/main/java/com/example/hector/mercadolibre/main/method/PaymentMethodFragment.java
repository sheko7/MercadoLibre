package com.example.hector.mercadolibre.main.method;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.hector.mercadolibre.MainActivityListener;
import com.example.hector.mercadolibre.R;
import com.example.hector.mercadolibre.Utilities.Methods;
import com.example.hector.mercadolibre.models.PaymentMethod;
import com.example.hector.mercadolibre.presenter.PaymentMVP;
import com.example.hector.mercadolibre.presenter.PaymentMethodPresenter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PaymentMethodFragment extends Fragment implements OnPaymentMethodInteractionListener, PaymentMVP.PaymentMethodView{
    @BindView(R.id.payment_method_recyclerview) RecyclerView mRecyclerViewPaymentMethod;
    @BindView(R.id.no_data_textview) TextView mTextViewNoData;

    private PaymentMVP.PaymentMethodPresenter paymentPresenter;
    private MainActivityListener mListener;

    private FragmentActivity fragmentActivity;

    public static PaymentMethodFragment newInstance() {
        return new PaymentMethodFragment();
    }

    public PaymentMethodFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_payment_method, container, false);

        ButterKnife.bind(this, rootView);

        paymentPresenter = new PaymentMethodPresenter();
        if(Methods.isNetworkAvailable(fragmentActivity))
            paymentPresenter.getPaymentMethod(getContext());
        else{
            mListener.hideProgressLayout();
            mListener.goToNoNetwork();
        }

        return rootView;
    }

    private void setRecyclerView(List<PaymentMethod> paymentMethodList){
        if(fragmentActivity != null){
            if(paymentMethodList.size() == 0){
                mTextViewNoData.setVisibility(View.VISIBLE);
                return;
            }

            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(fragmentActivity);
            mRecyclerViewPaymentMethod.setLayoutManager(layoutManager);
            mRecyclerViewPaymentMethod.setAdapter(new PaymentMethodRecyclerViewAdapter(fragmentActivity, paymentMethodList, this));
            mRecyclerViewPaymentMethod.setHasFixedSize(true);

            DividerItemDecoration horizontalDecoration = new DividerItemDecoration(fragmentActivity, DividerItemDecoration.VERTICAL);
            Drawable horizontalDivider = ContextCompat.getDrawable(fragmentActivity, R.drawable.horizontal_divider);
            if (horizontalDivider != null) {
                horizontalDecoration.setDrawable(horizontalDivider);
                mRecyclerViewPaymentMethod.addItemDecoration(horizontalDecoration);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        paymentPresenter.setView(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivityListener) {
            mListener = (MainActivityListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement MainActivityListener");
        }

        if(context instanceof FragmentActivity){
            fragmentActivity = (FragmentActivity) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        fragmentActivity = null;
    }

    @Override
    public void onSuccesfullGetPaymentMethod(List<PaymentMethod> paymentMethodList) {
        setRecyclerView(paymentMethodList);
        if(mListener != null)
            mListener.hideProgressLayout();
    }

    @Override
    public void onFailureGetPaymentMethod() {
        if(mListener != null){
            mListener.toast(getString(R.string.no_data_from_request));
            mListener.hideProgressLayout();
        }
    }

    @Override
    public void itemSelected(PaymentMethod paymentMethod) {
        if(mListener != null)
            mListener.goToCardIssuers(paymentMethod);
    }
}
