package com.example.week4.project.system.controller;

import java.util.List;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.week4.common.response.ApiResponse;
import com.example.week4.common.response.PageResponse;
import com.example.week4.project.system.domain.DictData;
import com.example.week4.project.system.service.DictDataService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;



@Tag(name="字典管理")
@RequiredArgsConstructor
@RestController
@RequestMapping("/dict")
public class DictController {
  private final DictDataService service;
  
  @GetMapping("/list")
  public ApiResponse<PageResponse<DictData>> getDictList(
    @ParameterObject DictData d
  ){
    return ApiResponse.success(service.getDictPages(d));
  }

  @PostMapping("/add")
  public ApiResponse<Void> createDictData(
    @RequestBody DictData d
  ){
    service.createDicData(d);
    return ApiResponse.success(null);
  }

  @PutMapping("update/{id}")
  public ApiResponse<Void> updateData(
    @PathVariable Integer id,
    @RequestBody DictData data
  ){
    service.updateById(id, data);
    return ApiResponse.success(null);
  }

  @DeleteMapping("/delete")
  public ApiResponse<Void> deleteData(
    @PathVariable List<Integer> ids
  ){
    service.deleteByIds(ids);
    return ApiResponse.success(null);
  }
}
