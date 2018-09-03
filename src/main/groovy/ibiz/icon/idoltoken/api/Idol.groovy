package ibiz.icon.idoltoken.api

class Idol {
    String owner;

    String name;
    BigInteger age;
    String gender;
    String ipfs_handle;

    Idol(String name, BigInteger age, String gender, String ipfs_handle) {
        this.name = name
        this.age = age
        this.gender = gender
        this.ipfs_handle = ipfs_handle
    }

    String getOwner() {
        return owner
    }

    void setOwner(String owner) {
        this.owner = owner
    }

    String getName() {
        return name
    }

    void setName(String name) {
        this.name = name
    }

    BigInteger getAge() {
        return age
    }

    void setAge(BigInteger age) {
        this.age = age
    }

    String getGender() {
        return gender
    }

    void setGender(String gender) {
        this.gender = gender
    }

    String getIpfs_handle() {
        return ipfs_handle
    }

    void setIpfs_handle(String ipfs_handle) {
        this.ipfs_handle = ipfs_handle
    }
}
