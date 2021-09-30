package com.intermercato.iws_m.adapters;

/**
 * Created by ch on 2018-08-14.
 */

public interface BankOnClickCallback {
    void onBankClick(String bankId, int totalBankWeight, int bankIndex);
    void onActiveBankUpdated(int totalBankWeight);
}
