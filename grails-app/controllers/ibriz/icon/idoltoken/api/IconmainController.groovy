package ibriz.icon.idoltoken.api

import foundation.icon.icx.KeyWallet
import grails.converters.JSON
import ibiz.icon.idoltoken.api.IconConfiguration
import ibiz.icon.idoltoken.api.Idol
import io.ipfs.api.IPFS
import io.ipfs.api.MerkleNode
import io.ipfs.api.NamedStreamable
import io.ipfs.multihash.Multihash
import org.apache.commons.codec.binary.Base64
import org.grails.web.json.JSONObject

class IconmainController {
    static responseFormats = ['json']

    def iconmainService

    def defaultAccountAddress = "hx65f6e18d378b57612a28f72acb97021eaa82aa5a"
    def defaultSCORE = "cx92b9ca3965c4f44f265a35f31498ddb9821ea5a0"
    def defaultToken = "IDOL"

    def about() {
        render([
                icon_java_sdk: "0.9.7",
                tbears       : "1.0.5.1",
                developedBy  : "iBriz.ai"
        ] as JSONObject)
    }

    def index(params) {
        redirect(action: "checkAccountPage", params: params)
    }

    def myWallet(params) {
        params.address = defaultAccountAddress
        String scoreAddress = IconConfiguration.getScoreMap(tokenType, defaultSCORE)

        checkAccountPage(params)
        render([
                address     : params.address,
                tokenType   : 'IDOL',
                scoreAddress: scoreAddress
        ] as JSONObject)
    }

    def transfer(params) {
        def requestMap = request.JSON as Map
        params.putAll(requestMap)

        def currentAddress = params.fromAddress
        String tokenType = params.tokenType ? params.tokenType : defaultToken
        def scoreAddress = IconConfiguration.getScoreMap(tokenType, defaultSCORE)
        def toAddress = params.toAddress
        String transferAmount = params.tokenAmount
        String tokenId = params?.tokenId
        KeyWallet currentWallet = IconConfiguration.getWalletByAddress(currentAddress)
        def tokenBalance
        def transactionHash
        def remainingBalance
        def icxbalance
        def tokens = []
        try {
            tokenBalance = iconmainService.getTokenBalance(currentWallet, scoreAddress) // initial balance

            if (params.tokenType == 'IDOL') {
                def approvalTransactionHash = iconmainService.approveTransaction(currentWallet, scoreAddress, toAddress, tokenId)
                transactionHash = iconmainService.sendTransaction(currentWallet, scoreAddress, toAddress, tokenId)
            } else
                transactionHash = iconmainService.transfer(currentWallet, scoreAddress, currentWallet.getAddress(), toAddress, transferAmount)
            remainingBalance = iconmainService.getTokenBalance(currentWallet, scoreAddress)
            icxbalance = iconmainService.balanceOfICX(currentAddress)
        } catch (Exception ex) {
            ex.printStackTrace()
            return render([error: "Couldn't transfer Idol Token. Error connecting to the blockchain."] as JSONObject)
        }
        def response = [
                address         : currentWallet.getAddress(),
                toAddress       : toAddress,
                scoreAddress    : scoreAddress,
                transferAmount  : transferAmount,
                transactionHash : transactionHash,
                remainingBalance: remainingBalance,
                tokenBalance    : tokenBalance,
                ICXbalance      : icxbalance,
                tokenType       : params.tokenType,
                tokenList       : tokens
        ]
        render(response as JSONObject)
    }

    def createIdolToken(params) {
        def requestMap = request.JSON as Map
        params.putAll(requestMap)

        def address = params.address ? params.address : defaultAccountAddress
        String tokenType = params.tokenType ? params.tokenType : defaultToken
        String scoreAddress = IconConfiguration.getScoreMap(tokenType, defaultSCORE)

        KeyWallet currentWallet = IconConfiguration.getWalletByAddress(address)

        String fullname = params.name ? params.name : ""
        String age = params.age ? params.age : "0"
        String gender = params.gender ? params.gender : ""
        String ipfs_handle = params.ipfs_handle ? params.ipfs_handle : ""
        def createTokenTransaction
        try {
            createTokenTransaction = iconmainService.createTokenTransaction(currentWallet, scoreAddress,
                    new Idol(fullname, age as BigInteger, gender, ipfs_handle))
        }catch (Exception ex){
            ex.printStackTrace()
            return render([error: "Couldn't create Idol Token. Error connecting to the blockchain."] as JSONObject)
        }
        render(
                [
                        address     : address,
                        scoreAddress: scoreAddress,
                        tokenType   : tokenType,
                        fullname    : fullname,
                        age         : age,
                        gender      : gender,
                        ipfs_handle : ipfs_handle,
                        txHash      : createTokenTransaction
                ] as JSONObject)
    }

    // TODO disabled the need of keystore right now,
    // changse will require the need of keystore for the entered address to see the account page
    def checkAccountPage(params) {
        def currentAddress = params.address ? params.address : defaultAccountAddress
        String tokenType = params.tokenType ? params.tokenType : defaultToken
        String scoreAddress = IconConfiguration.getScoreMap(tokenType, defaultSCORE)
        def icxbalance
        try {
            icxbalance = iconmainService.balanceOfICX(currentAddress)
        } catch (Exception ex) {
            ex.printStackTrace()
            return render([error: "Couldn't get Account Information. Error connecting to the blockchain."] as JSONObject)
        }
        def tokenBalance = null
        def tokens = []
        try {
            KeyWallet currentWallet = IconConfiguration.getWalletByAddress(currentAddress)
            tokenBalance = iconmainService.getTokenBalance(currentWallet, scoreAddress)

            if (tokenType == defaultToken) {
                tokens = iconmainService.getAllTokensOf(currentWallet, scoreAddress)
            }
        } catch (Exception ex) {
            ex.printStackTrace()
            println "Invalid Keystore Error:" + ex.toString()
            tokenBalance = iconmainService.getTokenBalance(currentAddress, scoreAddress)
            if (tokenType == defaultToken) {
                tokens = iconmainService.getAllTokensOf(currentAddress, scoreAddress)
            }
        }

        def response = [
                address     : currentAddress,
                tokenType   : tokenType,
                tokenBalance: tokenBalance,
                ICXbalance  : icxbalance,
                scoreAddress: scoreAddress,
                tokenList   : tokens,
                accountList : IconConfiguration.listOfAccounts()
        ]
        render(response as JSONObject)
    }

    def checkTokenDetails(params) {
        String currentAddress = params.address ? params.address : defaultAccountAddress
        String tokenId = params.tokenId ? params.tokenId : ""
        String tokenType = params.tokenType ? params.tokenType : defaultToken
        String scoreAddress = IconConfiguration.getScoreMap(tokenType, defaultSCORE)

        try {
            def tokenInfo = iconmainService.getTokenInfo(currentAddress, scoreAddress, tokenId)
            render(
                    [
                            address     : tokenInfo?.owner,
                            name        : tokenInfo?.name,
                            age         : tokenInfo?.age,
                            gender      : tokenInfo?.gender,
                            price       : tokenInfo?.price,
                            ipfs_handle : tokenInfo?.ipfs_handle,
                            tokenType   : tokenType,
                            scoreAddress: scoreAddress,
                            tokenId     : params.tokenId
                    ] as JSONObject)
        } catch (Exception ex) {
            ex.printStackTrace()
            render([error: "Couldn't get Token information. Error connecting to the blockchain."] as JSONObject)
        }

    }

    def uploadImage() {
        def fileByte = request.getFile('image')


        try {
            byte[] bytes = fileByte.getBytes();

            IPFS ipfs = new IPFS("/ip4/127.0.0.1/tcp/5001");
            NamedStreamable.ByteArrayWrapper file = new NamedStreamable.ByteArrayWrapper(bytes);
            MerkleNode addResult = ipfs.add(file).get(0);

            def result = addResult.toJSON();

            render([ipfsHash: result["Hash"], "name": result["Name"], "size": result["Size"]] as JSONObject)
        } catch (Exception e) {
            e.printStackTrace();
            render([error: "Error uploading the file. Please try again."] as JSONObject)
        }
    }

    def showImage(params) {
        try {
            IPFS ipfs = new IPFS("/ip4/127.0.0.1/tcp/5001");
            Multihash filePointer = Multihash.fromBase58(params.hash);
            byte[] fileContents = ipfs.cat(filePointer);
            String encodedfile = new String(Base64.encodeBase64(fileContents), "UTF-8");
            def response = [fileContentType: "image/png", fileByte: encodedfile, ipfsHash: params.hash]
            render(response as JSONObject)
        } catch (Exception ex) {
            ex.printStackTrace()
            render([error: "Error getting image information."] as JSONObject)
        }
    }
}
