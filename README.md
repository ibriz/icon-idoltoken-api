# iBriz | Idol Token (ICON Foundation)
---
<img src="https://ibriz.ai/wp-content/themes/ibriz-blog/assets/images/ibriz.svg" width="80" height="80"><img src="https://icondev.io/img/logo.png" width="188" height="35">
### Deploying **IdolToken** to tbears
``````sh
$ tbears deploy -t tbears idol_token -f hx65f6e18d378b57612a28f72acb97021eaa82aa5a -k keystore.json -c tbears_cli_config.json
``````
### Sample _keystore.json_ file  ( password is "_password_" )
``````json
{"address":"hx65f6e18d378b57612a28f72acb97021eaa82aa5a","id":"2f19df35-9a11-4b24-a83d-11c8d5e903db","coinType":"icx","version":3,"crypto":{"cipher":"aes-128-ctr","ciphertext":"469900238420d66b02dbbc1d6e978ef0e1f46321e8767cbc9f59bd93499166d4","cipherparams":{"iv":"432a04c7317d83e663e67f605befb326"},"kdf":"scrypt","kdfparams":{"dklen":32,"n":4096,"p":6,"r":8,"salt":"66fad93a131f21c18c8c8bf08ca56641d984eaa20855bcfeac78dcb5264ce3fb"},"mac":"036ab5c07ec0060bf558010108c78c5427e4223047fd2cca3ae523c8e4d9e25e"}}
``````

# How to use?

### Requirements:
- Grails 3.3
- Gradle 4.x
- JAVA 8
- tbears 1.0.5.1
- IPFS daemon

### Steps
We will be using Intellij to open the project in.
- Clone the project https://github.com/ibriz/ibriz-icon-foundation-java
- Open Intellij
- Open the existing project directory "ibriz-icon-foundation-java" or build.gradle file
- Dependencies for the project will be downloaed from build.gradle setup automatically

# API Documentation
### 1.  Check profile of an Account
> http://localhost:8080/iconmain/checkAccountPage?address=hx65f6e18d378b57612a28f72acb97021eaa82aa5a&tokenType=IDOL

#### Parameters

| Key | Value |
| ------ | ------ |
| `address` | hx65f6e18d378b57612a28f72acb97021eaa82aa5a |
| `tokenType` | IDOL |

#### Example Request to retrieve profile information of Account A
Check profile of Account A
```sh
curl --request GET \
  --url 'http://localhost:8080/iconmain/checkAccountPage?address=hx65f6e18d378b57612a28f72acb97021eaa82aa5a&tokenType=IDOL'
```

#### Response
```json
{
  "address": "hx65f6e18d378b57612a28f72acb97021eaa82aa5a",
  "ICXbalance": 0,
  "scoreAddress": "cx0bce5bfe899c4beec7ea93f2000e16351191017e",
  "scoreMap": "{IDOL=cx0bce5bfe899c4beec7ea93f2000e16351191017e}",
  "accountList": [
    "hx65f6e18d378b57612a28f72acb97021eaa82aa5a",
    "hx2a7c46497d99e64d7198c267b5ca7deca265a4f8",
    "hx40ebd13225ed28f7e98be3cd833ebe555cba72ca"
  ],
  "tokenType": "IDOL",
  "tokenList": [
    {
      "owner": "hx65f6e18d378b57612a28f72acb97021eaa82aa5a",
      "gender": "M",
      "tokenId": "f27cb376-af45-11e8-94b7-000c29be104e",
      "name": "Tom Hanks",
      "age": "0x3e",
      "ipfs_handle": "0xabcde12345"
    }
  ],
  "tokenBalance": 1
}
```

### 2.  Token Details
> http://localhost:8080/iconmain/checkTokenDetails?tokenType=IDOL&tokenId=0a55e0d0-af46-11e8-94b7-000c29be104e

#### Parameters

| Key | Value |
| ------ | ------ |
| `tokenType` | IDOL |
| `tokenId` | 0a55e0d0-af46-11e8-94b7-000c29be104e |

#### Example Request
Check profile of Account A
```sh
curl --request GET \
  --url 'http://localhost:8080/iconmain/checkTokenDetails?tokenType=IDOL&tokenId=0a55e0d0-af46-11e8-94b7-000c29be104e'
```

#### Response
```json
{
  "address": "hx40ebd13225ed28f7e98be3cd833ebe555cba72ca",
  "scoreAddress": "cx0bce5bfe899c4beec7ea93f2000e16351191017e",
  "gender": "F",
  "scoreMap": "{IDOL=cx0bce5bfe899c4beec7ea93f2000e16351191017e}",
  "name": "Jennifer Aniston",
  "tokenType": "IDOL",
  "age": "0x31",
  "ipfs_handle": "0xabcde12346"
}
```

### 3.  Create new Token
> http://localhost:8080/iconmain/createIdolToken?address=hx40ebd13225ed28f7e98be3cd833ebe555cba72ca&tokenType=IDOL&name=Jennifer%20Aniston&age=40&gender=F&ipfs_handle=0xsfalsdfjlk2

#### Parameters
| Key | Value |
| ------ | ------ |
| `address` | hx40ebd13225ed28f7e98be3cd833ebe555cba72ca |
| `tokenType` | IDOL |
| `name` | Jennifer Aniston |
| `gender` | F |
| `ipfs_handle` | 0xsfalsdfjlk2 |

#### Example Request
```sh
curl --request GET \
  --url 'http://localhost:8080/iconmain/createIdolToken?address=hx40ebd13225ed28f7e98be3cd833ebe555cba72ca&tokenType=IDOL&name="Jennifer Aniston"&age=40&gender=F&ipfs_handle=0xsfalsdfjlk2'
```

#### Response
```json
{
    "address": "hx40ebd13225ed28f7e98be3cd833ebe555cba72ca",
    "scoreAddress": "cx0bce5bfe899c4beec7ea93f2000e16351191017e",
    "gender": "F",
    "fullname": "\"Jennifer Aniston\"",
    "tokenType": "IDOL",
    "txHash": "0xf02ab1428b355b282323155538c3fb35aeb5f98a27bbbbc7eef335f4d6cf5538",
    "age": "40",
    "ipfs_handle": "0xsfalsdfjlk2"
}
```

### 4.  Transfer token
> http://localhost:8080/iconmain/transfer?fromAddress=hx65f6e18d378b57612a28f72acb97021eaa82aa5a&toAddress=hx40ebd13225ed28f7e98be3cd833ebe555cba72ca&tokenType=IDOL&tokenId=0a55e0d0-af46-11e8-94b7-000c29be104e

#### Parameters
| Key | Value |
| ------ | ------ |
| `fromAddress` | hx65f6e18d378b57612a28f72acb97021eaa82aa5a |
| `toAddress` | hx40ebd13225ed28f7e98be3cd833ebe555cba72ca |
| `tokenType` | IDOL |
| `tokenId` | 0a55e0d0-af46-11e8-94b7-000c29be104e |

#### Example Request
```sh
curl --request GET \
  --url 'http://localhost:8080/iconmain/transfer?fromAddress=hx65f6e18d378b57612a28f72acb97021eaa82aa5a&toAddress=hx40ebd13225ed28f7e98be3cd833ebe555cba72ca&tokenType=IDOL&tokenId=0a55e0d0-af46-11e8-94b7-000c29be104e'
 ```

#### Response
```json
{
  "address": "hx65f6e18d378b57612a28f72acb97021eaa82aa5a",
  "scoreAddress": "cx0bce5bfe899c4beec7ea93f2000e16351191017e",
  "ICXbalance": 0,
  "transferAmount": null,
  "remainingBalance": 1,
  "tokenType": "IDOL",
  "tokenList": [

  ],
  "toAddress": "hx40ebd13225ed28f7e98be3cd833ebe555cba72ca",
  "transactionHash": "0x9387d762562f0e95b09ea03efee8096f6900812df46d9ea905a4f232dcf502dd",
  "tokenBalance": 1
}
```

### 5.  Upload Image
> http://localhost:8080/iconmain/uploadImage

#### Parameters
| Key | Value |
| ------ | ------ |
| images | <bytes file> |

#### Example Request
```sh
curl -X POST \
  http://localhost:8081/iconmain/uploadImage \
  -H 'content-type: multipart/form-data;' \
  -F 'image=@C:\Users\user\Pictures\image.png'
```
#### Response
```json
{
    "ipfsHash": "QmTdejDU8ixgiTa4f986zziGWgYabBeeGHZrEsinFBxmWR",
    "name": "QmTdejDU8ixgiTa4f986zziGWgYabBeeGHZrEsinFBxmWR",
    "size": "10490"
}
```


### 6.  Read Image
> http://localhost:8080/iconmain/showImage?hash=QmTdejDU8ixgiTa4f986zziGWgYabBeeGHZrEsinFBxmWR

#### Parameters
| Key | Value |
| ------ | ------ |
| ipfsHash | QmTdejDU8ixgiTa4f986zziGWgYabBeeGHZrEsinFBxmWR |
| fileByte |  iVBORw0KG... |
| fileContentType | image/png |
#### Example Request
```sh
curl -X GET \
  'http://localhost:8081/iconmain/showImage?hash=QmTdejDU8ixgiTa4f986zziGWgYabBeeGHZrEsinFBxmWR'
```
#### Response
```json
{
    "ipfsHash": "QmTdejDU8ixgiTa4f986zziGWgYabBeeGHZrEsinFBxmWR",
    "fileByte": "iVBORw0KG...",
    "fileContentType": "image/png"
}
```


> **`TODO`:**
* Create generic Account Linked to Wallet