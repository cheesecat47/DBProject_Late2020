package phase3;

public class AccountInfo {
    private boolean status = false;
    private String id = "";
    private String pw = "";
    private String name = "";
    private String bday = "";
    private String sex = "";
    private String addr = "";
    private String phone = "";
    private String identity = "";
    private String job = "";
    private String membership = "";

    public AccountInfo(boolean status, String id, String pw, String name, String bday, String sex, String addr, String phone, String identity, String job, String membership) {
        this.status = status;
        this.id = id;
        this.pw = pw;
        this.name = name;
        this.bday = bday;
        this.sex = sex;
        this.addr = addr;
        this.phone = phone;
        this.identity = identity;
        this.job = job;
        this.membership = membership;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPw() {
        return pw;
    }

    public void setPw(String pw) {
        this.pw = pw;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBday() {
        return bday;
    }

    public void setBday(String bday) {
        this.bday = bday;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getMembership() {
        return membership;
    }

    public void setMembership(String membership) {
        this.membership = membership;
    }
}
