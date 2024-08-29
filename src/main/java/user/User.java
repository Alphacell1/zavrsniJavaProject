package user;

public class User extends BaseUser {
    private final Role role;

    private User(Integer id, String username, Role role) {
        super(id, username);
        this.role = role;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof User && ((User) obj).getId().equals(id));
    }

    public Role getRole() {
        return role;
    }

    @Override
    public String toString() {
        return role.name() + " " + this.getUsername();
    }

    public static class UserBuilder {
        private Integer id;
        private String username;
        private Role role;

        // Methods to set properties
        public UserBuilder setId(Integer id) {
            this.id = id;
            return this;
        }

        public UserBuilder setUsername(String username) {
            this.username = username;
            return this;
        }

        public UserBuilder setRole(Role role) {
            this.role = role;
            return this;
        }

        // Build method to create the User instance
        public User build() {
            return new User(id, username, role);
        }
    }
}
