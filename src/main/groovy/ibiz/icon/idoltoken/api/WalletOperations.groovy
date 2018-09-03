package ibiz.icon.idoltoken.api

import foundation.icon.icx.KeyWallet
import foundation.icon.icx.Wallet
import org.web3j.crypto.CipherException

class WalletOperations {
    public static loadWalletFromPrivateKey(String privateKey) {

    }

    public static loadWalletFromFile(String filename) throws Exception {
        Wallet wallet;
        File file = new File(filename);
        try {
            System.out.println("before loading wallet");
            wallet = KeyWallet.load("password", file);
            System.out.println("after loading wallet" + wallet.getAddress());
            return [address: wallet.getAddress(), wallet: wallet]
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CipherException e) {
            e.printStackTrace();
        }
        []
    }
}
