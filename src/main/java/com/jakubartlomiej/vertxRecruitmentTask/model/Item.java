package com.jakubartlomiej.vertxRecruitmentTask.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class Item {
  private UUID id;
  private UUID owner;
  private String name;
}
