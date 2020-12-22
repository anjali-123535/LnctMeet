package com.example.lnctmeet.fragment;

import android.util.Log;

import com.example.lnctmeet.utils.Constants;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class WorkshopFragment extends  BaseFragment{
    public Query getQuery() {
        Log.d(getActivity().toString(),"WORKSHOP fragment");
        return FirebaseFirestore.getInstance().collection("posts")
                .whereEqualTo(Constants.TAG, Constants.String_Workshop)
                .orderBy(Constants.DATE_CREATION, Query.Direction.DESCENDING);
    }
}
