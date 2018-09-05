package ibriz.icon.idoltoken.api

import foundation.icon.icx.Call
import foundation.icon.icx.IconService
import foundation.icon.icx.KeyWallet
import foundation.icon.icx.SignedTransaction
import foundation.icon.icx.Transaction
import foundation.icon.icx.TransactionBuilder
import foundation.icon.icx.data.Address
import foundation.icon.icx.data.Bytes
import foundation.icon.icx.transport.http.HttpProvider
import foundation.icon.icx.transport.jsonrpc.RpcItem
import foundation.icon.icx.transport.jsonrpc.RpcObject
import foundation.icon.icx.transport.jsonrpc.RpcValue
import ibiz.icon.idoltoken.api.IconConfiguration
import ibiz.icon.idoltoken.api.Idol
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.grails.web.json.JSONArray
import org.grails.web.json.JSONObject

class IconmainService {

    static def priceList = [10.1, 6, 7.5, 12.3, 9.12, 8.4, 8.2, 14.2, 6.9, 11.3, 4]
    static def tokenidPriceMap = [:]


    def iconService;

    def load() {
        final String URL = "http://localhost:9000/api/v3";

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();
        iconService = new IconService(new HttpProvider(httpClient, URL as String));
    }

    def transfer(KeyWallet wallet, scoreAddress, fromAddress, toAddress, value) {
        long timestamp = System.currentTimeMillis() * 1000L;
        RpcObject params = new RpcObject.Builder()
                .put("_to", new RpcValue(toAddress as String))
                .put("_value", new RpcValue(value as String))
                .build();

        Transaction transaction = TransactionBuilder.of(IconConfiguration.NETWORK_ID)
                .from(fromAddress)
                .to(new Address(scoreAddress))
                .stepLimit(IconConfiguration.STEP_LIMIT)
                .timestamp(new BigInteger(Long.toString(timestamp)))
                .nonce(IconConfiguration.NOUNCE)
                .call("transfer")
                .params(params)
                .build();

        SignedTransaction signedTransaction = new SignedTransaction(transaction, wallet);
        Bytes hash = iconService.sendTransaction(signedTransaction).execute();
        hash
    }

    def getTokenBalance(String address, scoreAddressStr) {
        Address currentAddress = new Address(address)
        Address scoreAddress = new Address(scoreAddressStr);

        RpcObject params = new RpcObject.Builder()
                .put("_owner", new RpcValue(currentAddress))
                .build();
        Call<RpcItem> call1 = new Call.Builder()
                .from(currentAddress)
                .to(scoreAddress)
                .method("balanceOf")
                .params(params)
                .build();
        RpcItem result = iconService.call(call1).execute();
        result.asInteger()
    }

    def getTokenBalance(KeyWallet currentWallet, scoreAddressStr) {
        Address currentAddress = currentWallet.getAddress();
        Address scoreAddress = new Address(scoreAddressStr);

        RpcObject params = new RpcObject.Builder()
                .put("_owner", new RpcValue(currentAddress))
                .build();
        Call<RpcItem> call1 = new Call.Builder()
                .from(currentAddress)
                .to(scoreAddress)
                .method("balanceOf")
                .params(params)
                .build();
        RpcItem result = iconService.call(call1).execute();
        result.asInteger()
    }

    def balanceOfICX(address) {
        BigInteger balance = iconService.getBalance(new Address(address)).execute();
        balance
    }

    def getAllTokensOf(String address, String scoreAddressStr) {
        Address currentAddress = new Address(address)
        Address scoreAddress = new Address(scoreAddressStr);
        RpcObject params = new RpcObject.Builder()
                .put("_owner", new RpcValue(currentAddress))
                .build();

        Call<RpcItem> call = new Call.Builder()
                .from(currentAddress)
                .to(scoreAddress)
                .method("get_tokens_of_owner")
                .params(params)
                .build();

        RpcItem result = iconService.call(call).execute();
        System.out.println(currentAddress.toString() + " :result:" + result.asString());

        def tokenList = []
        JSONObject obj = new JSONObject(result.asString());
        JSONArray idolTokensOfOwner = obj.getJSONArray("idols");
        int index=0;
        for (Object idolToken : idolTokensOfOwner) {
            tokenList.add(getTokenInfo(address, scoreAddressStr, idolToken.toString(),index).put("tokenId", idolToken.toString()))
            index= index+1;
        }
        tokenList
    }

    def getAllTokensOf(KeyWallet currentWallet, String scoreAddressStr) throws IOException {
        getAllTokensOf(currentWallet.getAddress().toString(), scoreAddressStr)
    }

    def getTokenInfo(String address, String scoreAddressStr, String tokenId, int index =0) throws IOException {
        Address firstAddress = new Address(address)

        RpcObject params = new RpcObject.Builder()
                .put("_tokenId", new RpcValue(tokenId))
                .build();

        Call<RpcItem> call = new Call.Builder()
                .from(firstAddress)
                .to(new Address(scoreAddressStr))
                .method("get_idol")
                .params(params)
                .build();

        RpcItem result = iconService.call(call).execute();

        def jsonObj = new JSONObject(result.asString())
        def price = tokenidPriceMap.containsKey(tokenId)? tokenidPriceMap.get(tokenId) : priceList.get(index)
        if(!tokenidPriceMap.containsKey(tokenId)) {
            tokenidPriceMap.put(tokenId, priceList.get(index))
        }

//        System.out.println("tokenidPriceMap.get(tokenId) for id :" + tokenId + "  - " + tokenidPriceMap.get(tokenId) + " price :" + price);

        jsonObj = jsonObj.accumulate("price", price)
        if(jsonObj.get("age") != null) {
            if(jsonObj.get("age").toString().startsWith('0x')) {
                jsonObj.put("age", Integer.parseInt(jsonObj.get("age").toString().substring(2), 16))
            }
            else{
                jsonObj.put("age", Integer.parseInt(jsonObj.get("age").toString()))
            }
        }

        System.out.println("index: " + index + " - " + firstAddress.toString() + " :result:" + jsonObj.toString());
        jsonObj;
    }

    def getTokenInfo(KeyWallet currentWallet, String scoreAddressStr, String tokenId) throws IOException {

        getTokenInfo(currentWallet.getAddress().toString(), scoreAddressStr, tokenId)
    }

    def approveTransaction(KeyWallet currentWallet, String scoreAddressStr, String toAddress, String tokenId) throws IOException {
        BigInteger networkId = new BigInteger("3");

        long timestamp = System.currentTimeMillis() * 1000L;
        String toAddStr = toAddress; // second address where the amount is being transfered
        RpcObject params = new RpcObject.Builder()
                .put("_to", new RpcValue(toAddStr))
                .put("_tokenId", new RpcValue(tokenId))
                .build();

        Transaction transaction = TransactionBuilder.of(networkId)
                .from(currentWallet.getAddress())
                .to(new Address(scoreAddressStr))
                .stepLimit(new BigInteger("10000"))
                .timestamp(new BigInteger(Long.toString(timestamp)))
                .nonce(new BigInteger("10000"))
                .call("approve")
                .params(params)
                .build();

        SignedTransaction signedTransaction = new SignedTransaction(transaction, currentWallet);
        Bytes hash = iconService.sendTransaction(signedTransaction).execute();
        System.out.println("txHash:" + hash);
        hash
    }

    def createTokenTransaction(KeyWallet currentWallet, String scoreAddressStr, Idol idol) throws IOException {
        Address ownerAddress = currentWallet.getAddress();
        long timestamp = System.currentTimeMillis() * 1000L;
        BigInteger networkId = new BigInteger("3");

        RpcObject transactionParams = new RpcObject.Builder()
                .put("_name", new RpcValue(idol.getName()))
                .put("_age", new RpcValue(idol.getAge()))
                .put("_gender", new RpcValue(idol.getGender()))
                .put("_ipfs_handle", new RpcValue(idol.getIpfs_handle()))
                .build();

        Transaction transaction = TransactionBuilder.of(networkId)
                .from(ownerAddress)
                .to(new Address(scoreAddressStr))
                .stepLimit(new BigInteger("10000"))
                .timestamp(new BigInteger(Long.toString(timestamp)))
                .nonce(new BigInteger("10000"))
                .call("create_idol")
                .params(transactionParams)
                .build();

        SignedTransaction signedTransaction = new SignedTransaction(transaction, currentWallet);
        Bytes hash = iconService.sendTransaction(signedTransaction).execute();
        hash
    }

    def sendTransaction(KeyWallet currentWallet, String scoreAddressStr, String toAddress, String tokenId) throws IOException {
        BigInteger networkId = new BigInteger("3");

        long timestamp = System.currentTimeMillis() * 1000L;
        String toAddStr = toAddress; // second address where the amount is being transfered
        RpcObject params = new RpcObject.Builder()
                .put("_to", new RpcValue(toAddStr))
                .put("_tokenId", new RpcValue(tokenId))
                .build();

        Transaction transaction = TransactionBuilder.of(networkId)
                .from(currentWallet.getAddress())
                .to(new Address(scoreAddressStr))
                .stepLimit(new BigInteger("10000"))
                .timestamp(new BigInteger(Long.toString(timestamp)))
                .nonce(new BigInteger("10000"))
                .call("transfer")
                .params(params)
                .build();

        SignedTransaction signedTransaction = new SignedTransaction(transaction, currentWallet);
        Bytes hash = iconService.sendTransaction(signedTransaction).execute();
        System.out.println("txHash:" + hash);
        hash
    }
}
