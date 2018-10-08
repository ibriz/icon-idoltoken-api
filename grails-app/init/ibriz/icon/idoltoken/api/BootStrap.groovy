package ibriz.icon.idoltoken.api

import foundation.icon.icx.KeyWallet
import ibiz.icon.idoltoken.api.IconConfiguration
import ibiz.icon.idoltoken.api.WalletOperations

class BootStrap {
    def iconmainService
    def init = { servletContext ->

        Map<String, String> accountKeystore = new HashMap<String, String>() {
            {
                put("hxe9d75191906ccc604fc1e45a9f3c59fb856c215f", "walletkeys" + File.separator + "keystore1.json")
                put("hx266b582598ee78a12825831ac76200520c3a187d", "walletkeys" + File.separator + "keystore2.json")
                put("hxda845aaf0c5829037324c8bbddc8da19ba026d03", "walletkeys" + File.separator + "keystore3.json")
            }
        }
        for (Map.Entry<String, String> accountKey : accountKeystore.entrySet()) {
            def walletMap = WalletOperations.loadWalletFromFile(accountKey.getValue())
            String walletAddress = walletMap['address'] as String
            KeyWallet currentWallet = walletMap['wallet'] as KeyWallet
            IconConfiguration.putAddress(walletAddress, currentWallet)
        }
        def env = System.getenv()
        IconConfiguration.setScoreMap(env['SCORE'])
        println "SCORE = " + env['SCORE']
        iconmainService.load()
    }
    def destroy = {
    }
}
