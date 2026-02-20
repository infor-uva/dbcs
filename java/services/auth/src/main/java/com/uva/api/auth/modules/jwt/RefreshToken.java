package com.uva.api.auth.modules.jwt;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "refresh_tokens")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true, nullable = false)
  private String token;

  @Column(nullable = false)
  private String email;

  @Column(nullable = false)
  private Instant expiresAt;

  @Builder.Default
  @Column(nullable = false)
  private boolean revoked = false;
}
