package ar.training.reactive.infrastructure.security;

import java.util.*;

public enum Role {
    READ,
    WRITE,
    ADMIN;

    public static final String SPRING_SECURITY_ROLE_PREFIX = "ROLE_";

    public static final Set<Role> ALL = EnumSet.allOf(Role.class);
    public static final List<String> ALL_NAMES = ALL.stream().map(Role::name).toList();
    /*
    public static final Map<String, String> ROLE_TO_SPRING_SECURITY_ROLE =
            Map.copyOf(ALL.stream()
                    .map(role -> {
                        Map<String, String> m = new HashMap<>();
                        m.put(role.name(), role.getSpringSecurityRole());
                        return m;
                    })
                    .reduce(
                            HashMap.<String, String>newHashMap(ALL.size()),
                            (map, otherMap) ->
                            {
                                map.putAll(otherMap);
                                return map;
                            }
                    ));*/

    private final String springSecurityRole;

    Role() {
        this.springSecurityRole = SPRING_SECURITY_ROLE_PREFIX + this.name();
    }

    public String getSpringSecurityRole() {
        return springSecurityRole;
    }
}
