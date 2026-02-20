package com.uva.api.auth.modules.user;

import com.uva.api.auth.modules.internal.dto.UserRol;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true, nullable = false)
  private String email;

  /** BCrypt hash — nullable for OAuth2-only users (no password set). */
  @Column
  private String password;

  @Column(nullable = false)
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  @Builder.Default
  private UserRol rol = UserRol.CLIENT;

  /** How this user was created: LOCAL, GITHUB, GOOGLE. */
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  @Builder.Default
  private AuthProvider provider = AuthProvider.LOCAL;

  /** External provider user ID (e.g. GitHub numeric ID). Null for LOCAL users. */
  @Column
  private String providerUserId;
}
