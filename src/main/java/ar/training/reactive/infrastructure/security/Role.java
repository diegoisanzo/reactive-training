package ar.training.reactive.infrastructure.security;

import java.util.List;

public enum Role {
    READ,
    WRITE,
    ADMIN;

    public static final String SPRING_SECURITY_ROLE_PREFIX = "ROLE_";

    public static final List<Role> ALL = List.of(values());
    public static final List<String> ALL_NAMES = ALL.stream().map(Role::name).toList();
}
