package com.example.gosport.user;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gosport.R;

public class VNPayWebViewActivity extends AppCompatActivity {

    private WebView webView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vnpay_webview);

        webView     = findViewById(R.id.webViewVNPay);
        progressBar = findViewById(R.id.progressBar);

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> handleBack());

        String paymentUrl = getIntent().getStringExtra("PAYMENT_URL");

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return handleUrl(url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return handleUrl(request.getUrl().toString());
            }
        });

        if (paymentUrl != null) {
            webView.loadUrl(paymentUrl);
        }
    }

    private boolean handleUrl(String url) {
        if (url.startsWith("gosport://vnpay-return")) {
            Uri uri = Uri.parse(url);
            String responseCode = uri.getQueryParameter("vnp_ResponseCode");
            Intent result = new Intent();
            if ("00".equals(responseCode)) {
                result.putExtra("PAYMENT_SUCCESS", true);
                setResult(RESULT_OK, result);
            } else {
                result.putExtra("PAYMENT_SUCCESS", false);
                setResult(RESULT_CANCELED, result);
            }
            finish();
            return true;
        }
        return false;
    }

    private void handleBack() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            setResult(RESULT_CANCELED);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        handleBack();
    }
}
