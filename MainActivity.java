package webapi.nesic.com.socket_server_example_1;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {
    final Handler handler = new Handler();

    Server server;
    TextView infoip, msg;
    boolean end = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        infoip = (TextView) findViewById(R.id.infoip);
       msg = (TextView) findViewById(R.id.msg);
       server = new Server(this);

        infoip.setText(server.getIpAddress());

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        server.onDestroy();
    }

    private void startServerSocket() {
        final String[] stringData = {"   "};

        Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    Log.e("","before while");
                    ServerSocket ss = new ServerSocket(8080);


                    while (true) {
                        Log.e("","before accept");
                        //Server is waiting for client here, if needed
                        Socket s = ss.accept();
                        Log.e("","after accept");
                        BufferedReader input = new BufferedReader(new InputStreamReader(s.getInputStream()));
                        PrintWriter output = new PrintWriter(s.getOutputStream());
                        stringData[0] = input.readLine();
                        output.println("FROM SERVER - " + stringData[0].toUpperCase());
                        output.flush();

                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        updateUI(stringData[0]);
                        if (stringData[0].equalsIgnoreCase("STOP")) {
                            end = true;
                            output.close();
                            s.close();
                            break;
                        }

                        output.close();
                        s.close();
                    }
                    ss.close();
                } catch (IOException e) {
                    System.out.print("Error when running server."+e.toString());
                    e.printStackTrace();
                }

            }
        };
        new Thread(r).start();


    }

    private void updateUI(final String stringData) {

        handler.post(new Runnable() {
            @Override
            public void run() {

                String s ="";// msg.getText().toString();
                if (stringData.trim().length() != 0)
                    msg.setText(s + "\n" + "From Server : " + stringData);
            }
        });
    }
}
