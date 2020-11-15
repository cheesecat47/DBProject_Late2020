package phase3;

public class LoginInfo {
    boolean status = false;
    String id = "";
    String pw = "";

    public LoginInfo () {}

    public LoginInfo(boolean status, String id, String pw) {
        setStatus(status);
        setId(id);
        setPw(pw);
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
}
