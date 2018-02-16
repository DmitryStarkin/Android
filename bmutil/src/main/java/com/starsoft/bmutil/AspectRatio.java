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

package com.starsoft.bmutil;

/**
 * Created by StarkinDG on 13.03.2017.
 */

public final class AspectRatio {
    
    public static final int RATE_4_TO_5 = 1250;
    public static final int RATE_3_TO_4 = 1330;
    public static final int RATE_1_to_1_34 = 1340;
    public static final int RATE_1_TO_1_375 = 1375;
    public static final int RATE_2_TO_3 = 1500;
    public static final int RATE_9_TO_14 = 1560;
    public static final int RATE_10_TO_16 = 1600;
    public static final int RATE_1_TO_1_66 = 1660;
    public static final int RATE_1_TO_1_85 = 1850;
    public static final int RATE_1_TO_2 = 2000;
    public static final int RATE_1_TO_2_2 = 2200;
    public static final int RATE_1_TO_2_35 = 2350;
    public static final int RATE_1_TO_2_39 = 2390;
    public static final int RATE_1_TO_2_4 = 2400;
    public static final int RATE_1_TO_2_55 = 2550;
    public static final int RATE_1_TO_2_6 = 2600;
    public static final int RATE_4_TO_11 = 2750;
    public static final int RATE_9_TO_16 = 1780;
    public static final int[] RATIOS = {RATE_4_TO_5, RATE_3_TO_4, RATE_1_to_1_34, RATE_1_TO_1_375, RATE_2_TO_3,
            RATE_9_TO_14, RATE_10_TO_16, RATE_1_TO_1_66, RATE_1_TO_1_85, RATE_1_TO_2, RATE_1_TO_2_2, RATE_1_TO_2_35,
            RATE_1_TO_2_39, RATE_1_TO_2_4, RATE_1_TO_2_55, RATE_1_TO_2_6, RATE_4_TO_11, RATE_9_TO_16};
    
    private static final int CAST_RATIO_ACCURACY = 1000;
    private static final int RATIO_ACCURACY = 5;
    private static final int MAX_AJUSTED_COEFICIENT = 20;
    private static final int FINAL_RATIO_ACCURACY = 2;
    
    private double heightCoefficient;
    private double widthCoefficient;
    private double ratio;
    private double inverseRatio;
    
    public AspectRatio() {
        
        heightCoefficient = 1;
        widthCoefficient = 1;
        ratio = 1;
        inverseRatio = 1;
    }
    
    public AspectRatio(int wanCoefficient, int twoCoefficient) {
        
        if (wanCoefficient > twoCoefficient) {
            heightCoefficient = wanCoefficient;
            widthCoefficient = twoCoefficient;
        } else {
            heightCoefficient = twoCoefficient;
            widthCoefficient = wanCoefficient;
        }
        int divisor = GratesCommonDivisor((int) heightCoefficient, (int) widthCoefficient);
        heightCoefficient /= divisor;
        widthCoefficient /= divisor;
        ratio = floorRounding(widthCoefficient / heightCoefficient, FINAL_RATIO_ACCURACY);
        inverseRatio = rounding(heightCoefficient / widthCoefficient, FINAL_RATIO_ACCURACY);
    }
    
    public AspectRatio(double ratio) {
        
        setCoefficients(ratio);
    }
    
    public AspectRatio(int ratio) {
        
        if (isStandardRatio(ratio)) {
            
            setCoefficients((double) ratio / CAST_RATIO_ACCURACY);
        } else {
            throw new IllegalStateException("Illegal argument");
        }
    }
    
    private void setCoefficients(double ratio) {
        
        if (ratio > 1) {
            inverseRatio = ratio;
            ratio = 1 / ratio;
        } else {
            inverseRatio = 1 / ratio;
        }
        inverseRatio = rounding(inverseRatio, RATIO_ACCURACY);
        this.ratio = floorRounding(ratio, RATIO_ACCURACY);
        
        if (!CoefficientAdjust(inverseRatio, calculateAccuracy(inverseRatio, RATIO_ACCURACY))) {
            widthCoefficient = 1;
            heightCoefficient = rounding(inverseRatio, FINAL_RATIO_ACCURACY);
        }
        inverseRatio = rounding(inverseRatio, FINAL_RATIO_ACCURACY);
        this.ratio = floorRounding(ratio, FINAL_RATIO_ACCURACY);
    }
    
    private boolean CoefficientAdjust(double ratio, int accuracy) {
        
        double r;
        for (int i = 1; i <= MAX_AJUSTED_COEFICIENT; i++) {
            for (int y = i + 1; y <= MAX_AJUSTED_COEFICIENT; y++) {
                r = rounding((double) y / (double) i, accuracy);
                if (r == ratio) {
                    heightCoefficient = y;
                    widthCoefficient = i;
                    return true;
                }
            }
        }
        return false;
    }
    
    private int calculateAccuracy(double value, int maxAccurasy) {
        
        int accuracyValue = 1;
        for (int i = 0; i < maxAccurasy; i++) {
            accuracyValue *= 10;
        }
        int tmp = (int) (value * accuracyValue);
        while (tmp % 10 == 0) {
            tmp /= 10;
            maxAccurasy--;
        }
        return maxAccurasy;
    }
    
    public double getHeightCoefficient() {
        
        return heightCoefficient;
    }
    
    public double getWidthCoefficient() {
        
        return widthCoefficient;
    }
    
    public double getRatio() {
        
        return ratio;
    }
    
    private int GratesCommonDivisor(int biggestNumber, int smallestNumber) {
        
        int tmp;
        while (smallestNumber != 0) {
            
            tmp = biggestNumber % smallestNumber;
            biggestNumber = smallestNumber;
            smallestNumber = tmp;
        }
        return biggestNumber;
    }
    
    private boolean isStandardRatio(int ratio) {
        
        for (int i = 0, y = RATIOS.length; i < y; i++) {
            if (ratio == RATIOS[i]) {
                return true;
            }
        }
        return false;
    }
    
    private double rounding(double value, int acurasy) {
        
        int accuracyValue = 1;
        for (int i = 0; i < acurasy; i++) {
            accuracyValue *= 10;
        }
        value *= accuracyValue;
        return (double) Math.round(value) / accuracyValue;
    }
    
    private double floorRounding(double value, int acurasy) {
        
        int accuracyValue = 1;
        for (int i = 0; i < acurasy; i++) {
            accuracyValue *= 10;
        }
        value *= accuracyValue;
        return (double) Math.floor(value) / accuracyValue;
    }
    
    @Override
    public boolean equals(Object obj) {
        
        return ((obj instanceof AspectRatio) && ((AspectRatio) obj).heightCoefficient == this.heightCoefficient
                && ((AspectRatio) obj).widthCoefficient == this.widthCoefficient
                && ((AspectRatio) obj).ratio == this.ratio
                && ((AspectRatio) obj).inverseRatio == this.inverseRatio);
    }
    
    @Override
    public String toString() {
        
        return heightCoefficient + ":" + widthCoefficient + " (" + inverseRatio + "|" + ratio + ")";
    }
}
