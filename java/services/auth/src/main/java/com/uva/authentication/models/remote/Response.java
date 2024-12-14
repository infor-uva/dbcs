package com.uva.authentication.models.remote;

import com.uva.authentication.models.RegisterRequest;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Response extends RegisterRequest {
  private int id;
}
