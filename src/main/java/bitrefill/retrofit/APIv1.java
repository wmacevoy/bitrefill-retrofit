/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bitrefill.retrofit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 *
 * @author wmacevoy
 */
public interface APIv1 {

    public static final String URL = "https://api.bitrefill.com/v1/";
    public static final String MOCK_URL
            = "https://private-anon-383604724-bitrefill.apiary-mock.com/";

    static class LookupNumberResponse {

        public static class Country {

            public String alpha2;
            public String name;
            public String slug;
            public List<String> countryCallingCodes;
            public List<String> currencies;
        }
        public Country country;

        public static class Operator {

            public String name;
            public String slug;
            public String currency;
            public String logoImage;
            public boolean isRanged;
            public boolean isPinRanged;

            public static class Package {

                public String value;
                public double eurPrice;
                public int satoshiPrice;
            }
            public List<Package> packages;
        }
        public Operator operator;

        public static class AltOperator {

            public String name;
            public String slug;
            public String logoImage;
        }
        public List<AltOperator> altOperators;
    }

    @GET("lookup_number")
    Call<LookupNumberResponse> lookup_number(
            @Query("number") String number
    );

    @GET("lookup_number")
    Call<LookupNumberResponse> lookup_number(
            @Query("number") String number,
            @Query("operatorSlug") String operatorSlug
    );

    static class OrderRequest {

        public String operatorSlug;
        public String valuePackage;
        public String number;
        public String email; // used for receipts, and in the case with PIN top ups to send out the PIN code to the user.
        public boolean sendEmail = true; // Optional. If false, receipt email won't be sent. Default: true
        public boolean sendSMS = true; // Optional. If false, receipt SMS won't be sent. Default: true
        public String refund_btc_address; // Optional. If there is an error, we will send a refund to this address.
        public String webhook_url = null; // Optional. If provided, we will send a POST JSON webhook to provided URL after the order is either successfully performed or failed. Content of webhook will be the same as in order info below. 
    }

    static class OrderResponse {

        public String btcPrice;
        public String itemDesc;
        public long invoiceTime;
        public long expirationTime;
        public double eurPrice;
        public String orderId;

        public static class Payment {

            public String human;
            public String address;
            public int satoshiPrice;
            public String BIP21;
            public String BIP73;
        }
        public Payment payment;
    }

    @POST("order/")
    Call<OrderResponse> order(
            @Body OrderRequest request
    );

    static class OrderStatusReponse {

        public boolean paymentReceived;
        public boolean delivered;
        public int value;
        public String number;
    }

    @GET("order/")
    Call<OrderStatusReponse> order(
            @Query("order_id") String order_id
    );

    static class InventoryResponse
            extends HashMap< String, InventoryResponse.Country> {

        public static class Country {

            public String alpha2;
            public String name;
            public String slug;
            public List<String> countryCallingCodes;

            public static class Operator {

                public String name;
                public String slug;
                public String logoImage;
                public String countryCode;
            }
            public Map<String, Operator> operators;
        }
    }

    @GET("inventory/")
    Call<InventoryResponse> inventory();
}
