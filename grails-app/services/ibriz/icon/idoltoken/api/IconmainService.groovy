package ibriz.icon.idoltoken.api

import foundation.icon.icx.*
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
        RpcObject params = new RpcObject.Builder()
                .put("_to", new RpcValue(toAddress as String))
                .put("_value", new RpcValue(value as String))
                .build();

        callXTransaction(wallet, fromAddress, new Address(scoreAddress as String), "transfer", params)
    }

    def getTokenBalance(String address, String scoreAddressStr) {
        Address currentAddress = new Address(address)
        RpcObject params = new RpcObject.Builder()
                .put("_owner", new RpcValue(currentAddress))
                .build();

        RpcItem result = callInternalTransaction(currentAddress, new Address(scoreAddressStr), "balanceOf", params)
        result.asInteger()
    }

    def getTokenBalance(KeyWallet currentWallet, String scoreAddressStr) {
        Address currentAddress = currentWallet.getAddress();

        RpcObject params = new RpcObject.Builder()
                .put("_owner", new RpcValue(currentAddress))
                .build();

        RpcItem result = callInternalTransaction(currentAddress, new Address(scoreAddressStr), "balanceOf", params)
        result.asInteger()
    }

    def balanceOfICX(address) {
        BigInteger balance = iconService.getBalance(new Address(address)).execute();
        balance
    }

    def getAllTokensOf(String address, String scoreAddressStr) {
        Address currentAddress = new Address(address)

        RpcObject params = new RpcObject.Builder()
                .put("_owner", new RpcValue(currentAddress))
                .build();

        RpcItem result = callInternalTransaction(currentAddress, new Address(scoreAddressStr),"get_tokens_of_owner", params )
        System.out.println(currentAddress.toString() + " :result:" + result.asString());

        def tokenList = []
        JSONObject obj = new JSONObject(result.asString());
        JSONArray idolTokensOfOwner = obj.getJSONArray("idols");
        int index = 0;
        for (Object idolToken : idolTokensOfOwner) {
            tokenList.add(getTokenInfo(address, scoreAddressStr, idolToken.toString(), index).put("tokenId", idolToken.toString()))
            index = index + 1;
        }
        tokenList
    }

    def getAllTokensOf(KeyWallet currentWallet, String scoreAddressStr) throws IOException {
        getAllTokensOf(currentWallet.getAddress().toString(), scoreAddressStr)
    }

    def getTokenInfo(String address, String scoreAddressStr, String tokenId, int index = 0) throws IOException {
        Address firstAddress = new Address(address)

        RpcObject params = new RpcObject.Builder()
                .put("_tokenId", new RpcValue(tokenId))
                .build();

        RpcItem result = callInternalTransaction(firstAddress, new Address(scoreAddressStr), "get_idol", params)

        def jsonObj = new JSONObject(result.asString())
        def price = tokenidPriceMap.containsKey(tokenId) ? tokenidPriceMap.get(tokenId) : priceList.get(index)
        if (!tokenidPriceMap.containsKey(tokenId)) {
            tokenidPriceMap.put(tokenId, priceList.get(index))
        }

        jsonObj = jsonObj.accumulate("price", price)
        if (jsonObj.get("age") != null) {
            if (jsonObj.get("age").toString().startsWith('0x')) {
                jsonObj.put("age", Integer.parseInt(jsonObj.get("age").toString().substring(2), 16))
            } else {
                jsonObj.put("age", Integer.parseInt(jsonObj.get("age").toString()))
            }
        }

//        System.out.println("index: " + index + " - " + firstAddress.toString() + " :result:" + jsonObj.toString())
        jsonObj
    }

    def getTokenInfo(KeyWallet currentWallet, String scoreAddressStr, String tokenId) throws IOException {

        getTokenInfo(currentWallet.getAddress().toString(), scoreAddressStr, tokenId)
    }

    def approveTransaction(KeyWallet currentWallet, String scoreAddressStr, String toAddress, String tokenId) throws IOException {
        RpcObject params = new RpcObject.Builder()
                .put("_to", new RpcValue(toAddress))
                .put("_tokenId", new RpcValue(tokenId))
                .build();

        callXTransaction(currentWallet, currentWallet.getAddress(), new Address(scoreAddressStr), "approve", params)
    }

    def createTokenTransaction(KeyWallet currentWallet, String scoreAddressStr, Idol idol) throws IOException {
        Address ownerAddress = currentWallet.getAddress();

        RpcObject transactionParams = new RpcObject.Builder()
                .put("_name", new RpcValue(idol.getName()))
                .put("_age", new RpcValue(idol.getAge()))
                .put("_gender", new RpcValue(idol.getGender()))
                .put("_ipfs_handle", new RpcValue(idol.getIpfs_handle()))
                .build();

        callXTransaction(currentWallet, ownerAddress, new Address(scoreAddressStr), "create_idol", transactionParams)
    }

    def sendTransaction(KeyWallet currentWallet, String scoreAddressStr, String toAddress, String tokenId) throws IOException {

        RpcObject params = new RpcObject.Builder()
                .put("_to", new RpcValue(toAddress))
                .put("_tokenId", new RpcValue(tokenId))
                .build();

        callXTransaction(currentWallet, currentWallet.getAddress(), new Address(scoreAddressStr), "transfer", params)
    }

    /*
    * callXTransaction Call CUSTOM transaction methods from the token
    * @params _from: Account where transaction is originating
    * @params _to: destination account for transaction
    * @params _method: method name to call
    * @params _params: RpcObject
    *
    * @return Bytes (Transaction Hash)
    *
    * */
    def callXTransaction(_currentWallet, _from, _to, _method, _params){
        long timestamp = System.currentTimeMillis() * 1000L;

        Transaction transaction = TransactionBuilder.of(IconConfiguration.NETWORK_ID)
                .from(_from)
                .to(_to)
                .stepLimit(IconConfiguration.STEP_LIMIT)
                .timestamp(new BigInteger(Long.toString(timestamp)))
                .nonce(IconConfiguration.NOUNCE)
                .call(_method)
                .params(_params)
                .build()

        SignedTransaction signedTransaction = new SignedTransaction(transaction, _currentWallet)
        Bytes hash = iconService.sendTransaction(signedTransaction).execute()
        println "Transaction hash : $hash"
        hash
    }

    /*
    * callInternalTransaction Call INTERNAL transaction methods
    *
    * @params _fromAddress: Address from where the method is being called
    * @params _scoreAddress: Address of the SCORE to call
    * @params _method: method present in the Token
    * @params _params: RpcObject
    *
    * @result RpcItem (result)
    * */
    def callInternalTransaction(_fromAddress, _scoreAddress, _method, _params) throws Exception{
        try {
            Call<RpcItem> call = new Call.Builder()
                    .from(_fromAddress)
                    .to(_scoreAddress)
                    .method(_method)
                    .params(_params)
                    .build();
            RpcItem result = iconService.call(call).execute();
            result
        }catch (Exception ex){
            throw ex
        }
    }
}
