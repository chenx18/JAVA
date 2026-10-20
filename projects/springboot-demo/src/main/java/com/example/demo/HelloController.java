
// 当前这个类属于 com.example.demo 这个包
// Java 用包来组织代码，避免重名，也方便分层管理
package com.example.demo;

// GetMapping 用来定义一个GET请求接口，告诉Spring Boot 访问某个路径时，执行这个方法；
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class HelloController {

  // 是一个 GET 请求接口
  // 访问 /hello 时执行这个方法
  @GetMapping("/hello") 
  public ApiResponse<String> hello() {
    return ApiResponse.success("Hello spring boot");
  }

  // @RequestParam 用于接收 查询参数，
  @GetMapping("/hello-name")
  public ApiResponse<String> helloName(@RequestParam String name) {
    if(name == null || name.trim().isEmpty()){
      throw new IllegalArgumentException("name is blank");
    }
    return ApiResponse.success("hello, name" + name);
  }

  // @PathVariable 是用在路径参数里的。
  @GetMapping("/user/{id}")
  public ApiResponse<String> gerUserById(@PathVariable int id) {
    if(id <= 0) {
      throw new IllegalArgumentException("id must be > 0");
    }
    if(id != 1) {
      throw new UserNotFoundException(id);
    }
    return ApiResponse.success("user id: " + id);
  }


}