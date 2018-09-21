package ibriz.icon.idoltoken.api

import foundation.icon.icx.KeyWallet
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

    Map<String, String> scoreMap = new HashMap<String, String>() {
        {
            put("IDOL", "cx2786071ed73dd62de1a2ef5e514f7253cd8389af")
        }
    }

    def index(params) {
        redirect(action: "checkAccountPage", params: params)
    }

    def myWallet(params) {
        params.address = defaultAccountAddress
        String scoreAddress = scoreMap.getOrDefault(params.tokenType, defaultSCORE)

        checkAccountPage(params)
        render([
                address     : params.address,
                tokenType   : 'MNT',
                scoreMap    : scoreMap,
                scoreAddress: scoreAddress
        ] as JSONObject)
    }

    def scoreDetail(String tokenType) {
        return scoreMap.get(tokenType)
    }

    def transferToken() {
        def currentAddress = params.address
        def tokenType = params.tokenType

        [
                address  : currentAddress,
                scoreMap : scoreMap,
                tokenType: tokenType
        ]
    }

    def transferIdolToken() {
        def currentAddress = params.address
        def tokenType = params.tokenType

        [address: currentAddress, scoreMap: scoreMap, tokenType: tokenType]
    }

    def transfer(params) {
        def currentAddress = params.fromAddress
        def scoreAddress = scoreMap.getOrDefault(params.tokenType, defaultSCORE)
        def toAddress = params.toAddress
        String transferAmount = params.tokenAmount
        String tokenId = params?.tokenId
        KeyWallet currentWallet = IconConfiguration.getWalletByAddress(currentAddress)
        def tokenBalance = iconmainService.getTokenBalance(currentWallet, scoreAddress) // initial balance
        def transactionHash
        def tokens = []

        if (params.tokenType == 'IDOL') {
            def approvalTransactionHash = iconmainService.approveTransaction(currentWallet, scoreAddress, toAddress, tokenId)
            transactionHash = iconmainService.sendTransaction(currentWallet, scoreAddress, toAddress, tokenId)
        } else
            transactionHash = iconmainService.transfer(currentWallet, scoreAddress, currentWallet.getAddress(), toAddress, transferAmount)
        def remainingBalance = iconmainService.getTokenBalance(currentWallet, scoreAddress)
        def icxbalance = iconmainService.balanceOfICX(currentAddress)

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

    def checkAccount() {
        def currentAddress = params.address ? params.address : defaultAccountAddress
        String tokenType = params.tokenType ? params.tokenType : defaultToken
        String scoreAddress = scoreMap.getOrDefault(tokenType, defaultSCORE)

        [
                address     : currentAddress,
                scoreMap    : scoreMap,
                tokenType   : tokenType,
                scoreAddress: scoreAddress
        ]
    }

    def createToken() {
        def ownerAddress = params.address ? params.address : defaultAccountAddress
        String tokenType = params.tokenType ? params.tokenType : defaultToken
        String scoreAddress = scoreMap.getOrDefault(tokenType, defaultSCORE)
        [
                address     : ownerAddress,
                tokenType   : tokenType,
                scoreAddress: scoreAddress,
                scoreMap    : scoreMap
        ]
    }

    def createIdolToken(params) {

        def address = params.address ? params.address : defaultAccountAddress
        String tokenType = params.tokenType ? params.tokenType : defaultToken
        String scoreAddress = scoreMap.getOrDefault(tokenType, defaultSCORE)

        KeyWallet currentWallet = IconConfiguration.getWalletByAddress(address)

        String fullname = params.name ? params.name : ""
        String age = params.age ? params.age : "0"
        String gender = params.gender ? params.gender : ""
        String ipfs_handle = params.ipfs_handle ? params.ipfs_handle : ""

        def createTokenTransaction = iconmainService.createTokenTransaction(currentWallet, scoreAddress,
                new Idol(fullname, age as BigInteger, gender, ipfs_handle))

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
        String scoreAddress = scoreMap.getOrDefault(tokenType, defaultSCORE)
        def icxbalance = iconmainService.balanceOfICX(currentAddress)
        def tokenBalance = null
        println "tokenBalance = " + IconConfiguration.listOfAccounts()
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
                scoreMap    : scoreMap,
                tokenList   : tokens,
                accountList : IconConfiguration.listOfAccounts()
        ]
        render(response as JSONObject)
    }

    def checkTokenDetails(params) {
        String currentAddress = params.address ? params.address : defaultAccountAddress
        String tokenId = params.tokenId ? params.tokenId : ""
        String tokenType = params.tokenType ? params.tokenType : defaultToken
        String scoreAddress = scoreMap.getOrDefault(tokenType, defaultSCORE)
        def tokenInfo = iconmainService.getTokenInfo(currentAddress, scoreAddress, tokenId)
        render(
                [
                        address     : tokenInfo.owner,
                        tokenType   : tokenType,
                        scoreAddress: scoreAddress,
                        scoreMap    : scoreMap,
                        name        : tokenInfo.name,
                        age         : tokenInfo.age,
                        gender      : tokenInfo.gender,
                        price       : tokenInfo?.price,
                        ipfs_handle : tokenInfo.ipfs_handle,
                        tokenId     : params.tokenId
                ] as JSONObject)
    }


    def uploadImage() {
        def fileByte = request.getFile('image')


        try {
            byte[] bytes = fileByte.getBytes();

            IPFS ipfs = new IPFS("/ip4/127.0.0.1/tcp/5001");
            NamedStreamable.ByteArrayWrapper file = new NamedStreamable.ByteArrayWrapper(bytes);
            MerkleNode addResult = ipfs.add(file).get(0);

            def result = addResult.toJSON();
            print("result: : " + result)

            render([ipfsHash: result["Hash"], "name": result["Name"], "size": result["Size"]] as JSONObject)
        } catch (Exception e) {
            e.printStackTrace();
            render([error: "Error uploading the file. Please try again."] as JSONObject)
        }
    }

    def showImage(params) {
        IPFS ipfs = new IPFS("/ip4/127.0.0.1/tcp/5001");
        Multihash filePointer = Multihash.fromBase58(params.hash);
        byte[] fileContents = ipfs.cat(filePointer);
        String encodedfile = new String(Base64.encodeBase64(fileContents), "UTF-8");
        def response = [fileContentType: "image/png", fileByte: encodedfile, ipfsHash: params.hash]
        render(response as JSONObject)
    }
}
