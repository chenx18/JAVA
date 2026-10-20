package com.example.week4.common.enums;

public enum DepartmentStatusEnum {
  
  DISABLED(0, "Disabled"),
  ENABLED(1, "Enabled");

  private final int code;
  private final String label;

  DepartmentStatusEnum(int code, String label) {
    this.code = code;
    this.label = label;
  }

  public int getCode() {
    return code;
  }

  public String getLabel() {
    return label;
  }

  public static boolean containsCode(Integer code) {
    if(code == null) {
      return true;
    }

    for(DepartmentStatusEnum status : DepartmentStatusEnum.values()) {
      if(status.getCode() == code) {
        return true;
      }
    }
    
    return false;
  }
}
