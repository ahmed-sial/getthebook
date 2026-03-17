package com.ahmedhassan.getthebook.controllers;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ahmedhassan.getthebook.dtos.responses.BookShareResponse;
import com.ahmedhassan.getthebook.services.BookShareService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/shares")
@RequiredArgsConstructor
public class BookShareController {

  private final BookShareService _bookShareService;
  
  @GetMapping("{share-id}")
  public ResponseEntity<BookShareResponse> fetchSingleBookShareRecord(
    @PathVariable("share-id") UUID bookShareId
  ) {
    var response = _bookShareService.getSingleBookShareRecord(bookShareId);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }
}
