package ibriz.icon.idoltoken.api

import foundation.icon.icx.KeyWallet
import ibiz.icon.idoltoken.api.IconConfiguration
import ibiz.icon.idoltoken.api.WalletOperations

class BootStrap {
    def iconmainService
    def init = { servletContext ->

        Map<String, String> accountKeystore = new HashMap<String, String>() {
            {
                put("hxaad0d6e403a9f62bcdbe4479a241786c1c346d3c", "walletkeys" + File.separator + "keystore1.json")
                put("hx829238bc4d7100ca918b2c9c3768edc97de1d375", "walletkeys" + File.separator + "keystore2.json")
                put("hx345bebadc9537481e445a721f5618acf0b4a3b2d", "walletkeys" + File.separator + "keystore3.json")
            }
        }
        for (Map.Entry<String, String> accountKey : accountKeystore.entrySet()) {
            def walletMap = WalletOperations.loadWalletFromFile(accountKey.getValue())
            String walletAddress = walletMap['address'] as String
            KeyWallet currentWallet = walletMap['wallet'] as KeyWallet
            IconConfiguration.putAddress(walletAddress, currentWallet)
        }
//        def env = System.getenv()
//        IconConfiguration.setScoreMap(env['SCORE'])
//        println "SCORE = " + env['SCORE']
        IconConfiguration.setScoreMap("cx93af6ebd0814bb6eddf662c2ea43a3138271e559")
        iconmainService.load()
    }
    def destroy = {
    }
}
