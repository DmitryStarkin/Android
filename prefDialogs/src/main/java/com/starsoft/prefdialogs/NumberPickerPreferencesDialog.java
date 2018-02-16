/*
 * Copyright © 2018. Dmitry Starkin Contacts: t0506803080@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the «License»);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * //www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an «AS IS» BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.starsoft.prefdialogs;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

/**
 * Created by StarkinDG on 02.03.2017.
 */

public class NumberPickerPreferencesDialog extends DialogPreference {

    private final int DEFAULT_MIN_VALUE = 1;
    private final int DEFAULT_MAX_VALUE = 1500;
    private final int DEFAULT_VALUE = 1;
    private int currentValue;
    private int minValue;
    private int maxValue;
    private NumberPicker intPicker;


    public NumberPickerPreferencesDialog(Context context, AttributeSet attributeSet) {

        super(context, attributeSet);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attributeSet,
                R.styleable.NumberPickerPreferenceDialogAttrs,
                0, 0);

        try {
            maxValue = a.getInteger(R.styleable.NumberPickerPreferenceDialogAttrs_maxValue, DEFAULT_MAX_VALUE);
            minValue = a.getInteger(R.styleable.NumberPickerPreferenceDialogAttrs_minValue, DEFAULT_MIN_VALUE);
        } finally {
            a.recycle();
        }

        setDialogLayoutResource(R.layout.int_picker_preferences_dialog);
        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(android.R.string.cancel);
        setDialogIcon(null);
    }

    @Override()
    protected void onDialogClosed(boolean positiveResult) {

        if (positiveResult) {
            persistInt(intPicker.getValue());
        }
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {

        if (restorePersistedValue) {
            currentValue = this.getPersistedInt(DEFAULT_VALUE);
        } else {
            currentValue = (Integer) defaultValue;
            persistInt(currentValue);
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {

        return a.getInteger(index, DEFAULT_VALUE);
    }

    @Override
    protected View onCreateDialogView() {

        LayoutInflater inflater =
                (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.int_picker_preferences_dialog, null);

        intPicker = (NumberPicker) view.findViewById(R.id.int_number_picker);
        intPicker.setMaxValue(maxValue);
        intPicker.setMinValue(minValue);
        intPicker.setValue(this.getPersistedInt(DEFAULT_VALUE));
        intPicker.setWrapSelectorWheel(false);

        return view;
    }

    private int getMinValue() {

        return minValue;

    }

    private int getMaxValue() {

        return maxValue;

    }

    public void setMinValue(int minValue) {

        this.minValue = minValue;
    }

    public void setMaxValue(int maxValue) {

        this.maxValue = maxValue;
    }


    //  This code copied from android's settings guide.

    private static class SavedState extends BaseSavedState {

        // Member that holds the setting's value
        // Change this data type to match the type saved by your Preference
        int value;

        public SavedState(Parcelable superState) {

            super(superState);
        }

        public SavedState(Parcel source) {

            super(source);
            // Get the current preference's value
            value = source.readInt();  // Change this to read the appropriate data type
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {

            super.writeToParcel(dest, flags);
            // Write the preference's value
            dest.writeInt(value);  // Change this to write the appropriate data type
        }

        // Standard creator object using an instance of this class
        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {

                    public SavedState createFromParcel(Parcel in) {

                        return new SavedState(in);
                    }

                    public SavedState[] newArray(int size) {

                        return new SavedState[size];
                    }
                };
    }

    @Override
    protected Parcelable onSaveInstanceState() {

        final Parcelable superState = super.onSaveInstanceState();
        // Check whether this Preference is persistent (continually saved)
        if (isPersistent()) {
            // No need to save instance state since it's persistent, use superclass state
            return superState;
        }

        // Create instance of custom BaseSavedState
        final SavedState myState = new SavedState(superState);
        // Set the state's value with the class member that holds current setting value
        myState.value = currentValue;
        return myState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        // Check whether we saved the state in onSaveInstanceState
        if (state == null || !state.getClass().equals(SavedState.class)) {
            // Didn't save the state, so call superclass
            super.onRestoreInstanceState(state);
            return;
        }

        // Cast state to custom BaseSavedState and pass to superclass
        SavedState myState = (SavedState) state;
        super.onRestoreInstanceState(myState.getSuperState());

        // Set this Preference's widget to reflect the restored state
        intPicker.setValue(myState.value);
    }
}
