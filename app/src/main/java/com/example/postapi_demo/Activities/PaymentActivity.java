package com.example.postapi_demo.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.postapi_demo.R;
import com.razorpay.Checkout;
import com.razorpay.ExternalWalletListener;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class PaymentActivity extends Activity implements PaymentResultWithDataListener, ExternalWalletListener {
    private static final String TAG = PaymentActivity.class.getSimpleName();
    private AlertDialog.Builder alertDialogBuilder;
    TextView txt1,txt2;
    ImageView imageView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_payment);

        /*
         To ensure faster loading of the Checkout form,
          call this method as early as possible in your checkout flow.
         */
        Checkout.preload(getApplicationContext());

        // Payment button created by you in XML layout
        Button button = (Button) findViewById(R.id.btn_pay);

        txt1=findViewById(R.id.payment_Name);
        txt2=findViewById(R.id.payment_Price);
        imageView=findViewById(R.id.payment_imgView);

        txt1.setText(""+getIntent().getStringExtra("proName"));
        txt2.setText("INR. "+getIntent().getStringExtra("proPrice"));

        String img = "https://amiparaandroid.000webhostapp.com/Myapp/"+getIntent().getStringExtra("proImage");
        Picasso.get().invalidate(img);
        Picasso.get().load(img).networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE).placeholder(R.drawable.jmkjkfg).into(imageView);


        alertDialogBuilder = new AlertDialog.Builder(PaymentActivity.this);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setTitle("Payment Result");
        alertDialogBuilder.setPositiveButton("Ok", (dialog, which) -> {
            //do nothing
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPayment();
            }
        });

        TextView privacyPolicy = (TextView) findViewById(R.id.txt_privacy_policy);

        privacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent httpIntent = new Intent(Intent.ACTION_VIEW);
                httpIntent.setData(Uri.parse("https://razorpay.com/sample-application/"));
                startActivity(httpIntent);
            }
        });
    }

    public void startPayment() {
        /*
          You need to pass current activity in order to let Razorpay create CheckoutActivity
         */
        final Activity activity = this;

        final Checkout co = new Checkout();

        EditText etApiKey = findViewById(R.id.et_api_key);

        etApiKey.setText("rzp_test_03yLdq1u2a6UR8");
        if (!TextUtils.isEmpty(etApiKey.getText().toString())){
            co.setKeyID(etApiKey.getText().toString());
        }
        EditText etCustomOptions = findViewById(R.id.et_custom_options);
        if (!TextUtils.isEmpty(etCustomOptions.getText().toString())){
            try{
                JSONObject options = new JSONObject(etCustomOptions.getText().toString());
                co.open(activity, options);
            }catch (JSONException e){
                Toast.makeText(activity, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT)
                        .show();
                e.printStackTrace();
            }
        }else{
            try {
                JSONObject options = new JSONObject();
                options.put("name", "Razorpay Corp");
                options.put("description", "Demoing Charges");
                options.put("send_sms_hash",true);
                options.put("allow_rotation", true);
                //You can omit the image option to fetch the image from dashboard
                options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
                options.put("currency", "INR");
                options.put("amount", "10000");

                JSONObject preFill = new JSONObject();
                preFill.put("email", "test@razorpay.com");
                preFill.put("contact", "9510277767");

                options.put("prefill", preFill);

                co.open(activity, options);
            } catch (Exception e) {
                Toast.makeText(activity, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT)
                        .show();
                e.printStackTrace();
            }
        }


    }

    /**
     * The name of the function has to be
     * onPaymentSuccess
     * Wrap your code in try catch, as shown, to ensure that this method runs correctly
     */


    @Override
    public void onExternalWalletSelected(String s, PaymentData paymentData) {
        try{
            alertDialogBuilder.setMessage("External Wallet Selected:\nPayment Data: "+paymentData.getData());
            alertDialogBuilder.show();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void onPaymentSuccess(String s, PaymentData paymentData) {
        try{
            alertDialogBuilder.setMessage("Payment Successful :\nPayment ID: "+s+"\nPayment Data: "+paymentData.getData());
            alertDialogBuilder.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onPaymentError(int i, String s, PaymentData paymentData) {
        try{
            alertDialogBuilder.setMessage("Payment Failed:\nPayment Data: "+paymentData.getData());
            alertDialogBuilder.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}