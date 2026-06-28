package ar.training.reactive.infrastructure.adapter.out.persistence.author;

import ar.training.reactive.infrastructure.adapter.out.persistence.BaseEntity;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table("author")
public class AuthorEntity extends BaseEntity<UUID> {

    @Id
    @Column("id")
    private UUID id;

    @Column("name")
    private String name;

    public AuthorEntity(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public UUID getId() { return id; }
    public String getName() { return name; }
    public void setId(UUID id) { this.id = id; }
    public void setName(String name) { this.name = name; }
}
