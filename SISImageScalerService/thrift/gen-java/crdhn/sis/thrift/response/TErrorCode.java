/**
 * Autogenerated by Thrift Compiler (0.9.1)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package crdhn.sis.thrift.response;


import java.util.Map;
import java.util.HashMap;
import org.apache.thrift.TEnum;

public enum TErrorCode implements org.apache.thrift.TEnum {
  EC_OK(0),
  EC_SYSTEM(1),
  EC_PARAM_ERROR(2);

  private final int value;

  private TErrorCode(int value) {
    this.value = value;
  }

  /**
   * Get the integer value of this enum value, as defined in the Thrift IDL.
   */
  public int getValue() {
    return value;
  }

  /**
   * Find a the enum type by its integer value, as defined in the Thrift IDL.
   * @return null if the value is not found.
   */
  public static TErrorCode findByValue(int value) { 
    switch (value) {
      case 0:
        return EC_OK;
      case 1:
        return EC_SYSTEM;
      case 2:
        return EC_PARAM_ERROR;
      default:
        return null;
    }
  }
}
