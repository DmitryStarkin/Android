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

package com.starsoft.dbtolls.runables;

import android.os.Message;

import com.starsoft.dbtolls.main.DataBaseTolls;

/**
 * Created by StarkinDG on 19.04.2017.
 */

public abstract class DbWorker implements Runnable {
    
    int mTag;
    Throwable mThrowable;
    long mSleepInterval;
    
    public void onError() {
        
        try {
            DataBaseTolls.getInstance().onError(mTag, mThrowable);
        } finally {
            clearReference();
        }
    }
    
    void sendHandlerMessage(int hMessage) {
        
        Message message = DataBaseTolls.getInstance().getDBHandler().obtainMessage(hMessage, this);
        message.sendToTarget();
    }
    
    @Override
    public boolean equals(Object obj) {
        
        return (obj instanceof DbWorker) && (((DbWorker) obj).mTag == this.mTag);
    }
    
    abstract void clearReference();
}
