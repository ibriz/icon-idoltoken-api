package ibiz.icon.idoltoken.api

import foundation.icon.icx.KeyWallet

public class IconConfiguration {
    public static Map<String, KeyWallet> walletMap = new HashMap<>();
    public static Map<String, String> scoreMap = new HashMap<String, String>();

    public static final BigInteger NETWORK_ID = new BigInteger("3");
    public static final BigInteger STEP_LIMIT = new BigInteger("2013265920");
    public static final BigInteger NOUNCE = new BigInteger("10000");

    public static Boolean hasAddress(String address) {
        return walletMap.containsKey(address)
    }

    public static void putAddress(String address, KeyWallet wallet) {
        walletMap.put(address, wallet)
    }

    public static KeyWallet getWalletByAddress(String address) {
        if (walletMap.containsKey(address))
            return walletMap.get(address)
        return null
    }

    public static Set<String> listOfAccounts() {
        return walletMap.keySet()
    }

    public static void setScoreMap(String score_address){
        scoreMap.put("IDOL", score_address)
    }

    public static String getScoreMap(String tokenType, String defaultSCORE){
        return scoreMap.getOrDefault(tokenType, defaultSCORE)
    }
}
