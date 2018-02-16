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

package com.starsoft.dbtolls.executors;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.starsoft.dbtolls.main.DataBaseTolls;
import com.starsoft.dbtolls.runables.CursorLoader;
import com.starsoft.dbtolls.runables.DataWriter;
import com.starsoft.dbtolls.runables.DbWorker;

import static com.starsoft.dbtolls.setting.Constants.MESSAGE_CLOSE_DB;
import static com.starsoft.dbtolls.setting.Constants.MESSAGE_ERROR;
import static com.starsoft.dbtolls.setting.Constants.MESSAGE_CURSOR_RECEIVED;
import static com.starsoft.dbtolls.setting.Constants.MESSAGE_DATA_RECORDED;

/**
 * Created by StarkinDG on 12.04.2017.
 */

public class DBHandler extends Handler {
    
    public DBHandler() {
        
        super(Looper.getMainLooper());
    }
    
    @Override
    public void handleMessage(Message msg) {
        
        if (msg.what == MESSAGE_CURSOR_RECEIVED) {
            ((CursorLoader) msg.obj).onPostCursorLoad();
        } else if (msg.what == MESSAGE_DATA_RECORDED) {
            ((DataWriter) msg.obj).onPostWrite();
        } else if (msg.what == MESSAGE_CLOSE_DB) {
            ((DataBaseTolls) msg.obj).closeDb();
        } else if (msg.what == MESSAGE_ERROR){
            ((DbWorker)msg.obj).onError();
        }
    }
}
