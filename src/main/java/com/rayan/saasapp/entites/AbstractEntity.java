package com.rayan.saasapp.entites;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

import static jakarta.persistence.GenerationType.UUID;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
//@FilterDef(
//        name = "tenantFilter",
//        parameters = @ParamDef(name = "tenantId", type = String.class),
//        defaultCondition = "tenant_id = :tenantId"
//)
//@Filter(name = "tenantFilter")
public class AbstractEntity {

    @Id
    @GeneratedValue(strategy = UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private String id;
    //    @Column(name = "tenant_id", nullable = false)
//    private String tenantId;
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdDate;
    @Column(name = "updated_at", insertable = false)
    @LastModifiedDate
    private LocalDateTime updatedAt;
    @Column(name = "deleted", nullable = false)
    private Boolean deleted;
    @CreatedBy
    @Column(name = "created_by", updatable = false, nullable = false)
    private String createdBy;
    @LastModifiedBy
    @Column(name = "updated_by", insertable = false)
    private String updatedBy;

    @PrePersist
    public void onCreate() {
        if (this.deleted == null) {
            this.deleted = false;
        }
//        if (this.tenantId == null) {
//            this.tenantId = "default";
//        }
    }
}
