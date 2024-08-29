package user;

import java.io.Serializable;

public enum Role implements Serializable {

    ADMIN,
    STANDARD;

    public static Role getRoleFromString(String role) {
        for (Role r : Role.values()) {
            if (r.toString().equals(role)) {
                return r;
            }
        }
        return null;
    }

    public static Role getRoleFromId(int id) {
        return id == 0 ? Role.ADMIN : Role.STANDARD;
    }

    public Integer getRoleId() {
        return this.ordinal();
    }
}
