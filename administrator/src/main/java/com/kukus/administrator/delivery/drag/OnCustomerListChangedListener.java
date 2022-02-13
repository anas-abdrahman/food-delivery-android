package com.kukus.administrator.delivery.drag;

import com.kukus.library.model.Ship;

import java.util.ArrayList;

public interface OnCustomerListChangedListener {
    void onNoteListChanged(ArrayList<Ship> ships);
}