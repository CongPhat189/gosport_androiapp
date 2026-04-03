package com.example.gosport.utils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class VNPayHelper {
    private static final String VNP_TMN_CODE    = "63NUWGNE";
    private static final String VNP_HASH_SECRET = "C3NBM8MWADUJ1HDMU1KZ67YICMAD9BSJ";
    private static final String VNP_URL         = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
    private static final String VNP_RETURN_URL  = "gosport://vnpay-return";

    public static String buildPaymentUrl(double amount, String txnRef, String orderInfo) {
        try {
            String cleanRef = txnRef.replaceAll("[^a-zA-Z0-9]", "");

            TreeMap<String, String> params = new TreeMap<>();
            params.put("vnp_Version",    "2.1.0");
            params.put("vnp_Command",    "pay");
            params.put("vnp_TmnCode",    VNP_TMN_CODE);
            params.put("vnp_Amount",     String.valueOf((long)(amount * 100)));
            params.put("vnp_CurrCode",   "VND");
            params.put("vnp_TxnRef",     cleanRef);
            params.put("vnp_OrderInfo",  orderInfo);
            params.put("vnp_OrderType",  "other");
            params.put("vnp_Locale",     "vn");
            params.put("vnp_ReturnUrl",  VNP_RETURN_URL);
            params.put("vnp_IpAddr",     "127.0.0.1");
            params.put("vnp_CreateDate", getCurrentTime());

            StringBuilder hashData = new StringBuilder();
            StringBuilder query    = new StringBuilder();
            boolean first = true;
            for (Map.Entry<String, String> e : params.entrySet()) {
                if (!first) { hashData.append('&'); query.append('&'); }
                first = false;
                String encoded = URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8.name());
                hashData.append(e.getKey()).append('=').append(encoded);
                query.append(e.getKey()).append('=').append(encoded);
            }

            Mac mac = Mac.getInstance("HmacSHA512");
            mac.init(new SecretKeySpec(
                    VNP_HASH_SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA512"));
            byte[] hash = mac.doFinal(hashData.toString().getBytes(StandardCharsets.UTF_8));
            String secureHash = bytesToHex(hash);

            return VNP_URL + "?" + query + "&vnp_SecureHash=" + secureHash;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        return sdf.format(new Date());
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) sb.append(String.format("%02x", b));
        return sb.toString();
    }
}
