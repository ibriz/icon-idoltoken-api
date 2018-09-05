package ibriz.icon.idoltoken.api

import foundation.icon.icx.KeyWallet
import ibiz.icon.idoltoken.api.IconConfiguration
import ibiz.icon.idoltoken.api.WalletOperations

class BootStrap {
    def iconmainService
    def init = { servletContext ->

        Map<String, String> accountKeystore = new HashMap<String, String>() {
            {
                put("hx65f6e18d378b57612a28f72acb97021eaa82aa5a", "walletkeys" + File.separator + "UTC--2018-08-24T13-35-41.671000000Z--hx65f6e18d378b57612a28f72acb97021eaa82aa5a.json")
                put("hx40ebd13225ed28f7e98be3cd833ebe555cba72ca", "walletkeys" + File.separator + "UTC--2018-08-24T13-38-56.387000000Z--hx40ebd13225ed28f7e98be3cd833ebe555cba72ca.json")
                put("hx2a7c46497d99e64d7198c267b5ca7deca265a4f8", "walletkeys" + File.separator + "UTC--2018-09-01T12-30-01.728000000Z--hx2a7c46497d99e64d7198c267b5ca7deca265a4f8.json")
            }
        }
        for (Map.Entry<String, String> accountKey : accountKeystore.entrySet()) {
            def walletMap = WalletOperations.loadWalletFromFile(accountKey.getValue())
            String walletAddress = walletMap['address'] as String
            KeyWallet currentWallet = walletMap['wallet'] as KeyWallet
            IconConfiguration.putAddress(walletAddress, currentWallet)
        }

        iconmainService.load()
    }
    def destroy = {
    }
}
