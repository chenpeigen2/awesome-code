package com.miui.lifecycle.lifecycle;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Random;

public class DiceRollViewModel extends ViewModel {

    private final MutableLiveData<DiceUiState> uiState =
            new MutableLiveData(new DiceUiState(null, null, 0));

    public LiveData<DiceUiState> getUiState() {
        return uiState;
    }

    public void rollDice() {
        Random random = new Random();
        uiState.setValue(
                new DiceUiState(
                        random.nextInt(7) + 1,
                        random.nextInt(7) + 1,
                        uiState.getValue().getNumberOfRolls() + 1
                )
        );
    }
}
