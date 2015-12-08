/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bitrefill.retrofit;

import com.google.gson.Gson;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import retrofit.Call;
import static bitrefill.retrofit.APIv1.*;


/**
 *
 * @author wmacevoy
 */
public class APIv1Test {
    APIv1 api;

    public APIv1Test() throws FileNotFoundException {
        Config config = Config.fromJson(new FileReader(
                                System.getenv("HOME") + "/private/bitrefill/config.json"));
        api = Generator.generate(config);
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of lookup_number method, of class APIv1.
     */
    @Test
    public void testLookupNumber() throws IOException {
        System.out.println("lookup_number");
        String number = "380995353237";
        String operatorSlug = "mts-ukraine";
        Call<LookupNumberResponse> call = api.lookup_number(number);
        LookupNumberResponse response = call.execute().body();
        System.out.println("json: " + new Gson().toJson(response));
    }

    @Test
    public void testOrder() throws IOException {
        System.out.println("order");
        String json = "{"
                + "'operatorSlug': 'Operator',"
                + "'valuePackage' : '50',"
                + "'number' : 'Number to look up',"
                + "'email' : 'Customer email',"
                + "'sendEmail' : false,"
                + "'sendSMS' : false"
                + "}";
        OrderRequest request = new Gson().fromJson(json, OrderRequest.class);
        Call<OrderResponse> call = api.order(request);
        OrderResponse response = call.execute().body();
        System.out.println("json: " + new Gson().toJson(response));
    }

    @Test
    public void testInventory() throws IOException {
        System.out.println("inventory");
        Call<InventoryResponse> call = api.inventory();
        InventoryResponse response = call.execute().body();
        System.out.println("json: " + new Gson().toJson(response));
    }
}
