package com.example.week3;

public class ApiResponse<T> {
  
  private int code;
  private String message;
  private T data;

  public ApiResponse(int code, String messag, T data) {
    this.code = code;
    this.message = messag;
    this.data = data;
  }
  // 定义一个静态方法 success,它们可接收任意类型data,然后返回一个装着任意类型data的 ApiResponse
  // static <T> 这里T是声明这个方法要使用泛型T；
  // 因为这里是static方法，static方法属于类本身，不属于某一对象。所以不能直接使用类上的泛型T
  // 但 static 方法不依赖对象，所以他要自己声明一份 <T>
  // 可以记忆为：static泛型方法要自己声明<T>
  public static <T> ApiResponse<T> success(Object data) {
    return new ApiResponse<T>(200, "Success", data);
  }

  public static <T> ApiResponse<T> fail(int code, String message) {
    return new ApiResponse<T>(code, message, null);
  }

  public int getCode() {
    return code;
  }

  public String getMessage() {
    return message;
  }

  public T getData() {
    return data;
  }
}