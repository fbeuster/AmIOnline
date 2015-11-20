package de.beusterse.amionline;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.widget.TextView;

import java.io.IOException;
import java.net.Socket;

public class CheckOnlineTask extends  AsyncTask<Object, Boolean, Void> {

    private Context context;
    private TextView textView;
    private int interval = 5000;

    public CheckOnlineTask(Context context, TextView textView) {
        this.context = context;
        this.textView = textView;
    }

    @Override
    protected Void doInBackground(Object... hosts) {
        boolean reachable = false;

        while (true) {
            try {
                if (isDeviceConnected()) {
                    reachable = isUrlReachable((String) hosts[0]);
                }
            } catch (Exception e) {
            }

            System.out.println(reachable);
            publishProgress(reachable);
            sleep();

            reachable = false;
        }
    }

    @Override
    protected void onProgressUpdate(Boolean... progress) {
        if (progress[0]) {
            textView.setText(context.getString(R.string.online_status_online));
        } else {
            textView.setText(context.getString(R.string.online_status_offline));
        }
    }

    private boolean isDeviceConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connMgr.getActiveNetworkInfo();

        return netInfo != null && netInfo.isConnected();
    }

    private void sleep() {
        try {
            Thread.sleep(interval);
        } catch(InterruptedException e) {}
    }

    private boolean isUrlReachable(String url) {
        boolean reachable = false;
        Socket socket = null;

        try {
            socket = new Socket(url, 80);
            reachable = true;
        } catch (IOException e) {
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
        }
        return reachable;
    }
}
