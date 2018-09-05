package ibiz.icon.idoltoken.api

import foundation.icon.icx.KeyWallet
import foundation.icon.icx.Wallet
import org.web3j.crypto.CipherException

import org.apache.commons.codec.binary.Base64;
import java.util.Arrays;

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

        public static void main(String[] args) {
            String hello = "Hello World";

            //
            // The encodeBase64 method take a byte[] as the paramater. The byte[]
            // can be from a simple string like in this example or it can be from
            // an image file data.
            //
            byte[] encoded = Base64.encodeBase64(hello.getBytes());

            //
            // Print the encoded byte array
            //
            System.out.println(Arrays.toString(encoded));

            //
            // Print the encoded string
            //
            String encodedString = new String(encoded);
            System.out.println(hello + " = " + encodedString);
        }

}
