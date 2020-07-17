package ibiz.icon.idoltoken.api

import foundation.icon.icx.KeyWallet
import foundation.icon.icx.Wallet

class WalletOperations {
    public static loadWalletFromPrivateKey(String privateKey) {

    }

    public static loadWalletFromFile(String filename) throws Exception {
        Wallet wallet;
        File file = new File(filename);
        try {
            System.out.println("before loading wallet");
            wallet = KeyWallet.load("p@ssword1", file);
            System.out.println("after loading wallet" + wallet.getAddress());
            return [address: wallet.getAddress(), wallet: wallet]
        } catch (IOException e) {
            e.printStackTrace();
        }
        []
    }
}
