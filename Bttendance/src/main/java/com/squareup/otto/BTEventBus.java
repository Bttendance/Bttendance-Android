
package com.squareup.otto;

import android.os.Handler;

import com.utopia.bttendance.BTDebug;

public class BTEventBus extends Bus {

    private static final Handler mHandler = new Handler();
    private static final BTEventBus mBus = new BTEventBus(ThreadEnforcer.ANY);

    private BTEventBus(ThreadEnforcer enforcer) {
        super(enforcer);
    }

    public static final BTEventBus getInstance() {
        return mBus;
    }

    @Override
    protected void dispatch(Object arg0, EventHandler arg1) {
        try {
            super.dispatch(arg0, arg1);
        } catch (RuntimeException e) {
            BTDebug.LogError(e.getMessage());
        }
    }

    public void postAsync(final Object event) {
        // get main thread.
        // post it inside maing thread.
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                post(event);
            }
        });
    }

    public void postWorker(final Object event) {
        new Thread() {
            @Override
            public void run() {
                post(event);
            }
        }.start();
    }

}// end of class
